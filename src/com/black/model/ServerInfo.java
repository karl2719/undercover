package com.black.model;

public class ServerInfo {
    private final String serverName;
    private final String address;
    private final int port;
    private long lastSeen;

    public ServerInfo(String serverInfo) {
        String[] parts = serverInfo.split(":");
        this.address = parts[0];
        this.port = Integer.parseInt(parts[1]);
        this.serverName = parts[2];
    }

    public String getServerName() {
        return serverName;
    }
    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getDisplayString() {
        return address;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    @Override
    public String toString() {
        return serverName + " (" + address + ":" + port + ")";
    }
}