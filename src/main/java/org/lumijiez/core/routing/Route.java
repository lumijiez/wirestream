package org.lumijiez.core.routing;

import org.lumijiez.core.http.HttpHandler;
import org.lumijiez.core.http.HttpRequest;
import org.lumijiez.core.http.HttpResponse;
import org.lumijiez.logging.Logger;

public class Route {
    private final String path;
    private final String method;
    private final HttpHandler handler;

    public Route(String method, String path, HttpHandler handler) {
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
        Logger.info("HTTP", "Incoming " + request.getMethod() + " " + request.getPath());
        handler.handle(request, response);
    }
}
