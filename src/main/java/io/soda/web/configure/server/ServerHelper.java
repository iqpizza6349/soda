package io.soda.web.configure.server;

import io.soda.web.router.RouterHelper;
import io.soda.web.router.RootController;

/**
 * This class is help to configure a web server with embedded server.
 */
public class ServerHelper {

    private final EmbeddedServer server;

    //TODO: 서버 종류가 여러 개가 생길지도 모르고
    //      보다 편하게 서블릿을 등록하도록 도와주는 도움 클래스
    //      아직은 톰캣 뿐이니 톰캣으로 강제 지정
    public ServerHelper(int port) {
        this.server = new EmbeddedTomcat(port);
    }

    public void registerRouter() {
        RouterHelper.getInstance().registerRouters();
        server.addServlet(server.getContext(), "RootServlet", new RootController());
    }

    public void run() {
        run(true);
    }

    public void run(boolean wait) {
        if (wait) {
            server.runUntilStop();
        }
        else {
            server.start();
        }
    }
}
