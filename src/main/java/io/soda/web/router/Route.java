package io.soda.web.router;

import io.soda.web.constant.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is route a uri on controller
 * @see Controller
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE_USE})
public @interface Route {

    String value() default "/";

    HttpMethod[] methods() default { HttpMethod.GET };

}
