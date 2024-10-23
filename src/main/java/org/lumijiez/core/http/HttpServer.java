package org.lumijiez.core.http;

import org.lumijiez.core.config.ServerConfig;
import org.lumijiez.core.middleware.Middleware;
import org.lumijiez.core.routing.Router;
import org.lumijiez.logging.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private boolean running;
    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private final Router router;
    private static int KEEP_ALIVE_TIMEOUT = 30000;
    private static int MAX_REQUESTS_PER_CONNECTION = 1000;
    private static int BUFFER_SIZE = 8192;

    public HttpServer(ServerConfig config) {
        this.running = false;
        this.port = config.getPort();
        KEEP_ALIVE_TIMEOUT = config.getKeepAliveTimeout();
        MAX_REQUESTS_PER_CONNECTION = config.getMaxRequestsPerConnection();
        BUFFER_SIZE = config.getBufferSize();
        this.router = new Router();
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void addMiddleware(Middleware middleware) {
        this.router.addMiddleware(middleware);
    }

    public void GET(String path, HttpHandler handler) {
        router.addRoute(HttpMethod.GET, path, handler);
    }

    public void POST(String path, HttpHandler handler) {
        router.addRoute(HttpMethod.POST, path, handler);
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
            clientSocket.setSoTimeout(KEEP_ALIVE_TIMEOUT);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()),
                    BUFFER_SIZE
            );
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(clientSocket.getOutputStream()),
                    BUFFER_SIZE
            );

            int requestCount = 0;
            boolean keepAlive = true;

            while (keepAlive && requestCount < MAX_REQUESTS_PER_CONNECTION && running) {
                try {
                    if (!in.ready()) {
                        // Thread.sleep(10);
                        continue;
                    }

                    HttpRequest request = new HttpRequest(in);
                    if (request.getMethod() == null || request.getPath() == null) {
                        break;
                    }

                    HttpResponse response = new HttpResponse(out);

                    Logger.info("HTTP", String.format(
                            "Incoming [%d]: %s %s (keep-alive: %s)",
                            requestCount + 1,
                            request.getMethod(),
                            request.getPath(),
                            request.isKeepAlive()
                    ));

                    router.handleRequest(request, response);

                    keepAlive = request.isKeepAlive();
                    requestCount++;

                    out.flush();

                } catch (SocketTimeoutException e) {
                    Logger.info("HTTP", "Keep-alive timeout reached");
                    break;
                } catch (IOException e) {
                    if (running) {
                        Logger.error("HTTP", "Error processing request: " + e.getMessage());
                    }
                    break;
                }
            }
        } catch (IOException e) {
            if (running) {
                Logger.error("HTTP", "Error handling client: " + e.getMessage());
            }
        } finally {
            try {
                clientSocket.close();
                Logger.info("HTTP", "Connection closed gracefully");
            } catch (IOException e) {
                Logger.error("HTTP", "Error closing socket: " + e.getMessage());
            }
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