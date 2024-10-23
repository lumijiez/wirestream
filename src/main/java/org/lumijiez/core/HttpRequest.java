package org.lumijiez.core;

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
        String requestLine = in.readLine();
        if (requestLine != null) {
            String[] tokens = requestLine.split(" ");
            if (tokens.length >= 3) {
                this.method = tokens[0];
                this.path = tokens[1];
                this.httpVersion = tokens[2];
            }
        }
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
