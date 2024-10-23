package org.lumijiez.core.routing;

import org.lumijiez.core.http.HttpHandler;
import org.lumijiez.core.http.HttpRequest;
import org.lumijiez.core.http.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class Router {
    private final List<Route> routes = new ArrayList<>();

    public void addRoute(String method, String path, HttpHandler handler) {
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
