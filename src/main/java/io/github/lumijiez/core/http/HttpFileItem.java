package io.github.lumijiez.core.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public record HttpFileItem(String fileName, String contentType, byte[] content) {

    public void saveTo(File destination) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            fos.write(content);
        }
    }
}
