package com.black.utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Utility class for consistent UI styling across the application.
 */
public class UIStyler {
    
    private UIStyler() {
        // Utility class, prevent instantiation
    }
    
    /**
     * Apply consistent styling to a text field.
     * @param field The text field to style
     */
    public static void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(new Color(38, 42, 48));
        field.setForeground(new Color(220, 220, 230));
        field.setCaretColor(new Color(180, 220, 140));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 75, 85), 1, true),
                BorderFactory.createLineBorder(new Color(55, 60, 68), 2)
            ),
            new EmptyBorder(6, 10, 6, 10)
        ));
    }
    
    /**
     * Apply consistent styling to a button with custom colors.
     * @param button The button to style
     * @param bgColor Background color
     * @param fgColor Foreground (text) color
     */
    public static void styleButton(JButton button, Color bgColor, Color fgColor) {
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
    
    /**
     * Create a styled label with consistent font and color.
     * @param text Label text
     * @return Styled JLabel
     */
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(200, 200, 210));
        return label;
    }
    
    /**
     * Create a card panel with rounded corners and shadow effect.
     * @param layout Layout manager for the panel
     * @return Styled JPanel
     */
    public static JPanel createCardPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout) {
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
        return panel;
    }
    
    /**
     * Create a light card panel (for chat area).
     * @param layout Layout manager for the panel
     * @return Styled JPanel
     */
    public static JPanel createLightCardPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, 12, 12);
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(2, 2, getWidth() - 2, getHeight() - 2, 12, 12);
                
                // Draw white background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 12, 12);
            }
        };
        panel.setOpaque(false);
        return panel;
    }
}
