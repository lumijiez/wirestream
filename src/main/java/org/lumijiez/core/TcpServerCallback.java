package org.lumijiez.core;

import java.net.Socket;

public interface TcpServerCallback {
    String onClientMessage(String message, Socket clientSocket);
}
