package io.github.lumijiez.core.middleware;

import io.github.lumijiez.core.http.HttpRequest;
import io.github.lumijiez.core.http.HttpResponse;

import java.io.IOException;

public interface Chain {
    void next(HttpRequest request, HttpResponse response) throws IOException;
}