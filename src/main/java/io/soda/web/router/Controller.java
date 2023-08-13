package io.soda.web.router;

import io.soda.core.recycle.Recyclable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is defines the controller class
 */
@Recyclable
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

    String rootUri() default "";

}
