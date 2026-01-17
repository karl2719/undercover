package com.black.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.black.client.Client;
import com.black.client.DiscoveryClient;
import com.black.interfaces.IConnectionHandler;
import com.black.model.ChatPacket;
import com.black.model.ServerInfo;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientFrame extends JFrame implements IConnectionHandler {
    // UI Components
    private JPanel leftPanel;
    private GamePanel gamePanel;
    private JLabel errorMessageLabel;
    
    // Panel references for accessing components
    private ChatPanel chatPanel;
    private ServerListPanel serverListPanel;
    
    // Network components
    private Client client;
    private DiscoveryClient discoveryClient;
    private Thread clientThread;

    public ClientFrame() {
        super();
        this.setTitle("Network Manager");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Modern UI settings
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default
        }
        
        this.setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(900, 600));
        
        // Gradient background
        JPanel contentPane = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(30, 32, 38),
                    0, getHeight(), new Color(42, 45, 52)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        this.setContentPane(contentPane);
        
        // Main content area
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15, 15, 5, 15);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3f;
        gbc.weighty = 1.0;

        leftPanel = createLeftPanel();
        mainPanel.add(leftPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7f;

        gamePanel = new GamePanel();
        mainPanel.add(gamePanel, gbc);
        
        contentPane.add(mainPanel, gbc);
        
        // Error message at the bottom (always visible)
        errorMessageLabel = new JLabel(" ");
        errorMessageLabel.setForeground(new Color(255, 160, 100));
        errorMessageLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorMessageLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
        errorMessageLabel.setOpaque(true);
        errorMessageLabel.setBackground(new Color(40, 44, 52));
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(errorMessageLabel, gbc);
        
        // Initialize discovery client
        this.discoveryClient = new DiscoveryClient();
        setDiscoveryClientListeners();
        startDiscoveryClient();
        
        this.pack();
    }

    private JPanel createLeftPanel() {
        CardLayout cl = new CardLayout();
        JPanel panel = new JPanel(cl);
        panel.setOpaque(false);

        // Create panel instances
        ConnectionTypePanel connectionTypePanel = new ConnectionTypePanel(this);
        DirectConnectionPanel directConnectionPanel = new DirectConnectionPanel(this);
        chatPanel = new ChatPanel(this);
        serverListPanel = new ServerListPanel(this);

        // Add panels to card layout
        panel.add(connectionTypePanel, "connectionType");
        panel.add(directConnectionPanel, "connection");
        panel.add(chatPanel, "action");
        panel.add(serverListPanel, "listServer");

        cl.show(panel, "connectionType");
        return panel;
    }
    
    // ========== ConnectionHandler Interface Implementation ==========
    
    @Override
    public void onDirectJoinRequested(String ipAddress, int port, String username) {
        if (client != null)
            return;

        clientThread = new Thread(() -> {
            try {
                Socket socket = new Socket(ipAddress, port);
                client = new Client(socket, username);
                setClientListeners();
                client.connect();
            } catch (UnknownHostException e) {
                showError("Ip address or Host could not be determined.");
            } catch (IllegalArgumentException e) {
                showError("Port out of range.");
            } catch (IOException e) {
                showError("An unexpected network error occurred while connecting to the server.");
            }
        }, "ClientConnectionThread");

        clientThread.start();
    }
    
    @Override
    public void onDiscoveredServerJoinRequested(String username) {
        if (client != null)
            return;

        // Get server info from discovered servers
        ServerInfo serverInfo = (ServerInfo) serverListPanel.getServerComboBox().getSelectedItem();
        if (serverInfo == null) {
            showError("No server selected.");
            return;
        }

        String address = serverInfo.getAddress();
        int portNumber = serverInfo.getPort();

        clientThread = new Thread(() -> {
            try {
                Socket socket = new Socket(address, portNumber);
                client = new Client(socket, username);
                setClientListeners();
                client.connect();
            } catch (UnknownHostException e) {
                showError("Ip address or Host could not be determined.");
            } catch (IllegalArgumentException e) {
                showError("Port out of range.");
            } catch (IOException e) {
                showError("An unexpected network error occurred while connecting to the server.");
            }
        }, "ClientConnectionThread");

        clientThread.start();
    }
    
    @Override
    public void onLeaveRequested() {
        if (client != null) {
            new Thread(() -> client.disconnect()).start();
        }
    }
    
    @Override
    public void onSendMessageRequested() {
        if (client == null) {
            System.out.println("Connect to the server before sending a message.");
            return;
        }
        
        String target = chatPanel.getTargetComboBox().getSelectedItem().toString().strip();
        String message = chatPanel.getMessageArea().getText().strip();

        if (message.isEmpty()) {
            showError("Message can't be empty.");
            return;
        }

        ChatPacket chatPacket = new ChatPacket(message, client.getUsername(), target);
        client.sendPacket(chatPacket);
        chatPanel.getMessageArea().setText("");
        clearError();
    }
    
    @Override
    public void onPanelSwitchRequested(String panelName) {
        CardLayout cl = (CardLayout) leftPanel.getLayout();
        cl.show(leftPanel, panelName);
    }
    
    @Override
    public void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            errorMessageLabel.setText(message);
        });
    }
    
    @Override
    public void clearError() {
        showError(" ");
    }
    
    // ========== Client Event Listeners ==========

    // ========== Client Event Listeners ==========
    
    private void setClientListeners() {
        if (client == null)
            return;

        client.setOnConnected(() -> {
            gamePanel.setClient(client);
            onPanelSwitchRequested("action");
        });

        client.setOnDisconnected(() -> {
            onPanelSwitchRequested("connection");
            client = null;
        });

        client.setOnMessageReceived(message -> {
            SwingUtilities.invokeLater(() -> {
                chatPanel.getChatArea().append(message + "\n");
            });
        });

        client.setOnSystemMessageReceived(message -> {
            showError(message);
        });

        client.setOnMoveReceived((x, y, sender) -> {
            // Movement handling removed - GamePanel redesigned for Undercover game
        });

        client.setOnJoinListener(uName -> {
            // Player join handling - can be used for game logic
            if (uName.equals(client.getUsername()))
                return;

            SwingUtilities.invokeLater(() -> {
                chatPanel.getTargetComboBox().addItem(uName);
            });
        });

        client.setOnLeaveListener(uName -> {
            // Player leave handling - can be used for game logic
            chatPanel.getTargetComboBox().removeItem(uName);
        });
    }

    private void setDiscoveryClientListeners() {
        if (discoveryClient == null)
            return;

        discoveryClient.setOnServerDiscovered(() -> {
            System.out.println("New server discovered!");
            updateDiscoveredServersList();
        });

        discoveryClient.setOnServerLost(serverInfo -> {
            System.out.println("Server lost: " + serverInfo);
            updateDiscoveredServersList();
        });
    }

    private void updateDiscoveredServersList() {
        SwingUtilities.invokeLater(() -> {
            serverListPanel.getServerComboBox().removeAllItems();

            for (String serverInfo : discoveryClient.discoveredServers) {
                ServerInfo si = new ServerInfo(serverInfo);
                serverListPanel.getServerComboBox().addItem(si);
            }
        });
    }

    private void startDiscoveryClient() {
        discoveryClient.listenForServers();
    }

    private void handleMove(int x, int y, String sender) {
        // Movement handling removed - GamePanel redesigned for Undercover game
    }
}
