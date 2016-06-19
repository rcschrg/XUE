package de.verygame.xue.handler.action;

import de.verygame.util.math.function.EaseFunction;
import de.verygame.util.modifier.SingleValueModifier;
import de.verygame.util.modifier.base.Modifier;
import de.verygame.util.modifier.base.ModifierCallback;

/**
 * @author Rico Schrage
 */
public class BasicAction implements Action {

    protected ModifierCallback actionCallback;
    protected Modifier modifier;

    protected float currentTime = 0f;
    protected float startTime;
    protected float stopTime;
    protected float to;
    protected float from;
    protected EaseFunction easeFunction;

    public BasicAction(float actionTime) {
        this(actionTime, actionTime);
    }

    public BasicAction(float startTime, float stopTime) {
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    public void setActionCallback(ModifierCallback actionCallback) {
        this.actionCallback = actionCallback;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

    public void setStopTime(float stopTime) {
        this.stopTime = stopTime;
    }

    public void setTo(float to) {
        this.to = to;
    }

    public void setFrom(float from) {
        this.from = from;
    }

    public void setEaseFunction(EaseFunction easeFunction) {
        this.easeFunction = easeFunction;
    }

    public void createModifier() {
        this.modifier = new SingleValueModifier(stopTime-startTime, from, to, easeFunction, actionCallback);
    }

    @Override
    public float getStartTime() {
        return startTime;
    }

    @Override
    public float getStopTime() {
        return stopTime;
    }

    @Override
    public float getDuration() {
        return Math.abs(stopTime-startTime);
    }

    @Override
    public void update(float delta) {
        currentTime += delta;

        if (startTime <= currentTime && currentTime <= stopTime) {
            modifier.update(delta);
        }
    }

    @Override
    public void reset() {
        currentTime = 0f;
        modifier.reset();
    }
}
