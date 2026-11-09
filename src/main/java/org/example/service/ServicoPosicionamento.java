package org.example.service;

import org.example.enums.Dificuldade;
import org.example.enums.TipoEmbarcacao;
import org.example.model.Tabuleiro;
import org.example.model.Embarcacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServicoPosicionamento {
    private final Random random;

    public ServicoPosicionamento() {
        this(new Random());
    }

    public ServicoPosicionamento(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("O gerador aleatorio e obrigatorio.");
        }
        this.random = random;
    }

    public List<Embarcacao> placeFleet(Tabuleiro board, Dificuldade difficulty) {
        if (board == null || difficulty == null || board.getSize() != difficulty.getBoardSize()) {
            throw new IllegalArgumentException("Tabuleiro e dificuldade incompativeis.");
        }
        List<Embarcacao> ships = new ArrayList<>();
        for (TipoEmbarcacao type : TipoEmbarcacao.values()) {
            for (int count = 0; count < difficulty.getShipCount(type); count++) {
                Embarcacao ship = new Embarcacao(type);
                placeShip(board, ship);
                ships.add(ship);
            }
        }
        return ships;
    }

    private void placeShip(Tabuleiro board, Embarcacao ship) {
        List<Posicao> availablePositions = new ArrayList<>();
        int size = board.getSize();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if (board.canPlaceShip(ship.getType().getLength(), row, column, true)) {
                    availablePositions.add(new Posicao(row, column, true));
                }
                if (ship.getType().getLength() > 1
                        && board.canPlaceShip(ship.getType().getLength(), row, column, false)) {
                    availablePositions.add(new Posicao(row, column, false));
                }
            }
        }
        if (availablePositions.isEmpty()) {
            throw new IllegalStateException("Nao ha espaco para posicionar " + ship.getType().getDisplayName() + ".");
        }
        Posicao position = availablePositions.get(random.nextInt(availablePositions.size()));
        board.placeShip(ship, position.row, position.column, position.horizontal);
    }

    private static class Posicao {
        private final int row;
        private final int column;
        private final boolean horizontal;

        private Posicao(int row, int column, boolean horizontal) {
            this.row = row;
            this.column = column;
            this.horizontal = horizontal;
        }
    }
}
