package com.black.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.black.interfaces.IConnectionHandler;
import com.black.utils.UIStyler;

import java.awt.*;

public class ConnectionTypePanel extends JPanel {
    private final IConnectionHandler handler;
    private final JButton directConnectButton;
    private final JButton discoverServersButton;
    
    public ConnectionTypePanel(IConnectionHandler handler) {
        this.handler = handler;
        
        JPanel panel = UIStyler.createCardPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));
        panel.setPreferredSize(new Dimension(320, 280));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Direct Connection Button
        directConnectButton = new JButton("Connexion Direct");
        UIStyler.styleButton(directConnectButton, new Color(130, 180, 100), new Color(30, 35, 40));
        directConnectButton.addActionListener(e -> handler.onPanelSwitchRequested("connection"));
        panel.add(directConnectButton, gbc);
        
        // Discover Servers Button
        gbc.gridy = 1;
        discoverServersButton = new JButton("Découvrir les serveurs");
        UIStyler.styleButton(discoverServersButton, new Color(180, 140, 200), new Color(30, 35, 40));
        discoverServersButton.addActionListener(e -> handler.onPanelSwitchRequested("listServer"));
        panel.add(discoverServersButton, gbc);
        
        // Layout
        setLayout(new BorderLayout());
        setOpaque(false);
        add(panel, BorderLayout.CENTER);
    }
}