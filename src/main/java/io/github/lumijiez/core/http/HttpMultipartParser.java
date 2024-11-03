package io.github.lumijiez.core.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpMultipartParser {
    private final String boundary;
    private final BufferedReader reader;
    private final Map<String, String> parts = new HashMap<>();

    public HttpMultipartParser(BufferedReader reader, String contentType) {
        this.reader = reader;
        this.boundary = extractBoundary(contentType);
    }

    private String extractBoundary(String contentType) {
        String[] parts = contentType.split(";");
        for (String part : parts) {
            if (part.trim().startsWith("boundary=")) {
                return "--" + part.split("=")[1].trim();
            }
        }
        return null;
    }

    public Map<String, String> parse() throws IOException {
        String line;
        StringBuilder content = new StringBuilder();
        String currentName = null;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith(boundary)) {
                if (currentName != null) {
                    parts.put(currentName, content.toString().trim());
                    content = new StringBuilder();
                }

                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    if (line.toLowerCase().startsWith("content-disposition:")) {
                        currentName = extractFieldName(line);
                    }
                }
            } else if (!line.equals(boundary + "--")) {
                content.append(line).append("\n");
            }
        }

        return parts;
    }

    private String extractFieldName(String header) {
        String[] parts = header.split(";");
        for (String part : parts) {
            if (part.trim().startsWith("name=")) {
                return part.split("=")[1].trim().replace("\"", "");
            }
        }
        return null;
    }
}
