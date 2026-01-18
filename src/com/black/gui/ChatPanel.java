package com.black.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.black.interfaces.IConnectionHandler;
import com.black.utils.UIStyler;

import java.awt.*;

public class ChatPanel extends JPanel {
    private final IConnectionHandler handler;
    private final JTextArea chatArea;
    private final JTextArea messageArea;
    private final JComboBox<String> targetComboBox;
    private final JButton leaveButton;
    private final JButton sendButton;
    private final JScrollPane scrollPane;
    
    public ChatPanel(IConnectionHandler handler) {
        this.handler = handler;
        
        JPanel panel = UIStyler.createLightCardPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setPreferredSize(new Dimension(320, 400));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        // Leave Server button at top left
        leaveButton = new JButton("\u2190 Leave");
        UIStyler.styleButton(leaveButton, new Color(220, 150, 70), new Color(30, 35, 40));
        leaveButton.addActionListener(e -> handler.onLeaveRequested());
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(leaveButton, gbc);

        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setOpaque(true);
        chatArea.setBackground(new Color(38, 42, 48));
        chatArea.setForeground(new Color(220, 220, 230));
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chatArea.setBorder(new EmptyBorder(12, 12, 12, 12));

        scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 75, 85), 1, true),
            BorderFactory.createLineBorder(new Color(55, 60, 68), 3)
        ));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(scrollPane, gbc);

        // Message input area
        messageArea = new JTextArea(3, 20);
        messageArea.setEditable(true);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setOpaque(true);
        messageArea.setBackground(new Color(38, 42, 48));
        messageArea.setForeground(new Color(220, 220, 230));
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 75, 85), 1, true),
                BorderFactory.createLineBorder(new Color(55, 60, 68), 2)
            ),
            new EmptyBorder(8, 10, 8, 10)
        ));
        messageArea.setCaretColor(new Color(180, 220, 140));

        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(messageArea, gbc);

        // Bottom panel: Target selector and Send button
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false);
        
        GridBagConstraints bpGbc = new GridBagConstraints();
        bpGbc.gridy = 0;
        bpGbc.fill = GridBagConstraints.HORIZONTAL;
        bpGbc.insets = new Insets(0, 0, 0, 8);
        
        // Target label
        JLabel targetLabel = new JLabel("To:");
        targetLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        targetLabel.setForeground(new Color(60, 100, 60));
        bpGbc.gridx = 0;
        bpGbc.weightx = 0;
        bottomPanel.add(targetLabel, bpGbc);
        
        // Target combo box
        targetComboBox = new JComboBox<>();
        targetComboBox.addItem("All");
        targetComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        targetComboBox.setBackground(new Color(38, 42, 48));
        targetComboBox.setForeground(new Color(60, 100, 60));
        targetComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 75, 85), 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
        bpGbc.gridx = 1;
        bpGbc.weightx = 1.0;
        bottomPanel.add(targetComboBox, bpGbc);
        
        // Send button
        sendButton = new JButton("Send");
        UIStyler.styleButton(sendButton, new Color(130, 180, 100), new Color(30, 35, 40));
        sendButton.addActionListener(e -> handler.onSendMessageRequested());
        bpGbc.gridx = 2;
        bpGbc.weightx = 0;
        bpGbc.insets = new Insets(0, 0, 0, 0);
        bottomPanel.add(sendButton, bpGbc);

        gbc.gridy = 3;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(bottomPanel, gbc);
        
        // Back to Menu button
        JButton backToMenuButton = new JButton("← Back to Menu");
        UIStyler.styleButton(backToMenuButton, new Color(100, 100, 120), new Color(30, 35, 40));
        backToMenuButton.addActionListener(e -> handler.onBackToMenuRequested());
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(backToMenuButton, gbc);
        
        // Layout
        setLayout(new BorderLayout());
        setOpaque(false);
        add(panel, BorderLayout.CENTER);
    }
    
    public JTextArea getChatArea() {
        return chatArea;
    }
    
    public JTextArea getMessageArea() {
        return messageArea;
    }
    
    public JComboBox<String> getTargetComboBox() {
        return targetComboBox;
    }
}
