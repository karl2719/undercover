package com.black.main;

import java.io.IOException;

import com.black.gui.ClientFrame;

public class ClientApp{
    public static void main(String[] args) throws IOException{
       ClientFrame fenetre = new ClientFrame();
       fenetre.setVisible(true);
    }
}