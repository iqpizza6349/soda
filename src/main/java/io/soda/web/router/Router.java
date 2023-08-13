package io.soda.web.router;

import java.lang.reflect.Method;

/**
 * This record is describes an uri and invokable method
 */
public record Router(String uri, Object controller, Method invokableMethod) {

    public static Router create(String uri, Object controller, Method invokableMethod) {
        return new Router(uri, controller, invokableMethod);
    }

}
