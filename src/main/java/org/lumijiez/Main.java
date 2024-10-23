package org.lumijiez;

import org.lumijiez.core.TcpServer;

public class Main {
    public static void main(String[] args) {
        TcpServer server = new TcpServer(8080, (message, clientSocket) -> {
            System.out.println("Processing message from " + clientSocket.getInetAddress() + ": " + message);

            if (message.equalsIgnoreCase("hello")) {
                return "Hello, client!";
            } else if (message.equalsIgnoreCase("bye")) {
                return "Goodbye!";
            } else {
                return "Unknown command.";
            }
        });

        new Thread(server::start).start();

        try {
            Thread.sleep(60000);
            server.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}