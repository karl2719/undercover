package com.black.gui;

import com.black.service.UnifiedDiscoveryService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel currentPanel;
    private ServerFrame serverFrame;
    private ClientFrame clientFrame;
    private UnifiedDiscoveryService discoveryService;
    
    private JButton configPortButton;
    
    public MainFrame() {
        super("Undercover - Network Manager");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Modern UI settings
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default
        }
        
        discoveryService = UnifiedDiscoveryService.getInstance();
        
        // Start discovery service immediately to listen for broadcasts
        discoveryService.start();
        
        this.setLayout(new BorderLayout());
        showModeSelector();
        
        this.setSize(500, 400);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }
    
    private void showModeSelector() {
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
        
        JPanel panel = createModeSelectorPanel();
        backgroundPanel.add(panel);
        
        getContentPane().removeAll();
        getContentPane().add(backgroundPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    private JPanel createModeSelectorPanel() {
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
        panel.setBorder(new EmptyBorder(40, 50, 40, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        
        // Title
        JLabel titleLabel = new JLabel("Undercover", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(180, 220, 140));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 30, 10);
        panel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Select Mode", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(200, 200, 210));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 20, 10);
        panel.add(subtitleLabel, gbc);
        
        // Host button
        JButton hostButton = new JButton("Host Server");
        styleButton(hostButton, new Color(130, 180, 100), new Color(30, 35, 40));
        hostButton.addActionListener(e -> showHostMode());
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(hostButton, gbc);
        
        // Join button
        JButton joinButton = new JButton("Join Game");
        styleButton(joinButton, new Color(100, 150, 200), new Color(30, 35, 40));
        joinButton.addActionListener(e -> showJoinMode());
        gbc.gridy = 3;
        panel.add(joinButton, gbc);
        
        // Configure port button
        configPortButton = new JButton("Configure Port (" + discoveryService.getBroadcastPort() + ")");
        styleButton(configPortButton, new Color(120, 120, 140), new Color(30, 35, 40));
        configPortButton.addActionListener(e -> showPortConfig());
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 10, 0, 10);
        panel.add(configPortButton, gbc);
        
        return panel;
    }
    
    private void showHostMode() {
        if (serverFrame == null) {
            serverFrame = new ServerFrame(this);
        }
        
        getContentPane().removeAll();
        getContentPane().add(serverFrame, BorderLayout.CENTER);
        setSize(450, 420);
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }
    
    private void showJoinMode() {
        if (clientFrame == null) {
            clientFrame = new ClientFrame(this);
        }
        
        getContentPane().removeAll();
        getContentPane().add(clientFrame, BorderLayout.CENTER);
        setSize(900, 600);
        setLocationRelativeTo(null);
        revalidate();
        repaint();
    }
    
    public void returnToModeSelector() {
        setSize(500, 400);
        setLocationRelativeTo(null);
        showModeSelector();
    }
    
    private void showPortConfig() {
        String input = JOptionPane.showInputDialog(
            this,
            "Enter discovery port (1024-65535):",
            discoveryService.getBroadcastPort()
        );
        
        if (input != null) {
            try {
                int port = Integer.parseInt(input.trim());
                if (port < 1024 || port > 65535) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Port must be between 1024 and 65535",
                        "Invalid Port",
                        JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                
                discoveryService.setBroadcastPort(port);
                configPortButton.setText("Configure Port (" + port + ")");
                JOptionPane.showMessageDialog(
                    this,
                    "Port set to " + port + "\nChanges will take effect on next connection.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid port number",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(14, 30, 14, 30));
        
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
                    g2d.setColor(new Color(80, 80, 90));
                    g2d.fillRoundRect(0, 0, c.getWidth() - 3, c.getHeight() - 3, 8, 8);
                }
                
                super.paint(g, c);
            }
        });
    }
    
    public void updatePortConfigButton(boolean enabled) {
        if (configPortButton != null) {
            configPortButton.setEnabled(enabled);
        }
    }
}
