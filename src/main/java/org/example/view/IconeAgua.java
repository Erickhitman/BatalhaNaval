package org.example.view;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class IconeAgua implements Icon {
    @Override
    public int getIconWidth() {
        return 36;
    }

    @Override
    public int getIconHeight() {
        return 32;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D desenho = (Graphics2D) graphics.create();
        desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        desenho.setColor(new Color(235, 255, 255, 230));
        desenho.drawArc(x + 4, y + 10, 13, 9, 5, 165);
        desenho.drawArc(x + 17, y + 10, 13, 9, 10, 165);
        desenho.setColor(new Color(244, 255, 255, 235));
        desenho.drawArc(x + 9, y + 15, 18, 11, 200, 140);
        desenho.setColor(new Color(255, 255, 255, 245));
        desenho.fillOval(x + 16, y + 5, 5, 5);
        desenho.dispose();
    }
}
