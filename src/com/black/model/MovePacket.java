package com.black.model;

import com.black.enums.PacketType;

public class MovePacket extends NetworkPacket {
    private int x, y;
    private String sender;

    public MovePacket(int x, int y, String sender) {
        super(PacketType.MOVE, null);
        this.x = x;
        this.y = y;
        this.sender = sender;
    }

    public String getSender(){
        return sender;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
