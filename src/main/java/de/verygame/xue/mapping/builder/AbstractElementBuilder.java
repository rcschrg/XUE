package de.verygame.xue.mapping.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.verygame.xue.exception.AttributeUnknownException;

/**
 * @author Rico Schrage
 */
public abstract class AbstractElementBuilder<T> implements GLMenuBuilder<T> {

    private final Class<? extends T> subjectClass;
    protected T element;

    public AbstractElementBuilder(Class<? extends T> subjectClass) {
        this.subjectClass = subjectClass;
    }

    protected void applyStringSpecial(String attribute, String value) throws AttributeUnknownException { }
    protected void applyIntSpecial(String attribute, int value) throws AttributeUnknownException { }
    protected void applyObjectSpecial(String attribute, Object value) throws AttributeUnknownException { }
    protected void applyFloatSpecial(String attribute, float value) throws AttributeUnknownException { }

    @Override
    public void preBuild() {
        //default
    }

    @Override
    public void postBuild() {
        //default
    }

    @Override
    public void applyString(String attribute, String value) throws AttributeUnknownException {
        try {
            Method matchedMethod = subjectClass.getMethod("set" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1), String.class);
            matchedMethod.setAccessible(true);
            matchedMethod.invoke(element, value);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            applyStringSpecial(attribute, value);
        }
    }

    @Override
    public void applyInt(String attribute, int value) throws AttributeUnknownException {
        try {
            Method matchedMethod = subjectClass.getMethod("set" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1), int.class);
            matchedMethod.setAccessible(true);
            matchedMethod.invoke(element, value);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            applyIntSpecial(attribute, value);
        }
    }

    @Override
    public void applyObject(String attribute, Object value) throws AttributeUnknownException {
        try {
            Method matchedMethod = subjectClass.getMethod("set" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1), value.getClass());
            matchedMethod.setAccessible(true);
            matchedMethod.invoke(element, value);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            applyObjectSpecial(attribute, value);
        }
    }

    @Override
    public void applyFloat(String attribute, float value) throws AttributeUnknownException {
        try {
            Method matchedMethod = subjectClass.getMethod("set" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1), float.class);
            matchedMethod.setAccessible(true);
            matchedMethod.invoke(element, value);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            applyFloatSpecial(attribute, value);
        }
    }

    @Override
    public T getElement() {
        return element;
    }

}
