package com.black.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.black.utils.IpUtilities;

public class DiscoveryServer {
    int broadcastPort;
    DatagramSocket socket;

    String serverAddress;
    int serverPort;

    private volatile boolean running = true;

    public static final long BROADCAST_INTERVAL_MS = 2000;

    private final long BROADCAST_LIST_RR_MS = 10000;

    public DiscoveryServer(String serverAddress, int serverPort) {
        this.broadcastPort = 65300;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public void start() {
        new Thread(() -> {
            try {
                this.socket = new DatagramSocket();
                socket.setBroadcast(true);
                socket.setReuseAddress(true);
                String message = serverAddress + ":" + serverPort;
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
            } catch (IOException e) {
                System.out.println("Error during broadcasting: " + e.getMessage());

            } catch (InterruptedException e) {
                System.out.println("Discovery server interrupted. Trying again...");
            } finally {
                closeEverything();
            }
        }, "DiscoveryServer-BroadcastThread").start();
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
