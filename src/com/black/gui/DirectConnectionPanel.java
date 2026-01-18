package com.black.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.black.interfaces.IConnectionHandler;
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
    private final JTextField topTextField;
    private final JButton topJoinButton;
    
    public DirectConnectionPanel(IConnectionHandler handler) {
        this.handler = handler;
        
        // Create top panel
        JPanel topPanel = UIStyler.createCardPanel(new GridBagLayout());
        topPanel.setBorder(new EmptyBorder(20, 35, 20, 35));
        
        GridBagConstraints topGbc = new GridBagConstraints();
        topGbc.fill = GridBagConstraints.HORIZONTAL;
        topGbc.insets = new Insets(5, 5, 5, 5);
        topGbc.gridx = 0;
        topGbc.gridy = 0;
        topGbc.weightx = 0;
        
        JLabel serverNameLabel = UIStyler.createStyledLabel("Server Name:");
        topPanel.add(serverNameLabel, topGbc);
        
        topGbc.gridx = 1;
        topGbc.weightx = 1.0;
        topTextField = new JTextField(20);
        UIStyler.styleTextField(topTextField);
        topPanel.add(topTextField, topGbc);
        
        topGbc.gridx = 0;
        topGbc.gridy = 1;
        topGbc.gridwidth = 2;
        topGbc.weightx = 0;
        topJoinButton = new JButton("Join");
        UIStyler.styleButton(topJoinButton, new Color(130, 180, 100), new Color(30, 35, 40));
        topJoinButton.addActionListener(e -> handleTopJoinClick());
        topPanel.add(topJoinButton, topGbc);
        
        // Create bottom panel
        JPanel bottomPanel = UIStyler.createCardPanel(new GridBagLayout());
        bottomPanel.setBorder(new EmptyBorder(30, 35, 30, 35));
        bottomPanel.setMinimumSize(new Dimension(350, 380));
        bottomPanel.setMaximumSize(new Dimension(500, 500));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // IP Address
        ipAddressField = new JTextField(16);
        UIStyler.styleTextField(ipAddressField);
        JLabel addressLabel = UIStyler.createStyledLabel("Ip Address: ");
        bottomPanel.add(addressLabel, gbc);
        gbc.gridx = 1;
        bottomPanel.add(ipAddressField, gbc);

        // Port
        gbc.gridy = 1;
        gbc.gridx = 0;
        NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setGroupingUsed(false);
        portField = new JFormattedTextField(nf);
        portField.setColumns(16);
        UIStyler.styleTextField(portField);
        JLabel portLabel = UIStyler.createStyledLabel("Port: ");
        bottomPanel.add(portLabel, gbc);
        gbc.gridx = 1;
        bottomPanel.add(portField, gbc);

        // Username
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel nameLabel = UIStyler.createStyledLabel("Username:");
        bottomPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(16);
        UIStyler.styleTextField(usernameField);
        bottomPanel.add(usernameField, gbc);

        // Join Button
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        joinButton = new JButton("Join");
        UIStyler.styleButton(joinButton, new Color(130, 180, 100), new Color(30, 35, 40));
        joinButton.addActionListener(e -> handleJoinClick());
        bottomPanel.add(joinButton, gbc);

        // Back Button
        backButton = new JButton("Retour");
        UIStyler.styleButton(backButton, new Color(100, 100, 120), new Color(220, 220, 230));
        backButton.addActionListener(e -> handler.onPanelSwitchRequested("connectionType"));
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        bottomPanel.add(backButton, gbc);
        
        // Back to Menu Button
        JButton backToMenuButton = new JButton("\u2190 Menu");
        UIStyler.styleButton(backToMenuButton, new Color(100, 100, 120), new Color(30, 35, 40));
        backToMenuButton.addActionListener(e -> handler.onBackToMenuRequested());
        gbc.gridx = 1;
        bottomPanel.add(backToMenuButton, gbc);
        
        // Main container panel
        JPanel mainContainer = new JPanel(new BorderLayout(10, 10));
        mainContainer.setOpaque(false);
        mainContainer.add(topPanel, BorderLayout.NORTH);
        mainContainer.add(bottomPanel, BorderLayout.CENTER);
        
        // Layout
        setLayout(new BorderLayout());
        setOpaque(false);
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private void handleTopJoinClick() {
        String text = topTextField.getText().strip();
        if (text.isEmpty()) {
            handler.showError("Text field can't be empty.");
            return;
        }
        handler.clearError();
        // TODO: Implement top join logic
        System.out.println("Top join clicked with: " + text);
    }
    
    private void handleJoinClick() {
        // Validation
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
