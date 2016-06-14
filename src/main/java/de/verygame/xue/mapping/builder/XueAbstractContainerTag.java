package de.verygame.xue.mapping.builder;

/**
 * @author Rico Schrage
 */
public abstract class XueAbstractContainerTag<T> extends XueAbstractElementTag<T> implements XueContainerTag<T> {

    public XueAbstractContainerTag(Class<? extends T> subjectClass) {
        super(subjectClass);
    }

}
