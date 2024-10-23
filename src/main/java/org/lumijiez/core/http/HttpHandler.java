package org.lumijiez.core.http;

import java.io.IOException;

@FunctionalInterface
public interface HttpHandler {
    void handle(HttpRequest request, HttpResponse response) throws IOException;
}