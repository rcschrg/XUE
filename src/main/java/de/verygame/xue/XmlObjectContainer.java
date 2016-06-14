package de.verygame.xue;

import de.verygame.xue.exception.XueException;
import de.verygame.xue.handler.BuilderMapping;
import de.verygame.xue.handler.TagHandler;
import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.mapping.GlobalMappings;
import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Rico Schrage
 *
 * Simple implementation of {@link Xue}. Parses given XML input to a set of objects of the type {@link T}.
 * Should mostly be used as base of the xml extension you want to create.
 */
public class XmlObjectContainer<T> implements Xue<T> {

    private XueCore<T> core;

    /** Resource of the menu, all elements of the menu are described in this file */
    private final InputStream resource;

    /** Contains all elements, which have the attribute <code>name</code> */
    private Map<String, T> elementMap;

    /** Contains all constants */
    private Map<String, Object> constMap;

    /** see {@link #bind(Object)} */
    private Object bindTarget;

    /**
     * Creates a container with the given mappings.
     *
     * @param mappings {@link GlobalMappings<T>}
     */
    public XmlObjectContainer(GlobalMappings<T> mappings, InputStream xml) {
        this.core = new XueCore<>(mappings);
        this.resource = xml;
    }

    public void addElementMapping(BuilderMapping<T> mapping) {
        this.core.addElementMapping(mapping);
    }

    public void addConstMapping(BuilderMapping<Object> mapping) {
        this.core.addConstantMapping(mapping);
    }

    public <B, D> void addMapping(Class<TagHandler<B, D>> tagHandler, BuilderMapping<B> builderMapping) {
        this.core.addMapping(tagHandler, builderMapping);
    }

    /**
     * Get the number of elements. Must not be called before {@link #load()}.
     *
     * @return number of elements
     */
    public int getElementSize() {
        if (elementMap == null) {
            throw new IllegalStateException("You have to load the gl-menu-file first!");
        }

        return elementMap.size();
    }

    @Override
    public T getElementByName(final String name) {
        if (elementMap == null) {
            throw new IllegalStateException("You have to load the gl-menu-file first!");
        }

        return elementMap.get(name);
    }

    @Override
    public Object getConstByName(String name) {
        if (constMap == null) {
            throw new IllegalStateException("You have to load the gl-menu-file first!");
        }
        return constMap.get(name);
    }

    /**
     * Will be called directly before {@link #load()} )}.
     */
    protected void preLoad() {
        //nothing to do
    }

    /**
     * Will be called directly after {@link #load()}.
     */
    protected void postLoad() {
        Set<Map.Entry<String, T>> entries = elementMap.entrySet();
        if (bindTarget == null) {
            return;
        }
        for (final Field field : bindTarget.getClass().getDeclaredFields()) {
            for (final Map.Entry<String, T> entry : entries) {
                if (field.getName().equals(entry.getKey())) {
                    try {
                        field.setAccessible(true);
                        field.set(bindTarget, entry.getValue());
                    }
                    catch (IllegalAccessException e) {
                        //TODO
                    }
                }
            }
        }
    }

    @Override
    public void load() {
        try {
            this.preLoad();
            final KXmlParser parser = new KXmlParser();
            parser.setInput(resource, "UTF-8");
            this.core.load(parser);
            this.elementMap = core.getElementMap();
            this.constMap = core.getConstMap();
            this.postLoad();
        }
        catch (XmlPullParserException e) {
            //TODO
        }
        catch (IOException e) {
            //TODO
        }
        catch (XueException e) {
            //TODO
        }
    }

    @Override
    public void bind(Object bindTarget) {
        this.bindTarget = bindTarget;
    }

    @Override
    public void onInputEvent(XueInputEvent inputEvent) {
        core.onInputEvent(inputEvent);
    }
}
