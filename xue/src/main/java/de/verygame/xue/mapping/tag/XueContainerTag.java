package de.verygame.xue.mapping.tag;

/**
 * @author Rico Schrage
 */
public interface XueContainerTag<T> extends XueTag<T> {

    void applyChild(T element);

}
