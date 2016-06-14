package de.verygame.xue.handler.dom;

import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.exception.TagUnknownException;
import de.verygame.xue.mapping.builder.XueTag;

/**
 * @author Rico Schrage
 */
public interface DomRepresentation<T> {

    void begin();
    void apply(String attribute, String value) throws AttributeUnknownException, TagUnknownException;
    void end() throws AttributeUnknownException;

    T getObject();
    XueTag<T> getBuilder();
}
