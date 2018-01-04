package org.rschrage.xue.mapping.tag.attribute;

import org.rschrage.xue.input.XueInputEvent;

/**
 * @author Rico Schrage
 *
 * Convinience class to avoid implementing some empty methods every time.
 */
public abstract class AbstractAttribute<T, V> implements Attribute<T, V> {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void begin(T element) {
        //default: do nothing
    }

    @Override
    public void end(T element) {
        //default: do nothing
    }

    @Override
    public void onInputEvent(XueInputEvent inputEvent) {
        //default: do nothing
    }

    @Override
    public void onUpdate(float delta) {
        //default: do nothing
    }
}
