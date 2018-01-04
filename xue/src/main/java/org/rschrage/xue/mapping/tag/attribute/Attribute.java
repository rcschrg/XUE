package org.rschrage.xue.mapping.tag.attribute;

import org.rschrage.xue.input.XueInputHandler;
import org.rschrage.xue.input.XueUpdateHandler;

/**
 * @author Rico Schrage
 */
public interface Attribute<T, V> extends XueInputHandler, XueUpdateHandler {
    String getName();
    void begin(T element);
    void apply(T element, V value);
    void end(T element);
}
