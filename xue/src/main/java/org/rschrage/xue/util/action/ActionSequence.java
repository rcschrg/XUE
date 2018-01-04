package org.rschrage.xue.util.action;

import org.rschrage.xue.input.XueInputEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Rico Schrage
 */
public class ActionSequence {

    private final List<Action> actions;
    private boolean active = false;
    private XueInputEvent startEvent;

    public ActionSequence() {
        actions = new ArrayList<>();
    }

    public XueInputEvent getStartEvent()  {
        return this.startEvent;
    }

    public float getDuration() {
        float max = 0f;
        for (int i = 0; i < actions.size(); ++i) {
            float stopTime = actions.get(i).getStopTime();
            if (stopTime > max) {
                max = stopTime;
            }
        }
        return max;
    }

    public int getActionCounter() {
        return actions.size();
    }

    public Action getActionByIndex(int i) {
        return actions.get(i);
    }

    /**
     * @return an unmodifiable list
     */
    public List<Action> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public void setStartEvent(XueInputEvent startEvent) {
        this.startEvent = startEvent;
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public void onInputEvent(XueInputEvent inputEvent) {
        if (inputEvent == startEvent) {
            start();
        }
    }

    public void update(float delta) {
        if (active) {
            for (final Action a : actions) {
                a.update(delta);
            }
        }
    }

    public void start() {
        reset();
        this.active = true;
    }

    public void stop() {
        this.active = false;
    }

    public void reset() {
        this.active = false;

        for (final Action a : actions) {
            a.reset();
        }
    }

}
