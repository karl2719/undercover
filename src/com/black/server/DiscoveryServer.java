package com.black.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import com.black.utils.IpUtilities;

/**
 * @deprecated This class is no longer used. Use {@link com.black.service.UnifiedDiscoveryService} instead.
 * The UnifiedDiscoveryService combines both client discovery and server broadcasting functionality.
 */
@Deprecated
public class DiscoveryServer {
    int broadcastPort;
    DatagramSocket socket;

    String serverAddress;
    int serverPort;
    String serverName;

    private volatile boolean running = true;

    public static final long BROADCAST_INTERVAL_MS = 2000;

    private final long BROADCAST_LIST_RR_MS = 10000;

    public DiscoveryServer(String serverAddress, int serverPort, String serverName) {
        this.broadcastPort = 65300;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.serverName = serverName;
    }

    public void start() {
        new Thread(() -> {
            try {
                this.socket = new DatagramSocket();
                socket.setBroadcast(true);
                socket.setReuseAddress(true);
                String message = serverAddress + ":" + serverPort + ":" + serverName;
                byte[] buffer = message.getBytes();

                List<InetAddress> broadcastAddresses = IpUtilities.getBroadcastAddresses();
                long lastRefresh = System.currentTimeMillis();
                while (running) {
                    long currentTime = System.currentTimeMillis();

                    if (currentTime - lastRefresh > BROADCAST_LIST_RR_MS) {
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
                            socket.send(packet);
                            System.out.println("Broadcasting to: " + broadcastAddress.getHostAddress());
                        } catch (IOException e) {
                            System.out.println("Failed to broadcast to " + broadcastAddress.getHostAddress() + ": "
                                    + e.getMessage());
                        }
                    }
                    Thread.sleep(BROADCAST_INTERVAL_MS);
                }

            } catch (SocketException e) {
                System.out.println("Could not start discovery server: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("Discovery server interrupted. Trying again...");
            } finally {
                closeEverything();
            }
        }, "DiscoveryServer-BroadcastThread").start();
    }

    public boolean checkForNameConflict(String nameToCheck) {
        long startingTime = System.currentTimeMillis();
        boolean conflict = false;
 
        try (DatagramSocket socket = new DatagramSocket(broadcastPort)) {
            socket.setBroadcast(true);
            socket.setReuseAddress(true);
            socket.setSoTimeout(3000);

            byte[] buffer = new byte[256];

            while (System.currentTimeMillis() - startingTime < 3000) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength());
                String[] parts = msg.split(":");

                if (parts.length < 3)
                    continue;

                if (msg.startsWith(serverAddress + ":" + serverPort))
                    continue;

                String name = parts[2];
                if (nameToCheck.equals(name)) {
                    conflict = true;
                    break;
                }
            }

        } catch (SocketTimeoutException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }

        return conflict;
    }

    public void stop() {
        running = false;
        new Thread(() -> closeEverything()).start();

    }

    void closeEverything() {
        if (socket != null && !socket.isClosed())
            socket.close();
        running = false;
    }
}
