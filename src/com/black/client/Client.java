package com.black.client;

import com.black.enums.PacketType;
import com.black.listeners.JoinListener;
import com.black.listeners.LeaveListener;
import com.black.listeners.MessageListener;
import com.black.listeners.MoveListener;
import com.black.listeners.SystemListener;
import com.black.model.ChatPacket;
import com.black.model.MovePacket;
import com.black.model.NetworkPacket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;
    private boolean connected = false;

    private Runnable onConnected, onDisconnected;
    private MessageListener messageListener;
    private SystemListener systemMessageListener;
    private MoveListener moveListener;
    private JoinListener joinListener;
    private LeaveListener leaveListener;

    public void setOnJoinListener(JoinListener listener) {
        this.joinListener = listener;
    }

    public void setOnLeaveListener(LeaveListener listener) {
        this.leaveListener = listener;
    }

    public void setOnMoveReceived(MoveListener listener) {
        this.moveListener = listener;
    }

    public void setOnConnected(Runnable callback) {
        this.onConnected = callback;
    }

    public void setOnDisconnected(Runnable callback) {
        this.onDisconnected = callback;
    }

    public void setOnMessageReceived(MessageListener listener) {
        this.messageListener = listener;
    }

    public void setOnSystemMessageReceived(SystemListener listener) {
        this.systemMessageListener = listener;
    }

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());

            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    public void connect() {
        connected = true;
        if (onConnected != null)
            onConnected.run();

        sendUsername();
        listenForPacket();
    }

    public void disconnect() {
        closeEverything(socket, in, out);
    }

    public void sendUsername() {
        NetworkPacket packet = new NetworkPacket(PacketType.JOIN, username);
        try {
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    public void sendPacket(NetworkPacket packet) {
        try {
            if (socket.isConnected()) {
                out.writeObject(packet);
                out.flush();
            }

        } catch (Exception e) {
            closeEverything(socket, in, out);
        }
    }

    private void closeEverything(Socket socket, ObjectInputStream in, ObjectOutputStream out) {

        connected = false;
        if (onDisconnected != null)
            onDisconnected.run();

        try {

            if (out != null) {
                out.flush();
                out.close();
            }

            if (in != null) {
                in.close();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listenForPacket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkPacket packetFromServer;
                try {
                    while (connected) {
                        packetFromServer = (NetworkPacket) in.readObject();
                        System.out.println(packetFromServer);
                        handlePacket(packetFromServer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    closeEverything(socket, in, out);
                }

            }
        }).start();
    }

    private void handlePacket(NetworkPacket packet) {
        PacketType type = packet.getType();
        Object content = packet.getContent();
        System.out.println(packet);
        switch (type) {
            case SYSTEM:
                String sysMessage = (String) content;

                if (systemMessageListener != null) {
                    String notif = sysMessage;
                    if (sysMessage.equalsIgnoreCase("shutdown")) {
                        notif = "The server has been stopped. You have been disconnected.";
                    }
                    systemMessageListener.onMessageReceived(notif);
                }

                if ("shutdown".equalsIgnoreCase(sysMessage)) {
                    connected = false;
                }

                break;

            case JOIN:
                if (joinListener != null){
                    joinListener.onJoin(content.toString());
                }

                if (messageListener != null) {
                    String message = "SERVER: " + content.toString() + " has joined.";
                    messageListener.onMessageReceived(message);
                }
                break;

            case NOTIFICATION:
                if (messageListener != null) {
                    String message = "NOTIFICATION: " + content.toString();
                    messageListener.onMessageReceived(message);
                }
                break;

            case LEAVE:
                if (leaveListener != null)
                    leaveListener.onLeave(username);

                if (messageListener != null) {
                    String message = "SERVER: " + content.toString() + " has left.";
                    messageListener.onMessageReceived(message);
                }
                break;

            case CHAT:
                ChatPacket chatPacket = (ChatPacket) packet;

                if (!shouldDisplayChat(chatPacket))
                    break;

                if (messageListener != null) {
                    String message = chatPacket.getSender() + ": " + chatPacket.getMessage();
                    messageListener.onMessageReceived(message);
                }
                break;

            case MOVE:
                MovePacket movePacket = (MovePacket) packet;
                if (moveListener != null) {
                    moveListener.onMoveReceived(movePacket.getX(), movePacket.getY(), movePacket.getSender());
                }
                break;

            default:
                break;
        }
    }

    private boolean shouldDisplayChat(ChatPacket packet) {
        String target = packet.getTarget();
        String sender = packet.getSender();
        return "all".equalsIgnoreCase(target) || username.equals(sender) || username.equals(target);
    }

    public boolean isConnected() {
        return connected;
    }

    public String getUsername() {
        return username;
    }

}
