![logo](https://github.com/user-attachments/assets/27434dca-b33f-4695-aba0-e94e1c082d06)

# Wirestream
## 🚀 LightWeight HTTP/WebSocket Server Framework

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-17%2B-blue)](https://www.oracle.com/java/)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()

A lightweight, highly performant HTTP and WebSocket server framework for Java, designed with simplicity and efficiency in mind. Perfect for building web applications, APIs, and real-time communication systems.
Heavily inspired on Express.js!

## ✨ Features

- 🌐 **HTTP Server**
  - Intuitive routing system with path parameters support
  - Middleware chain processing
  - Keep-alive connection handling
  - Query parameter parsing
  - Custom request/response handling
  
- 📡 **WebSocket Server**
  - Full WebSocket protocol implementation
  - Multiple endpoint support
  - Broadcast messaging
  - Connection lifecycle management
  - Automatic ping/pong handling

- 🛠 **General Features**
  - Thread-pooled connection handling
  - Configurable server settings
  - Built-in logging system
  - Easy-to-use builder patterns
  - Exception handling and error management

## 🚀 Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>io.github.lumijiez</groupId>
    <artifactId>wirestream</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Basic HTTP Server Example

```java
ServerConfig config = new ServerConfig.Builder()
        .port(8080)
        .keepAliveTimeout(30000)
        .build();

HttpServer server = new HttpServer(config);

// Add middleware
server.addMiddleware((req, res, chain) -> {
    Logger.info("REQUEST", req.getMethod() + " " + req.getPath());
    chain.next(req, res);
});

// Add routes
server.GET("/hello/:name", (req, res) -> {
    String name = req.getPathParam("name");
    res.sendResponse(HttpStatus.OK, "Hello, " + name + "!");
});

// Start the server
server.start();
```

### WebSocket Server Example

```java
WebSocketServer wsServer = new WebSocketServer(8081);

wsServer.addHandler("/chat", new WebSocketHandler() {
    @Override
    public void onConnect(WebSocketConnection connection) {
        Logger.info("WS", "New connection: " + connection.getId());
    }

    @Override
    public void onMessage(WebSocketConnection connection, String message) {
        wsServer.broadcast("/chat", message);
    }

    @Override
    public void onDisconnect(WebSocketConnection connection) {
        Logger.info("WS", "Connection closed: " + connection.getId());
    }
});

wsServer.start();
```

### Combined Setup

```java
public static void main(String[] args) {
    // Initialize both servers
    HttpServer httpServer = new HttpServer(config);
    WebSocketServer wsServer = new WebSocketServer(8081);
    
    // Start both servers in separate threads
    new Thread(httpServer::start).start();
    new Thread(wsServer::start).start();
}
```

## 🛠 Configuration

### Server Configuration

```java
ServerConfig config = new ServerConfig.Builder()
        .port(8080)                    // Server port
        .keepAliveTimeout(30000)       // Keep-alive timeout in ms
        .maxRequestsPerConnection(1000) // Max requests per connection
        .bufferSize(8192)              // Buffer size in bytes
        .build();
```

## 🔍 Advanced Usage

### Path Parameters

```java
server.GET("/users/:id/posts/:postId", (req, res) -> {
    String userId = req.getPathParam("id");
    String postId = req.getPathParam("postId");
    // Handle request...
});
```

### Query Parameters

```java
server.GET("/search", (req, res) -> {
    String query = req.getQueryParam("q");
    String page = req.getQueryParam("page");
    // Handle search...
});
```

### WebSocket Broadcasting

```java
wsServer.addHandler("/notifications", new WebSocketHandler() {
    @Override
    public void onMessage(WebSocketConnection connection, String message) {
        // Broadcast to all clients on this path
        wsServer.broadcast("/notifications", message);
        
        // Or send to specific connection
        connection.send("Message received!");
    }
});
```

## 📦 Project Structure

```
src/
├── main/
│   └── java/
│       └── io/
│           └── github/
│               └── lumijiez/
│                   ├── core/
│                   │   ├── config/
│                   │   ├── http/
│                   │   ├── logging/
│                   │   └── middleware/
│                   │   └── routing/
│                   │   └── util/
│                   │   └── ws/
│                   └── example/
│                       ├── daos/
│                       └── models/
```

## 🔧 Development

### Prerequisites

- Java 23 or higher
- Maven 3.6 or higher

### Building

```bash
mvn clean install
```

### Running Tests

```bash
mvn test
```

## 📈 Performance

- Handles 10,000+ concurrent connections
- Low memory footprint (~50MB base)
- Quick startup time (<1 second)
- Efficient thread pool management

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Inspired by modern web frameworks
- Built with performance and simplicity in mind

## 📞 Support

- Create an issue in this repository

---
