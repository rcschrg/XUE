package de.verygame.xue.tag;

import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.input.XueInputEvent;
import de.verygame.xue.mapping.tag.XueTag;

/**
 * @author Rico Schrage
 */
public class PrimitiveTag implements XueTag<Object> {

    Object object = null;

    @Override
    public void apply(String attribute, Object value) throws AttributeUnknownException {
        object = value;
    }

    @Override
    public void preBuild() {
        //nothing to do
    }

    @Override
    public void postBuild() {
        //nothing to do
    }

    @Override
    public void onInputEvent(XueInputEvent inputEvent) {
        //nothing to do
    }

    @Override
    public void onUpdate(float delta) {
        //nothing to do
    }

    @Override
    public Object getElement() {
        return object;
    }
}
