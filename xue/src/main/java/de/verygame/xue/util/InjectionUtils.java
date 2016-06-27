package de.verygame.xue.util;

import de.verygame.util.ReflectionUtils;
import de.verygame.xue.exception.XueException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Rico Schrage
 */
public class InjectionUtils {

    private InjectionUtils() {
        //utility class
    }

    public static void injectByType(Class<? extends Annotation> marker, Object injectTarget, Object injectable) {
        inject(marker, injectTarget, injectable, InjectionByTypeStrategy.getInstance());
    }

    public static void injectByName(Class<? extends Annotation> marker, Object injectTarget, Object injectable) {
        inject(marker, injectTarget, injectable, InjectionByNameStrategy.getInstance());
    }

    /**
     * Injects dependencies.
     *
     * @param injectTarget target of the injection
     * @param injectable injectable which contains injectable result or tag
     */
    public static void inject(Class<? extends Annotation> marker, Object injectTarget, Object injectable, InjectionStrategy injectionStrategy) throws XueException {
        List<Field> fields = ReflectionUtils.getAllFields(injectTarget.getClass());
        for (final Field field : fields) {
            if (field.isAnnotationPresent(marker) && injectionStrategy.injectionCondition(field, injectable)) {
                try {
                    field.setAccessible(true);
                    field.set(injectTarget, injectable);
                }
                catch (IllegalAccessException e) {
                    throw new XueException(e);
                }
            }
        }
    }

    /**
     * Created by rschr on 17.06.2016.
     */
    public interface InjectionStrategy {

        boolean injectionCondition(Field targetField, Object injectable);
    }

    public static class InjectionByNameStrategy implements InjectionStrategy {

        private static InjectionByNameStrategy strategy = new InjectionByNameStrategy();

        public static InjectionByNameStrategy getInstance() {
            return strategy;
        }

        @Override
        public boolean injectionCondition(Field targetField, Object injectable) {
            return targetField.getName().equals(injectable.getClass().getSimpleName().substring(0, 1).toLowerCase() +
                    injectable.getClass().getSimpleName().substring(1));
        }
    }

    public static class InjectionByTypeStrategy implements InjectionStrategy {

        private static InjectionByTypeStrategy strategy = new InjectionByTypeStrategy();

        public static InjectionByTypeStrategy getInstance() {
            return strategy;
        }

        @Override
        public boolean injectionCondition(Field targetField, Object injectable) {
            return targetField.getType() == injectable.getClass();
        }
    }

}
