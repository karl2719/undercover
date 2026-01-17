package com.black.model;

import com.black.enums.PacketType;

public class ChatPacket extends NetworkPacket {
    private final String sender;
    private final String target;

    public ChatPacket(String content, String sender, String target) {
        super(PacketType.CHAT, content);
        if (content == null || sender == null || target == null || sender.isEmpty() || target.isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.sender = sender;
        this.target = target;
    }

    public String getMessage() {
        return (String) content;
    }

    public String getTarget() {
        return target;
    }

    public String getSender() {
        return sender;
    }
}
