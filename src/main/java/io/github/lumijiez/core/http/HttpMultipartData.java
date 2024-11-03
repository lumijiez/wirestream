package io.github.lumijiez.core.http;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpMultipartData {
    private final Map<String, String> fields = new HashMap<>();
    private final Map<String, HttpFileItem> files = new HashMap<>();

    public void addField(String name, String value) {
        fields.put(name, value);
    }

    public void addFile(String name, HttpFileItem file) {
        files.put(name, file);
    }

    public String getField(String name) {
        return fields.get(name);
    }

    public HttpFileItem getFile(String name) {
        return files.get(name);
    }

    public Map<String, String> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    public Map<String, HttpFileItem> getFiles() {
        return Collections.unmodifiableMap(files);
    }
}

