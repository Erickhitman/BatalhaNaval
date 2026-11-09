package org.example.enums;

public enum Dificuldade {
    EASY("Fácil", 6, 30, new int[]{4, 2, 1, 1, 1}),
    HARD("Difícil", 8, 40, new int[]{6, 2, 2, 2, 1});

    private final String displayName;
    private final int boardSize;
    private final int shotLimit;
    private final int[] shipCounts;

    Dificuldade(String displayName, int boardSize, int shotLimit, int[] shipCounts) {
        this.displayName = displayName;
        this.boardSize = boardSize;
        this.shotLimit = shotLimit;
        this.shipCounts = shipCounts.clone();
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getShotLimit() {
        return shotLimit;
    }

    public int getShipCount(TipoEmbarcacao type) {
        return shipCounts[type.ordinal()];
    }
}
