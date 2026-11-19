package org.example.view;

import org.example.enums.TipoEmbarcacao;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

/*
 * Navio desenhado na mão (com retângulos e polígonos) pra lista de embarcações.
 * Agora que temos as imagens PNG ele quase não aparece, mas fica de reserva:
 * se apagarem alguma imagem de navio, é esse desenho que entra no lugar.
 */
public class IconeFrota implements Icon {
    private static final Color CASCO = new Color(52, 112, 190);
    private static final Color CONVES = new Color(92, 152, 222);
    private static final Color CONTORNO = new Color(170, 215, 255);
    private static final Color DETALHE = new Color(225, 242, 255);

    private final TipoEmbarcacao type;

    public IconeFrota(TipoEmbarcacao type) {
        this.type = type;
    }

    @Override
    public int getIconWidth() {
        return 22 + type.getLength() * 20; // cada casa do navio deixa o desenho 20px mais comprido
    }

    @Override
    public int getIconHeight() {
        return 28;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int x, int y) {
        Graphics2D desenho = (Graphics2D) graphics.create();
        desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        desenho.translate(x, y); // daqui pra frente (0,0) é o canto do ícone, facilita as contas
        desenho.setStroke(new BasicStroke(1.2f));
        int largura = getIconWidth();

        // submarino tem formato diferente, o resto é tudo casco pontudo com ponte em cima
        if (type == TipoEmbarcacao.SUBMARINE) {
            desenharSubmarino(desenho, largura);
        } else {
            desenharNavio(desenho, largura);
        }
        desenho.dispose();
    }

    private void desenharSubmarino(Graphics2D desenho, int largura) {
        desenho.setColor(CASCO);
        desenho.fillRoundRect(4, 12, largura - 8, 10, 10, 10);
        desenho.fillRect(largura / 2 - 5, 7, 10, 6);
        desenho.setColor(CONTORNO);
        desenho.drawRoundRect(4, 12, largura - 8, 10, 10, 10);
        desenho.drawRect(largura / 2 - 5, 7, 10, 6);
        desenho.drawLine(largura / 2, 7, largura / 2, 3);
        desenho.setColor(DETALHE);
        desenho.fillOval(largura / 2 - 3, 15, 6, 4);
        desenho.drawLine(1, 14, 3, 17);
        desenho.drawLine(1, 20, 3, 17);
    }

    private void desenharNavio(Graphics2D desenho, int largura) {
        // casco: reto atrás e com a ponta (proa) do lado direito
        Polygon casco = new Polygon(
                new int[]{2, largura - 14, largura - 2, largura - 14, 6},
                new int[]{12, 12, 17, 23, 23}, 5);
        desenho.setColor(CASCO);
        desenho.fillPolygon(casco);
        desenho.setColor(CONTORNO);
        desenho.drawPolygon(casco);

        int tamanho = type.getLength();
        if (type == TipoEmbarcacao.AIRCRAFT_CARRIER) {
            // porta-aviões: convés reto com as faixas da pista e uma torrezinha
            desenho.setColor(CONVES);
            desenho.fillRect(4, 8, largura - 20, 5);
            desenho.setColor(DETALHE);
            for (int xLinha = 10; xLinha < largura - 22; xLinha += 10) {
                desenho.drawLine(xLinha, 10, xLinha + 5, 10);
            }
            desenho.setColor(CONVES);
            desenho.fillRect(largura - 40, 2, 10, 7);
            desenho.setColor(CONTORNO);
            desenho.drawRect(largura - 40, 2, 10, 7);
            return;
        }

        // os outros: uma "ponte" no meio que cresce com o tamanho do navio, mastro e janelinhas
        int ponte = 10 + tamanho * 5;
        int inicioPonte = largura / 2 - ponte / 2 - 4;
        desenho.setColor(CONVES);
        desenho.fillRect(inicioPonte, 7, ponte, 6);
        desenho.setColor(CONTORNO);
        desenho.drawRect(inicioPonte, 7, ponte, 6);
        desenho.drawLine(inicioPonte + ponte / 2, 7, inicioPonte + ponte / 2, 1);

        desenho.setColor(DETALHE);
        for (int i = 0; i < tamanho; i++) {
            desenho.fillOval(inicioPonte + 3 + i * (ponte - 6) / Math.max(1, tamanho), 15, 4, 4);
        }
        desenho.fillOval(7, 13, 5, 5);
        desenho.fillOval(largura - 24, 13, 5, 5);
    }
}
