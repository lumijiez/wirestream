package org.lumijiez.core.routing;

import org.lumijiez.core.http.*;
import org.lumijiez.core.middleware.Chain;
import org.lumijiez.core.middleware.Middleware;
import org.lumijiez.core.util.UrlParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Router {
    private final List<Route> routes = new ArrayList<>();
    private final List<Middleware> middleware = new ArrayList<>();

    public void addMiddleware(Middleware middleware) {
        this.middleware.add(middleware);
    }

    public void addRoute(HttpMethod method, String pattern, HttpHandler handler) {
        routes.add(new Route(method, pattern, handler));
    }

    public void handleRequest(HttpRequest request, HttpResponse response) throws IOException {
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

        chain.next(request, response);
    }

    private void executeHandler(HttpRequest request, HttpResponse response) throws IOException {
        UrlParser urlParser = new UrlParser(request.getPath());
        Route matchedRoute = null;

        for (Route route : routes) {
            if (route.method().name().equals(request.getMethod()) &&
                    urlParser.matchesPattern(route.path())) {
                matchedRoute = route;
                break;
            }
        }

        if (matchedRoute != null) {
            request.setUrlParser(urlParser);
            matchedRoute.handler().handle(request, response);
        } else {
            response.sendResponse(HttpStatus.NOT_FOUND, "Not Found");
        }
    }
}

