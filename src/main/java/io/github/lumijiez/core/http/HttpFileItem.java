package io.github.lumijiez.core.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HttpFileItem {
    private final String fileName;
    private final String contentType;
    private final byte[] content;

    public HttpFileItem(String fileName, String contentType, byte[] content) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void saveTo(File destination) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            fos.write(content);
        }
    }
}
