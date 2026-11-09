package org.example.service;

import org.example.enums.Dificuldade;
import org.example.enums.EstadoDisparo;
import org.example.model.Tabuleiro;
import org.example.model.ResultadoDisparo;

import java.util.Random;

public class ServicoJogo {
    private final Dificuldade difficulty;
    private final Tabuleiro board;
    private int shotsUsed;
    private boolean finished;
    private boolean won;

    public ServicoJogo(Dificuldade difficulty) {
        this(difficulty, new Random());
    }

    public ServicoJogo(Dificuldade difficulty, Random random) {
        if (difficulty == null || random == null) {
            throw new IllegalArgumentException("A dificuldade e o gerador aleatorio sao obrigatorios.");
        }
        this.difficulty = difficulty;
        this.board = new Tabuleiro(difficulty.getBoardSize());
        new ServicoPosicionamento(random).placeFleet(board, difficulty);
    }

    public ServicoJogo(Dificuldade difficulty, Tabuleiro board) {
        if (difficulty == null || board == null || board.getSize() != difficulty.getBoardSize()
                || board.getShipCount() == 0) {
            throw new IllegalArgumentException("Tabuleiro e dificuldade incompativeis.");
        }
        this.difficulty = difficulty;
        this.board = board;
    }

    public ResultadoDisparo fire(int row, int column) {
        if (finished) {
            return new ResultadoDisparo(EstadoDisparo.GAME_OVER, null);
        }
        ResultadoDisparo result = board.shoot(row, column);
        if (result.getOutcome() == EstadoDisparo.ALREADY_SHOT) {
            return result;
        }
        shotsUsed++;
        won = board.getShipCount() > 0 && board.getRemainingShipCells() == 0;
        finished = won || shotsUsed == difficulty.getShotLimit();
        return result;
    }

    public Dificuldade getDifficulty() {
        return difficulty;
    }

    public int getShotsUsed() {
        return shotsUsed;
    }

    public int getShotsRemaining() {
        return difficulty.getShotLimit() - shotsUsed;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isWon() {
        return won;
    }
}
