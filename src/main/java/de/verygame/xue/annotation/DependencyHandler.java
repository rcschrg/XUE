package de.verygame.xue.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Rico Schrage
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.FIELD)
public @interface DependencyHandler {
    //marker
}
