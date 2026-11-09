package org.example.view;

import org.example.enums.TipoEmbarcacao;

import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

public class IconeEmbarcacao implements Icon {
    private static final Color HULL = new Color(48, 57, 62);
    private static final Color DECK = new Color(104, 112, 115);
    private static final Color DETAIL = new Color(219, 225, 222);
    private final TipoEmbarcacao type;

    public IconeEmbarcacao(TipoEmbarcacao type) {
        this.type = type;
    }

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
        desenho.translate(x, y);
        switch (type) {
            case SUBMARINE -> desenharSubmarino(desenho);
            case DESTROYER -> desenharDestroier(desenho);
            case CRUISER -> desenharCruzador(desenho);
            case BATTLESHIP -> desenharEncouracado(desenho);
            case AIRCRAFT_CARRIER -> desenharPortaAvioes(desenho);
        }
        desenho.dispose();
    }

    private void desenharSubmarino(Graphics2D desenho) {
        desenho.setColor(HULL);
        desenho.fillRoundRect(3, 12, 29, 11, 10, 10);
        desenho.fillRect(15, 8, 8, 5);
        desenho.setColor(DECK);
        desenho.fillRect(18, 5, 2, 4);
        desenho.setColor(DETAIL);
        desenho.fillOval(7, 16, 3, 3);
        desenho.fillOval(13, 16, 3, 3);
        desenho.fillOval(19, 16, 3, 3);
        desenho.fillOval(25, 16, 3, 3);
    }

    private void desenharDestroier(Graphics2D desenho) {
        desenharCasco(desenho, new int[]{3, 8, 29, 34}, new int[]{21, 12, 12, 21});
        desenho.setColor(DECK);
        desenho.fillRect(13, 8, 12, 6);
        desenho.fillRect(17, 4, 5, 5);
        desenho.setColor(HULL);
        desenho.fillRect(7, 17, 6, 3);
        desenho.fillRect(24, 17, 6, 3);
        desenho.setColor(DETAIL);
        desenho.drawLine(18, 4, 18, 1);
    }

    private void desenharCruzador(Graphics2D desenho) {
        desenharCasco(desenho, new int[]{2, 6, 30, 34}, new int[]{22, 10, 10, 22});
        desenho.setColor(DECK);
        desenho.fillRect(11, 8, 15, 7);
        desenho.fillRect(16, 4, 7, 5);
        desenho.setColor(HULL);
        desenho.fillRect(5, 15, 7, 4);
        desenho.fillRect(25, 15, 7, 4);
        desenho.setColor(DETAIL);
        desenho.drawLine(19, 4, 19, 1);
        desenho.drawLine(14, 8, 14, 5);
    }

    private void desenharEncouracado(Graphics2D desenho) {
        desenharCasco(desenho, new int[]{1, 5, 31, 35}, new int[]{23, 9, 9, 23});
        desenho.setColor(DECK);
        desenho.fillRect(12, 7, 13, 9);
        desenho.fillRect(17, 3, 6, 5);
        desenho.setColor(HULL);
        desenho.fillRect(4, 14, 9, 5);
        desenho.fillRect(24, 14, 9, 5);
        desenho.setColor(DETAIL);
        desenho.drawLine(8, 13, 3, 11);
        desenho.drawLine(29, 13, 34, 11);
        desenho.drawLine(20, 3, 20, 0);
    }

    private void desenharPortaAvioes(Graphics2D desenho) {
        desenharCasco(desenho, new int[]{1, 4, 32, 35}, new int[]{24, 8, 8, 24});
        desenho.setColor(new Color(72, 81, 84));
        desenho.fillRoundRect(5, 9, 25, 12, 4, 4);
        desenho.setColor(DETAIL);
        desenho.drawLine(8, 11, 27, 11);
        desenho.drawLine(8, 19, 27, 19);
        desenho.setColor(DECK);
        desenho.fillRect(22, 5, 5, 5);
        desenho.setColor(HULL);
        desenho.fillRect(23, 3, 2, 3);
    }

    private void desenharCasco(Graphics2D desenho, int[] pontosX, int[] pontosY) {
        desenho.setColor(HULL);
        desenho.fillPolygon(new Polygon(pontosX, pontosY, pontosX.length));
    }
}
