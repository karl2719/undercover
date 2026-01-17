package com.black.main;

import java.io.IOException;
import javax.swing.SwingUtilities;
import com.black.gui.MainFrame;

/**
 * @deprecated Use UndercoverApp instead
 */
@Deprecated
public class ClientApp{
    public static void main(String[] args) throws IOException{
       // Redirect to unified app
       SwingUtilities.invokeLater(() -> {
           MainFrame mainFrame = new MainFrame();
           mainFrame.setVisible(true);
       });
    }
}