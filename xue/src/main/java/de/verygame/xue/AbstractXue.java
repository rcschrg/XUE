package de.verygame.xue;

import de.verygame.xue.annotation.Dependency;
import de.verygame.xue.constants.Constant;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.handler.TagGroupHandler;
import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.input.XueInputHandler;
import de.verygame.xue.input.XueUpdateHandler;
import de.verygame.xue.mapping.TagMapping;
import de.verygame.xue.util.InjectionUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rico Schrage
 *         <p>
 *         Should mostly be used as base of the xml extension you want to create.
 */
public abstract class AbstractXue {
    protected final XueCore core;
    /** Resource of the menu, all elements of the menu are described in this file */
    protected final InputStream resource;
    /** List of input handlers, which are used to react to input events */
    protected List<XueInputHandler> inputHandlers;
    /** List of update handlers, which are used to react to update ticks */
    protected List<XueUpdateHandler> updateHandlers;

    /**
     * Creates a container with the given mappings.
     */
    public AbstractXue(InputStream xml) {
        this.core = new XueCore();
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

    public void addMappingUnsafe(Class<? extends TagGroupHandler> tagHandler, TagMapping<?> tagMapping) {
        this.core.addMappingUnsafe(tagHandler, tagMapping);
    }

    public <B, D> void addMapping(Class<TagGroupHandler<B, D>> tagHandler, TagMapping<B> tagMapping) {
        this.core.addMappingUnsafe(tagHandler, tagMapping);
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
        //nothing to do
    }

    public void load() throws XueException {
        this.preLoad();

        this.core.load(resource);

        this.postLoad();
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
