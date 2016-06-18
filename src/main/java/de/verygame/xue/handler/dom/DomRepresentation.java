package de.verygame.xue.handler.dom;

import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.mapping.builder.XueTag;

/**
 * @author Rico Schrage
 */
public interface DomRepresentation<T> {

    void begin();
    void apply(String attribute, String value) throws XueException;
    void end() throws AttributeUnknownException;

    String getName();
    void setName(String name);

    T getObject();
    XueTag<T> getTag();
}
