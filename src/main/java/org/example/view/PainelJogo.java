package org.example.view;

import org.example.enums.Dificuldade;
import org.example.enums.EstadoDisparo;
import org.example.enums.TipoEmbarcacao;
import org.example.model.ResultadoDisparo;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiConsumer;

/*
 * Tela da partida. O layout é em 3 colunas:
 *   esquerda -> cartão com os navios que ainda faltam afundar
 *   meio     -> letras, números e o tabuleiro (cresce com a janela)
 *   direita  -> cartão com os tiros restantes
 * Em cima ficam Voltar/Reiniciar e embaixo a mensagem do último tiro.
 */
public class PainelJogo extends PainelFundo {
    // cores que pintam a casa depois do tiro (o último número é a transparência)
    private static final Color ACERTO = new Color(200, 48, 36, 125); // vermelho pra acerto
    private static final Color AGUA = new Color(255, 255, 255, 22);  // só clareia um pouco no tiro na água

    private final JLabel shotsLabel = Tema.rotulo("", Font.BOLD, 56f);
    private final JLabel messageLabel = Tema.rotulo("Clique em uma casa para disparar.", Font.PLAIN, 17f);
    private final JButton resultButton = Tema.botaoPrimario("Ver resultado");
    private final Celula[][] cells;
    private final JPanel fleetList = new JPanel();
    // pra cada tipo de navio, as linhas dele na lista da esquerda (pra tirar uma quando afundar)
    private final Map<TipoEmbarcacao, Deque<Component>> fleetRows = new EnumMap<>(TipoEmbarcacao.class);
    private final JLabel fleetTitle = Tema.legenda("Embarcações restantes", 13f);
    private final JLabel shotsTitle = Tema.legenda("Tiros restantes", 13f);
    private final JLabel shotsTotalLabel = Tema.rotulo("", Font.PLAIN, 15f); // o "de 30" embaixo do número
    private final int tamanhoCasa;     // tamanho "inicial" da casa, depois ela cresce com a janela
    private double escalaAtual = 1.0;  // quanto os textos laterais estão aumentados agora
    private boolean finished;          // depois que acaba não precisa mais perguntar "tem certeza?"

    public PainelJogo(Dificuldade difficulty, BiConsumer<Integer, Integer> onShoot,
                      Runnable onMenu, Runnable onRestart) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(16, 28, 18, 28));
        shotsLabel.setForeground(Tema.DESTAQUE);
        messageLabel.setForeground(Tema.TEXTO_SUAVE);

        // --- topo: Voltar e Reiniciar, alinhados na direita ---
        JButton backButton = Tema.botao("Voltar");
        JButton restartButton = Tema.botao("Reiniciar");
        // no meio da partida pergunta antes, pra ninguém perder o jogo sem querer
        backButton.addActionListener(event -> {
            if (finished || confirmar("Abandonar a partida e voltar ao menu?", "Voltar")) {
                onMenu.run();
            }
        });
        restartButton.addActionListener(event -> {
            if (finished || confirmar("Reiniciar a partida?", "Reiniciar")) {
                onRestart.run();
            }
        });
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        top.setOpaque(false);
        top.add(backButton);
        top.add(restartButton);
        add(top, BorderLayout.NORTH);

        // --- meio: frota | tabuleiro | tiros ---
        int size = difficulty.getBoardSize();
        tamanhoCasa = size <= 6 ? 72 : 56; // no 8x8 as casas começam menores pra caber
        cells = new Celula[size][size];
        // GridBagLayout porque preciso que só a coluna do meio estique
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH; // tudo grudado em cima
        c.weighty = 1;
        c.insets = new Insets(20, 10, 14, 10);

        c.gridx = 0;
        c.weightx = 0; // weightx 0 = a coluna não estica
        center.add(criarPainelFrota(difficulty), c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH; // o tabuleiro fica com todo o espaço que sobrar
        center.add(new AreaTabuleiro(size, onShoot), c);
        c.gridx = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        center.add(criarPainelTiros(), c);
        add(center, BorderLayout.CENTER);

        // Toda vez que a janela muda de tamanho recalcula o tamanho dos textos da lateral.
        // 1100x650 é o tamanho normal; o Math.max(1.0, ...) é pra nunca ficar menor que isso.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                aplicarEscala(Math.max(1.0, Math.min(getWidth() / 1100.0, getHeight() / 650.0)));
            }
        });

        // --- embaixo: mensagem do tiro e o "Ver resultado" (escondido até o jogo acabar) ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        footer.setOpaque(false);
        footer.add(messageLabel);
        resultButton.setVisible(false);
        footer.add(resultButton);
        add(footer, BorderLayout.SOUTH);

        shotsTotalLabel.setText("de " + difficulty.getShotLimit());
        updateShots(difficulty.getShotLimit());
    }

    // Cartão da esquerda: uma linha pra cada navio da partida (ícone + "N casas")
    private JPanel criarPainelFrota(Dificuldade difficulty) {
        JPanel painel = new JPanel(new BorderLayout(0, 14));
        painel.setOpaque(false);
        fleetTitle.setHorizontalAlignment(SwingConstants.LEFT);
        painel.add(fleetTitle, BorderLayout.NORTH);

        fleetList.setLayout(new BoxLayout(fleetList, BoxLayout.Y_AXIS)); // um embaixo do outro
        fleetList.setOpaque(false);
        for (TipoEmbarcacao type : TipoEmbarcacao.values()) {
            Deque<Component> linhas = new ArrayDeque<>();
            // a dificuldade diz quantos de cada tipo tem (ex.: 4 submarinos no fácil)
            for (int i = 0; i < difficulty.getShipCount(type); i++) {
                int casas = type.getLength();
                JLabel linha = Tema.rotulo(casas + (casas == 1 ? " casa" : " casas"), Font.PLAIN, 16f);
                linha.setHorizontalAlignment(SwingConstants.LEFT);
                linha.setIcon(new IconeAlinhado(Imagens.iconeFrota(type, 1.0), 1.0));
                // guardo o tipo do navio dentro do próprio label, o aplicarEscala precisa saber depois
                linha.putClientProperty(TipoEmbarcacao.class, type);
                linha.setIconTextGap(14);
                linha.setToolTipText(type.getDisplayName());
                linha.setAlignmentX(Component.LEFT_ALIGNMENT);
                linha.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
                fleetList.add(linha);
                linhas.add(linha);
            }
            fleetRows.put(type, linhas);
        }
        painel.add(fleetList, BorderLayout.CENTER);
        return Tema.cartao(painel); // coloca tudo dentro da caixa escura
    }

    // Os ícones têm larguras diferentes (porta-aviões é bem maior que submarino),
    // aí o texto "N casas" ficava torto. Esse ícone "de mentira" tem sempre a mesma
    // largura e desenha o navio de verdade centralizado dentro dele.
    private static final class IconeAlinhado implements javax.swing.Icon {
        private final javax.swing.Icon icone;
        private final int largura;

        private IconeAlinhado(javax.swing.Icon icone, double escala) {
            this.icone = icone;
            this.largura = (int) (180 * escala);
        }

        @Override
        public int getIconWidth() {
            return largura;
        }

        @Override
        public int getIconHeight() {
            return icone.getIconHeight();
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            icone.paintIcon(component, graphics, x + (largura - icone.getIconWidth()) / 2, y);
        }
    }

    /*
     * Parte do meio: letras em cima, números do lado e o tabuleiro.
     * Não usei layout pronto aqui porque precisava que o tabuleiro fosse sempre
     * quadrado e crescesse com a janela, então o doLayout calcula tudo na mão.
     */
    private final class AreaTabuleiro extends JPanel {
        private final int size;
        private final JLabel[] letras;
        private final JLabel[] numeros;
        private final JPanel board;

        private AreaTabuleiro(int size, BiConsumer<Integer, Integer> onShoot) {
            super(null); // layout null = eu mesmo posiciono os componentes no doLayout
            setOpaque(false);
            this.size = size;
            letras = new JLabel[size];
            numeros = new JLabel[size];
            for (int i = 0; i < size; i++) {
                letras[i] = Tema.rotulo(String.valueOf((char) ('A' + i)), Font.PLAIN, 36f);
                numeros[i] = Tema.rotulo(Integer.toString(i + 1), Font.PLAIN, 36f);
                letras[i].setForeground(Tema.TEXTO_SUAVE);
                numeros[i].setForeground(Tema.TEXTO_SUAVE);
                add(letras[i]);
                add(numeros[i]);
            }
            board = new PainelTabuleiro(new GridLayout(size, size));
            board.setBorder(BorderFactory.createLineBorder(new Color(84, 196, 255, 120), 2)); // contorno azul de 2px
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    Celula cell = new Celula();
                    cell.setToolTipText("Linha " + (row + 1) + ", coluna " + (char) ('A' + column));
                    final int selectedRow = row;
                    final int selectedColumn = column;
                    cell.addActionListener(event -> onShoot.accept(selectedRow, selectedColumn));
                    cells[row][column] = cell;
                    board.add(cell);
                }
            }
            add(board);
        }

        // tamanho que ele "gostaria" de ter: as casas + quase uma casa a mais pras letras/números
        @Override
        public Dimension getPreferredSize() {
            int lado = (int) (tamanhoCasa * (size + 0.9));
            return new Dimension(lado, lado);
        }

        // menor que isso as casas ficam pequenas demais pra clicar
        @Override
        public Dimension getMinimumSize() {
            int lado = (int) (36 * (size + 0.9));
            return new Dimension(lado, lado);
        }

        // Chamado sempre que a área muda de tamanho. Aqui é onde a mágica do "responsivo" acontece.
        @Override
        public void doLayout() {
            // Pega o lado menor (largura ou altura) e divide pelo número de casas.
            // O +0.9 é o espaço reservado pras letras/números (0.7 da casa) + um respiro (0.2).
            int casa = (int) Math.floor(Math.min(getWidth(), getHeight()) / (size + 0.9));
            casa = Math.max(casa, 20);
            int cabecalho = (int) (casa * 0.7);
            int espaco = (int) (casa * 0.2);
            int ladoTabuleiro = casa * size + 4; // +4 por causa da borda de 2px dos dois lados
            int larguraTotal = cabecalho + espaco + ladoTabuleiro;
            int x0 = (getWidth() - larguraTotal) / 2; // centraliza na horizontal
            int boardX = x0 + cabecalho + espaco;
            int boardY = cabecalho + espaco;

            board.setBounds(boardX, boardY, ladoTabuleiro, ladoTabuleiro);
            Font fonte = Tema.fonte(Font.BOLD, casa * 0.36f); // letra proporcional à casa
            for (int i = 0; i < size; i++) {
                // só troca a fonte se mudou, senão o setFont pede outro layout e fica num loop
                if (!fonte.equals(letras[i].getFont())) {
                    letras[i].setFont(fonte);
                    numeros[i].setFont(fonte);
                }
                // cada letra em cima da sua coluna, cada número do lado da sua linha
                letras[i].setBounds(boardX + 2 + i * casa, 0, casa, cabecalho);
                numeros[i].setBounds(x0, boardY + 2 + i * casa, cabecalho, casa);
            }
        }
    }

    // Aumenta os textos e ícones das laterais. escala 1.0 = tamanho normal, 1.5 = 50% maior...
    private void aplicarEscala(double escala) {
        if (Math.abs(escala - escalaAtual) < 0.02) {
            return; // mudou quase nada, não vale a pena refazer tudo
        }
        escalaAtual = escala;
        Font legenda = Tema.fonteEspacada(Font.BOLD, (float) (13 * escala), 0.14f);
        fleetTitle.setFont(legenda);
        shotsTitle.setFont(legenda);
        shotsTotalLabel.setFont(Tema.fonte(Font.PLAIN, (float) (15 * escala)));
        shotsLabel.setFont(Tema.fonte(Font.BOLD, (float) (56 * escala)));
        messageLabel.setFont(Tema.fonte(Font.PLAIN, (float) (17 * escala)));
        // os ícones dos navios também precisam ser recriados no tamanho novo
        for (Component componente : fleetList.getComponents()) {
            if (componente instanceof JLabel linha) {
                TipoEmbarcacao type = (TipoEmbarcacao) linha.getClientProperty(TipoEmbarcacao.class);
                linha.setFont(Tema.fonte(Font.PLAIN, (float) (16 * escala)));
                linha.setIcon(new IconeAlinhado(Imagens.iconeFrota(type, escala), escala));
            }
        }
        revalidate(); // avisa o Swing que os tamanhos mudaram
        repaint();
    }

    // Cartão da direita: "TIROS RESTANTES", o número grande e o "de 30"
    private JPanel criarPainelTiros() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setOpaque(false);
        shotsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        shotsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        shotsTotalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        shotsTotalLabel.setForeground(Tema.TEXTO_SUAVE);
        painel.add(shotsTitle);
        painel.add(Box.createVerticalStrut(10));
        painel.add(shotsLabel);
        painel.add(shotsTotalLabel);
        JPanel cartao = Tema.cartao(painel);
        cartao.setBorder(BorderFactory.createEmptyBorder(18, 34, 20, 34)); // um pouco mais largo que o padrão
        return cartao;
    }

    // caixinha de Sim/Não do Java, devolve true se clicou Sim
    private boolean confirmar(String mensagem, String titulo) {
        return JOptionPane.showConfirmDialog(this, mensagem, titulo, JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    public void markShot(int row, int column, ResultadoDisparo result, int shipPart, boolean horizontal) {
        Celula cell = cells[row][column];
        cell.setEnabled(false);
        if (result.getOutcome() == EstadoDisparo.HIT) {
            TipoEmbarcacao type = result.getShipType();
            String ship = type.getDisplayName();
            mostrarNavio(cell, type, shipPart, horizontal, false); // desenha o pedaço do navio dessa casa
            cell.marcar(ACERTO);
            if (result.isSunk()) {
                cell.setToolTipText(ship + " afundado");
                setMessage("Acertou e afundou: " + ship + "!");
                removerDaFrota(type); // some uma linha da lista da esquerda
            } else {
                cell.setToolTipText(ship + " atingido");
                setMessage("Acertou! Embarcação atingida: " + ship + ".");
            }
        } else {
            cell.setIcon(Imagens.iconeAgua(tamanhoCasa));
            cell.marcar(AGUA);
            cell.setToolTipText("Tiro na água");
            setMessage("Tiro na água.");
        }
    }

    // tira da lista um navio daquele tipo (tanto faz qual, são iguais)
    private void removerDaFrota(TipoEmbarcacao type) {
        Deque<Component> linhas = fleetRows.get(type);
        if (linhas != null && !linhas.isEmpty()) {
            fleetList.remove(linhas.pollLast());
            fleetList.revalidate();
            fleetList.repaint();
        }
    }

    public void revealShip(int row, int column, TipoEmbarcacao type, int shipPart, boolean horizontal) {
        Celula cell = cells[row][column];
        mostrarNavio(cell, type, shipPart, horizontal, true);
        cell.setToolTipText(type.getDisplayName() + " (não encontrado)");
    }

    // Se tem a imagem do navio, mostra só o pedaço daquela casa.
    // Se não tem, usa o desenho antigo (o navio inteiro pequenininho na casa).
    // revelado = fim de jogo mostrando os que o jogador não achou, por isso fica apagado (0.45)
    private void mostrarNavio(Celula cell, TipoEmbarcacao type, int shipPart, boolean horizontal,
                              boolean revelado) {
        BufferedImage imagem = Imagens.carregar(Imagens.arquivoNavio(type));
        if (imagem != null && shipPart >= 0) {
            cell.setPedaco(imagem, type.getLength(), shipPart, horizontal, revelado ? 0.45f : 1f);
        } else {
            cell.setIcon(Imagens.iconeCasa(type, revelado, tamanhoCasa));
        }
    }

    /** Trava o tabuleiro, mostra o desfecho e exibe o botão "Ver resultado". */
    public void showEnd(boolean won, Runnable onContinue) {
        finished = true;
        for (Celula[] line : cells) {
            for (Celula cell : line) {
                cell.setEnabled(false);
            }
        }
        setMessage(won ? "Frota destruída!" : "Acabaram os tiros! Veja onde a frota estava.");
        // o tabuleiro fica na tela e aparece o botão pra ir pro resultado quando a pessoa quiser
        resultButton.addActionListener(event -> onContinue.run());
        resultButton.setVisible(true);
        revalidate();
    }

    public void updateShots(int shotsRemaining) {
        shotsLabel.setText(Integer.toString(shotsRemaining));
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    /*
     * Cada casa do tabuleiro é um botão, mas desenhado do nosso jeito:
     * borda fininha, acende quando o mouse passa e muda de cor depois do tiro.
     * Também é ela que desenha o pedaço do navio quando acerta.
     */
    private static final class Celula extends JButton {
        private boolean hover;      // mouse em cima?
        private Color destaque;     // cor depois do tiro (vermelho ou "água"), null = ainda não atirou
        // dados do pedaço de navio que essa casa mostra (null = nenhum)
        private BufferedImage pedacoImagem;
        private int pedacoTotal;        // quantas casas o navio tem
        private int pedacoIndice;       // qual delas é essa (0 = primeira)
        private boolean pedacoHorizontal;
        private float pedacoOpacidade;

        private Celula() {
            setFocusable(false);
            // tira o visual padrão do botão, quem desenha é o paintComponent
            setContentAreaFilled(false);
            setBorderPainted(false);
            setBorder(BorderFactory.createEmptyBorder()); // sem isso sobrava uma margem e o navio ficava desalinhado
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)); // cursor de mira :)
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent event) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent event) {
                    hover = false;
                    repaint();
                }
            });
        }

        // ex.: setPedaco(imagemDoDestroier, 2, 1, true, 1f) = segunda metade do destroier, deitado
        private void setPedaco(BufferedImage imagem, int total, int indice, boolean horizontal, float opacidade) {
            pedacoImagem = imagem;
            pedacoTotal = total;
            pedacoIndice = indice;
            pedacoHorizontal = horizontal;
            pedacoOpacidade = opacidade;
            setIcon(null); // não precisa de ícone, o pedaço é desenhado direto
            repaint();
        }

        /*
         * Como funciona o recorte: imagina o navio inteiro desenhado ocupando
         * várias casas em fila. Cada casa desenha esse navio inteiro, só que
         * empurrado pra esquerda (pedacoIndice * tamanho da casa) e com um
         * "recorte" do tamanho da casa. Aí cada uma só mostra a sua fatia
         * e, juntas, formam o navio completo.
         */
        private void desenharPedaco(Graphics2D desenho, int w, int h) {
            Graphics2D g = (Graphics2D) desenho.create();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            if (pedacoOpacidade < 1f) {
                g.setComposite(AlphaComposite.SrcOver.derive(pedacoOpacidade));
            }
            int comprimento = w; // tamanho da casa no sentido do navio
            int largura = h;     // e no outro sentido
            if (!pedacoHorizontal) {
                // navio em pé: gira o "papel" 90° no sentido horário,
                // aí a frente do navio (que na imagem fica na direita) aponta pra baixo
                g.translate(w, 0);
                g.rotate(Math.PI / 2);
                comprimento = h;
                largura = w;
            }
            g.clipRect(0, 0, comprimento, largura); // nada sai pra fora da casa

            double total = pedacoTotal * comprimento;          // comprimento do navio inteiro em pixels
            int larguraNavio = (int) Math.round(total * 0.96); // deixa 2% de folga em cada ponta
            // altura proporcional à imagem, mas no máximo 85% da casa pra não encostar nas bordas
            int alturaNavio = (int) Math.min(
                    pedacoImagem.getHeight() * (larguraNavio / (double) pedacoImagem.getWidth()),
                    largura * 0.85);
            int x = (int) Math.round(total * 0.02) - pedacoIndice * comprimento; // o "empurrão" pra esquerda
            int y = (largura - alturaNavio) / 2;
            g.drawImage(pedacoImagem, x, y, larguraNavio, alturaNavio, null);
            g.dispose();
        }

        // pinta a casa depois do tiro e volta o cursor pro normal (não dá pra atirar de novo)
        private void marcar(Color cor) {
            destaque = cor;
            setCursor(Cursor.getDefaultCursor());
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D desenho = (Graphics2D) graphics.create();
            desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            // ordem importa: primeiro a cor do tiro, depois o navio por cima, depois a borda
            if (destaque != null) {
                desenho.setColor(destaque);
                desenho.fillRect(0, 0, w, h);
            }
            if (pedacoImagem != null) {
                desenharPedaco(desenho, w, h);
            }
            boolean ativo = hover && isEnabled(); // só acende se ainda dá pra atirar ali
            if (ativo) {
                desenho.setColor(new Color(84, 196, 255, 45));
                desenho.fillRect(0, 0, w, h);
            }
            // borda: fininha e apagada normalmente, mais grossa e azul com o mouse em cima
            desenho.setColor(ativo ? new Color(130, 216, 255, 230) : new Color(150, 200, 240, 38));
            desenho.setStroke(new BasicStroke(ativo ? 2f : 1f));
            desenho.drawRect(ativo ? 1 : 0, ativo ? 1 : 0, w - (ativo ? 3 : 1), h - (ativo ? 3 : 1));
            desenho.dispose();
            super.paintComponent(graphics); // isso desenha o ícone (ondinha da água), se tiver
        }
    }

    private static class PainelTabuleiro extends JPanel {
        private PainelTabuleiro(GridLayout layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D desenho = (Graphics2D) graphics.create();
            desenho.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // se tiver a foto do mar (tabuleiro.jpg) usa ela, senão cai no azul com ondinhas de antes
            BufferedImage textura = Imagens.carregar("tabuleiro.png");
            if (textura != null) {
                Imagens.desenharCobrindo(desenho, textura, getWidth(), getHeight());
                // passa uma camada preta meio transparente por cima pra escurecer a foto
                desenho.setColor(new Color(2, 8, 20, 105));
                desenho.fillRect(0, 0, getWidth(), getHeight());
                desenho.dispose();
                return;
            }
            desenho.setPaint(new GradientPaint(0, 0, new Color(20, 180, 255),
                    getWidth(), getHeight(), new Color(0, 130, 230)));
            desenho.fillRect(0, 0, getWidth(), getHeight());
            desenho.setColor(new Color(220, 245, 255, 40));
            for (int y = 12; y < getHeight(); y += 24) {
                for (int x = (y / 24 % 2) * 12; x < getWidth(); x += 32) {
                    desenho.drawArc(x, y, 22, 9, 15, 150);
                    desenho.drawArc(x + 10, y + 7, 18, 8, 20, 145);
                }
            }
            desenho.dispose();
        }
    }
}
