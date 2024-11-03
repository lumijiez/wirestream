package io.github.lumijiez.core.http;

public enum HttpContentType {
    TEXT_PLAIN("text/plain"),
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    MULTIPART_FORM_DATA("multipart/form-data"),
    APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded");

    private final String value;

    HttpContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static HttpContentType fromString(String value) {
        for (HttpContentType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return TEXT_PLAIN;
    }
}
