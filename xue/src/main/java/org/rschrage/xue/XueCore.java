package org.rschrage.xue;

import org.rschrage.util.ReflectionUtils;
import org.rschrage.xue.annotation.Dependency;
import org.rschrage.xue.constants.Constant;
import org.rschrage.xue.dom.DomRepresentation;
import org.rschrage.xue.exception.*;
import org.rschrage.xue.handler.TagGroupHandler;
import org.rschrage.xue.mapping.TagMapping;
import org.rschrage.xue.util.InjectionUtils;
import org.rschrage.xue.util.StreamUtils;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rico on 10.07.2015.
 *
 * @author Rico Schrage
 */
public class XueCore {
    private static final String ENCODING = "UTF-8";
    private static final int TRIES = 1;

    private final List<TagGroupHandler<?, ?>> tagGroupHandlerList;
    private final List<Class<? extends TagGroupHandler>> closed;

    private final Map<Constant, String> constantMap;

    private TagGroupHandler<?, ?> currentHandler;
    private int remainingTries = TRIES;

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
            if (closed.contains(t.getClass())) {
                continue;
            }
            List<Class<?>> depend = calcDependencies(t);
            if (depend.isEmpty() || closed.containsAll(depend)) {
                closed.add(t.getClass());
                return t;
            }
        }
        if (closed.size() == tagGroupHandlerList.size())
            return null;

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
            if (other.getClass() == tagGroupHandler.getClass()) {
                throw new IllegalArgumentException();
            }
        }

        for (TagGroupHandler<?, ?> other : tagGroupHandlerList) {
            InjectionUtils.injectByType(Dependency.class, tagGroupHandler, other);
            InjectionUtils.injectByType(Dependency.class, other, tagGroupHandler);
        }
        tagGroupHandler.setConstantMap(constantMap);

        this.tagGroupHandlerList.add(tagGroupHandler);
    }

    public <E, D extends DomRepresentation<E>> Map<String, D> getDom(Class<TagGroupHandler<E, D>> tagGroupHandlerClass) {
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
     * @param tagMapping mapping
     * @param <B> makes sure that the tagHandler und tagMapping fit together
     */
    public <B, D extends DomRepresentation<B>> void addMapping(Class<? extends TagGroupHandler<B, D>> tagHandler, TagMapping<B> tagMapping) {
        addMappingUnsafe(tagHandler, tagMapping);
    }

    /**
     * Generic <b>unsafe</b> method to add a mapping to the given tagHandler. The given class object must be a tag handler which can add the given
     * mapping. Just use this method when you have to otherwise {@link #addMapping(Class, TagMapping)} is the far better choice.
     * @param tagHandler class of the taghandler
     * @param tagMapping mapping you want to add
     * @param <B> internal
     * @throws ClassCastException can be thrown when the class object is not a tagHandler or if the tagHandler can't add the mapping.
     */
    public <B> void addMappingUnsafe(Class<? extends TagGroupHandler> tagHandler, TagMapping<B> tagMapping) {
        for (TagGroupHandler<?, ?> t : tagGroupHandlerList) {
            if (t.getClass() == tagHandler) {
                //noinspection unchecked
                ((TagGroupHandler<B, ?>)t).addBuilderMapping(tagMapping);
            }
        }
    }

    public <B> Map<String, B> getResult(Class<? extends TagGroupHandler<B, ?>> handlerClass, String domain, Class<B> resultClass) {
        //noinspection unchecked
        return getResultUnsafe(handlerClass, domain, resultClass);
    }

    /**
    * Generic method to get/create the result map of a tagHandler.
    *
    * @param handlerClass type of the handler you want to get the map from
    * @param <B> ensures a correct return type
    * @return Result of the handlerClass
    */
    public <B> Map<String, B> getResultUnsafe(Class<? extends TagGroupHandler> handlerClass, String domain, Class<B> resultClass) {
        for (TagGroupHandler<?, ?> t : tagGroupHandlerList) {
            if (t.getClass() == handlerClass) {
                //noinspection unchecked
                List<?> dom = t.getDom();
                Map<String, B> resultMap = new HashMap<>();
                for (int i = 0; i < dom.size(); ++i) {
                    DomRepresentation<B> domObject = (DomRepresentation<B>) dom.get(i);
                    //noinspection unchecked
                    if (domain.equals(domObject.getDomain())) {
                        resultMap.put(domObject.getName(), domObject.getObject());
                    }
                }
                return resultMap;
            }
        }
        throw new IllegalArgumentException(handlerClass + " is not part of this core!");
    }

    public void loadDirectory(File directory) throws FileNotFoundException {
        if (!directory.isDirectory()) {
            throw new XueException("The object have to be a directory!");
        }
        File[] files = directory.listFiles();
        if (files == null) {
            throw new XueException("There are no listable files in this directory!");
        }
        for (File file : files) {
            load(new FileInputStream(file), file.getName());
            closed.clear();
        }
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
    public void load(InputStream inputXml, String filename) {
        if (tagGroupHandlerList.isEmpty() || closed.size() == tagGroupHandlerList.size()) {
            throw new IllegalArgumentException();
        }
        try {
            boolean finished = false;
            String xml = StreamUtils.convertStreamToString(inputXml);
            while (!finished) {
                finished = true;
                Pattern pattern = Pattern.compile("<" + Constant.XUE_INPUT_TAG_NAME + " " + Constant.XUE_INPUT_SRC_ATT + "?=?\"(.*)\".*/>");
                Matcher m = pattern.matcher(xml);
                StringBuffer sb = new StringBuffer();
                while (m.find()) {
                    String src = m.group(1);
                    m.appendReplacement(sb, StreamUtils.convertStreamToString(getClass().getResourceAsStream(src)));
                    finished = false;
                }
                m.appendTail(sb);
                xml = sb.toString();
            }
            currentHandler = calculateNextTagHandler();
            //noinspection ConstantConditions intellij too bad not true fakenews!
            currentHandler.setDomain(filename);
            while (currentHandler != null) {
                XmlPullParser xpp = new KXmlParser();
                inputXml = new ByteArrayInputStream(xml.getBytes());
                inputXml.mark(Integer.MAX_VALUE);
                xpp.setInput(inputXml, ENCODING);
                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                    switch (xpp.getEventType()) {

                        case XmlPullParser.START_TAG:
                            handleStartTag(xpp);
                            break;

                        case XmlPullParser.END_TAG:
                            handleEndTag(xpp);
                            if (currentHandler != null) {
                                currentHandler.setDomain(filename);
                            }
                            break;

                        default:
                            break;
                    }
                    xpp.next();
                    if (currentHandler == null) {
                        break;
                    }
                }
                if (remainingTries == 0) {
                    currentHandler = calculateNextTagHandler();
                    //noinspection ConstantConditions intellij too bad not true fakenews!
                    if (currentHandler != null) {
                        currentHandler.setDomain(filename);
                    }
                    remainingTries = TRIES;
                }
                else {
                    remainingTries--;
                }
                inputXml.reset();
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
    private void handleStartTag(XmlPullParser xpp) throws XmlPullParserException {
        if (!currentHandler.isActive() && currentHandler.getName().equals(xpp.getName())) {
            currentHandler.setActive(true);
            currentHandler.startHandle(xpp);
            return;
        }

        if (currentHandler.isActive()) {
            currentHandler.handle(xpp);
        }
    }

    private String handleXueInput(XmlPullParser xpp) {
        if (xpp.getAttributeName(0).equals(Constant.XUE_INPUT_SRC_ATT.toString())) {
            return xpp.getAttributeValue(0);
        }
        return null;
    }

    /**
     * Defines what happens when the parser reaches an end-tag.
     *
     * @param xpp PullParser, which has been created with the xml resource.
     */
    private void handleEndTag(XmlPullParser xpp) throws XmlPullParserException {
        if (currentHandler.getName().equals(xpp.getName()) && currentHandler.isActive()) {
            currentHandler.setActive(false);
            currentHandler.stopHandle(xpp);
            currentHandler = calculateNextTagHandler();
        }
    }
}
