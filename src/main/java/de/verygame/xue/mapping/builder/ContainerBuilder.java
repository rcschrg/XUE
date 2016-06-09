package de.verygame.xue.mapping.builder;

/**
 * @author Rico Schrage
 */
public interface ContainerBuilder<T> extends GLMenuBuilder<T> {

    void applyChild(T element);

}
