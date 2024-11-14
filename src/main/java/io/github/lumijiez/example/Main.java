package io.github.lumijiez.example;

import io.github.lumijiez.core.config.ServerConfig;
import io.github.lumijiez.core.http.HttpFileItem;
import io.github.lumijiez.core.http.HttpMultipartData;
import io.github.lumijiez.core.http.HttpServer;
import io.github.lumijiez.core.http.HttpStatus;
import io.github.lumijiez.core.ws.WebSocketConnection;
import io.github.lumijiez.core.ws.WebSocketHandler;
import io.github.lumijiez.core.ws.WebSocketServer;
import io.github.lumijiez.example.daos.ProductDao;
import io.github.lumijiez.example.models.Product;
import io.github.lumijiez.core.logging.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        ThreadedWriter writer = new ThreadedWriter("test.txt");
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

        server.POST("/write/:text", (req, res) -> {
            Logger.info("PATH", req.getPathParam("text"));
            writer.writeToFile(req.getPathParam("text"));
            res.sendResponse(HttpStatus.OK, "OK");
        });

        server.GET("/test/:lel/", (req, res) -> {
            Logger.info("PATH", req.getPathParam("lel"));
            Logger.info("QUERY", req.getQueryParam("lol"));
            res.sendResponse(HttpStatus.OK, "All good, lil bro");
        });

        server.POST("/upload", (req, res) -> {
            HttpMultipartData multipartData = req.getMultipartData();

            String description = multipartData.getField("description");
            String category = multipartData.getField("category");

            HttpFileItem uploadedFile = multipartData.getFile("file");
            if (uploadedFile != null) {
                String fileName = uploadedFile.fileName();
                String contentType = uploadedFile.contentType();
                byte[] fileContent = uploadedFile.content();

                File uploadDir = new File("uploads");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                Logger.info("START UPLOAD", fileName);
                File destination = new File(uploadDir, fileName);
                uploadedFile.saveTo(destination);
                Logger.info("DONE UPLOAD", fileName);
                res.sendResponse(HttpStatus.OK, "Uploaded: " + fileName);
//                res.sendJson(HttpStatus.OK, Map.of(
//                        "message", "File uploaded successfully",
//                        "fileName", fileName,
//                        "size", fileContent.length,
//                        "description", description
//                ));
                Logger.info("START UPLOAD", fileName);
            } else {
                res.sendJson(HttpStatus.BAD_REQUEST, Map.of(
                        "error", "No file provided"
                ));
            }
        });

        server.GET("/user", (req, res) -> {
            Product product = productDao.getProductById(5);
            res.sendJson(HttpStatus.OK, product);
        });

        server.GET("/products/:page/", (req, res) -> {
            Logger.info("PATH", req.getPathParam("page"));
            List<Product> products = productDao.getProductsByPage(Integer.parseInt(req.getPathParam("page")), 5);
            res.sendResponse(HttpStatus.OK, products.toString());
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