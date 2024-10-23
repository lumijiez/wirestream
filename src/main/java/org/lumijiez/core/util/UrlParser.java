package org.lumijiez.core.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UrlParser {
    private final String path;
    private final Map<String, String> queryParams;
    private final Map<String, String> pathParams;

    public UrlParser(String rawUrl) {
        this.queryParams = new HashMap<>();
        this.pathParams = new HashMap<>();

        String[] urlParts = rawUrl.split("\\?", 2);
        this.path = urlParts[0];

        if (urlParts.length > 1) {
            parseQueryParams(urlParts[1]);
        }
    }

    private void parseQueryParams(String queryString) {
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                queryParams.put(keyValue[0], keyValue[1]);
            } else if (keyValue.length == 1) {
                queryParams.put(keyValue[0], "");
            }
        }
    }

    public boolean matchesPattern(String pattern) {
        String[] patternParts = pattern.split("/");
        String[] pathParts = this.path.split("/");

        if (patternParts.length != pathParts.length) {
            return false;
        }

        for (int i = 0; i < patternParts.length; i++) {
            String patternPart = patternParts[i];
            String pathPart = pathParts[i];

            if (patternPart.startsWith(":")) {
                String paramName = patternPart.substring(1);
                pathParams.put(paramName, pathPart);
            } else if (!patternPart.equals(pathPart)) {
                return false;
            }
        }

        return true;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(String name) {
        return queryParams.get(name);
    }

    public String getPathParam(String name) {
        return pathParams.get(name);
    }

    public Map<String, String> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    public Map<String, String> getPathParams() {
        return Collections.unmodifiableMap(pathParams);
    }
}
