package main;

import gui.Options;

public class main {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new Options().setVisible(true);
        });
    }
}