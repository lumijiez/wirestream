package io.github.lumijiez.core.ws;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class WebSocketConnection {
    private final Socket socket;
    private final String id;
    private final String path;
    private boolean isOpen;

    public WebSocketConnection(Socket socket, String path) {
        this.socket = socket;
        this.id = UUID.randomUUID().toString();
        this.path = path;
        this.isOpen = true;
    }

    public void send(String message) throws IOException {
        if (!isOpen) throw new IOException("Connection is closed");

        byte[] payload = message.getBytes();
        WebSocketFrame frame = new WebSocketFrame();
        frame.setFin(true);
        frame.setOpcode(0x1);
        frame.setPayload(payload);

        frame.write(socket.getOutputStream());
    }

    public void sendPong() throws IOException {
        if (!isOpen) return;

        WebSocketFrame frame = new WebSocketFrame();
        frame.setFin(true);
        frame.setOpcode(0xA);
        frame.setPayload(new byte[0]);

        frame.write(socket.getOutputStream());
    }

    public void close() throws IOException {
        if (!isOpen) return;

        WebSocketFrame frame = new WebSocketFrame();
        frame.setFin(true);
        frame.setOpcode(0x8);
        frame.setPayload(new byte[0]);

        frame.write(socket.getOutputStream());
        isOpen = false;
        socket.close();
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public boolean isOpen() {
        return isOpen;
    }
}