package io.github.lumijiez.example;

import io.github.lumijiez.core.config.ServerConfig;
import io.github.lumijiez.core.http.HttpServer;
import io.github.lumijiez.core.http.HttpStatus;
import io.github.lumijiez.core.ws.WebSocketConnection;
import io.github.lumijiez.core.ws.WebSocketHandler;
import io.github.lumijiez.core.ws.WebSocketServer;
import io.github.lumijiez.example.daos.ProductDao;
import io.github.lumijiez.example.models.Product;
import io.github.lumijiez.core.logging.Logger;

public class Main {
    public static void main(String[] args) {
        ProductDao productDao = new ProductDao();

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

        server.GET("/user", (req, res) -> {
            Product product = productDao.getProductById(5);
            res.sendJson(HttpStatus.OK, product);
        });

        server.GET("/products", (req, res) -> {
            Product product = productDao.getProductById(5);
            res.sendResponse(HttpStatus.OK, product.toString());
        });

        WebSocketServer wsServer = new WebSocketServer(8081);

        wsServer.addHandler("/chat", new WebSocketHandler() {
            @Override
            public void onConnect(WebSocketConnection connection) {
                Logger.info("WS", "Client connected to chat: " + connection.getId());
            }

            @Override
            public void onMessage(WebSocketConnection connection, String message) {
                Logger.info("WS", "Received message: " + message);
                wsServer.broadcast("/chat", message);
            }

            @Override
            public void onDisconnect(WebSocketConnection connection) {
                Logger.info("WS", "Client disconnected from chat: " + connection.getId());
            }
        });

        new Thread(server::start).start();
        new Thread(wsServer::start).start();
    }
}