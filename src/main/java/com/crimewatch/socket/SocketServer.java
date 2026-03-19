package com.crimewatch.socket;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketServer {

    private static final Logger log = LoggerFactory.getLogger(SocketServer.class);

    @Value("${socket.server.port}")
    private int port;

    private final List<PrintWriter> clients = new CopyOnWriteArrayList<>();
    private volatile boolean running = true;
    private ServerSocket serverSocket;
    private Thread acceptThread;

    @PostConstruct
    public void start() {
        acceptThread = new Thread(this::runAcceptLoop, "cw-socket-accept");
        acceptThread.setDaemon(true);
        acceptThread.start();
        log.info("Socket server started on port {}", port);
    }

    private void runAcceptLoop() {
        try {
            serverSocket = new ServerSocket(port);
            while (running) {
                Socket client = serverSocket.accept();
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                clients.add(writer);
                log.info("Socket client connected from {}. Total clients: {}",
                         client.getInetAddress(), clients.size());
            }
        } catch (IOException e) {
            if (running) log.error("Socket accept loop error", e);
        }
    }

    List<PrintWriter> getClients() {
        return clients;
    }

    @PreDestroy
    public void stop() {
        running = false;
        clients.forEach(PrintWriter::close);
        clients.clear();
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {}
        log.info("Socket server stopped");
    }
}
