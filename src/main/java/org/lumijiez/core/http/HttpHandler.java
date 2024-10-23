package org.lumijiez.core.http;

@FunctionalInterface
public interface HttpHandler {
    void handle(HttpRequest request, HttpResponse response);
}