package org.example.view;

import org.example.enums.Dificuldade;
import org.example.model.ResultadoDisparo;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;

public class JanelaJogo extends JFrame {
    private final AcoesControladorJogo actions;
    private PainelJogo gamePanel;

    public JanelaJogo(AcoesControladorJogo actions) {
        super("Batalha Naval");
        this.actions = actions;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(420, 400));
        showMainMenu();
        setLocationRelativeTo(null);
    }

    public void showMainMenu() {
        showScreen(new PainelMenuPrincipal(actions::showDifficulty, actions::exit));
    }

    public void showDifficulty() {
        showScreen(new PainelDificuldade(actions::startGame, this::showMainMenu));
    }

    public void showGame(Dificuldade difficulty) {
        gamePanel = new PainelJogo(difficulty, actions::fire, this::showMainMenu);
        showScreen(gamePanel);
    }

    public void updateGame(int row, int column, ResultadoDisparo result, int shotsRemaining) {
        gamePanel.markShot(row, column, result);
        gamePanel.updateShots(shotsRemaining);
    }

    public void showResult(boolean won, int shotsUsed, Dificuldade difficulty) {
        showScreen(new PainelResultado(won, shotsUsed,
                () -> actions.startGame(difficulty), this::showMainMenu));
    }

    private void showScreen(JPanel panel) {
        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public interface AcoesControladorJogo {
        void showDifficulty();

        void startGame(Dificuldade difficulty);

        void fire(int row, int column);

        void exit();
    }
}
