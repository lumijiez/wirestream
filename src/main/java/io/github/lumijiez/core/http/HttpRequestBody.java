package io.github.lumijiez.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;

public class HttpRequestBody {
    private final String rawContent;
    private final HttpContentType contentType;
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();

    public HttpRequestBody(String rawContent, HttpContentType contentType) {
        this.rawContent = rawContent;
        this.contentType = contentType;
    }

    public <T> T parseAs(Class<T> clazz) throws IOException {
        return switch (contentType) {
            case APPLICATION_JSON -> jsonMapper.readValue(rawContent, clazz);
            case APPLICATION_XML -> xmlMapper.readValue(rawContent, clazz);
            default -> throw new IOException("Unsupported content type for parsing: " + contentType);
        };
    }

    public String getRawContent() {
        return rawContent;
    }

    public HttpContentType getContentType() {
        return contentType;
    }
}
