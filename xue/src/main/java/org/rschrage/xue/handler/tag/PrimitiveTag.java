package org.rschrage.xue.handler.tag;

import org.rschrage.xue.exception.AttributeUnknownException;
import org.rschrage.xue.input.XueInputEvent;
import org.rschrage.xue.mapping.tag.XueTag;

/**
 * @author Rico Schrage
 */
public class PrimitiveTag implements XueTag<Object> {
    public static final String NAME = "const";

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
    public void applyChild(Object child) {

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
