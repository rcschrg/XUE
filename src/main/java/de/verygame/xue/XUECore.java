package de.verygame.xue;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.verygame.xue.util.ReflectionUtils;
import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.exception.ConstTagUnknownException;
import de.verygame.xue.exception.ElementTagUnknownException;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.exception.XueSyntaxException;
import de.verygame.xue.handler.ActionSequence;
import de.verygame.xue.handler.ActionSequenceTagHandler;
import de.verygame.xue.handler.BuilderMapping;
import de.verygame.xue.handler.ConstantTagHandler;
import de.verygame.xue.handler.ElementsTagHandler;
import de.verygame.xue.mapping.GlobalMappings;
import de.verygame.xue.handler.TagHandler;
import de.verygame.xue.handler.action.Action;
import de.verygame.xue.handler.annotation.DependencyHandler;

/**
 * Created by Rico on 10.07.2015.
 *
 * @author Rico Schrage
 */
public class XueCore<T> {

    private final ElementsTagHandler<T> elementsTagHandler;
    private final ConstantTagHandler constantTagHandler;
    private final ActionSequenceTagHandler actionSequenceTagHandler;

    private final List<TagHandler<?, ?>> tagHandlerList;
    private final List<TagHandler<?, ?>> closed;

    /**
     * Constructs XueCore, which uses tagMapping and attributeMapping for everything, which have to be mapped.
     *
     * @param globalMappings Mapping to builders
     */
    public XueCore(GlobalMappings<T> globalMappings) {
        tagHandlerList = new ArrayList<>();
        closed = new ArrayList<>();

        constantTagHandler = new ConstantTagHandler();
        elementsTagHandler = new ElementsTagHandler<>(globalMappings);
        actionSequenceTagHandler = new ActionSequenceTagHandler();

        tagHandlerList.add(constantTagHandler);
        tagHandlerList.add(elementsTagHandler);
        tagHandlerList.add(actionSequenceTagHandler);

        for (TagHandler<?, ?> otherT : tagHandlerList) {
            for (TagHandler<?, ?> t : tagHandlerList) {
                if (otherT != t) {
                    injectDependency(otherT, t);
                }
            }
        }
    }

    private TagHandler<?, ?> calculateNextTagHandler() throws XueException {
        for (TagHandler<?, ?> t : tagHandlerList) {
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

    private List<Class<?>> calcDependencies(TagHandler<?, ?> tagHandler) {
        List<Field> fields = ReflectionUtils.getAllFields(tagHandler.getClass());
        List<Class<?>> tagHandlers = new ArrayList<>();
        for (final Field field : fields) {
            if (field.isAnnotationPresent(DependencyHandler.class)) {
                tagHandlers.add(field.getType());
            }
        }
        return tagHandlers;
    }

    public void update(float delta) {
        actionSequenceTagHandler.updateActionSequences(delta);
    }

    public void onInputEvent(XueInputEvent inputEvent) {
        switch (inputEvent) {
            case RESIZE:
                elementsTagHandler.onResizeEvent();
                break;

            case ACTIVATE:
            case DEACTIVATE:
                actionSequenceTagHandler.onInputEvent(inputEvent);
                break;

            default:
                break;
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
    public <B, D> void addMapping(Class<TagHandler<B, D>> tagHandler, BuilderMapping<B> builderMapping) {
        for (TagHandler<?, ?> t : tagHandlerList) {
            if (t.getClass() == tagHandler) {
                //noinspection unchecked
                ((TagHandler<B, D>)t).addBuilderMapping(builderMapping);
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
    public <B, D> Map<String, B> getResult(Class<TagHandler<B, D>> handlerClass) {
        for (TagHandler<?, ?> t : tagHandlerList) {
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
     * @throws XmlPullParserException see: {@link XmlPullParserException}
     * @throws IOException see: {@link IOException}
     * @throws XueSyntaxException see {@link XueSyntaxException}
     * @throws ConstTagUnknownException see {@link ConstTagUnknownException}
     * @throws AttributeUnknownException see {@link AttributeUnknownException}
     * @throws ElementTagUnknownException see {@link ElementTagUnknownException}
     */
    public void load(XmlPullParser xpp) throws XmlPullParserException, IOException, XueException {
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
    }

    /**
     * Defines what happens when the parser reaches a start-tag.
     *
     * @param xpp PullParser, which has been created with the xml resource.
     * @throws XueSyntaxException if a const tag has been placed wrong
     * @throws ConstTagUnknownException if a tag in <constants>...</constants> is unknown
     * @throws XmlPullParserException see: {@link XmlPullParserException}
     * @throws AttributeUnknownException if an attribute is unknow.
     * @throws ElementTagUnknownException if a tag in <elements>...</elements> is unknown
     */
    private void handleStartTag(XmlPullParser xpp) throws XmlPullParserException, XueException {
        for (TagHandler pT : tagHandlerList) {
            if (pT.getName().equals(xpp.getName())) {
                pT.setActive(true);
                pT.startHandle(xpp);
                return;
            }
        }
        for (TagHandler t : tagHandlerList) {
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
        for (TagHandler t : tagHandlerList) {
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
    private void injectDependency(TagHandler injectTarget, TagHandler injectable) {
        List<Field> fields = ReflectionUtils.getAllFields(injectTarget.getClass());
        for (final Field field : fields) {
            if (field.isAnnotationPresent(DependencyHandler.class) && field.getType() == injectable.getClass()) {
                try {
                    field.setAccessible(true);
                    field.set(injectTarget, injectable);
                }
                catch (IllegalAccessException e) {
                    //TODO LOGGER
                }
            }
        }
    }

}
