package org.example.view;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;

// Tela final: diz se ganhou ou perdeu e quantos tiros foram usados.
public class PainelResultado extends PainelFundo {
    public PainelResultado(boolean won, int shotsUsed, Runnable onPlayAgain, Runnable onMenu) {
        setLayout(new GridBagLayout());

        // textinho em cima do título: azul se ganhou, avermelhado se perdeu
        JLabel tag = Tema.legenda(won ? "Missão cumprida" : "Missão fracassada", 13f);
        tag.setForeground(won ? Tema.DESTAQUE : new Color(255, 120, 105));
        JLabel result = Tema.titulo(won ? "VITÓRIA" : "DERROTA", 60f);
        JLabel usedShots = Tema.rotulo("Tiros utilizados: " + shotsUsed, Font.PLAIN, 20f);
        usedShots.setForeground(Tema.TEXTO_SUAVE);

        JButton againButton = Tema.botaoPrimario("Jogar novamente");
        JButton menuButton = Tema.botao("Voltar ao menu");

        // os textos aparecem na ordem que são passados: tag, título, tiros
        add(PainelMenuPrincipal.montarConteudo(
                Tema.colunaBotoes(againButton, menuButton), tag, result, usedShots));

        againButton.addActionListener(event -> onPlayAgain.run());
        menuButton.addActionListener(event -> onMenu.run());
    }
}
