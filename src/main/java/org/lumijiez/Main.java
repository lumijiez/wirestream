package org.lumijiez;

import org.lumijiez.core.HttpServer;

import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        TcpServerCallback callback = new TcpServerCallback() {
            @Override
            public String onClientMessage(String message, Socket clientSocket) {
                return "";
            }

            @Override
            public String onClientConnected(Socket clientSocket) {
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                return "";
            }
        };

        HttpServer httpServer = new HttpServer(8080, callback);

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