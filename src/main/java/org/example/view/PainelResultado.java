package org.example.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class PainelResultado extends JPanel {
    public PainelResultado(boolean won, int shotsUsed, Runnable onPlayAgain, Runnable onMenu) {
        setBorder(BorderFactory.createEmptyBorder(55, 50, 55, 50));
        setLayout(new BorderLayout(20, 20));

        JLabel result = new JLabel(won ? "Vitória!" : "Fim de jogo: você perdeu.",
                SwingConstants.CENTER);
        result.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        add(result, BorderLayout.NORTH);

        JLabel usedShots = new JLabel("Tiros utilizados: " + shotsUsed, SwingConstants.CENTER);
        usedShots.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        add(usedShots, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(2, 1, 12, 12));
        JButton againButton = new JButton("Jogar novamente");
        JButton menuButton = new JButton("Voltar ao menu");
        buttons.add(againButton);
        buttons.add(menuButton);
        add(buttons, BorderLayout.SOUTH);

        againButton.addActionListener(event -> onPlayAgain.run());
        menuButton.addActionListener(event -> onMenu.run());
    }
}
