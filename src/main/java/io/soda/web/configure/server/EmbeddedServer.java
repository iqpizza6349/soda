package io.soda.web.configure.server;

import jakarta.servlet.Servlet;

/**
 * This interface is defines to embedded server such as embedded-tomcat.
 */
public sealed interface EmbeddedServer permits EmbeddedTomcat {

    int getPort();

    void start();

    void runUntilStop();

    Object getContext();

    void addServlet(Object context, String name, Servlet servlet);

    void addServlet(Object context, String name, Servlet servlet, String pattern);

}
