package org.lumijiez.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.BiConsumer;

public class HttpServer extends TcpServer {
    private final Router router;

    public HttpServer(int port, TcpServerCallback callback) {
        super(port);
        this.router = new Router();
    }

    public void GET(String path, BiConsumer<HttpRequest, HttpResponse> handler) {
        router.addRoute("GET", path, handler);
    }

    public void POST(String path, BiConsumer<HttpRequest, HttpResponse> handler) {
        router.addRoute("POST", path, handler);
    }

    @Override
    protected void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            router.handleRequest(request, response);
        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        }
    }
}