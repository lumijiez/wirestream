package io.github.lumijiez.core.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    private static final Logger instance = new Logger(LogLevel.INFO);

    private LogLevel currentLogLevel;

    private Logger(LogLevel level) {
        this.currentLogLevel = level;
    }

    public static Logger getInstance() {
        return instance;
    }

    public void log(LogLevel level, String source, String message) {
        if (level.ordinal() >= currentLogLevel.ordinal()) {
            String timestamp = LocalDateTime.now().format(formatter);
            System.out.println("[" + timestamp + "][" + level + "][" + source + "] " + message);
        }
    }

    public static void debug(String source, String message) {
        getInstance().log(LogLevel.DEBUG, source, message);
    }

    public static void info(String source, String message) {
        getInstance().log(LogLevel.INFO, source, message);
    }

    public static void warn(String source, String message) {
        getInstance().log(LogLevel.WARN, source, message);
    }

    public static void error(String source, String message) {
        getInstance().log(LogLevel.ERROR, source, message);
    }

    public static void error(String source, String message, Throwable throwable) {
        getInstance().log(LogLevel.ERROR, source, message + ": " + throwable.getMessage());
        // throwable.printStackTrace();
    }

    public void setLogLevel(LogLevel level) {
        this.currentLogLevel = level;
    }

    public LogLevel getLogLevel() {
        return currentLogLevel;
    }
}
