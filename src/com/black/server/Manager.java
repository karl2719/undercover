package com.black.server;

import com.black.enums.PacketType;
import com.black.model.NetworkPacket;
import java.awt.Point;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Manager {
    private static Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

    public static void addClient(String username, ClientHandler ch) {
        clients.put(username, ch);
    }

    public static void removeClient(String username) {
        clients.remove(username);
    }

    public static void handlePacket(ClientHandler sender, NetworkPacket packet) {
        PacketType type = packet.getType();

        switch (type) {
            case JOIN:
                for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
                    ClientHandler ch = entry.getValue();
                    String username = entry.getKey();
                    if (!sender.equals(ch)) {
                        sender.writePacket(new NetworkPacket(type, username));
                    }
                }

                sender.broadcastPacket(packet);
                break;

            case CHAT:
                sender.broadcastPacket(packet);
                break;

            default:
                sender.broadcastPacket(packet);
                break;
        }
    }
}
