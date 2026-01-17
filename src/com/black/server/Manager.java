package com.black.server;

import com.black.enums.PacketType;
import com.black.model.MovePacket;
import com.black.model.NetworkPacket;
import java.awt.Point;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Manager {
    private static Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private static Map<String, Point> clientsPosition = new ConcurrentHashMap<>();

    public static void addClient(String username, ClientHandler ch) {
        clients.put(username, ch);
    }

    public static void removeClient(String username) {
        clients.remove(username);
    }

    public static void setClientPosition(String username, Point position) {
        // not working yet, need to debug
        synchronized(clientsPosition){
            clientsPosition.put(username, position);
            System.out.println(clientsPosition.get(username));
        }
    }

    public static void handlePacket(ClientHandler sender, NetworkPacket packet) {
        PacketType type = packet.getType();

        switch (type) {
            case JOIN:
                for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
                    ClientHandler ch = entry.getValue();
                    String username = entry.getKey();
                    Point p = clientsPosition.get(username);
                    if (!sender.equals(ch)) {
                        if(p == null){
                            p = new Point(0,0);
                            clientsPosition.putIfAbsent(username, p);
                        }
                        System.out.println(username + " to " + p);
                        sender.writePacket(new NetworkPacket(type, username));
                        sender.writePacket(new MovePacket(p.x, p.y, username));
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
