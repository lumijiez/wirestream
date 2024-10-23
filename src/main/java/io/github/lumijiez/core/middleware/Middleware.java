package io.github.lumijiez.core.middleware;

import io.github.lumijiez.core.http.HttpRequest;
import io.github.lumijiez.core.http.HttpResponse;

import java.io.IOException;

public interface Middleware {
    void process(HttpRequest request, HttpResponse response, Chain chain) throws IOException;
}
