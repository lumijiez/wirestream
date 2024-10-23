package org.lumijiez.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class Router {
    private final List<Route> routes = new ArrayList<>();

    public void addRoute(String method, String path, BiConsumer<HttpRequest, HttpResponse> handler) {
        routes.add(new Route(method, path, handler));
    }

    public void handleRequest(HttpRequest request, HttpResponse response) {
        for (Route route : routes) {
            if (route.getMethod().equalsIgnoreCase(request.getMethod()) &&
                    route.getPath().equals(request.getPath())) {
                route.handle(request, response);
                return;
            }
        }
        response.sendResponse(404, "Not Found");
    }
}
