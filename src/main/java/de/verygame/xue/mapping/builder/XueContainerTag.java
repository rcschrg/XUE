package de.verygame.xue.mapping.builder;

/**
 * @author Rico Schrage
 */
public interface XueContainerTag<T> extends XueTag<T> {

    void applyChild(T element);

}
