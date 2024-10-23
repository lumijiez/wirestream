package org.lumijiez;

import org.lumijiez.core.config.ServerConfig;
import org.lumijiez.core.http.HttpServer;
import org.lumijiez.core.http.HttpStatus;
import org.lumijiez.logging.Logger;

public class Main {
    public static void main(String[] args) {
        ServerConfig config = new ServerConfig.Builder()
                .port(8080)
                .keepAliveTimeout(30000)
                .build();

        HttpServer server = new HttpServer(config);

        server.addMiddleware((req, res, chain) -> {
            Logger.info("MIDDLEWARE", "Request: " + req.getMethod() + " " + req.getPath());
            chain.next(req, res);
        });

        server.GET("/test/:lel/", (req, res) -> {
            Logger.info("PATH", req.getPathParam("lel"));
            Logger.info("QUERY", req.getQueryParam("lol"));
            res.sendResponse(HttpStatus.OK, "All good, lil bro");
        });

        server.start();
    }
}