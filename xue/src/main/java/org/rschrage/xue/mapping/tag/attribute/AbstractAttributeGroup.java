package org.rschrage.xue.mapping.tag.attribute;

import org.rschrage.xue.annotation.AttributeHandler;
import org.rschrage.xue.exception.XueException;
import org.rschrage.xue.input.XueInputEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Rico Schrage
 */
public abstract class AbstractAttributeGroup<T> implements AttributeGroup<T> {
    private static final String APPLY_PREFIX = "apply";

    @Override
    public void begin(T element) {
        //default: do nothing
    }

    @Override
    public void apply(T element, Object value, String attribute) {
        Method[] methods = getClass().getDeclaredMethods();
        for (Method method : methods) {
            try {
                if (method.isAnnotationPresent(AttributeHandler.class) && method.getName().equals(APPLY_PREFIX + attribute.substring(0, 1).toUpperCase() + attribute.substring(1))) {
                    method.invoke(this, element, value);
                    return;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new XueException(e);
            }
        }
    }

    @Override
    public void end(T element) {
        //default: do nothing
    }

    @Override
    public void onInputEvent(XueInputEvent event) {
        //default: do nothing
    }

    @Override
    public void onUpdate(float delta) {
        //default: do nothing
    }
}
