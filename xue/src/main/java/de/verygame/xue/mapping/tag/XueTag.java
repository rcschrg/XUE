package de.verygame.xue.mapping.tag;

import de.verygame.xue.input.XueInputHandler;
import de.verygame.xue.input.XueUpdateHandler;

/**
 * @author Rico Schrage
 */
public interface XueTag<T> extends XueInputHandler, XueUpdateHandler {
    void preBuild();
    <V> void apply(String attribute, V value);
    void postBuild();

    T getElement();
}
