package com.black.gui;

import com.black.interfaces.IConnectionHandler;
import com.black.model.ServerInfo;
import com.black.utils.UIStyler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel for joining servers discovered through network discovery.
 */
public class ServerListPanel extends JPanel {
    private final IConnectionHandler handler;
    private final JComboBox<ServerInfo> serverComboBox;
    private final JTextField usernameField;
    private final JButton joinButton;
    private final JButton backButton;
    
    public ServerListPanel(IConnectionHandler handler) {
        this.handler = handler;
        
        JPanel panel = UIStyler.createCardPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));
        panel.setPreferredSize(new Dimension(380, 350));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Back Button
        backButton = new JButton("Retour");
        UIStyler.styleButton(backButton, new Color(100, 100, 120), new Color(220, 220, 230));
        backButton.addActionListener(e -> handler.onPanelSwitchRequested("connectionType"));
        panel.add(backButton, gbc);

        // Server List Label
        JLabel serverListLabel = UIStyler.createStyledLabel("Discovered Servers:");
        gbc.gridy = 1;
        panel.add(serverListLabel, gbc);

        // Server ComboBox
        serverComboBox = new JComboBox<>();
        serverComboBox.setFont(new Font("Segoe UI", Font.BOLD, 12));
        serverComboBox.setBorder(BorderFactory.createLineBorder(new Color(70, 75, 85), 1, true));
        gbc.gridx = 1;
        panel.add(serverComboBox, gbc);

        // Username
        gbc.gridy = 2;
        gbc.gridx = 0;
        usernameField = new JTextField(16);
        UIStyler.styleTextField(usernameField);
        JLabel nameLabel = UIStyler.createStyledLabel("Username:");
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Join Button
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        joinButton = new JButton("Join");
        UIStyler.styleButton(joinButton, new Color(130, 180, 100), new Color(30, 35, 40));
        joinButton.addActionListener(e -> handleJoinClick());
        panel.add(joinButton, gbc);
        
        // Back to Menu Button
        JButton backToMenuButton = new JButton("\u2190 Menu");
        UIStyler.styleButton(backToMenuButton, new Color(100, 100, 120), new Color(30, 35, 40));
        backToMenuButton.addActionListener(e -> handler.onBackToMenuRequested());
        gbc.gridx = 1;
        panel.add(backToMenuButton, gbc);
        
        // Layout
        setLayout(new BorderLayout());
        setOpaque(false);
        add(panel, BorderLayout.CENTER);
    }
    
    private void handleJoinClick() {
        String username = usernameField.getText().strip();
        if (username.isEmpty()) {
            handler.showError("Username can't be empty.");
            return;
        }
        
        handler.clearError();
        handler.onDiscoveredServerJoinRequested(username);
    }
    
    public JComboBox<ServerInfo> getServerComboBox() {
        return serverComboBox;
    }
}
