package org.lumijiez.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    private LogLevel currentLogLevel;

    public Logger(LogLevel level) {
        this.currentLogLevel = level;
    }

    public void log(LogLevel level, String source, String message) {
        if (level.ordinal() >= currentLogLevel.ordinal()) {
            String timestamp = LocalDateTime.now().format(formatter);
            System.out.println("[" + timestamp + "] [" + level + "] [" + source + "] " + message);
        }
    }

    public void debug(String source, String message) {
        log(LogLevel.DEBUG, source, message);
    }

    public void info(String source, String message) {
        log(LogLevel.INFO, source, message);
    }

    public void warn(String source, String message) {
        log(LogLevel.WARN, source, message);
    }

    public void error(String source, String message) {
        log(LogLevel.ERROR, source, message);
    }

    public void error(String source, String message, Throwable throwable) {
        log(LogLevel.ERROR, source, message + ": " + throwable.getMessage());
        // throwable.printStackTrace();
    }

    public void setLogLevel(LogLevel level) {
        this.currentLogLevel = level;
    }

    public LogLevel getLogLevel() {
        return currentLogLevel;
    }
}
