package org.lumijiez.core.middleware;

import org.lumijiez.core.http.HttpRequest;
import org.lumijiez.core.http.HttpResponse;

import java.io.IOException;

public interface Chain {
    void next(HttpRequest request, HttpResponse response) throws IOException;
}