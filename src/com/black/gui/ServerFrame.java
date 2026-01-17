package com.black.gui;

import com.black.server.DiscoveryServer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.black.server.Server;
import com.black.utils.IpUtilities;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.text.NumberFormat;

public class ServerFrame extends JFrame {
    JLabel serverIpAddress;
    JFormattedTextField port;
    JButton startServer, stopServer;
    JPanel panel;

    Server server;
    DiscoveryServer discoveryServer;
    JLabel errorMessage;

    int portNumber;
    String hostAddress;

    Thread serverThread;

    public ServerFrame() {
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
        
        // Gradient background panel
        JPanel backgroundPanel = new JPanel(new GridBagLayout()) {
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

        panel = createPanel();
        backgroundPanel.add(panel);
        this.add(backgroundPanel, BorderLayout.CENTER);

        this.setSize(450, 280);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }

    public JPanel createPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, 12, 12);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 12, 12);
                
                // Draw dark card background
                g2d.setColor(new Color(48, 52, 60));
                g2d.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 12, 12);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));
        
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        serverIpAddress = new JLabel("\u25CF Host: xx.xx.xx.xx");
        serverIpAddress.setFont(new Font("Segoe UI", Font.BOLD, 14));
        serverIpAddress.setForeground(new Color(220, 220, 230));
        serverIpAddress.setOpaque(true);
        serverIpAddress.setBackground(new Color(38, 42, 48));
        serverIpAddress.setBorder(new EmptyBorder(10, 15, 10, 15));
        panel.add(serverIpAddress, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 1;

        NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setGroupingUsed(false);
        port = new JFormattedTextField(nf);
        port.setColumns(16);
        port.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        port.setBackground(new Color(38, 42, 48));
        port.setForeground(new Color(220, 220, 230));
        port.setCaretColor(new Color(180, 220, 140));
        port.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 75, 85), 1, true),
                BorderFactory.createLineBorder(new Color(55, 60, 68), 2)
            ),
            new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel portLabel = new JLabel("Port: ");
        portLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        portLabel.setForeground(new Color(200, 200, 210));
        panel.add(portLabel, gbc);
        gbc.gridx = 1;
        panel.add(port, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        startServer = new JButton("Host");
        styleButton(startServer, new Color(130, 180, 100), new Color(30, 35, 40));
        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                host();
            }
        });

        stopServer = new JButton("Stop");
        styleButton(stopServer, new Color(220, 150, 70), new Color(30, 35, 40));
        stopServer.setEnabled(false);
        stopServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stop();
            }
        });

        panel.add(startServer, gbc);
        gbc.gridx = 1;
        panel.add(stopServer, gbc);

        errorMessage = new JLabel();
        errorMessage.setForeground(new Color(255, 160, 100));
        errorMessage.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(errorMessage, gbc);

        return panel;
    }
    
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(12, 24, 12, 24));
        
        // Custom painting for gradient and shadow
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                JButton btn = (JButton) c;
                if (btn.isEnabled()) {
                    // Shadow
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fillRoundRect(2, 3, c.getWidth() - 2, c.getHeight() - 2, 8, 8);
                    
                    // Gradient background
                    Color color1 = btn.getBackground();
                    Color color2 = color1.darker();
                    if (btn.getModel().isPressed()) {
                        color2 = color1;
                        color1 = color1.darker();
                    } else if (btn.getModel().isRollover()) {
                        color1 = color1.brighter();
                    }
                    
                    GradientPaint gradient = new GradientPaint(
                        0, 0, color1,
                        0, c.getHeight(), color2
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(0, 0, c.getWidth() - 3, c.getHeight() - 3, 8, 8);
                } else {
                    g2d.setColor(new Color(200, 200, 210));
                    g2d.fillRoundRect(0, 0, c.getWidth() - 3, c.getHeight() - 3, 8, 8);
                }
                
                super.paint(g, c);
            }
        });
    }

    void updateButton() {
        if (server == null) {
            startServer.setEnabled(true);
            stopServer.setEnabled(false);
            return;
        }

        boolean running = server.isRunning();
        startServer.setEnabled(!running);
        stopServer.setEnabled(running);
    }

    public void host() {
        if (server != null && server.isRunning())
            return;

        Number number = (Number) port.getValue();

        if (number == null) {
            setErrorMessage("Port can't be empty.");
            return;
        }

        portNumber = number.intValue();
        try {
            hostAddress = IpUtilities.getLocalIp();
            SwingUtilities.invokeLater(() -> {
                serverIpAddress.setText("Host: " + hostAddress + " port: " + portNumber);
            });
            clearErrorMessage();
        } catch (IOException e1) {
            SwingUtilities.invokeLater(() -> {
                serverIpAddress.setText("Host: xx.xx.xx.xx");
            });
            setErrorMessage("Could not determine local IP address.");
            return;
        }

        serverThread = new Thread(() -> {

            try {
                ServerSocket serverSocket = new ServerSocket(portNumber);
                server = new Server(serverSocket);
                server.setOnStartEvent(() -> SwingUtilities.invokeLater(() -> updateButton()));
                server.startServer();

            } catch (IllegalArgumentException e) {
                SwingUtilities.invokeLater(() -> {
                    serverIpAddress.setText("Host: xx.xx.xx.xx");
                });
                setErrorMessage("Port value out of range.");
            } catch (BindException e) {
                SwingUtilities.invokeLater(() -> {
                    serverIpAddress.setText("Host: xx.xx.xx.xx");
                });
                setErrorMessage("Port already in use.");
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    serverIpAddress.setText("Host: xx.xx.xx.xx");
                });
                setErrorMessage("An unexpected network error occurred while starting the server.");
            }

        });
        serverThread.start();

        System.out.println("Starting discovery server at " + hostAddress + ":" + portNumber);
        discoveryServer = new DiscoveryServer(hostAddress, portNumber);
        discoveryServer.start();
    }

    public void setHostAddress(String address) {
        this.hostAddress = address;
    }

    public void setPortNumber(int port) {
        this.portNumber = port;
    }

    public void stop() {
        if (server == null || !server.isRunning())
            return;

        server.closeServerSocket();

        discoveryServer.stop();

        SwingUtilities.invokeLater(
                () -> {
                    updateButton();
                    serverIpAddress.setText("Host: xx.xx.xx.xx");
                });
    }

    public void clearErrorMessage() {
        setErrorMessage("");
    }

    public void setErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            errorMessage.setText(message);
        });
    }
}
