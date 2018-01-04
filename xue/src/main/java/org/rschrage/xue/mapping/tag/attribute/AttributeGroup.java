package org.rschrage.xue.mapping.tag.attribute;

import org.rschrage.xue.input.XueInputHandler;
import org.rschrage.xue.input.XueUpdateHandler;

import java.util.List;

/**
 * @author Rico Schrage
 */
public interface AttributeGroup<T> extends XueUpdateHandler, XueInputHandler {
    List<AttributeGroupElementMeta> getGroupMeta();

    void begin(T element);
    void apply(T element, Object value, String attribute);
    void end(T element);
}
