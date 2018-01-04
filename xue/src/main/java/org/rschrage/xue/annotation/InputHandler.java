package org.rschrage.xue.annotation;

import org.rschrage.xue.input.XueInputEvent;

import java.lang.annotation.*;

/**
 * @author Rico Schrage
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface InputHandler {
    XueInputEvent value();
}
