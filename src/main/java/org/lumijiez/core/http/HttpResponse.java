package org.lumijiez.core.http;

import org.lumijiez.logging.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class HttpResponse {
    private final BufferedWriter out;

    public HttpResponse(BufferedWriter out) {
        this.out = out;
    }

    public void sendResponse(HttpStatus status, String message) throws IOException {
        Logger.info("HTTP", "Outgoing: " + status.getCode() + " " + message);

        out.write("HTTP/1.1 " + status.getCode() + " " + status.getMessage());
        out.write("\r\n");

        out.write("Content-Type: text/plain");
        out.write("\r\n");
        out.write("Content-Length: " + message.getBytes(StandardCharsets.UTF_8).length);
        out.write("\r\n");
        out.write("Connection: keep-alive");
        out.write("\r\n");
        out.write("Keep-Alive: timeout=30");
        out.write("\r\n");

        out.write("\r\n");

        out.write(message);

        out.flush();
    }
}
