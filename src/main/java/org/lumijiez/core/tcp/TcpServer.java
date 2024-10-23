package org.lumijiez.core.tcp;
import org.lumijiez.logging.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class TcpServer {
    private final int port;
    private boolean running;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;

    public TcpServer(int port) {
        this.port = port;
        this.running = false;
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("Server started on port " + port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    System.out.println("New client connected: " + clientSocket.getInetAddress());

                    threadPool.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (running) {
                        System.out.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        } finally {
            stop();
        }
    }

    protected abstract void handleClient(Socket clientSocket);

    public void stop() {
        running = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
                System.out.println("Server stopped.");
            } catch (IOException e) {
                System.out.println("Error stopping server: " + e.getMessage());
            }
        }
        threadPool.shutdownNow();
    }

    public boolean isRunning() {
        return running;
    }
}