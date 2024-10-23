package org.lumijiez.core;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer {
    private final int port;
    private boolean running;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private final TcpServerCallback callback;

    public TcpServer(int port, TcpServerCallback callback) {
        this.port = port;
        this.callback = callback;
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

                    TcpClientHandler clientHandler = new TcpClientHandler(clientSocket, callback);
                    threadPool.submit(clientHandler);
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

