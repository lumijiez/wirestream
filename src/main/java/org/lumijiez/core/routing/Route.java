package org.lumijiez.core.routing;

import org.lumijiez.core.http.HttpHandler;
import org.lumijiez.core.http.HttpMethod;

public record Route(HttpMethod method, String path, HttpHandler handler) {
}