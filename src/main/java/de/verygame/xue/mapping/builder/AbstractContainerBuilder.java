package de.verygame.xue.mapping.builder;

/**
 * @author Rico Schrage
 */
public abstract class AbstractContainerBuilder<T> extends AbstractElementBuilder<T> implements ContainerBuilder<T> {

    public AbstractContainerBuilder(Class<? extends T> subjectClass) {
        super(subjectClass);
    }

}
