package com.black.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;

    private boolean running = false;

    private Runnable onStartCallback;

    public void setOnStartEvent(Runnable callback){
        this.onStartCallback = callback;
    }

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        running = true;

        if (onStartCallback != null) {
            onStartCallback.run();
        }

        try {
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("A new client has connected");

                    ClientHandler clientHandler = new ClientHandler(socket);
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                } catch (IOException e) {
                    if (!running) {
                        System.out.println("Server stopped accepting clients.");
                        break;
                    } else {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        } catch(Exception e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        running = false;
        new Thread(() -> {
            ClientHandler.disconnectAllClients();
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, "StopServerThread").start();
    }

    public boolean isRunning() {
        return running;
    }

}
