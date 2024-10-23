package org.lumijiez.core;

import java.util.function.BiConsumer;

public class Route {
    private final String path;
    private final String method;
    private final BiConsumer<HttpRequest, HttpResponse> handler;

    public Route(String method, String path, BiConsumer<HttpRequest, HttpResponse> handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public void handle(HttpRequest request, HttpResponse response) {
        handler.accept(request, response);
    }
}
