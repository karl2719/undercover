package com.black.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.black.client.Client;

public class GamePanel extends JPanel {
    private static final String DISPLAY_PANEL = "display";
    private static final String INPUT_PANEL = "input";
    private static final String VOTING_PANEL = "voting";

    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    private JLabel displayWordLabel;
    private JTextField inputWordField;
    private JButton submitWordButton;
    private JPanel votingOptionsPanel;
    private ButtonGroup votingButtonGroup;
    private JButton submitVoteButton;
    
    private Client client;

    public GamePanel() {
        super();
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);
        this.setPreferredSize(new Dimension(600, 500));

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.BLACK);

        // Create the three panels
        JPanel displayPanel = createDisplayPanel();
        JPanel inputPanel = createInputPanel();
        JPanel votingPanel = createVotingPanel();

        // Add panels to card layout
        cardPanel.add(displayPanel, DISPLAY_PANEL);
        cardPanel.add(inputPanel, INPUT_PANEL);
        cardPanel.add(votingPanel, VOTING_PANEL);

        this.add(cardPanel, BorderLayout.CENTER);
        
        // Show display panel by default
        showDisplayPanel();
    }

    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 30));
        
        displayWordLabel = new JLabel("Your word will appear here");
        displayWordLabel.setFont(new Font("Arial", Font.BOLD, 48));
        displayWordLabel.setForeground(Color.WHITE);
        displayWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        
        panel.add(displayWordLabel, gbc);
        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        
        JLabel promptLabel = new JLabel("Enter your word:");
        promptLabel.setFont(new Font("Arial", Font.BOLD, 24));
        promptLabel.setForeground(Color.WHITE);
        panel.add(promptLabel, gbc);
        
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 10, 20);
        
        inputWordField = new JTextField(20);
        inputWordField.setFont(new Font("Arial", Font.PLAIN, 20));
        inputWordField.setPreferredSize(new Dimension(300, 40));
        panel.add(inputWordField, gbc);
        
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 20, 20, 20);
        
        submitWordButton = new JButton("Submit Word");
        submitWordButton.setFont(new Font("Arial", Font.BOLD, 18));
        submitWordButton.setPreferredSize(new Dimension(200, 50));
        submitWordButton.addActionListener(e -> handleWordSubmit());
        panel.add(submitWordButton, gbc);
        
        return panel;
    }

    private JPanel createVotingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Vote for the Undercover Player", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        votingOptionsPanel = new JPanel();
        votingOptionsPanel.setLayout(new BoxLayout(votingOptionsPanel, BoxLayout.Y_AXIS));
        votingOptionsPanel.setBackground(new Color(40, 40, 40));
        votingButtonGroup = new ButtonGroup();
        
        JScrollPane scrollPane = new JScrollPane(votingOptionsPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        scrollPane.getViewport().setBackground(new Color(40, 40, 40));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(30, 30, 30));
        
        submitVoteButton = new JButton("Submit Vote");
        submitVoteButton.setFont(new Font("Arial", Font.BOLD, 18));
        submitVoteButton.setPreferredSize(new Dimension(200, 50));
        submitVoteButton.addActionListener(e -> handleVoteSubmit());
        buttonPanel.add(submitVoteButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    // Panel switching methods
    public void showDisplayPanel() {
        cardLayout.show(cardPanel, DISPLAY_PANEL);
    }

    public void showInputPanel() {
        inputWordField.setText("");
        cardLayout.show(cardPanel, INPUT_PANEL);
        inputWordField.requestFocusInWindow();
    }

    public void showVotingPanel() {
        cardLayout.show(cardPanel, VOTING_PANEL);
    }

    // Display panel methods
    public void setDisplayWord(String word) {
        displayWordLabel.setText(word);
    }

    // Input panel methods
    public String getInputWord() {
        return inputWordField.getText().trim();
    }

    private void handleWordSubmit() {
        String word = getInputWord();
        if (!word.isEmpty() && client != null) {
            // TODO: Send word to server
            System.out.println("Submitted word: " + word);
            // You can implement packet sending here
            // client.sendPacket(new WordPacket(word));
        }
    }

    // Voting panel methods
    public void setVotingOptions(List<String> players) {
        votingOptionsPanel.removeAll();
        votingButtonGroup = new ButtonGroup();
        
        for (String player : players) {
            JRadioButton radioButton = new JRadioButton(player);
            radioButton.setFont(new Font("Arial", Font.PLAIN, 20));
            radioButton.setForeground(Color.WHITE);
            radioButton.setBackground(new Color(40, 40, 40));
            radioButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            votingButtonGroup.add(radioButton);
            votingOptionsPanel.add(radioButton);
        }
        
        votingOptionsPanel.revalidate();
        votingOptionsPanel.repaint();
    }

    public String getSelectedVote() {
        if (votingButtonGroup.getSelection() != null) {
            JRadioButton selectedButton = null;
            var buttons = votingButtonGroup.getElements();
            while (buttons.hasMoreElements()) {
                JRadioButton button = (JRadioButton) buttons.nextElement();
                if (button.isSelected()) {
                    selectedButton = button;
                    break;
                }
            }
            return selectedButton != null ? selectedButton.getText() : null;
        }
        return null;
    }

    private void handleVoteSubmit() {
        String selectedPlayer = getSelectedVote();
        if (selectedPlayer != null && client != null) {
            // TODO: Send vote to server
            System.out.println("Voted for: " + selectedPlayer);
            // You can implement packet sending here
            // client.sendPacket(new VotePacket(selectedPlayer));
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
