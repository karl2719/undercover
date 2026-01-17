package com.black.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.black.enums.PacketType;
import com.black.model.NetworkPacket;

public class ClientHandler implements Runnable {
    public static List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());
    
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String clientUsername;
    
    private boolean isClosed = false;
    
    
    public String getClientUsername() {
        return clientUsername;
    }
    
    void broadcastPacket(NetworkPacket packet) {
        synchronized (clientHandlers) {
            for (ClientHandler clientHandler : clientHandlers) {
                try {
                    clientHandler.out.writeObject(packet);
                    clientHandler.out.flush();
                } catch (IOException e) {
                    clientHandler.disconnect();
                }
            }
        }
    }
    
    public void writePacket(NetworkPacket packet) {
        if (out == null)
            return;
        try {
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            disconnect();
        }
    }
    
    public void removeClientHandler() {
        synchronized (clientHandlers) {
            clientHandlers.remove(this);
        }
        Manager.removeClient(clientUsername);
        NetworkPacket packet = new NetworkPacket(PacketType.LEAVE, clientUsername);
        broadcastPacket(packet);
    }

    void disconnect() {
        if (isClosed)
            return;
        isClosed = true;
        removeClientHandler();
        closeEverything(socket, in, out);
    }

    public static void disconnectAllClients() {
        List<ClientHandler> copy;
        synchronized (clientHandlers) {
            copy = new ArrayList<>(clientHandlers);
        }

        for (ClientHandler ch : copy) {
            new Thread(() -> {

                ch.broadcastPacket(new NetworkPacket(PacketType.SYSTEM, "SHUTDOWN"));
                ch.disconnect();
            }, "deconnectAllThread").start();
        }
    }

    public static boolean clientExist(String username) {
        synchronized (clientHandlers) {
            return clientHandlers.stream()
                    .anyMatch(
                            ch -> username.equals(ch.clientUsername));
        }
    }

    public ClientHandler(Socket _socket) {
        try {
            this.socket = _socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            disconnect();
        }
    }

    private void closeEverything(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        try {
            if (out != null) {
                out.flush();
                out.close();
            }

            if (in != null)
                in.close();

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            NetworkPacket packet = (NetworkPacket) in.readObject();

            if (!isValidInput(packet)) {
                disconnect();
                return;
            }

            String requestedUsername = ((String) packet.getContent()).strip();

            synchronized (clientHandlers) {
                if (clientExist(requestedUsername)) {
                    writePacket(new NetworkPacket(PacketType.SYSTEM, "ERR|USERNAME_TAKEN"));
                    closeEverything(socket, in, out);
                    return;
                }

                this.clientUsername = requestedUsername;
                clientHandlers.add(this);
                Manager.addClient(requestedUsername, this);
            }
            Manager.handlePacket(this, packet);
            NetworkPacket packetFromClient;
            try {
                while (!isClosed) {
                    packetFromClient = (NetworkPacket) in.readObject();
                    Manager.handlePacket(this, packetFromClient);
                }
            } catch (IOException e) {

            } finally {
                disconnect();
            }

        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    boolean isValidInput(NetworkPacket packet) {
        if (packet.getType() != PacketType.JOIN) {
            return false;
        }

        if (!(packet.getContent() instanceof String)) {
            return false;
        }

        String requestedUsername = ((String) packet.getContent()).strip();

        if (requestedUsername.isEmpty()) {
            return false;
        }
        return true;
    }
}
