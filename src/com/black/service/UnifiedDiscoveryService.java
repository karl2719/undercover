package com.black.service;

import com.black.listeners.ServerLostListener;
import com.black.model.ServerInfo;
import com.black.utils.IpUtilities;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unified Discovery Service that handles both server broadcasting and client discovery
 * using a single shared DatagramSocket.
 */
public class UnifiedDiscoveryService {
    private static UnifiedDiscoveryService instance;
    
    private int broadcastPort = 65300;
    private DatagramSocket receiveSocket;
    private DatagramSocket sendSocket;
    private volatile boolean running = false;
    
    // Server broadcasting state
    private volatile boolean broadcasting = false;
    private String serverAddress;
    private int serverPort;
    private String serverName;
    
    // Constants
    private static final long BROADCAST_INTERVAL_MS = 2000;
    
    // Client discovery state
    private final Map<String, ServerInfo> serverMap = Collections.synchronizedMap(new HashMap<>());
    private static final long SERVER_TIMEOUT_MS = BROADCAST_INTERVAL_MS * 3;
    
    // Callbacks
    private Runnable onServerDiscovered;
    private ServerLostListener onServerLost;
    
    // Threads
    private Thread listenerThread;
    private Thread broadcasterThread;
    private Thread cleanupThread;
    
    private UnifiedDiscoveryService() {
        // Singleton
    }
    
    public static synchronized UnifiedDiscoveryService getInstance() {
        if (instance == null) {
            instance = new UnifiedDiscoveryService();
        }
        return instance;
    }
    
    public void setBroadcastPort(int port) {
        if (!running) {
            this.broadcastPort = port;
        }
    }
    
    public int getBroadcastPort() {
        return broadcastPort;
    }
    
    public void setOnServerDiscovered(Runnable callback) {
        this.onServerDiscovered = callback;
    }
    
    public void setOnServerLost(ServerLostListener listener) {
        this.onServerLost = listener;
    }
    
    public Map<String, ServerInfo> getServerMap() {
        return Map.copyOf(serverMap);
    }
    
    /**
     * Start the unified discovery service
     */
    public synchronized void start() {
        if (running) {
            return;
        }
        
        running = true;
        
        try {
            receiveSocket = new DatagramSocket(broadcastPort);
            receiveSocket.setBroadcast(true);
            receiveSocket.setReuseAddress(true);
            
            sendSocket = new DatagramSocket();
            sendSocket.setBroadcast(true);
            
            // Start listener thread (always active)
            startListener();
            
            // Start cleanup thread
            startCleanup();
            
            System.out.println("Unified Discovery Service started on port " + broadcastPort);
        } catch (SocketException e) {
            System.err.println("Failed to start discovery service: " + e.getMessage());
            running = false;
        }
    }
    
    /**
     * Start broadcasting as a server
     */
    public synchronized void startBroadcasting(String serverAddress, int serverPort, String serverName) {
        // System.out.println("startBroadcasting called with: " + serverAddress + ":" + serverPort + ":" + serverName);
        
        if (!running) {
            // System.out.println("Service not running, starting it now...");
            start();
        }
        
        if (broadcasting) {
            // System.out.println("Already broadcasting, stopping previous broadcast...");
            stopBroadcasting();
        }
        
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.serverName = serverName;
        this.broadcasting = true;
        
        // System.out.println("Starting broadcaster thread...");
        startBroadcaster();
    }
    
    /**
     * Stop broadcasting (but keep listening)
     */
    public synchronized void stopBroadcasting() {
        broadcasting = false;
        if (broadcasterThread != null) {
            broadcasterThread.interrupt();
            broadcasterThread = null;
        }
    }
    
    /**
     * Completely stop the service
     */
    public synchronized void stop() {
        running = false;
        broadcasting = false;
        
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
        if (broadcasterThread != null) {
            broadcasterThread.interrupt();
        }
        if (cleanupThread != null) {
            cleanupThread.interrupt();
        }
        
        if (receiveSocket != null && !receiveSocket.isClosed()) {
            receiveSocket.close();
        }
        
        if (sendSocket != null && !sendSocket.isClosed()) {
            sendSocket.close();
        }
        
        serverMap.clear();
        System.out.println("Unified Discovery Service stopped");
    }
    
    private void startListener() {
        listenerThread = new Thread(() -> {
            byte[] buffer = new byte[256];
            
            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    receiveSocket.receive(packet);
                    
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    
                    boolean added = addServer(msg);
                    
                    // System.out.println(msg);

                    if (added && onServerDiscovered != null) {
                        onServerDiscovered.run();
                    }
                    
                    synchronized (serverMap) {
                        ServerInfo info = serverMap.get(msg);
                        if (info != null) {
                            info.setLastSeen(System.currentTimeMillis());
                        }
                    }
                    
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Listener error: " + e.getMessage());
                    }
                }
            }
        }, "UnifiedDiscovery-Listener");
        listenerThread.start();
    }
    
    private void startBroadcaster() {
        // System.out.println("startBroadcaster method entered");
        broadcasterThread = new Thread(() -> {
            // System.out.println("Broadcaster thread started. broadcasting=" + broadcasting + ", running=" + running);
            String message = serverAddress + ":" + serverPort + ":" + serverName;
            byte[] buffer = message.getBytes();
            
            List<InetAddress> broadcastAddresses = IpUtilities.getBroadcastAddresses();
            // System.out.println("Found " + broadcastAddresses.size() + " broadcast addresses");
            long lastRefresh = System.currentTimeMillis();
            
            while (broadcasting && running) {
                try {
                    long currentTime = System.currentTimeMillis();
                    
                    // Refresh broadcast addresses periodically
                    if (currentTime - lastRefresh > 10000) {
                        broadcastAddresses = IpUtilities.getBroadcastAddresses();
                        lastRefresh = currentTime;
                    }
                    
                    if (broadcastAddresses.isEmpty()) {
                        Thread.sleep(1000);
                        continue;
                    }
                    
                    for (InetAddress broadcastAddress : broadcastAddresses) {
                        try {
                            DatagramPacket packet = new DatagramPacket(
                                buffer, buffer.length, broadcastAddress, broadcastPort);
                            sendSocket.send(packet);
                        } catch (IOException e) {
                            System.err.println("Failed to broadcast to " + 
                                broadcastAddress.getHostAddress() + ": " + e.getMessage());
                        }
                    }
                    
                    Thread.sleep(BROADCAST_INTERVAL_MS);
                    
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "UnifiedDiscovery-Broadcaster");
        broadcasterThread.start();
    }
    
    private void startCleanup() {
        cleanupThread = new Thread(() -> {
            while (running) {
                try {
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
                            if (onServerLost != null) {
                                onServerLost.onServerLost(server);
                            }
                        }
                    }
                    
                    Thread.sleep(1500);
                    
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "UnifiedDiscovery-Cleanup");
        cleanupThread.start();
    }
    
    private boolean addServer(String serverInfo) {
        synchronized (serverMap) {
            if (!serverMap.containsKey(serverInfo)) {
                serverMap.put(serverInfo, new ServerInfo(serverInfo));
                return true;
            }
        }
        return false;
    }
    
    public boolean isBroadcasting() {
        return broadcasting;
    }
    
    public boolean isRunning() {
        return running;
    }

    public boolean containsServer(String serverName){
        synchronized(serverMap){
            for(Map.Entry<String, ServerInfo> entry: serverMap.entrySet()){
                String n = entry.getValue().getServerName();
                if(serverName.equals(n))
                    return true;
            }
        }
        return false;
    }
}
