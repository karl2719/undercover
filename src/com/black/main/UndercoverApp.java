package com.black.main;

import com.black.gui.MainFrame;
import javax.swing.SwingUtilities;

public class UndercoverApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
