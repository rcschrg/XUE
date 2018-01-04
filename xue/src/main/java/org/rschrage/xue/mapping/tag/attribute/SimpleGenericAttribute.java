package org.rschrage.xue.mapping.tag.attribute;

import org.rschrage.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author Rico Schrage
 */

public class SimpleGenericAttribute<T, V> extends AbstractAttribute<T, V> {
    private String name;

    public SimpleGenericAttribute(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void apply(T element, V value) {
        //makes it possible to address methods with a primitive parameter
        Class<?> c = value.getClass();
        if (c == Float.class) {
            c = float.class;
        }
        else if (c == Integer.class) {
            c = int.class;
        }

        Method targetMethod = ReflectionUtils.retrieveMethod(element.getClass(), "set" + name.substring(0,1).toUpperCase() + name.substring(1), c);
        ReflectionUtils.invokeMethod(targetMethod, element, value);
    }
}
