package io.github.lumijiez.core.routing;

import io.github.lumijiez.core.http.HttpHandler;
import io.github.lumijiez.core.http.HttpMethod;

public record Route(HttpMethod method, String path, HttpHandler handler) { }