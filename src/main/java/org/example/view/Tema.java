package org.example.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/*
 * Juntei aqui tudo que é "estilo" do jogo: cores, fontes, botões e os cartões.
 * Assim as telas só pedem Tema.botao(...), Tema.rotulo(...) etc. e ficam todas iguais.
 * Se quiser mudar a cara do jogo inteiro, geralmente é só mexer nas cores daqui.
 */
public final class Tema {
    public static final Color TEXTO = new Color(226, 236, 248);        // branco meio azulado, pros textos principais
    public static final Color TEXTO_SUAVE = new Color(132, 156, 184);  // cinza-azulado pros textos secundários
    public static final Color DESTAQUE = new Color(84, 196, 255);      // o azul "neon" do radar
    public static final Color CARTAO_FUNDO = new Color(6, 14, 30, 185); // o último número é a transparência
    public static final Color CARTAO_BORDA = new Color(84, 196, 255, 45);

    // Se tiver um arquivo em resources/fonts/titulo.ttf (tipo a Orbitron) ele usa,
    // senão pega uma fonte que já vem no Windows.
    private static final Font FONTE_BASE = carregarFonte();

    private Tema() {
        // classe só de métodos estáticos, ninguém precisa criar um Tema
    }

    public static Font fonte(int estilo, float tamanho) {
        return FONTE_BASE.deriveFont(estilo, tamanho);
    }

    // espacamento é tipo uma porcentagem: 0.12 deixa 12% de espaço a mais entre as letras
    public static Font fonteEspacada(int estilo, float tamanho, float espacamento) {
        return fonte(estilo, tamanho).deriveFont(Map.of(TextAttribute.TRACKING, espacamento));
    }

    // texto normal, centralizado e já na cor certa
    public static JLabel rotulo(String texto, int estilo, float tamanho) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(fonte(estilo, tamanho));
        label.setForeground(TEXTO);
        return label;
    }

    // aqueles textinhos pequenos em maiúsculo, tipo "TIROS RESTANTES"
    public static JLabel legenda(String texto, float tamanho) {
        JLabel label = new JLabel(texto.toUpperCase(), SwingConstants.CENTER);
        label.setFont(fonteEspacada(Font.BOLD, tamanho, 0.14f));
        label.setForeground(TEXTO_SUAVE);
        folgaParaEspacamento(label, tamanho, 0.14f);
        return label;
    }

    // título grandão do menu e da tela de resultado ("BATALHA NAVAL", "VITÓRIA")
    public static JLabel titulo(String texto, float tamanho) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(fonteEspacada(Font.BOLD, tamanho, 0.12f));
        label.setForeground(TEXTO);
        folgaParaEspacamento(label, tamanho, 0.12f);
        return label;
    }

    // Com letra espaçada o JLabel calcula o tamanho errado e corta a última letra,
    // e o acento do "Ó" também sumia em cima. Essa borda vazia dá uma folga pra isso.
    private static void folgaParaEspacamento(JLabel label, float tamanho, float espacamento) {
        int folga = (int) Math.ceil(tamanho * espacamento) + 2;
        label.setBorder(BorderFactory.createEmptyBorder((int) (tamanho * 0.15f), folga, 0, folga));
    }

    // botão "normal": escuro, só com a borda azul
    public static JButton botao(String texto) {
        return new Botao(texto, false);
    }

    // botão da ação principal da tela (Jogar, Jogar novamente), todo azul
    public static JButton botaoPrimario(String texto) {
        return new Botao(texto, true);
    }

    // empilha os botões um embaixo do outro, todos com a mesma largura
    public static JPanel colunaBotoes(JButton... botoes) {
        JPanel coluna = new JPanel(new GridLayout(botoes.length, 1, 0, 12));
        coluna.setOpaque(false);
        for (JButton botao : botoes) {
            coluna.add(botao);
        }
        return coluna;
    }

    // Caixa escura com cantinho arredondado. É o que fica em volta da lista de navios
    // e do contador de tiros na tela do jogo.
    public static JPanel cartao(JComponent conteudo) {
        JPanel cartao = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                // desenho na mão porque o Swing não tem painel com canto arredondado
                Graphics2D desenho = (Graphics2D) graphics.create();
                desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                desenho.setColor(CARTAO_FUNDO);
                desenho.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                desenho.setColor(CARTAO_BORDA);
                desenho.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                desenho.dispose();
            }
        };
        cartao.setOpaque(false); // senão o Swing pinta um retângulo por trás e estraga os cantos
        cartao.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22)); // espaço interno
        cartao.add(conteudo, BorderLayout.CENTER);
        return cartao;
    }

    // O JButton padrão do Java é aquele cinza quadrado, então esse aqui desenha
    // o próprio fundo, a borda e o texto.
    private static final class Botao extends JButton {
        private final boolean primario;

        private Botao(String texto, boolean primario) {
            super(texto);
            this.primario = primario;
            setFont(fonte(Font.BOLD, 17f));
            setForeground(primario ? new Color(4, 20, 38) : TEXTO); // no azul o texto fica escuro pra dar pra ler
            // desliga tudo que o Swing desenharia sozinho, quem desenha é o paintComponent ali embaixo
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 26, 10, 26)); // define o "tamanho" do botão
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D desenho = (Graphics2D) graphics.create();
            desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            desenho.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            boolean hover = getModel().isRollover();       // mouse em cima
            boolean pressionado = getModel().isPressed();  // clicando
            int w = getWidth();
            int h = getHeight();
            int arco = 14;

            if (primario) {
                // clicando fica mais escuro, com o mouse em cima fica mais claro
                Color cor = pressionado ? new Color(60, 160, 220) : hover ? new Color(130, 216, 255) : DESTAQUE;
                desenho.setColor(cor);
                desenho.fillRoundRect(0, 0, w - 1, h - 1, arco, arco);
            } else {
                // fundo quase transparente e a borda acende quando passa o mouse
                desenho.setColor(hover ? new Color(84, 196, 255, 38) : new Color(255, 255, 255, 10));
                desenho.fillRoundRect(0, 0, w - 1, h - 1, arco, arco);
                desenho.setStroke(new BasicStroke(1.2f));
                desenho.setColor(hover ? new Color(84, 196, 255, 220) : new Color(84, 196, 255, 90));
                desenho.drawRoundRect(0, 0, w - 1, h - 1, arco, arco);
            }

            // texto no meio do botão (FontMetrics é o que diz a largura/altura do texto)
            desenho.setFont(getFont());
            desenho.setColor(isEnabled() ? getForeground() : TEXTO_SUAVE);
            FontMetrics medidas = desenho.getFontMetrics();
            int x = (w - medidas.stringWidth(getText())) / 2;
            int y = (h - medidas.getHeight()) / 2 + medidas.getAscent();
            desenho.drawString(getText(), x, y);
            desenho.dispose();
        }
    }

    // Tenta a fonte do arquivo; se não tiver, vai testando fontes do sistema na ordem
    // (a Bahnschrift vem no Windows 10/11 e combina bem com o estilo).
    private static Font carregarFonte() {
        try (InputStream entrada = Tema.class.getResourceAsStream("/fonts/titulo.ttf")) {
            if (entrada != null) {
                Font fonte = Font.createFont(Font.TRUETYPE_FONT, entrada);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(fonte);
                return fonte.deriveFont(16f);
            }
        } catch (Exception ignored) {
            // cai na fonte do sistema
        }
        List<String> disponiveis = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String nome : new String[]{"Bahnschrift", "Segoe UI", "Verdana"}) {
            if (disponiveis.contains(nome)) {
                return new Font(nome, Font.PLAIN, 16);
            }
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    }
}
