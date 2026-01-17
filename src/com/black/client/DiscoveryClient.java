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

public class DiscoveryClient {
    private final int broadcastPort;
    private DatagramSocket socket;
    private volatile boolean running = true;

    public final List<String> discoveredServers = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, Long> serverLastSeen = Collections.synchronizedMap(new HashMap<>());
    
    private final Map<String, ServerInfo> serverMap = Collections.synchronizedMap(new HashMap<>());

    private static final long SERVER_TIMEOUT_MS = DiscoveryServer.BROADCAST_INTERVAL_MS * 3;

    private Runnable onServerDiscovered;
    private ServerLostListener onServerLost;

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
        synchronized (discoveredServers) {
            if (!discoveredServers.contains(serverInfo)) {
                return discoveredServers.add(serverInfo);
            }
        }
        return false;
    }

    public boolean removeServer(String serverInfo) {
        synchronized (discoveredServers) {
            return discoveredServers.remove(serverInfo);
        }
    }

    public static String getAddress(String serverInfo) {
        return serverInfo.split(":")[0];
    }

    public static int getPort(String serverInfo) {
        return Integer.parseInt(serverInfo.split(":")[1]);
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

                    synchronized (serverLastSeen) {
                        serverLastSeen.put(msg, System.currentTimeMillis());
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

                synchronized (serverLastSeen) {
                    for (Map.Entry<String, Long> entry : serverLastSeen.entrySet()) {
                        if (currentTime - entry.getValue() > SERVER_TIMEOUT_MS) {
                            toRemove.add(entry.getKey());
                        }
                    }

                    for (String server : toRemove) {
                        serverLastSeen.remove(server);
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
