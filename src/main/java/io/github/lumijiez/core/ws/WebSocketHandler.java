package io.github.lumijiez.core.ws;

public interface WebSocketHandler {
    void onConnect(WebSocketConnection connection);
    void onMessage(WebSocketConnection connection, String message);
    void onDisconnect(WebSocketConnection connection);
}
