package de.verygame.xue.mapping.builder;

import de.verygame.xue.exception.AttributeUnknownException;

/**
 * @author Rico Schrage
 */
public interface XueTag<T> {

    void preBuild();
    void postBuild();

    void applyString(String attribute, String value) throws AttributeUnknownException;
    void applyInt(String attribute, int value) throws AttributeUnknownException;
    void applyObject(String attribute, Object value) throws AttributeUnknownException;
    void applyFloat(String attribute, float value) throws AttributeUnknownException;

    T getElement();

}
