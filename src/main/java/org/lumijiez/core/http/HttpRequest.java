package org.lumijiez.core.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private String httpVersion;
    private final Map<String, String> headers;

    public HttpRequest(BufferedReader in) throws IOException {
        this.headers = new HashMap<>();
        parseRequest(in);
    }

    private void parseRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine != null && !requestLine.trim().isEmpty()) {
            String[] tokens = requestLine.split(" ");
            if (tokens.length == 3) {
                this.method = tokens[0];
                this.path = tokens[1];
                this.httpVersion = tokens[2];
            } else {
                throw new IOException("Invalid request line format.");
            }
        }

        String headerLine;
        while ((headerLine = in.readLine()) != null && !headerLine.trim().isEmpty()) {
            int separator = headerLine.indexOf(':');
            if (separator > 0) {
                String key = headerLine.substring(0, separator).trim().toLowerCase();
                String value = headerLine.substring(separator + 1).trim();
                headers.put(key, value);
            }
        }
    }

    public boolean isKeepAlive() {
        String connection = headers.get("connection");
        if ("close".equalsIgnoreCase(connection)) {
            return false;
        }
        return "HTTP/1.1".equals(httpVersion) ||
                "keep-alive".equalsIgnoreCase(connection);
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
