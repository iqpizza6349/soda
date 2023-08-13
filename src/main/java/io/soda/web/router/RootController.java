package io.soda.web.router;

import io.soda.web.constant.HttpMethod;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class RootController extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(RootController.class);

    @Override
    public void init() throws ServletException {
        super.init();
        LOGGER.info("Initialized Root Controller to request/response.");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Set<Router> routersUsingGet = RouterHelper.getInstance().findRoutersByHttpMethod(HttpMethod.GET);
        String requestedUri = req.getRequestURI();
        String message = defaultMessage(requestedUri);
        for (Router router : routersUsingGet) {
            if (router.uri().equals(requestedUri)) {
                Object controller = RouterHelper.getInstance().getRouter(HttpMethod.GET, requestedUri)
                        .orElseThrow(/* cannot found match controller */)
                        .controller();
                Object result = invoke(router.invokableMethod(), controller);
                if (result != null) {
                    message = result.toString();
                }
            }
        }

        resp.getOutputStream().print(message);
        resp.getOutputStream().flush();
    }

    private String defaultMessage(String uri) {
        return "Cannot find router mapping on " + uri;
    }

    private Object invoke(Method method, Object controller) {
        try {
            return method.invoke(controller);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
