package org.example.view;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/*
 * Fundo de todas as telas: um azul bem escuro com os círculos de radar no meio.
 * As telas (menu, jogo, resultado...) estendem essa classe em vez de JPanel,
 * aí todas ganham o mesmo fundo e a animação sem precisar fazer nada.
 */
public class PainelFundo extends JPanel {
    private static final Dimension TAMANHO_PADRAO = new Dimension(1100, 650);
    private static final long INICIO = System.nanoTime(); // marca quando o jogo abriu, a animação conta a partir daqui

    // A cada 33 ms (uns 30 quadros por segundo) manda redesenhar a tela.
    // Ela é ligada/desligada lá embaixo no addNotify/removeNotify.
    private final Timer animacao = new Timer(33, evento -> repaint());

    public PainelFundo() {
        setOpaque(true);
    }

    // Garante que a janela abre pelo menos em 1100x650, mesmo que a tela tenha pouca coisa dentro
    @Override
    public Dimension getPreferredSize() {
        Dimension minimo = super.getPreferredSize();
        return new Dimension(Math.max(minimo.width, TAMANHO_PADRAO.width),
                Math.max(minimo.height, TAMANHO_PADRAO.height));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D desenho = (Graphics2D) graphics.create();
        desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // bordas lisas
        int largura = getWidth();
        int altura = getHeight();
        float centroX = largura / 2f;
        float centroY = altura / 2f;
        float raio = Math.max(largura, altura) * 0.75f;

        // se alguém colocar um fundo.png na pasta de imagens, usa ele no lugar do radar
        BufferedImage imagem = Imagens.carregar("fundo.png");
        if (imagem != null) {
            Imagens.desenharCobrindo(desenho, imagem, largura, altura);
            desenho.dispose();
            return;
        }

        // degradê do centro pra fora: azul escuro no meio, quase preto nas bordas
        desenho.setPaint(new RadialGradientPaint(new Point2D.Float(centroX, centroY), raio,
                new float[]{0f, 0.45f, 1f},
                new Color[]{new Color(13, 38, 74), new Color(6, 18, 40), new Color(2, 5, 14)}));
        desenho.fillRect(0, 0, largura, altura);

        // 4 círculos do radar, cada um mais apagado que o anterior
        int base = Math.min(largura, altura);
        desenho.setStroke(new BasicStroke(1.2f));
        for (int i = 1; i <= 4; i++) {
            int r = (int) (base * 0.17 * i);
            desenho.setColor(new Color(84, 196, 255, 26 - i * 4));
            desenho.drawOval((int) centroX - r, (int) centroY - r, r * 2, r * 2);
        }
        // a "mira": uma linha vertical e uma horizontal passando pelo centro
        desenho.setColor(new Color(84, 196, 255, 12));
        desenho.drawLine((int) centroX, 0, (int) centroX, altura);
        desenho.drawLine(0, (int) centroY, largura, (int) centroY);

        desenharAnimacao(desenho, centroX, centroY, (int) (base * 0.17 * 4)); // raio = o círculo maior
        desenho.dispose();
    }

    /*
     * Desenha as duas animações: a linha girando e o anel que pulsa.
     * A posição sai do tempo que passou (e não de um contador de quadros), então
     * se o PC engasgar um pouco a animação não fica lenta, só pula pro lugar certo.
     */
    private void desenharAnimacao(Graphics2D desenho, float centroX, float centroY, int raio) {
        double segundos = (System.nanoTime() - INICIO) / 1_000_000_000.0;
        int x = (int) centroX - raio;
        int y = (int) centroY - raio;

        // --- varredura ---
        // 45 graus por segundo = uma volta a cada 8 s. Aumenta o 45 se quiser mais rápido.
        double angulo = (segundos * 45) % 360;

        // O rastro atrás da linha. Desenho várias fatias bem transparentes uma por cima
        // da outra, todas começando na linha: perto dela a cor acumula e fica mais forte,
        // e vai sumindo conforme se afasta. Fazendo fatias separadas ficavam umas listras.
        Graphics2D rastro = (Graphics2D) desenho.create();
        rastro.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        rastro.setColor(new Color(84, 196, 255, 2));
        for (int i = 1; i <= 12; i++) {
            // o Arc2D conta o ângulo ao contrário da tela, por isso o -angulo
            rastro.fill(new Arc2D.Double(x, y, raio * 2.0, raio * 2.0, -angulo, i * 3.5, Arc2D.PIE));
        }
        rastro.dispose();

        // a linha em si, do centro até a borda do radar
        double rad = Math.toRadians(angulo);
        desenho.setStroke(new BasicStroke(1.5f));
        desenho.setColor(new Color(84, 196, 255, 55));
        desenho.drawLine((int) centroX, (int) centroY,
                (int) (centroX + Math.cos(rad) * raio), (int) (centroY + Math.sin(rad) * raio));

        // --- pulso ---
        // fase vai de 0 a 1 a cada 4 segundos: o anel cresce do centro e vai ficando transparente
        double fase = (segundos % 4.0) / 4.0;
        int r = (int) (raio * fase);
        int alfaPulso = (int) (40 * (1 - fase));
        desenho.setStroke(new BasicStroke(2f));
        desenho.setColor(new Color(84, 196, 255, alfaPulso));
        desenho.drawOval((int) centroX - r, (int) centroY - r, r * 2, r * 2);
    }

    // Quando a tela aparece na janela liga o timer, quando sai (trocou de tela) desliga.
    // Sem isso as telas antigas ficariam redesenhando pra sempre em segundo plano.
    @Override
    public void addNotify() {
        super.addNotify();
        animacao.start();
    }

    @Override
    public void removeNotify() {
        animacao.stop();
        super.removeNotify();
    }
}
