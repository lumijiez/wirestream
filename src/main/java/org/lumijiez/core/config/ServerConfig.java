package org.lumijiez.core.config;

public class ServerConfig {
    private final int port;
    private final int keepAliveTimeout;
    private final int maxRequestsPerConnection;
    private final int bufferSize;
    private final int threadPoolSize;

    private ServerConfig(Builder builder) {
        this.port = builder.port;
        this.keepAliveTimeout = builder.keepAliveTimeout;
        this.maxRequestsPerConnection = builder.maxRequestsPerConnection;
        this.bufferSize = builder.bufferSize;
        this.threadPoolSize = builder.threadPoolSize;
    }

    public static class Builder {
        private int port = 8080;
        private int keepAliveTimeout = 30000;
        private int maxRequestsPerConnection = 1000;
        private int bufferSize = 8192;
        private int threadPoolSize = Runtime.getRuntime().availableProcessors() * 2;

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public ServerConfig build() {
            return new ServerConfig(this);
        }
    }
}
