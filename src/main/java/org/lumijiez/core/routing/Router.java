package org.lumijiez.core.routing;

import org.lumijiez.core.http.HttpHandler;
import org.lumijiez.core.http.HttpRequest;
import org.lumijiez.core.http.HttpResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router {
    private final Map<String, HttpHandler> routes = new HashMap<>();

    public void addRoute(String method, String path, HttpHandler handler) {
        String key = method.toUpperCase() + ":" + path;
        routes.put(key, handler);
    }

    public void handleRequest(HttpRequest request, HttpResponse response) {
        String key = request.getMethod().toUpperCase() + ":" + request.getPath();
        HttpHandler handler = routes.get(key);

        if (handler != null) {
            handler.handle(request, response);
        } else {
            response.sendResponse(404, "Not Found");
        }
    }
}
