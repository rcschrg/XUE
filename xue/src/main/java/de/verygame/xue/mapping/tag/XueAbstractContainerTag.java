package de.verygame.xue.mapping.tag;

/**
 * @author Rico Schrage
 */
public abstract class XueAbstractContainerTag<T> extends XueAbstractElementTag<T> implements XueContainerTag<T> {

    public XueAbstractContainerTag(T element) {
        super(element);
    }

}
