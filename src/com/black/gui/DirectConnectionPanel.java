package com.black.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.black.interfaces.IConnectionHandler;
import com.black.utils.UIStyler;

import java.awt.*;
import java.text.NumberFormat;

/**
 * Panel for direct server connection using IP address and port.
 */
public class DirectConnectionPanel extends JPanel {
    private final IConnectionHandler handler;
    private final JTextField ipAddressField;
    private final JFormattedTextField portField;
    private final JTextField usernameField;
    private final JButton joinButton;
    private final JButton backButton;
    
    public DirectConnectionPanel(IConnectionHandler handler) {
        this.handler = handler;
        
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

        // IP Address
        ipAddressField = new JTextField(16);
        UIStyler.styleTextField(ipAddressField);
        JLabel addressLabel = UIStyler.createStyledLabel("Ip Address: ");
        panel.add(addressLabel, gbc);
        gbc.gridx = 1;
        panel.add(ipAddressField, gbc);

        // Port
        gbc.gridy = 1;
        gbc.gridx = 0;
        NumberFormat nf = NumberFormat.getIntegerInstance();
        nf.setGroupingUsed(false);
        portField = new JFormattedTextField(nf);
        portField.setColumns(16);
        UIStyler.styleTextField(portField);
        JLabel portLabel = UIStyler.createStyledLabel("Port: ");
        panel.add(portLabel, gbc);
        gbc.gridx = 1;
        panel.add(portField, gbc);

        // Username
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel nameLabel = UIStyler.createStyledLabel("Username:");
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(16);
        UIStyler.styleTextField(usernameField);
        panel.add(usernameField, gbc);

        // Join Button
        gbc.gridy = 3;
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
        gbc.gridy = 4;
        gbc.gridx = 0;
        panel.add(backButton, gbc);
        
        // Layout
        setLayout(new BorderLayout());
        setOpaque(false);
        add(panel, BorderLayout.CENTER);
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
