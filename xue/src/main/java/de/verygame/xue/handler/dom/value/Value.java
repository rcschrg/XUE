package de.verygame.xue.handler.dom.value;

/**
 * @author Rico Schrage
 */
public interface Value<T, A> {

    T getValue();
    A getAddition();
    <C> C getAddition(Class<C> castTo);
    <C> C getValue(Class<C> castTo);

    boolean is(Class<?> check);
    boolean additionIs(Class<?> check);
    boolean hasAddition();

}
