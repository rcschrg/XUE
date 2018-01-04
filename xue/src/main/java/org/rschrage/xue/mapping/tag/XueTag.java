package org.rschrage.xue.mapping.tag;

import org.rschrage.xue.input.XueInputHandler;
import org.rschrage.xue.input.XueUpdateHandler;

/**
 * @author Rico Schrage
 */
public interface XueTag<T> extends XueInputHandler, XueUpdateHandler {
    void preBuild();
    <V> void apply(String attribute, V value);
    void applyChild(Object child);
    void postBuild();

    T getElement();
}
