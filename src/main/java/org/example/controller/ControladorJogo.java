package org.example.controller;

import org.example.enums.Dificuldade;
import org.example.enums.EstadoDisparo;
import org.example.model.ResultadoDisparo;
import org.example.service.ServicoJogo;
import org.example.view.JanelaJogo;

import javax.swing.JOptionPane;

public class ControladorJogo implements JanelaJogo.AcoesControladorJogo {
    private JanelaJogo window;
    private ServicoJogo game;

    public void start() {
        window = new JanelaJogo(this);
    }

    @Override
    public void showDifficulty() {
        window.showDifficulty();
    }

    @Override
    public void startGame(Dificuldade difficulty) {
        game = new ServicoJogo(difficulty);
        window.showGame(difficulty);
    }

    @Override
    public void fire(int row, int column) {
        ResultadoDisparo result = game.fire(row, column);
        if (result.getOutcome() == EstadoDisparo.ALREADY_SHOT) {
            return;
        }
        window.updateGame(row, column, result, game.getShotsRemaining());
        if (game.isFinished()) {
            window.showResult(game.isWon(), game.getShotsUsed(), game.getDifficulty());
        }
    }

    @Override
    public void exit() {
        int choice = JOptionPane.showConfirmDialog(window, "Deseja sair do jogo?",
                "Sair", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            window.dispose();
        }
    }
}
