package org.lumijiez.core.middleware;

import org.lumijiez.core.http.HttpRequest;
import org.lumijiez.core.http.HttpResponse;

import java.io.IOException;

public interface Middleware {
    void process(HttpRequest request, HttpResponse response, Chain chain) throws IOException;
}
