package org.example.model;

import org.example.enums.EstadoCasa;
import org.example.enums.Dificuldade;
import org.example.enums.TipoEmbarcacao;
import org.example.enums.EstadoDisparo;
import org.example.service.ServicoJogo;
import org.example.service.ServicoPosicionamento;

import java.util.List;
import java.util.Random;

public class TesteRegrasJogo {
    public static void main(String[] args) {
        verifyDifficultyRules();
        verifyRandomFleetPlacement();
        verifyShotsAndVictory();
        verifyDefeatAndRepeatedShots();
        System.out.println("Todos os testes de regras passaram.");
    }

    private static void verifyDifficultyRules() {
        check(Dificuldade.EASY.getBoardSize() == 6, "Tabuleiro facil deve ser 6x6.");
        check(Dificuldade.EASY.getShotLimit() == 30, "Nivel facil deve ter 30 tiros.");
        check(Dificuldade.HARD.getBoardSize() == 8, "Tabuleiro dificil deve ser 8x8.");
        check(Dificuldade.HARD.getShotLimit() == 40, "Nivel dificil deve ter 40 tiros.");
        check(Dificuldade.EASY.getShipCount(TipoEmbarcacao.SUBMARINE) == 4, "Submarinos do nivel facil.");
        check(Dificuldade.EASY.getShipCount(TipoEmbarcacao.DESTROYER) == 2, "Destroieres do nivel facil.");
        check(Dificuldade.EASY.getShipCount(TipoEmbarcacao.CRUISER) == 1, "Cruzadores do nivel facil.");
        check(Dificuldade.EASY.getShipCount(TipoEmbarcacao.BATTLESHIP) == 1, "Encouracados do nivel facil.");
        check(Dificuldade.EASY.getShipCount(TipoEmbarcacao.AIRCRAFT_CARRIER) == 1, "Porta-avioes do nivel facil.");
        check(Dificuldade.HARD.getShipCount(TipoEmbarcacao.SUBMARINE) == 6, "Submarinos do nivel dificil.");
        check(Dificuldade.HARD.getShipCount(TipoEmbarcacao.DESTROYER) == 2, "Destroieres do nivel dificil.");
        check(Dificuldade.HARD.getShipCount(TipoEmbarcacao.CRUISER) == 2, "Cruzadores do nivel dificil.");
        check(Dificuldade.HARD.getShipCount(TipoEmbarcacao.BATTLESHIP) == 2, "Encouracados do nivel dificil.");
        check(Dificuldade.HARD.getShipCount(TipoEmbarcacao.AIRCRAFT_CARRIER) == 1, "Porta-avioes do nivel dificil.");

        Tabuleiro boundaryBoard = new Tabuleiro(6);
        check(!boundaryBoard.canPlaceShip(5, 2, 2, true), "Navio nao pode ultrapassar a borda horizontal.");
        check(!boundaryBoard.canPlaceShip(5, 2, 2, false), "Navio nao pode ultrapassar a borda vertical.");
        check(!boundaryBoard.canPlaceShip(7, 0, 0, true), "Navio nao pode ser maior que o tabuleiro.");
        boundaryBoard.placeShip(new Embarcacao(TipoEmbarcacao.DESTROYER), 0, 0, true);
        check(!boundaryBoard.canPlaceShip(2, 0, 1, true), "Navios nao podem se sobrepor.");
    }

    private static void verifyRandomFleetPlacement() {
        Tabuleiro easyBoard = new Tabuleiro(Dificuldade.EASY.getBoardSize());
        List<Embarcacao> easyFleet = new ServicoPosicionamento(new Random(1)).placeFleet(easyBoard, Dificuldade.EASY);
        check(easyFleet.size() == 9, "Frota facil deve ter 9 embarcacoes.");
        check(easyBoard.getRemainingShipCells() == 20, "Frota facil deve ocupar 20 casas.");
        verifyBoard(easyBoard, Dificuldade.EASY);

        Tabuleiro hardBoard = new Tabuleiro(Dificuldade.HARD.getBoardSize());
        List<Embarcacao> hardFleet = new ServicoPosicionamento(new Random(1)).placeFleet(hardBoard, Dificuldade.HARD);
        check(hardFleet.size() == 13, "Frota dificil deve ter 13 embarcacoes.");
        check(hardBoard.getRemainingShipCells() == 29, "Frota dificil deve ocupar 29 casas.");
        verifyBoard(hardBoard, Dificuldade.HARD);

        Tabuleiro secondEasyBoard = new Tabuleiro(Dificuldade.EASY.getBoardSize());
        new ServicoPosicionamento(new Random(2)).placeFleet(secondEasyBoard, Dificuldade.EASY);
        check(!layout(easyBoard).equals(layout(secondEasyBoard)),
                "Sementes aleatorias diferentes devem gerar posicionamentos diferentes.");
    }

    private static void verifyBoard(Tabuleiro board, Dificuldade difficulty) {
        int occupiedCells = 0;
        int[] counts = new int[TipoEmbarcacao.values().length];
        for (int row = 0; row < board.getSize(); row++) {
            for (int column = 0; column < board.getSize(); column++) {
                Embarcacao ship = board.getCell(row, column).getShip();
                if (ship != null) {
                    occupiedCells++;
                    counts[ship.getType().ordinal()]++;
                }
            }
        }
        check(occupiedCells == board.getRemainingShipCells(), "Casas ocupadas devem ser distintas.");
        for (TipoEmbarcacao type : TipoEmbarcacao.values()) {
            check(counts[type.ordinal()] == difficulty.getShipCount(type) * type.getLength(),
                    "Quantidade/posicionamento incorreto para " + type.getDisplayName() + ".");
        }
    }

    private static void verifyShotsAndVictory() {
        Tabuleiro board = new Tabuleiro(6);
        board.placeShip(new Embarcacao(TipoEmbarcacao.DESTROYER), 0, 0, true);
        ServicoJogo game = new ServicoJogo(Dificuldade.EASY, board);

        ResultadoDisparo hit = game.fire(0, 0);
        check(hit.getOutcome() == EstadoDisparo.HIT, "Disparo em embarcacao deve acertar.");
        check(hit.getShipType() == TipoEmbarcacao.DESTROYER, "Acerto deve identificar a embarcacao.");
        check(board.getCellStatus(0, 0) == EstadoCasa.HIT, "Casa atingida deve ser marcada.");
        check(game.getShotsRemaining() == 29 && game.getShotsUsed() == 1,
                "Acerto valido consome exatamente um tiro.");

        ResultadoDisparo miss = game.fire(0, 2);
        check(miss.getOutcome() == EstadoDisparo.MISS, "Disparo sem embarcacao deve errar.");
        check(board.getCellStatus(0, 2) == EstadoCasa.MISS, "Agua deve ser marcada.");
        check(game.getShotsRemaining() == 28, "Erro valido consome exatamente um tiro.");

        game.fire(0, 1);
        check(game.isFinished() && game.isWon(), "Afundar toda a frota deve vencer.");
        check(game.getShotsUsed() == 3, "Vitoria deve informar os tiros usados.");
        check(game.fire(1, 1).getOutcome() == EstadoDisparo.GAME_OVER,
                "Nao deve aceitar disparos apos o fim.");
    }

    private static void verifyDefeatAndRepeatedShots() {
        verifyDefeat(Dificuldade.EASY);
        verifyDefeat(Dificuldade.HARD);
    }

    private static void verifyDefeat(Dificuldade difficulty) {
        int size = difficulty.getBoardSize();
        Tabuleiro board = new Tabuleiro(size);
        board.placeShip(new Embarcacao(TipoEmbarcacao.SUBMARINE), size - 1, size - 1, true);
        ServicoJogo game = new ServicoJogo(difficulty, board);
        check(game.fire(0, 0).getOutcome() == EstadoDisparo.MISS, "Primeiro tiro deve atingir agua.");
        check(game.fire(0, 0).getOutcome() == EstadoDisparo.ALREADY_SHOT,
                "Casa ja atingida nao pode ser disparada novamente.");
        check(game.getShotsUsed() == 1 && game.getShotsRemaining() == difficulty.getShotLimit() - 1,
                "Disparo repetido nao consome tiro.");

        for (int row = 0; row < size && !game.isFinished(); row++) {
            for (int column = 0; column < size && !game.isFinished(); column++) {
                if ((row != 0 || column != 0) && (row != size - 1 || column != size - 1)) {
                    game.fire(row, column);
                }
            }
        }
        check(game.isFinished() && !game.isWon(), "Acabar a municao sem destruir a frota deve perder.");
        check(game.getShotsRemaining() == 0 && game.getShotsUsed() == difficulty.getShotLimit(),
                "Derrota deve consumir o limite de tiros.");
    }

    private static String layout(Tabuleiro board) {
        StringBuilder result = new StringBuilder();
        for (int row = 0; row < board.getSize(); row++) {
            for (int column = 0; column < board.getSize(); column++) {
                Embarcacao ship = board.getCell(row, column).getShip();
                result.append(ship == null ? '.' : ship.getType().ordinal());
            }
        }
        return result.toString();
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
