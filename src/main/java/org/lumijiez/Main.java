package org.lumijiez;

import org.lumijiez.core.http.HttpServer;
import org.lumijiez.logging.Logger;

public class Main {
    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer(8080);

        httpServer.GET("/hello", (req, res) -> {
            res.sendResponse(200, "Hello, World!");
        });

        httpServer.GET("/goodbye", (req, res) -> {
            res.sendResponse(200, "Goodbye, World!");
        });

        httpServer.POST("/data", (req, res) -> {
            res.sendResponse(200, "Data received");
        });

        httpServer.start();
    }
}