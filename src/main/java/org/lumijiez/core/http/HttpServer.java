package org.lumijiez.core.http;

import org.lumijiez.core.routing.Router;
import org.lumijiez.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private boolean running;
    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private final Router router;

    public HttpServer(int port) {
        this.running = false;
        this.port = port;
        this.router = new Router();
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void GET(String path, HttpHandler handler) {
        router.addRoute("GET", path, handler);
    }

    public void POST(String path, HttpHandler handler) {
        router.addRoute("POST", path, handler);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;

            Logger.info("HTTP", "Server started on port " + port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    // Logger.info("HTTP", "Client connected " + clientSocket.getInetAddress());

                    threadPool.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (running) {
                        Logger.error("HTTP", "Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            Logger.error("HTTP", "Error starting server: " + e.getMessage());
        } finally {
            stop();
        }
    }

    protected void handleClient(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            in.mark(32000);
            if (!in.ready()) {
                clientSocket.close();
                return;
            }

            String firstLine = in.readLine();
            if (firstLine == null || firstLine.trim().isEmpty()) {
                clientSocket.close();
                return;
            }
            in.reset();

            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            if (request.getMethod() != null && request.getPath() != null) {
                Logger.info("HTTP", "Incoming: " + request.getMethod() + " " + request.getPath());
                router.handleRequest(request, response);
            }

            clientSocket.close();
        } catch (IOException e) {
            Logger.error("HTTP", "Error handling client: " + e.getMessage());
            try {
                clientSocket.close();
            } catch (IOException ignored) {}
        }
    }

    public void stop() {
        running = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
                Logger.info("HTTP", "Server stopped");
            } catch (IOException e) {
                Logger.error("HTTP", "Error stopping server: " + e.getMessage());
            }
        }
        threadPool.shutdownNow();
    }

    public boolean isRunning() {
        return running;
    }
}