package io.github.lumijiez.core.http;

import io.github.lumijiez.core.logging.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpMultipartParser {
    private final BufferedReader reader;
    private final String boundary;
    private final HttpMultipartData multipartData;
    private static final int MAX_BUFFER_SIZE = 1024 * 1024; // 1MB

    public HttpMultipartParser(BufferedReader reader, String contentType) {
        this.reader = reader;
        this.boundary = "--" + extractBoundary(contentType);
        this.multipartData = new HttpMultipartData();
        Logger.debug("HTTP", "Initialized parser with boundary: " + this.boundary);
    }

    private String extractBoundary(String contentType) {
        String[] parts = contentType.split(";");
        for (String part : parts) {
            part = part.trim();
            if (part.startsWith("boundary=")) {
                String trim = part.substring(("boundary=".length())).trim();
                Logger.debug("HTTP", "Boundary: " + trim);
                return trim;
            }
        }
        return null;
    }

    public HttpMultipartData parse() throws IOException {
        String line;
        StringBuilder currentPart = new StringBuilder();
        Map<String, String> currentHeaders = new HashMap<>();
        boolean isReadingHeaders = false;

        while ((line = reader.readLine()) != null) {
            Logger.debug("HTTP", "Reading line: '" + line + "'");

            line = line.trim();

            if (line.startsWith(boundary + "--")) {
                if (!currentHeaders.isEmpty() && !currentPart.isEmpty()) {
                    processContent(currentHeaders, currentPart.toString());
                }
                Logger.debug("HTTP", "Found end boundary, finishing parse");
                break;
            }

            if (line.startsWith(boundary)) {
                if (!currentHeaders.isEmpty() && !currentPart.isEmpty()) {
                    processContent(currentHeaders, currentPart.toString());
                }
                currentPart.setLength(0);
                currentHeaders.clear();
                isReadingHeaders = true;
                continue;
            }

            if (isReadingHeaders) {
                if (line.isEmpty()) {
                    isReadingHeaders = false;
                    continue;
                }
                int separator = line.indexOf(':');
                if (separator > 0) {
                    String headerName = line.substring(0, separator).trim().toLowerCase();
                    String headerValue = line.substring(separator + 1).trim();
                    currentHeaders.put(headerName, headerValue);
                    Logger.debug("HTTP", "Found header: " + headerName + " = " + headerValue);
                }
            } else {
                currentPart.append(line).append("\r\n");
            }
        }

        return multipartData;
    }

    private void processContent(Map<String, String> headers, String content) {
        String contentDisposition = headers.get("content-disposition");
        if (contentDisposition == null) {
            return;
        }

        Map<String, String> dispositionParams = parseContentDisposition(contentDisposition);
        String name = dispositionParams.get("name");
        String fileName = dispositionParams.get("filename");

        if (fileName != null) {
            String contentType = headers.getOrDefault("content-type", "application/octet-stream");
            if (content.endsWith("\r\n")) {
                content = content.substring(0, content.length() - 2);
            }
            byte[] fileContent = content.getBytes(StandardCharsets.UTF_8);
            HttpFileItem fileItem = new HttpFileItem(fileName, contentType, fileContent);
            multipartData.addFile(name, fileItem);
            Logger.debug("HTTP", "Added file: " + name + ", filename: " + fileName);
        } else {
            multipartData.addField(name, content.trim());
            Logger.debug("HTTP", "Added field: " + name);
        }
    }

    private Map<String, String> parseContentDisposition(String contentDisposition) {
        Map<String, String> params = new HashMap<>();
        String[] parts = contentDisposition.split(";");

        for (String part : parts) {
            part = part.trim();
            if (part.contains("=")) {
                String[] keyValue = part.split("=", 2);
                String key = keyValue[0].trim();
                String value = keyValue[1].trim().replace("\"", "");
                params.put(key, value);
            }
        }

        return params;
    }
}
