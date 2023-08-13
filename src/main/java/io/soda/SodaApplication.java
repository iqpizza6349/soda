package io.soda;

import io.soda.core.recycle.RecycleContainer;
import io.soda.web.configure.server.ServerHelper;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;

/**
 * This final class is start to run the application
 */
public final class SodaApplication {
    static {
        java.util.logging.Logger.getLogger("org.reflections").setLevel(Level.WARNING);
    }

    public static void run(Class<?> caller, String[] args) {
        Logger logger = LoggerFactory.getLogger(caller);
        StopWatch stopWatch = new StopWatch();
        stopWatch.reset();
        stopWatch.start();
        RecycleContainer.getInstance().processRegister();
        ServerHelper helper = new ServerHelper(8080);
        helper.registerRouter();
        stopWatch.stop();
        logger.info("Started {} in {} seconds", caller.getSimpleName(),
                (float) stopWatch.getTime() / 1000);
        helper.run();
    }
}
