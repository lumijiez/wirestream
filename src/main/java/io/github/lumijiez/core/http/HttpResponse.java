package io.github.lumijiez.core.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.github.lumijiez.core.logging.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final BufferedWriter out;
    private final Map<String, String> headers;
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();

    public HttpResponse(BufferedWriter out) {
        this.out = out;
        this.headers = new HashMap<>();
        headers.put("Connection", "keep-alive");
        headers.put("Keep-Alive", "timeout=30");
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void sendResponse(HttpStatus status, String message) throws IOException {
        sendResponse(status, message, HttpContentType.TEXT_PLAIN);
    }

    public void sendResponse(HttpStatus status, String message, HttpContentType contentType) throws IOException {
        Logger.info("HTTP", "Outgoing: " + status.getCode() + " " + message);

        writeStatusLine(status);
        writeHeaders(contentType, message.getBytes(StandardCharsets.UTF_8).length);
        out.write("\r\n");
        out.write(message);
        out.flush();
    }

    public void sendJson(HttpStatus status, Object obj) throws IOException {
        String jsonContent = jsonMapper.writeValueAsString(obj);
        sendResponse(status, jsonContent, HttpContentType.APPLICATION_JSON);
    }

    public void sendXml(HttpStatus status, Object obj) throws IOException {
        String xmlContent = xmlMapper.writeValueAsString(obj);
        sendResponse(status, xmlContent, HttpContentType.APPLICATION_XML);
    }

    private void writeStatusLine(HttpStatus status) throws IOException {
        out.write("HTTP/1.1 " + status.getCode() + " " + status.getMessage() + "\r\n");
    }

    private void writeHeaders(HttpContentType contentType, int contentLength) throws IOException {
        headers.put("Content-Type", contentType.getValue());
        headers.put("Content-Length", String.valueOf(contentLength));

        for (Map.Entry<String, String> header : headers.entrySet()) {
            out.write(header.getKey() + ": " + header.getValue() + "\r\n");
        }
    }
}
