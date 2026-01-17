package com.black.model;

import java.io.Serializable;

import com.black.enums.PacketType;

public class NetworkPacket implements Serializable{
    private static final long serialVersionUID = 1L;

    private PacketType type;
    protected Object content;

    public NetworkPacket(PacketType type, Object content) {
        this.type = type;
        this.content = content;
    }

    public PacketType getType() {
        return type;
    }

    public Object getContent() {
        return content;
    }

    @Override
    public String toString(){
        return "Packet: {" + type + ", " + content+ "}";
    }
}
