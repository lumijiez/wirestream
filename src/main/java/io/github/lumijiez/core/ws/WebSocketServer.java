package io.github.lumijiez.core.ws;

import io.github.lumijiez.logging.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebSocketServer {
    private boolean running;
    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private final ConcurrentHashMap<String, WebSocketConnection> connections;
    private final Map<String, WebSocketHandler> handlers;

    public WebSocketServer(int port) {
        this.port = port;
        this.running = false;
        this.threadPool = Executors.newCachedThreadPool();
        this.connections = new ConcurrentHashMap<>();
        this.handlers = new HashMap<>();
    }

    public void addHandler(String path, WebSocketHandler handler) {
        handlers.put(path, handler);
    }

    public void broadcast(String path, String message) {
        connections.values().stream()
                .filter(conn -> conn.getPath().equals(path))
                .forEach(conn -> {
                    try {
                        conn.send(message);
                    } catch (IOException e) {
                        Logger.error("WS", "Error broadcasting message: " + e.getMessage());
                    }
                });
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;

            Logger.info("WS", "WebSocket server started on port " + port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (running) {
                        Logger.error("WS", "Error accepting WebSocket connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            Logger.error("WS", "Error starting WebSocket server: " + e.getMessage());
        } finally {
            stop();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String line = in.readLine();
            if (line == null) return;

            String[] requestLine = line.split(" ");
            if (requestLine.length != 3) return;

            String path = requestLine[1];
            WebSocketHandler handler = handlers.get(path);

            if (handler == null) {
                clientSocket.close();
                return;
            }

            Map<String, String> headers = new HashMap<>();
            while (!(line = in.readLine()).isEmpty()) {
                String[] parts = line.split(": ", 2);
                if (parts.length == 2) {
                    headers.put(parts[0].toLowerCase(), parts[1]);
                }
            }

            String key = headers.get("sec-websocket-key");
            if (key == null) {
                clientSocket.close();
                return;
            }

            String acceptKey = generateAcceptKey(key);
            out.write("HTTP/1.1 101 Switching Protocols\r\n");
            out.write("Upgrade: websocket\r\n");
            out.write("Connection: Upgrade\r\n");
            out.write("Sec-WebSocket-Accept: " + acceptKey + "\r\n");
            out.write("\r\n");
            out.flush();

            WebSocketConnection connection = new WebSocketConnection(clientSocket, path);
            String connId = connection.getId();
            connections.put(connId, connection);

            handler.onConnect(connection);

            while (running && connection.isOpen()) {
                WebSocketFrame frame = WebSocketFrame.read(clientSocket.getInputStream());
                if (frame == null) break;

                switch (frame.getOpcode()) {
                    case 0x1:
                        handler.onMessage(connection, new String(frame.getPayload()));
                        break;
                    case 0x8:
                        connection.close();
                        break;
                    case 0x9:
                        connection.sendPong();
                        break;
                }
            }

            handler.onDisconnect(connection);
            connections.remove(connId);

        } catch (IOException e) {
            Logger.error("WS", "Error handling WebSocket client: " + e.getMessage());
        }
    }

    private String generateAcceptKey(String key) {
        String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return Base64.getEncoder().encodeToString(
                    md.digest((key + GUID).getBytes())
            );
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        running = false;
        connections.values().forEach(conn -> {
            try {
                conn.close();
            } catch (IOException e) {
                Logger.error("WS", "Error closing connection: " + e.getMessage());
            }
        });
        connections.clear();
        if (serverSocket != null) {
            try {
                serverSocket.close();
                Logger.info("WS", "WebSocket server stopped");
            } catch (IOException e) {
                Logger.error("WS", "Error stopping WebSocket server: " + e.getMessage());
            }
        }
        threadPool.shutdownNow();
    }
}
