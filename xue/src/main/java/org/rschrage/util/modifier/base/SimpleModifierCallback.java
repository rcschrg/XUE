package org.rschrage.util.modifier.base;

/**
 * @author Rico Schrage
 */
public abstract class SimpleModifierCallback implements ModifierCallback {

    @Override
    public void call(float value) {
        action(value);
    }

    @Override
    public void finish(float value) {
        action(value);
    }

    @Override
    public void init(float value) {
        action(value);
    }

    protected abstract void action(float value);


}
