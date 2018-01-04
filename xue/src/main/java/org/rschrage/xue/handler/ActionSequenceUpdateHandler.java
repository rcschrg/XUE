package org.rschrage.xue.handler;

import org.rschrage.xue.annotation.Dependency;
import org.rschrage.xue.input.XueInputEvent;
import org.rschrage.xue.input.XueInputHandler;
import org.rschrage.xue.input.XueUpdateHandler;
import org.rschrage.xue.util.action.ActionSequence;

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
