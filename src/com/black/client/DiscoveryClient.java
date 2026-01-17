package com.black.client;

import com.black.listeners.ServerLostListener;
import com.black.model.ServerInfo;
import com.black.server.DiscoveryServer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated This class is no longer used. Use {@link com.black.service.UnifiedDiscoveryService} instead.
 * The UnifiedDiscoveryService combines both client discovery and server broadcasting functionality.
 */
@Deprecated
public class DiscoveryClient {
    private final int broadcastPort;
    private DatagramSocket socket;
    private volatile boolean running = true;
    
    private final Map<String, ServerInfo> serverMap = Collections.synchronizedMap(new HashMap<>());

    private static final long SERVER_TIMEOUT_MS = DiscoveryServer.BROADCAST_INTERVAL_MS * 3;

    private Runnable onServerDiscovered;
    private ServerLostListener onServerLost;

    public Map<String, ServerInfo> getServerMap(){
        return Map.copyOf(serverMap);
    }

    public void setOnServerDiscovered(Runnable callback) {
        this.onServerDiscovered = callback;
    }

    public void setOnServerLost(ServerLostListener listener) {
        this.onServerLost = listener;
    }

    public DiscoveryClient() {
        this.broadcastPort = 65300;
    }

    public void stop() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        this.running = false;
    }

    private void closeEverything() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        running = false;
    }

    public boolean addServer(String serverInfo) {
        synchronized (serverMap) {
            if (!serverMap.containsKey(serverInfo)) {
                serverMap.put(serverInfo, new ServerInfo(serverInfo));
                return true;
            }
        }
        return false;
    }

    public boolean removeServer(String serverInfo) {
        boolean contains = serverMap.containsKey(serverInfo);
        synchronized (serverMap) {
            serverMap.remove(serverInfo);
        }
        return contains;
    }

    public static String getAddress(String serverInfo) {
        return serverInfo.split(":")[0];
    }

    public static int getPort(String serverInfo) {
        return Integer.parseInt(serverInfo.split(":")[1]);
    }

    public static String getName(String serverInfo) {
        return serverInfo.split(":")[2];
    }

    public void listenForServers() {
        new Thread(() -> {
            try {
                socket = new DatagramSocket(broadcastPort);
                socket.setBroadcast(true);
                socket.setReuseAddress(true);

                byte[] buffer = new byte[256];
                while (running) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String msg = new String(packet.getData(), 0, packet.getLength());

                    boolean added = addServer(msg);

                    if (added && onServerDiscovered != null) {
                        onServerDiscovered.run();
                    }

                    synchronized (serverMap) {
                        serverMap.get(msg).setLastSeen(System.currentTimeMillis());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                System.out.println("The packet received was too large: " + e.getMessage());

            } finally {
                System.out.println("Discovery client stopping...");
                closeEverything();
            }
        }, "DiscoveryClient-ListenerThread").start();

        new Thread(() -> {
            while (running) {
                long currentTime = System.currentTimeMillis();
                List<String> toRemove = new ArrayList<>();

                synchronized (serverMap) {
                    for (Map.Entry<String, ServerInfo> entry : serverMap.entrySet()) {
                        long lastSeen = entry.getValue().getLastSeen();
                        if (currentTime - lastSeen > SERVER_TIMEOUT_MS) {
                            toRemove.add(entry.getKey());
                        }
                    }

                    for (String server : toRemove) {
                        serverMap.remove(server);

                        removeServer(server);
                        if (onServerLost != null) {
                            onServerLost.onServerLost(server);
                        }
                    }
                }

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }, "DiscoveryClient-CleanupThread").start();
    }


}
