package org.lumijiez.core.routing;

import org.lumijiez.core.http.HttpHandler;
import org.lumijiez.core.http.HttpMethod;
import org.lumijiez.core.http.HttpRequest;
import org.lumijiez.core.http.HttpResponse;
import org.lumijiez.core.middleware.Chain;
import org.lumijiez.core.middleware.Middleware;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router {
    private final Map<String, Route> routes = new HashMap<>();
    private final List<Middleware> middleware = new ArrayList<>();

    public void addMiddleware(Middleware middleware) {
        this.middleware.add(middleware);
    }

    public void addRoute(HttpMethod method, String path, HttpHandler handler) {
        String key = method.name() + ":" + path;
        routes.put(key, new Route(method, path, handler));
    }

    public void handleRequest(HttpRequest request, HttpResponse response) throws IOException {
        // Create middleware chain
        Chain chain = new Chain() {
            private int index = 0;

            @Override
            public void next(HttpRequest request, HttpResponse response) throws IOException {
                if (index < middleware.size()) {
                    middleware.get(index++).process(request, response, this);
                } else {
                    executeHandler(request, response);
                }
            }
        };

        // Start middleware chain
        chain.next(request, response);
    }

    private void executeHandler(HttpRequest request, HttpResponse response) throws IOException {
        String key = request.getMethod() + ":" + request.getPath();
        Route route = routes.get(key);

        if (route != null) {
            route.handler().handle(request, response);
        } else {
            response.sendResponse(HttpStatus.NOT_FOUND, "Not Found");
        }
    }
}

