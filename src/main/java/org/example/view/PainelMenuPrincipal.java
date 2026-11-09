package org.example.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class PainelMenuPrincipal extends JPanel {
    public PainelMenuPrincipal(Runnable onPlay, Runnable onExit) {
        setBorder(BorderFactory.createEmptyBorder(60, 50, 60, 50));
        setLayout(new BorderLayout(20, 20));

        JLabel title = new JLabel("BATALHA NAVAL", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 34));
        add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(3, 1, 12, 12));
        JButton playButton = new JButton("Jogar");
        JButton instructionsButton = new JButton("Como jogar");
        JButton exitButton = new JButton("Sair");
        buttons.add(playButton);
        buttons.add(instructionsButton);
        buttons.add(exitButton);
        add(buttons, BorderLayout.CENTER);

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
}
