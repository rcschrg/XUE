package de.verygame.xue.handler.dom.value;

/**
 * @author Rico Schrage
 */
public abstract class AbstractValue<T, A> implements Value<T, A> {

    private T value;
    private A addition;

    public AbstractValue(T value) {
        this(value, null);
    }

    public AbstractValue(T value, A addition) {
        this.value = value;
        this.addition = addition;
    }

    public T getValue() {
        return value;
    }

    public A getAddition() {
        return addition;
    }

    public boolean is(Class<?> check) {
        return value.getClass() == check;
    }

    public boolean additionIs(Class<?> check) {
        return addition.getClass() == check;
    }

    public boolean hasAddition() {
        return addition != null;
    }

    public <C> C getAddition(Class<C> castTo) {
        return getGeneric(castTo, addition);
    }

    public <C> C getValue(Class<C> castTo) {
        return getGeneric(castTo, value);
    }

    private <C> C getGeneric(Class<C> castTo, Object v) {
        if (castTo.isInstance(v)) {
            //noinspection unchecked
            return (C) v;
        }
        throw new IllegalArgumentException("The var 'v' is no instance of the given class!");
    }
}
