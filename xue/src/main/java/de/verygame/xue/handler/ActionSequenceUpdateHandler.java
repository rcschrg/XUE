package de.verygame.xue.handler;

import de.verygame.xue.annotation.Dependency;
import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.input.XueInputHandler;
import de.verygame.xue.input.XueUpdateHandler;
import de.verygame.xue.util.action.ActionSequence;

import java.util.Map;

/**
 * @author Rico Schrage
 */
public class ActionSequenceUpdateHandler implements XueUpdateHandler, XueInputHandler {

    @Dependency
    protected ActionSequenceTagGroupHandler actionSequenceTagGroupHandler;

    @Override
    public void onUpdate(float delta) {
        Map<String, ActionSequence> actionSequenceMap = actionSequenceTagGroupHandler.getActionSequenceMap();
        for (Map.Entry<String, ActionSequence> actionSequenceEntry : actionSequenceMap.entrySet()) {
            actionSequenceEntry.getValue().update(delta);
        }
    }

    @Override
    public void onInputEvent(XueInputEvent event) {
        Map<String, ActionSequence> actionSequenceMap = actionSequenceTagGroupHandler.getActionSequenceMap();
        for (Map.Entry<String, ActionSequence> actionSequenceEntry : actionSequenceMap.entrySet()) {
            actionSequenceEntry.getValue().onInputEvent(event);
        }
    }
}
