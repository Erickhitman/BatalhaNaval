package org.example.view;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

// Menu inicial. Deixei bem limpo: título, uma frase e os 3 botões no meio da tela.
public class PainelMenuPrincipal extends PainelFundo {
    public PainelMenuPrincipal(Runnable onPlay, Runnable onExit) {
        setLayout(new GridBagLayout()); // GridBag sem configurar nada = centraliza o conteúdo

        JLabel title = Tema.titulo("BATALHA NAVAL", 58f);
        JLabel subtitle = Tema.legenda("Afunde a frota antes que a munição acabe", 13f);

        JButton playButton = Tema.botaoPrimario("Jogar"); // o azul, que chama mais atenção
        JButton instructionsButton = Tema.botao("Como jogar");
        JButton exitButton = Tema.botao("Sair");

        add(montarConteudo(Tema.colunaBotoes(playButton, instructionsButton, exitButton), title, subtitle));

        playButton.addActionListener(event -> onPlay.run());
        instructionsButton.addActionListener(event -> JOptionPane.showMessageDialog(
                this,
                "Escolha uma dificuldade e clique nas casas para disparar.\n"
                        + "Acertos revelam a embarcação; tiros na água são marcados com ondas.\n"
                        + "Cada casa aceita apenas um disparo. Afunde toda a frota antes de acabar a munição.",
                "Como jogar",
                JOptionPane.INFORMATION_MESSAGE));
        exitButton.addActionListener(event -> onExit.run());
    }

    // Monta o bloco do meio: os textos um embaixo do outro e os botões no final.
    // As telas de dificuldade e resultado usam esse mesmo método, por isso não é private.
    static JPanel montarConteudo(JPanel buttons, JComponent... textos) {
        JPanel conteudo = new JPanel();
        conteudo.setOpaque(false);
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        for (int i = 0; i < textos.length; i++) {
            textos[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            if (i > 0) {
                conteudo.add(Box.createVerticalStrut(12)); // espacinho entre um texto e outro
            }
            conteudo.add(textos[i]);
        }
        buttons.setAlignmentX(Component.CENTER_ALIGNMENT);
        // trava os botões em 280px de largura, senão eles esticam até a largura do título
        Dimension tamanho = new Dimension(280, buttons.getPreferredSize().height);
        buttons.setPreferredSize(tamanho);
        buttons.setMaximumSize(tamanho);
        conteudo.add(Box.createVerticalStrut(48)); // espaço maior antes dos botões
        conteudo.add(buttons);
        return conteudo;
    }
}
