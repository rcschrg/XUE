package de.verygame.xue;

import de.verygame.xue.exception.*;
import de.verygame.xue.handler.*;
import de.verygame.xue.handler.action.Action;
import de.verygame.xue.handler.annotation.DependencyHandler;
import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.mapping.GlobalMappings;
import de.verygame.xue.util.ReflectionUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rico on 10.07.2015.
 *
 * @author Rico Schrage
 */
public class XueCore<T> {

    private final ElementsTagGroupHandler<T> elementsTagHandler;
    private final ConstantTagGroupHandler constantTagHandler;
    private final ActionSequenceTagGroupHandler actionSequenceTagHandler;

    private final List<TagGroupHandler<?, ?>> tagGroupHandlerList;
    private final List<TagGroupHandler<?, ?>> closed;

    /**
     * Constructs XueCore, which uses tagMapping and attributeMapping for everything, which have to be mapped.
     *
     * @param globalMappings Mapping to builders
     */
    public XueCore(GlobalMappings<T> globalMappings) throws XueException {
        tagGroupHandlerList = new ArrayList<>();
        closed = new ArrayList<>();

        constantTagHandler = new ConstantTagGroupHandler();
        elementsTagHandler = new ElementsTagGroupHandler<>(globalMappings);
        actionSequenceTagHandler = new ActionSequenceTagGroupHandler();

        addHandler(constantTagHandler);
        addHandler(elementsTagHandler);
        addHandler(actionSequenceTagHandler);

        for (TagGroupHandler<?, ?> otherT : tagGroupHandlerList) {
            for (TagGroupHandler<?, ?> t : tagGroupHandlerList) {
                if (otherT != t) {
                    injectDependency(otherT, t);
                }
            }
        }
    }

    private TagGroupHandler<?, ?> calculateNextTagHandler() throws XueException {
        for (TagGroupHandler<?, ?> t : tagGroupHandlerList) {
            if (closed.contains(t)) {
                continue;
            }
            List<Class<?>> depend = calcDependencies(t);
            if (depend.isEmpty() || depend.containsAll(closed)) {
                closed.add(t);
                return t;
            }
        }
        throw new XueException("The dependencies of the tag handler are cyclic!");
    }

    private List<Class<?>> calcDependencies(TagGroupHandler<?, ?> tagGroupHandler) {
        List<Field> fields = ReflectionUtils.getAllFields(tagGroupHandler.getClass());
        List<Class<?>> tagHandlers = new ArrayList<>();
        for (final Field field : fields) {
            if (field.isAnnotationPresent(DependencyHandler.class)) {
                tagHandlers.add(field.getType());
            }
        }
        return tagHandlers;
    }

    public void addHandler(TagGroupHandler<?, ?> tagGroupHandler) {
        this.tagGroupHandlerList.add(tagGroupHandler);
    }

    public void update(float delta) {
        for (int i = 0; i < tagGroupHandlerList.size(); ++i) {
            TagGroupHandler<?, ?> handler = tagGroupHandlerList.get(i);
            handler.update(delta);
        }
    }

    public void onInputEvent(XueInputEvent inputEvent) {
        for (int i = 0; i < tagGroupHandlerList.size(); ++i) {
            TagGroupHandler<?, ?> handler = tagGroupHandlerList.get(i);
            handler.onInputEvent(inputEvent);
        }
    }

    /**
     * Convenience method to set the mapping of the constant group.
     *
     * @param mapping maps tag names to the constant builders.
     */
    public void addElementMapping(BuilderMapping<T> mapping) {
        elementsTagHandler.addBuilderMapping(mapping);
    }

    /**
     * Convenience method to set the mapping of the element group.
     *
     * @param mapping maps tag names to the corresponding element builders.
     */
    public void addConstantMapping(BuilderMapping<Object> mapping) {
        constantTagHandler.addBuilderMapping(mapping);
    }

    /**
     * @param mapping mapping name - actionSequence implementation
     */
    public void addActionSequenceMapping(BuilderMapping<Action> mapping) {
        actionSequenceTagHandler.addBuilderMapping(mapping);
    }

    /**
     * Generic method to add a mapping to the given tagHandler. For core tag handler use the special methods instead.
     * This method should be used for custom tagHandler.
     *
     * @param tagHandler handler of a group of tags
     * @param builderMapping mapping
     * @param <B> makes sure that the tagHandler und builderMapping fit together
     */
    public <B, D> void addMapping(Class<TagGroupHandler<B, D>> tagHandler, BuilderMapping<B> builderMapping) {
        for (TagGroupHandler<?, ?> t : tagGroupHandlerList) {
            if (t.getClass() == tagHandler) {
                //noinspection unchecked
                ((TagGroupHandler<B, D>)t).addBuilderMapping(builderMapping);
            }
        }
    }

    /**
     * @param name name of the element you want to retrieve
     * @return element mapped to the given name
     */
    public T getElementByName(String name) {
        return elementsTagHandler.getResultMap().get(name);
    }

    /**
     * @param name name of the const you want to retrieve
     * @return const mapped to the given name
     */
    public Object getConstByName(String name) {
        return constantTagHandler.getResultMap().get(name);
    }

    /**
     * @return map containing all elements mapped to their name
     */
    public Map<String, T> getElementMap() {
        return elementsTagHandler.getResultMap();
    }

    /**
     * @return map containing all constants mapped to their name
     */
    public Map<String, Object> getConstMap() {
        return constantTagHandler.getResultMap();
    }

    /**
     * @return map containing all action sequences mapped to their name
     */
    public Map<String, ActionSequence> getActionSequenceMap() {
        return actionSequenceTagHandler.getActionSequenceMap();
    }

    /**
     * Generic method to get the map of a tagHandler.
     *
     * @param handlerClass type of the handler you want to get the map from
     * @param <B> ensures a correct return type
     * @return Result of the handlerClass
     */
    public <B, D> Map<String, B> getResult(Class<TagGroupHandler<B, D>> handlerClass) {
        for (TagGroupHandler<?, ?> t : tagGroupHandlerList) {
            if (t.getClass() == handlerClass) {
                //noinspection unchecked
                return (Map<String, B>) t.getResultMap();
            }
        }
        throw new IllegalArgumentException(handlerClass + " is no part of this core!");
    }

    /**
     * Creates all elements specified in xml-file, which has been used to create the PullParser.
     *
     * @param xpp PullParser, which has been created with the xml resource.
     *
     * @throws XueSyntaxException see {@link XueSyntaxException}
     * @throws ConstTagUnknownException see {@link ConstTagUnknownException}
     * @throws AttributeUnknownException see {@link AttributeUnknownException}
     * @throws ElementTagUnknownException see {@link ElementTagUnknownException}
     */
    public void load(XmlPullParser xpp) throws XueException {
        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {

                    case XmlPullParser.START_TAG:
                        handleStartTag(xpp);
                        break;

                    case XmlPullParser.END_TAG:
                        handleEndTag(xpp);
                        break;

                    default:
                        break;
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            throw new XueParseException(e);
        } catch (IOException e) {
            throw new XueIOException(e);
        }
    }

    /**
     * Defines what happens when the parser reaches a start-tag.
     *
     * @param xpp PullParser, which has been created with the xml resource.
     * @throws XueSyntaxException if a const tag has been placed wrong
     * @throws ConstTagUnknownException if a tag in <constants>...</constants> is unknown
     * @throws AttributeUnknownException if an attribute is unknow.
     * @throws ElementTagUnknownException if a tag in <elements>...</elements> is unknown
     */
    private void handleStartTag(XmlPullParser xpp) throws XueException {
        for (TagGroupHandler pT : tagGroupHandlerList) {
            if (pT.getName().equals(xpp.getName())) {
                pT.setActive(true);
                pT.startHandle(xpp);
                return;
            }
        }
        for (TagGroupHandler t : tagGroupHandlerList) {
            if (t.isActive()) {
                t.handle(xpp);
            }
        }
    }

    /**
     * Defines what happens when the parser reaches an end-tag.
     *
     * @param xpp PullParser, which has been created with the xml resource.
     */
    private void handleEndTag(XmlPullParser xpp) throws XueException {
        for (TagGroupHandler t : tagGroupHandlerList) {
            if (t.getName().equals(xpp.getName()) && t.isActive()) {
                t.setActive(false);
                t.stopHandle(xpp);
            }
        }
    }

    /**
     * Injects dependencies.
     *
     * @param injectTarget target of the injection
     * @param injectable injectable which contains injectable result or builder
     */
    private void injectDependency(TagGroupHandler injectTarget, TagGroupHandler injectable) throws XueException {
        List<Field> fields = ReflectionUtils.getAllFields(injectTarget.getClass());
        for (final Field field : fields) {
            if (field.isAnnotationPresent(DependencyHandler.class) && field.getType() == injectable.getClass()) {
                try {
                    field.setAccessible(true);
                    field.set(injectTarget, injectable);
                }
                catch (IllegalAccessException e) {
                    throw new XueException(e);
                }
            }
        }
    }

}
