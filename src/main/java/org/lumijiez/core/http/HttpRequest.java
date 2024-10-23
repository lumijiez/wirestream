package org.lumijiez.core.http;

import org.lumijiez.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    private String method;
    private String path;
    private String httpVersion;

    public HttpRequest(BufferedReader in) throws IOException {
        parseRequest(in);
    }

    private void parseRequest(BufferedReader in) throws IOException {
        String requestLine;

        while ((requestLine = in.readLine()) != null) {

            if (!requestLine.trim().isEmpty()) {
                String[] tokens = requestLine.split(" ");
                if (tokens.length == 3) {
                    this.method = tokens[0];
                    this.path = tokens[1];
                    this.httpVersion = tokens[2];
                    break;
                } else {
                    Logger.error("HTTP", "Invalid line format: " + requestLine);
                    throw new IOException("Invalid request line format.");
                }
            }
        }

//        String headerLine;
//        while ((headerLine = in.readLine()) != null && !headerLine.trim().isEmpty()) {
//            Logger.info("HTTP-DEBUG", "Header: " + headerLine);
//        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
