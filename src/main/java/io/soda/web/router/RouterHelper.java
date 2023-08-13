package io.soda.web.router;

import io.soda.core.recycle.RecycleContainer;
import io.soda.web.constant.HttpMethod;

import java.lang.reflect.Method;
import java.util.*;

/**
 * This class is get all controller classes with {@link Controller}.
 */
public class RouterHelper {
    private final HashMap<HttpMethod, Set<Router>> routers = new HashMap<>();
    private static RouterHelper helper;
    public static RouterHelper getInstance() {
        if (helper == null) {
            helper = new RouterHelper();
        }
        return helper;
    }

    private RouterHelper() {}

    public void registerRouters() {
        updateRouters();
    }

    private List<Object> getControllersFromContainer() {
        Collection<Object> recyclables = RecycleContainer.getInstance().getRecyclables();
        return recyclables.stream().filter(o ->
                o.getClass().getAnnotation(Controller.class) != null)
                .toList();
    }

    private int countSlash(String uri) {
        return (int) uri.chars().filter(c -> c == '/').count();
    }

    public Set<Router> findRoutersByHttpMethod(HttpMethod httpMethod) {
        return routers.get(httpMethod);
    }

    private void updateRouters() {
        List<Object> assigned = getControllersFromContainer();
        for (Object controller : assigned) {
            Controller controllerInfo = controller.getClass().getAnnotation(Controller.class);
            String rootUri = controllerInfo.rootUri();
            Method[] methods = controller.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (!method.canAccess(controller)) {
                    continue;
                }

                if (!method.isAnnotationPresent(Route.class)) {
                    continue;
                }

                Route route = method.getAnnotation(Route.class);
                HttpMethod[] httpMethods = route.methods();
                for (HttpMethod httpMethod : httpMethods) {
                    String routingUri = rootUri + route.value();
                    int count = countSlash(routingUri);
                    if (count > 1) {
                        routingUri = routingUri.substring(0, routingUri.length() - 1);
                    }

                    boolean isDuplicated = isDuplicatedURI(routingUri, httpMethod);
                    if (isDuplicated) {
                        //TODO: change to custom exception which occurs by `same url with same method`
                        throw new RuntimeException();
                    }

                    Router router = Router.create(routingUri, controller, method);
                    routers.computeIfAbsent(httpMethod, k -> new HashSet<>());
                    routers.get(httpMethod).add(router);
                }
            }
        }
    }

    private boolean isDuplicatedURI(String uri, HttpMethod httpMethod) {
        Set<Router> routersByHttpMethod = findRoutersByHttpMethod(httpMethod);
        if (routersByHttpMethod == null) {
            return false;
        }

        for (Router router : routersByHttpMethod) {
            if (router.uri().equals(uri)) {
                return true;
            }
        }
        return false;
    }

    public Optional<Router> getRouter(HttpMethod httpMethod, String uri) {
        Router rightRouter = null;
        Set<Router> routersByHttpMethod = findRoutersByHttpMethod(httpMethod);
        for (Router router  : routersByHttpMethod) {
            if (router.uri().equals(uri)) {
                rightRouter = router;
                break;
            }
        }

        return Optional.ofNullable(rightRouter);
    }
}
