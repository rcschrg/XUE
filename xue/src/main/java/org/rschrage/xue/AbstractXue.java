package org.rschrage.xue;

import org.rschrage.util.Tuple;
import org.rschrage.xue.annotation.Dependency;
import org.rschrage.xue.constants.Constant;
import org.rschrage.xue.dom.DomRepresentation;
import org.rschrage.xue.exception.XueException;
import org.rschrage.xue.handler.TagGroupHandler;
import org.rschrage.xue.input.XueInputEvent;
import org.rschrage.xue.input.XueInputHandler;
import org.rschrage.xue.input.XueUpdateHandler;
import org.rschrage.xue.mapping.TagMapping;
import org.rschrage.xue.util.InjectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    /** List of input handlers, which are used to react to input events */
    protected List<XueInputHandler> inputHandlers;
    /** List of update handlers, which are used to react to update ticks */
    protected List<XueUpdateHandler> updateHandlers;

    protected List<Tuple<InputStream, String>> files;
    protected List<File> dirs;

    public AbstractXue() {
        this.core = new XueCore();
        this.updateHandlers = new ArrayList<>();
        this.inputHandlers = new ArrayList<>();
        this.files = new ArrayList<>();
        this.dirs = new ArrayList<>();
    }

    public AbstractXue(InputStream xml, String fileName) {
        this();

        files.add(new Tuple<>(xml, fileName));
    }

    public void addFile(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("The object have to be a file!");
        }

        try {
            Tuple<InputStream, String> tuple = new Tuple<InputStream, String>(new FileInputStream(file), file.getName());
            files.add(tuple);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addFile(InputStream xml, String fileName) {
        files.add(new Tuple<>(xml, fileName));
    }

    public void addDir(File directory)  {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("The object have to be a directory!");
        }
        dirs.add(directory);
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
            InjectionUtils.injectByName(Dependency.class, target, groupHandler);
        }
    }

    public void addMappingUnsafe(Class<? extends TagGroupHandler> tagHandler, TagMapping<?> tagMapping) {
        this.core.addMappingUnsafe(tagHandler, tagMapping);
    }

    public <B, D extends DomRepresentation<?>> void addMapping(Class<TagGroupHandler<B, D>> tagHandler, TagMapping<B> tagMapping) {
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

    public void load() throws XueException, FileNotFoundException {
        if (dirs.isEmpty() && files.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.preLoad();

        for (Tuple<InputStream, String> file : files) {
            core.load(file.getFirst(), file.getSecond());
        }
        for (File dir : dirs) {
            core.loadDirectory(dir);
        }

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
