package de.verygame.xue.mapping.tag.attribute;

import de.verygame.xue.input.XueInputHandler;
import de.verygame.xue.input.XueUpdateHandler;

/**
 * @author Rico Schrage
 */
public interface Attribute<T, V> extends XueInputHandler, XueUpdateHandler {
    String getName();
    void begin(T element);
    void apply(T element, V value);
    void end(T element);
}
