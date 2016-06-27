package de.verygame.xue;

import de.verygame.util.ReflectionUtils;
import de.verygame.xue.annotation.Dependency;
import de.verygame.xue.exception.*;
import de.verygame.xue.handler.ActionSequenceTagGroupHandler;
import de.verygame.xue.handler.ConstantTagGroupHandler;
import de.verygame.xue.handler.ElementsTagGroupHandler;
import de.verygame.xue.handler.TagGroupHandler;
import de.verygame.xue.handler.dom.DomElement;
import de.verygame.xue.handler.dom.DomObject;
import de.verygame.xue.handler.dom.DomRepresentation;
import de.verygame.xue.mapping.BuilderMapping;
import de.verygame.xue.util.InjectionUtils;
import de.verygame.xue.util.action.Action;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rico on 10.07.2015.
 *
 * @author Rico Schrage
 */
public class XueCore<T> {
    private static final String ENCODING = "UTF-8";

    private final ElementsTagGroupHandler<T> elementsTagHandler;
    private final ConstantTagGroupHandler constantTagHandler;
    private final ActionSequenceTagGroupHandler actionSequenceTagHandler;

    private final List<TagGroupHandler<?, ?>> tagGroupHandlerList;
    private final List<TagGroupHandler<?, ?>> closed;

    /**
     * Constructs XueCore, which uses tagMapping and attributeMapping for everything, which have to be mapped.
     */
    public XueCore() {
        tagGroupHandlerList = new ArrayList<>();
        closed = new ArrayList<>();

        constantTagHandler = new ConstantTagGroupHandler();
        elementsTagHandler = new ElementsTagGroupHandler<>();
        actionSequenceTagHandler = new ActionSequenceTagGroupHandler();

        addHandler(constantTagHandler);
        addHandler(elementsTagHandler);
        addHandler(actionSequenceTagHandler);
    }

    private TagGroupHandler<?, ?> calculateNextTagHandler() {
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
            if (field.isAnnotationPresent(Dependency.class)) {
                tagHandlers.add(field.getType());
            }
        }
        return tagHandlers;
    }

    public void addHandler(TagGroupHandler<?, ?> tagGroupHandler) {
        for (TagGroupHandler<?, ?> other : tagGroupHandlerList) {
            InjectionUtils.injectByType(Dependency.class, tagGroupHandler, other);
        }
        this.tagGroupHandlerList.add(tagGroupHandler);
    }

    public List<DomElement<T>> getElementDom() {
        return elementsTagHandler.getDom();
    }

    public List<DomObject<Object>> getConstantDom() {
        return constantTagHandler.getDom();
    }

    public List<DomRepresentation<Action>> getActionSequenceDom() {
        return actionSequenceTagHandler.getDom();
    }

    public <E, D> Map<String, D> getDom(Class<TagGroupHandler<E, D>> tagGroupHandlerClass) {
        for (TagGroupHandler<?, ?> t : tagGroupHandlerList) {
            if (t.getClass() == tagGroupHandlerClass) {
                //noinspection unchecked
                return (Map<String, D>) t.getDom();
            }
        }
        throw new IllegalArgumentException("There does not exist a tag-group handler of the given class!");
    }

    public List<TagGroupHandler<?, ?>> getDomContainer() {
        return tagGroupHandlerList;
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
     * @return element map
     */
    public Map<String, T> getElementMap() {
        //noinspection unchecked
        return getResult((Class<? extends TagGroupHandler<T, DomElement<T>>>) elementsTagHandler.getClass());
    }

    /**
     * @return const map
     */
    public Map<String, Object> getConstMap() {
        return getResult(ConstantTagGroupHandler.class);
    }

    /**
     * Generic method to get/create the result map of a tagHandler.
     *
     * @param handlerClass type of the handler you want to get the map from
     * @param <B> ensures a correct return type
     * @return Result of the handlerClass
     */
    public <B, D> Map<String, B> getResult(Class<? extends TagGroupHandler<B, D>> handlerClass) {
        for (TagGroupHandler<?, ?> t : tagGroupHandlerList) {
            if (t.getClass() == handlerClass) {
                //noinspection unchecked
                List<D> dom = (List<D>) t.getDom();
                Map<String, B> resultMap = new HashMap<>();
                for (int i = 0; i < dom.size(); ++i) {
                    //noinspection unchecked
                    DomRepresentation<B> domObject = (DomRepresentation<B>) dom.get(i);
                    resultMap.put(domObject.getName(), domObject.getObject());
                }
                return resultMap;
            }
        }
        throw new IllegalArgumentException(handlerClass + " is no part of this core!");
    }

    /**
     * Creates all elements specified in xml-file, which has been used to create the PullParser.
     *
     * @param inputXml Xml input stream.
     *
     * @throws XueSyntaxException see {@link XueSyntaxException}
     * @throws ConstTagUnknownException see {@link ConstTagUnknownException}
     * @throws AttributeUnknownException see {@link AttributeUnknownException}
     * @throws ElementTagUnknownException see {@link ElementTagUnknownException}
     */
    public void load(InputStream inputXml) {
        try {
            XmlPullParser xpp = new KXmlParser();
            xpp.setInput(inputXml, ENCODING);
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
        catch (XmlPullParserException e) {
            throw new XueParseException(e);
        }
        catch (IOException e) {
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
    private void handleStartTag(XmlPullParser xpp) {
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
    private void handleEndTag(XmlPullParser xpp) {
        for (TagGroupHandler t : tagGroupHandlerList) {
            if (t.getName().equals(xpp.getName()) && t.isActive()) {
                t.setActive(false);
                t.stopHandle(xpp);
            }
        }
    }
}
