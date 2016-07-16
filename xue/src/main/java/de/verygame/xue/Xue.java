package de.verygame.xue;

import de.verygame.xue.annotation.Bind;
import de.verygame.xue.annotation.Dependency;
import de.verygame.xue.constants.Constant;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.handler.TagGroupHandler;
import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.input.XueInputHandler;
import de.verygame.xue.input.XueUpdateHandler;
import de.verygame.xue.mapping.BuilderMapping;
import de.verygame.xue.util.InjectionUtils;

import java.io.InputStream;
import java.util.*;

/**
 * @author Rico Schrage
 *         <p>
 *         Parses given XML input to a set of objects of the type {@link T}.
 *         Should mostly be used as base of the xml extension you want to create.
 */
public class Xue<T> {
    private static final String LOAD_BEFORE_MESSAGE = "You have to load the xml-file first!";

    private final XueCore<T> core;

    /**
     * Resource of the menu, all elements of the menu are described in this file
     */
    private final InputStream resource;

    /**
     * Contains all elements, which have the attribute <code>name</code>
     */
    private Map<String, T> elementMap;

    /**
     * Contains all constants
     */
    private Map<String, Object> constMap;

    /**
     * List of input handlers, which are used to react to input events
     */
    private List<XueInputHandler> inputHandlers;

    /**
     * List of update handlers, which are used to react to update ticks
     */
    private List<XueUpdateHandler> updateHandlers;

    /**
     * see {@link #bind(Object)}
     */
    private Object bindTarget;

    /**
     * Creates a container with the given mappings.
     */
    public Xue(InputStream xml) {
        this.core = new XueCore<>();
        this.resource = xml;
        this.updateHandlers = new ArrayList<>();
        this.inputHandlers = new ArrayList<>();
    }

    public void overwriteConstant(Constant constant, String value) {
        core.overwriteConstant(constant, value);
    }

    public void addInputHandler(XueInputHandler inputHandler) {
        inputHandlers.add(inputHandler);

        injectDomContainer(inputHandler);
    }

    public void addUpdateHandler(XueUpdateHandler updateHandler) {
        updateHandlers.add(updateHandler);

        injectDomContainer(updateHandler);
    }

    private void injectDomContainer(Object target) {
        for (TagGroupHandler<?, ?> groupHandler : core.getDomContainer()) {
            InjectionUtils.injectByName(Dependency.class, groupHandler, target);
        }
    }

    public void addElementMapping(BuilderMapping<T> mapping) {
        this.core.addElementMapping(mapping);
    }

    public void addConstMapping(BuilderMapping<Object> mapping) {
        this.core.addConstantMapping(mapping);
    }

    public <B, D> void addMapping(Class<TagGroupHandler<B, D>> tagHandler, BuilderMapping<B> builderMapping) {
        this.core.addMapping(tagHandler, builderMapping);
    }

    /**
     * Get the number of elements. Must not be called before {@link #load()}.
     *
     * @return number of elements
     */
    public int getElementSize() {
        if (elementMap == null) {
            throw new IllegalStateException(LOAD_BEFORE_MESSAGE);
        }

        return elementMap.size();
    }

    public T getElementByName(final String name) {
        if (elementMap == null) {
            throw new IllegalStateException(LOAD_BEFORE_MESSAGE);
        }

        return elementMap.get(name);
    }

    public Object getConstByName(String name) {
        if (constMap == null) {
            throw new IllegalStateException(LOAD_BEFORE_MESSAGE);
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
        for (final Map.Entry<String, T> entry : entries) {
            InjectionUtils.injectByName(Bind.class, bindTarget, entry.getKey());
        }
    }

    public void load() throws XueException {
        this.preLoad();

        this.core.load(resource);
        this.elementMap = core.getElementMap();
        this.constMap = core.getConstMap();

        this.postLoad();
    }

    public void bind(Object bindTarget) {
        this.bindTarget = bindTarget;
    }

    public void onInputEvent(XueInputEvent inputEvent) {
        for (int i = 0; i < inputHandlers.size(); ++i) {
            XueInputHandler inputHandler = inputHandlers.get(i);
            inputHandler.onInputEvent(inputEvent);
        }
    }

    public void onUpdate(float delta) {
        for (int i = 0; i < updateHandlers.size(); ++i) {
            XueUpdateHandler updateHandler = updateHandlers.get(i);
            updateHandler.onUpdate(delta);
        }
    }
}
