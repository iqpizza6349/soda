package io.soda.core.recycle;

import java.lang.annotation.*;

/**
 * This annotation is make class to single-ton
 */
@Inherited
@Recyclable
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
public @interface Recyclable {
}
