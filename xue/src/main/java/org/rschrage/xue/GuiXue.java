package org.rschrage.xue;

import org.rschrage.xue.constants.Constant;
import org.rschrage.xue.handler.ActionSequenceTagGroupHandler;
import org.rschrage.xue.handler.ActionSequenceUpdateHandler;
import org.rschrage.xue.handler.ResizeInputHandler;
import org.rschrage.xue.handler.UpdateStringHandler;
import org.rschrage.xue.mapping.DummyGlobalMappings;
import org.rschrage.xue.mapping.GlobalMappings;
import org.rschrage.xue.util.action.ActionSequence;

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

    public GuiXue() {
        this(new DummyGlobalMappings<T>());
    }

    public GuiXue(GlobalMappings<T> globalMappings) {
        super(globalMappings);

        taskList = new ArrayList<>();
        actionSequenceTagGroupHandler = new ActionSequenceTagGroupHandler(Constant.obtainDefaultMap());
        core.addHandler(actionSequenceTagGroupHandler);

        this.addUpdateHandler(new ActionSequenceUpdateHandler());
        this.addInputHandler(new ActionSequenceUpdateHandler());
        this.addInputHandler(new ResizeInputHandler<>());
        this.addInputHandler(new UpdateStringHandler<>());
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