package de.verygame.xue;

import de.verygame.xue.constants.Constant;
import de.verygame.xue.handler.ActionSequenceTagGroupHandler;
import de.verygame.xue.handler.ActionSequenceUpdateHandler;
import de.verygame.xue.handler.ResizeInputHandler;
import de.verygame.xue.mapping.DummyGlobalMappings;
import de.verygame.xue.mapping.GlobalMappings;
import de.verygame.xue.util.action.ActionSequence;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Rico Schrage
 *
 * Special extension of BasicXue made for GUI frameworks.
 */
public class GuiXue<T> extends BasicXue<T> {
    private final ActionSequenceTagGroupHandler actionSequenceTagGroupHandler;
    private final List<LoadTask> taskList;

    public GuiXue(InputStream xml) {
        this(xml, new DummyGlobalMappings<T>());
    }

    public GuiXue(InputStream xml, GlobalMappings<T> globalMappings) {
        super(xml, globalMappings);

        taskList = new ArrayList<>();
        actionSequenceTagGroupHandler = new ActionSequenceTagGroupHandler(Constant.obtainDefaultMap());
        core.addHandler(actionSequenceTagGroupHandler);

        this.addUpdateHandler(new ActionSequenceUpdateHandler());
        this.addInputHandler(new ActionSequenceUpdateHandler());
        this.addInputHandler(new ResizeInputHandler<>());
    }

    public Map<String, ActionSequence> getActionSequenceMap() {
        return actionSequenceTagGroupHandler.getActionSequenceMap();
    }

    public void addLoadTask(LoadTask task) {
        taskList.add(task);
    }

    @Override
    protected void postLoad() {
        super.postLoad();

        for (final LoadTask task : taskList) {
            task.postLoad();
        }
    }

    @Override
    protected void preLoad() {
        super.preLoad();

        for (final LoadTask task : taskList) {
            task.preLoad();
        }
    }

    public interface LoadTask {
        void postLoad();
        void preLoad();
    }
}