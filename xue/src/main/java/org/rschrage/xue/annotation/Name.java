package org.rschrage.xue.annotation;

import java.lang.annotation.*;

/**
 * @author Rico Schrage
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
public @interface Name {
    String value();
}
