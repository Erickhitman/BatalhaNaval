package org.example.view;

import org.example.enums.Dificuldade;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.function.Consumer;

public class PainelDificuldade extends JPanel {
    public PainelDificuldade(Consumer<Dificuldade> onSelect, Runnable onBack) {
        setBorder(BorderFactory.createEmptyBorder(60, 50, 60, 50));
        setLayout(new BorderLayout(20, 20));

        JLabel title = new JLabel("Selecione a dificuldade", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(3, 1, 12, 12));
        JButton easyButton = new JButton("Fácil (6x6)");
        JButton hardButton = new JButton("Difícil (8x8)");
        JButton backButton = new JButton("Voltar ao menu");
        buttons.add(easyButton);
        buttons.add(hardButton);
        buttons.add(backButton);
        add(buttons, BorderLayout.CENTER);

        easyButton.addActionListener(event -> onSelect.accept(Dificuldade.EASY));
        hardButton.addActionListener(event -> onSelect.accept(Dificuldade.HARD));
        backButton.addActionListener(event -> onBack.run());
    }
}
