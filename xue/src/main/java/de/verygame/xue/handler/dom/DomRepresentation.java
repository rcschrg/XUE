package de.verygame.xue.handler.dom;

import de.verygame.xue.exception.AttributeUnknownException;
import de.verygame.xue.exception.XueException;
import de.verygame.xue.handler.dom.value.Value;
import de.verygame.xue.mapping.tag.XueTag;

import java.util.Map;

/**
 * @author Rico Schrage
 */
public interface DomRepresentation<T> {

    void begin();
    void apply(String attribute, String value) throws XueException;
    void end() throws AttributeUnknownException;

    String getName();
    void setName(String name);

    Value<?, ?> getValue(String attribute);
    Map<String, Value<?, ?>> getValues();
    boolean valueExists(String... attribute);

    T getObject();
    XueTag<T> getTag();
}
