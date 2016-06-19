package de.verygame.xue.mapping;

import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.mapping.builder.XueTag;

/**
 * @author Rico Schrage
 */
public class PrimitiveTag implements XueTag<Object> {

    Object object = null;

    @Override
    public void preBuild() {
        //nothing to do
    }

    @Override
    public void postBuild() {
        //nothing to do
    }

    @Override
    public void applyString(String attribute, String value) throws AttributeUnknownException {
        object = value;
    }

    @Override
    public void applyInt(String attribute, int value) throws AttributeUnknownException {
        object = value;
    }

    @Override
    public void applyObject(String attribute, Object value) throws AttributeUnknownException {
        object = value;
    }

    @Override
    public void applyFloat(String attribute, float value) throws AttributeUnknownException {
        object = value;
    }

    @Override
    public Object getElement() {
        return object;
    }
}
