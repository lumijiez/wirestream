package io.github.lumijiez.core.http;

public enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD, OPTIONS;

    public static HttpMethod fromString(String method) {
        try {
            return valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("Unsupported HTTP method: " + method);
        }
    }
}
