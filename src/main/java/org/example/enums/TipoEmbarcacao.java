package org.example.enums;

public enum TipoEmbarcacao {
    SUBMARINE("Submarino", 1),
    DESTROYER("Destroier", 2),
    CRUISER("Cruzador", 3),
    BATTLESHIP("Encouraçado", 4),
    AIRCRAFT_CARRIER("Porta-aviões", 5);

    private final String displayName;
    private final int length;

    TipoEmbarcacao(String displayName, int length) {
        this.displayName = displayName;
        this.length = length;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLength() {
        return length;
    }
}
