package de.verygame.xue.util;

import de.verygame.xue.annotation.DependencyHandler;
import de.verygame.xue.exception.XueException;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Rico Schrage
 */
public class InjectionUtils {

    public static void injectDependencyByType(Object injectTarget, Object injectable) {
        injectDependency(injectTarget, injectable, InjectionByTypeStrategy.getInstance());
    }

    public static void injectDependencyByName(Object injectTarget, Object injectable) {
        injectDependency(injectTarget, injectable, InjectionByNameStrategy.getInstance());
    }

    /**
     * Injects dependencies.
     *
     * @param injectTarget target of the injection
     * @param injectable injectable which contains injectable result or builder
     */
    public static void injectDependency(Object injectTarget, Object injectable, InjectionStrategy injectionStrategy) throws XueException {
        List<Field> fields = ReflectionUtils.getAllFields(injectTarget.getClass());
        for (final Field field : fields) {
            if (field.isAnnotationPresent(DependencyHandler.class) && injectionStrategy.injectionCondition(field, injectable)) {
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
            return targetField.getName().equals(injectable.getClass().getName().substring(0, 1).toLowerCase() +
                    injectable.getClass().getName().substring(1));
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
