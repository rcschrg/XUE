package de.verygame.xue;

import de.verygame.util.ReflectionUtils;
import de.verygame.xue.annotation.Dependency;
import de.verygame.xue.constants.Constant;
import de.verygame.xue.dom.DomRepresentation;
import de.verygame.xue.exception.*;
import de.verygame.xue.handler.TagGroupHandler;
import de.verygame.xue.mapping.BuilderMapping;
import de.verygame.xue.util.InjectionUtils;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by Rico on 10.07.2015.
 *
 * @author Rico Schrage
 */
public class XueCore {
    private static final String ENCODING = "UTF-8";

    private final List<TagGroupHandler<?, ?>> tagGroupHandlerList;
    private final List<TagGroupHandler<?, ?>> closed;

    private final Map<Constant, String> constantMap;

    /**
     * Constructs XueCore, which uses tagMapping and attributeMapping for everything, which have to be mapped.
     */
    public XueCore() {
        tagGroupHandlerList = new ArrayList<>();
        closed = new ArrayList<>();
        constantMap = new EnumMap<>(Constant.class);

        for (Constant constant : Constant.values()) {
            constantMap.put(constant, constant.toString());
        }
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

    public void overwriteConstant(Constant constant, String value) {
        constantMap.put(constant, value);
    }

    public void addHandler(TagGroupHandler<?, ?> tagGroupHandler) {
        for (TagGroupHandler<?, ?> other : tagGroupHandlerList) {
            InjectionUtils.injectByType(Dependency.class, tagGroupHandler, other);
            InjectionUtils.injectByType(Dependency.class, other, tagGroupHandler);
        }
        tagGroupHandler.setConstantMap(constantMap);

        this.tagGroupHandlerList.add(tagGroupHandler);
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
     * Generic method to add a mapping to the given tagHandler. For core tag handler use the special methods instead.
     * This method should be used for custom tagHandler.
     *
     * @param tagHandler handler of a group of tags
     * @param builderMapping mapping
     * @param <B> makes sure that the tagHandler und builderMapping fit together
     */
    public <B, D> void addMapping(Class<? extends TagGroupHandler<B, D>> tagHandler, BuilderMapping<B> builderMapping) {
        addMappingUnsafe(tagHandler, builderMapping);
    }

    /**
     * Generic <b>unsafe</b> method to add a mapping to the given tagHandler. The given class object must be a tag handler which can add the given
     * mapping. Just use this method when you have to otherwise {@link #addMapping(Class, BuilderMapping)} is the far better choice.
     * @param tagHandler class of the taghandler
     * @param builderMapping mapping you want to add
     * @param <B> internal
     * @throws ClassCastException can be thrown when the class object is not a tagHandler or if the tagHandler can't add the mapping.
     */
    public <B> void addMappingUnsafe(Class<? extends TagGroupHandler> tagHandler, BuilderMapping<B> builderMapping) {
        for (TagGroupHandler<?, ?> t : tagGroupHandlerList) {
            if (t.getClass() == tagHandler) {
                //noinspection unchecked
                ((TagGroupHandler<B, ?>)t).addBuilderMapping(builderMapping);
            }
        }
    }

    public <B> Map<String, B> getResult(Class<? extends TagGroupHandler<B, ?>> handlerClass, Class<B> resultClass) {
        //noinspection unchecked
        return getResultUnsafe(handlerClass, resultClass);
    }

    /**
    * Generic method to get/create the result map of a tagHandler.
    *
    * @param handlerClass type of the handler you want to get the map from
    * @param <B> ensures a correct return type
    * @return Result of the handlerClass
    */
    public <B> Map<String, B> getResultUnsafe(Class<? extends TagGroupHandler> handlerClass, Class<B> resultClass) {
        for (TagGroupHandler<?, ?> t : tagGroupHandlerList) {
            if (t.getClass() == handlerClass) {
                //noinspection unchecked
                List<?> dom = t.getDom();
                Map<String, B> resultMap = new HashMap<>();
                for (int i = 0; i < dom.size(); ++i) {
                    //noinspection unchecked
                    DomRepresentation<B> domObject = (DomRepresentation<B>) dom.get(i);
                    resultMap.put(domObject.getName(), domObject.getObject());
                }
                return resultMap;
            }
        }
        throw new IllegalArgumentException(handlerClass + " is not part of this core!");
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
