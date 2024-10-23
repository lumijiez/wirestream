package org.lumijiez.core;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class TcpClientHandler implements Runnable {
    private final Socket clientSocket;
    private final TcpServerCallback callback;

    public TcpClientHandler(Socket clientSocket, TcpServerCallback callback) {
        this.clientSocket = clientSocket;
        this.callback = callback;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            String receivedMessage;
            while ((receivedMessage = in.readLine()) != null) {
                System.out.println("Received from client: " + receivedMessage);

                String response = callback.onClientMessage(receivedMessage, clientSocket);

                if (response != null) {
                    out.write((response + "\n").getBytes());
                    out.flush();
                }
            }

        } catch (IOException e) {
            System.out.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
