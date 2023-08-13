package io.soda.web.configure.server;

import jakarta.servlet.Servlet;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;

/**
 * This class is contains components for embedded tomcat.
 */
public final class EmbeddedTomcat implements EmbeddedServer {
    private static final Logger log = LoggerFactory.getLogger(EmbeddedTomcat.class);

    private final Tomcat tomcat;
    private final int port;

    public EmbeddedTomcat() {
        this(8080);
    }

    /**
     * Initialized tomcat and create default http(Http/1.1) connector.
     * @param port to be listened to by tomcat
     */
    public EmbeddedTomcat(int port) {
        java.util.logging.Logger.getLogger("org.apache").setLevel(Level.WARNING);
        this.port = port;
        this.tomcat = new Tomcat();
        setPortNumber(port);
        tomcat.getConnector();
    }

    /**
     * Set port number to listening
     * @param port will be listening
     */
    private void setPortNumber(int port) {
        tomcat.setPort(port);
    }

    @Override
    public Context getContext() {
        return tomcat.addContext("", null);
    }

    @Override
    public void start() {
        try {
            tomcat.start();
        } catch (LifecycleException e) {
            log.error("Exception occurs when tomcat start", e);
            throw new RuntimeException(e);
        }
        log.info("Tomcat is listening port: {}", port);
    }

    @Override
    public void runUntilStop() {
        start();
        tomcat.getServer().await();
    }

    @Override
    public int getPort() {
        return tomcat.getServer().getPort();
    }

    @Override
    public void addServlet(Object context, String name, Servlet servlet) {
        addServlet(context, name, servlet, "/");
    }

    @Override
    public void addServlet(Object context, String name, Servlet servlet, String pattern) {
        Tomcat.addServlet((Context) context, name, servlet);
        ((Context) context).addServletMappingDecoded(pattern, name);
    }
}
