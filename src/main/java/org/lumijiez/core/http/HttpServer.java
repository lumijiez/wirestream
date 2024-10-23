package org.lumijiez.core.http;

import org.lumijiez.core.routing.Router;
import org.lumijiez.core.tcp.TcpServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HttpServer extends TcpServer {
    private final Router router;

    public HttpServer(int port) {
        super(port);
        this.router = new Router();
    }

    public void GET(String path, HttpHandler handler) {
        router.addRoute("GET", path, handler);
    }

    public void POST(String path, HttpHandler handler) {
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