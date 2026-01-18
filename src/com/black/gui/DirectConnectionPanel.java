package com.black.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.black.interfaces.IConnectionHandler;
import com.black.service.UnifiedDiscoveryService;
import com.black.utils.UIStyler;

import java.awt.*;
import java.text.NumberFormat;

public class DirectConnectionPanel extends JPanel {
    private final IConnectionHandler handler;
    private final JTextField ipAddressField;
    private final JFormattedTextField portField;
    private final JTextField usernameField;
    private final JButton joinButton;
    private final JButton backButton;
    private final JTextField serverNameField;
    private final JCheckBox useServerNameCheckbox;
    private final JLabel serverNameLabel;
    private final JLabel ipAddressLabel;
    private final JLabel portLabel;
    
    public DirectConnectionPanel(IConnectionHandler handler) {
        this.handler = handler;
        
        // Create main panel
        JPanel panel = UIStyler.createCardPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));
        panel.setMinimumSize(new Dimension(350, 380));
        panel.setMaximumSize(new Dimension(500, 500));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Checkbox to toggle between modes
        gbc.gridwidth = 2;
        useServerNameCheckbox = new JCheckBox("Use Server Name");
        useServerNameCheckbox.setOpaque(false);
        useServerNameCheckbox.setFont(new Font("Arial", Font.PLAIN, 14));
        useServerNameCheckbox.setForeground(new Color(220, 220, 230));
        useServerNameCheckbox.addActionListener(e -> toggleInputMode());
        panel.add(useServerNameCheckbox, gbc);
        
        // Server Name (initially hidden)
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        serverNameLabel = UIStyler.createStyledLabel("Server Name:");
        panel.add(serverNameLabel, gbc);
        gbc.gridx = 1;
        serverNameField = new JTextField(16);
        UIStyler.styleTextField(serverNameField);
        panel.add(serverNameField, gbc);

        // IP Address
        gbc.gridy = 2;
        gbc.gridx = 0;
        ipAddressField = new JTextField(16);
        UIStyler.styleTextField(ipAddressField);
        ipAddressLabel = UIStyler.createStyledLabel("Ip Address: ");
        panel.add(ipAddressLabel, gbc);
        gbc.gridx = 1;
        panel.add(ipAddressField, gbc);

        // Port
        gbc.gridy = 3;
        gbc.gridx = 0;
        NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setGroupingUsed(false);
        portField = new JFormattedTextField(nf);
        portField.setColumns(16);
        UIStyler.styleTextField(portField);
        portLabel = UIStyler.createStyledLabel("Port: ");
        panel.add(portLabel, gbc);
        gbc.gridx = 1;
        panel.add(portField, gbc);

        // Username
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel nameLabel = UIStyler.createStyledLabel("Username:");
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(16);
        UIStyler.styleTextField(usernameField);
        panel.add(usernameField, gbc);

        // Join Button
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        joinButton = new JButton("Join");
        UIStyler.styleButton(joinButton, new Color(130, 180, 100), new Color(30, 35, 40));
        joinButton.addActionListener(e -> handleJoinClick());
        panel.add(joinButton, gbc);

        // Back Button
        backButton = new JButton("Retour");
        UIStyler.styleButton(backButton, new Color(100, 100, 120), new Color(220, 220, 230));
        backButton.addActionListener(e -> handler.onPanelSwitchRequested("connectionType"));
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        panel.add(backButton, gbc);
        
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
        
        // Initialize visibility
        toggleInputMode();
    }
    
    private void toggleInputMode() {
        boolean useServerName = useServerNameCheckbox.isSelected();
        
        
        serverNameLabel.setVisible(useServerName);
        serverNameField.setVisible(useServerName);
        
        
        ipAddressLabel.setVisible(!useServerName);
        ipAddressField.setVisible(!useServerName);
        portLabel.setVisible(!useServerName);
        portField.setVisible(!useServerName);
    }

    private void handleJoinClick() {
        if (useServerNameCheckbox.isSelected()) {
            // Server name mode
            String serverName = serverNameField.getText().strip();
            if (serverName.isEmpty()) {
                handler.showError("Server name can't be empty.");
                return;
            }

            if(!UnifiedDiscoveryService.getInstance().containsServer(serverName)){
                handler.showError("No server with that name is currently available.");
                return;
            }
            
            String username = usernameField.getText().strip();
            if (username.isEmpty()) {
                handler.showError("Username can't be empty.");
                return;
            }
            
            // TODO: Connect to server by name
            handler.clearError();
        } else {
            // IP and Port mode
            String address = ipAddressField.getText().strip();
            if (address.isEmpty()) {
                handler.showError("Ip Address can't be empty.");
                return;
            }

            Number number = (Number) portField.getValue();
            if (number == null) {
                handler.showError("Port can't be empty.");
                return;
            }
            int port = number.intValue();

            String username = usernameField.getText().strip();
            if (username.isEmpty()) {
                handler.showError("Username can't be empty.");
                return;
            }

            handler.clearError();
            handler.onDirectJoinRequested(address, port, username);
        }
    }
}
