package org.rschrage.xue.dom;

import org.rschrage.xue.exception.AttributeUnknownException;
import org.rschrage.xue.exception.XueException;
import org.rschrage.xue.dom.value.Value;
import org.rschrage.xue.mapping.tag.XueTag;

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

    String getDomain();
    void setDomain(String domain);

    Value<?, ?> getValue(String attribute);
    Map<String, Value<?, ?>> getValues();
    boolean valueExists(String... attribute);

    T getObject();
    XueTag<? extends T> getTag();
}
