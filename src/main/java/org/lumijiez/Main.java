package org.lumijiez;

import org.lumijiez.core.http.HttpServer;

public class Main {
    public static void main(String[] args) {
        HttpServer server = new HttpServer(8080);

        server.GET("/hello", (req, res) -> res.sendResponse(200, "Hello, World!"));

        server.GET("/goodbye", (req, res) -> res.sendResponse(200, "Goodbye, World!"));

        server.POST("/data", (req, res) -> res.sendResponse(200, "Data received"));

        server.start();
    }
}