package org.lumijiez.core.routing;

public class Route {
    private final HttpMethod method;
    private final String path;
    private final HttpHandler handler;

    public Route(HttpMethod method, String path, HttpHandler handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    // Add getters...
}