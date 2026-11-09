package org.example;

import org.example.controller.ControladorJogo;

import javax.swing.SwingUtilities;

public class Inicio {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ControladorJogo().start());
    }
}
