package org.lumijiez.core.http;

import org.lumijiez.logging.Logger;

import java.io.PrintWriter;

public class HttpResponse {
    private final PrintWriter out;

    public HttpResponse(PrintWriter out) {
        this.out = out;
    }

    public void sendResponse(int statusCode, String message) {
        Logger.info("HTTP", "Outgoing: " + statusCode + " " + message);
        out.println("HTTP/1.1 " + statusCode + " " + message);
        out.println("Content-Type: text/plain");
        out.println("Connection: close");
        out.println();
        out.println(message);
        out.flush();
    }
}
