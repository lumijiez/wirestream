package io.github.lumijiez.core.http;

import io.github.lumijiez.core.util.UrlParser;
import io.github.lumijiez.core.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private String httpVersion;
    private final Map<String, String> headers;
    private final Map<String, List<String>> queryParams;
    private UrlParser urlParser;
    private HttpRequestBody body;
    private Map<String, String> formData;
    private HttpMultipartData multipartData;
    private final Map<String, String> cookies;

    public HttpRequest(BufferedReader in) throws IOException {
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();
        this.cookies = new HashMap<>();
        parseRequest(in);
        parseCookies();
        if (hasBody()) {
            parseBody(in);
        }
    }

    private void parseRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine == null || requestLine.trim().isEmpty()) {
            throw new IOException("Empty request line");
        }

        String[] tokens = requestLine.split(" ");
        if (tokens.length != 3) {
            throw new IOException("Invalid request line format: " + requestLine);
        }

        this.method = tokens[0].toUpperCase();
        parsePathAndQuery(tokens[1]);
        this.httpVersion = tokens[2];

        String headerLine;
        while ((headerLine = in.readLine()) != null && !headerLine.trim().isEmpty()) {
            int separator = headerLine.indexOf(':');
            if (separator > 0) {
                String key = headerLine.substring(0, separator).trim().toLowerCase();
                String value = headerLine.substring(separator + 1).trim();
                headers.put(key, value);
                Logger.debug("HTTP", "Header: " + key + " = " + value);
            }
        }
    }

    private void parsePathAndQuery(String fullPath) throws IOException {
        int queryStart = fullPath.indexOf('?');
        if (queryStart != -1) {
            this.path = fullPath.substring(0, queryStart);
            parseQueryString(fullPath.substring(queryStart + 1));
        } else {
            this.path = fullPath;
        }

        this.path = URLDecoder.decode(this.path, StandardCharsets.UTF_8);
    }

    private void parseQueryString(String queryString) throws IOException {
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);

                queryParams.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                Logger.debug("HTTP", "Query param: " + key + " = " + value);
            }
        }
    }

    private void parseCookies() {
        String cookieHeader = headers.get("cookie");
        if (cookieHeader != null) {
            String[] cookiePairs = cookieHeader.split(";");
            for (String cookiePair : cookiePairs) {
                String[] parts = cookiePair.trim().split("=", 2);
                if (parts.length == 2) {
                    cookies.put(parts[0], parts[1]);
                    Logger.debug("HTTP", "Cookie: " + parts[0] + " = " + parts[1]);
                }
            }
        }
    }

    private boolean hasBody() {
        if ("GET".equals(method) || "HEAD".equals(method)) {
            return false;
        }

        String contentLengthHeader = headers.get("content-length");
        return contentLengthHeader != null &&
                Integer.parseInt(contentLengthHeader) > 0;
    }

    private void parseBody(BufferedReader in) throws IOException {
        int contentLength = Integer.parseInt(headers.get("content-length"));
        String contentType = headers.getOrDefault("content-type", "text/plain");

        if (contentType.startsWith("multipart/form-data")) {
            Logger.debug("CONTENT TYPE", contentType);
            HttpMultipartParser parser = new HttpMultipartParser(in, contentType);
            this.multipartData = parser.parse();
            Logger.debug("HTTP", "Parsed multipart data with " +
                    multipartData.getFiles().size() + " files and " +
                    multipartData.getFields().size() + " fields");
        } else {
            if ("chunked".equalsIgnoreCase(headers.get("transfer-encoding"))) {
                parseChunkedBody(in);
                return;
            }

            char[] buffer = new char[contentLength];
            int totalRead = 0;
            while (totalRead < contentLength) {
                int read = in.read(buffer, totalRead, contentLength - totalRead);
                if (read == -1) {
                    throw new IOException("Unexpected end of stream");
                }
                totalRead += read;
            }
            String content = new String(buffer);

            if (contentType.startsWith("application/x-www-form-urlencoded")) {
                this.formData = parseUrlEncodedForm(content);
                Logger.debug("HTTP", "Parsed URL encoded form data: " + formData.size() + " fields");
            } else {
                this.body = new HttpRequestBody(content, HttpContentType.fromString(contentType));
                Logger.debug("HTTP", "Parsed body with content type: " + contentType);
            }
        }
    }

    private void parseChunkedBody(BufferedReader in) throws IOException {
        StringBuilder content = new StringBuilder();
        while (true) {
            String chunkSizeLine = in.readLine();
            if (chunkSizeLine == null) {
                throw new IOException("Unexpected end of stream in chunked body");
            }

            int chunkSize = Integer.parseInt(chunkSizeLine.trim(), 16);
            if (chunkSize == 0) {
                break;
            }

            char[] chunk = new char[chunkSize];
            int totalRead = 0;
            while (totalRead < chunkSize) {
                int read = in.read(chunk, totalRead, chunkSize - totalRead);
                if (read == -1) {
                    throw new IOException("Unexpected end of stream in chunk");
                }
                totalRead += read;
            }
            content.append(chunk);

            in.readLine();
        }

        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            // TO DO
            // Trailing headers
            // :/
        }

        String contentType = headers.getOrDefault("content-type", "text/plain");
        this.body = new HttpRequestBody(content.toString(), HttpContentType.fromString(contentType));
    }

    private Map<String, String> parseUrlEncodedForm(String content) {
        Map<String, String> params = new HashMap<>();
        if (content == null || content.trim().isEmpty()) {
            return params;
        }

        String[] pairs = content.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    public HttpMultipartData getMultipartData() {
        return multipartData;
    }

    public HttpRequestBody getBody() {
        return body;
    }

    public Map<String, String> getFormData() {
        return formData != null ? new HashMap<>(formData) : Map.of();
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }

    public Map<String, String> getCookies() {
        return new HashMap<>(cookies);
    }

    public String getQueryParam(String name) {
        List<String> values = queryParams.get(name);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    public List<String> getQueryParams(String name) {
        return queryParams.getOrDefault(name, new ArrayList<>());
    }

    public Map<String, List<String>> getAllQueryParams() {
        return new HashMap<>(queryParams);
    }

    public void setUrlParser(UrlParser urlParser) {
        this.urlParser = urlParser;
    }

    public String getPathParam(String name) {
        return urlParser != null ? urlParser.getPathParam(name) : null;
    }

    public Map<String, String> getPathParams() {
        return urlParser != null ? urlParser.getPathParams() : Map.of();
    }

    public boolean isKeepAlive() {
        String connection = headers.get("connection");
        if ("close".equalsIgnoreCase(connection)) {
            return false;
        }
        return "HTTP/1.1".equals(httpVersion) ||
                "keep-alive".equalsIgnoreCase(connection);
    }
}