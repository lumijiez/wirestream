package io.github.lumijiez.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadedWriter {
    private final String filePath;
    private final Lock lock;

    public ThreadedWriter(String fileName) {
        String resourcesPath = System.getProperty("user.dir") + "/resources";
        File resourcesDir = new File(resourcesPath);
        if (!resourcesDir.exists()) {
            resourcesDir.mkdirs();
        }
        this.filePath = resourcesPath + "/" + fileName;
        this.lock = new ReentrantLock();
        createFileIfNotExist();
    }

    private void createFileIfNotExist() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                Files.createDirectories(Paths.get(file.getParent()));
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Failed to create file: " + e.getMessage());
            }
        }
    }

    public void writeToFile(String content) {
        lock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(content);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        } finally {
            lock.unlock();
        }
    }
}
