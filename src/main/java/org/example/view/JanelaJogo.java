package org.example.view;

import org.example.enums.Dificuldade;
import org.example.enums.TipoEmbarcacao;
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
        setMinimumSize(new Dimension(1000, 620)); // menor que isso o layout novo começa a apertar
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
        // o último parâmetro é o botão Reiniciar: começa outra partida na mesma dificuldade
        gamePanel = new PainelJogo(difficulty, actions::fire, this::showMainMenu,
                () -> actions.startGame(difficulty));
        showScreen(gamePanel);
    }

    public void updateGame(int row, int column, ResultadoDisparo result, int shotsRemaining,
                           int shipPart, boolean horizontal) {
        gamePanel.markShot(row, column, result, shipPart, horizontal);
        gamePanel.updateShots(shotsRemaining);
    }

    public void revealShip(int row, int column, TipoEmbarcacao type, int shipPart, boolean horizontal) {
        gamePanel.revealShip(row, column, type, shipPart, horizontal);
    }

    /** Mantem o tabuleiro na tela e libera o botao "Ver resultado". */
    public void showEnd(boolean won, int shotsUsed, Dificuldade difficulty) {
        gamePanel.showEnd(won, () -> showResult(won, shotsUsed, difficulty));
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
