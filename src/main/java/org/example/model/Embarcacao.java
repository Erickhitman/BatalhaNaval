package org.example.model;

import org.example.enums.TipoEmbarcacao;

public class Embarcacao {
    private final TipoEmbarcacao type;
    private int hits;

    public Embarcacao(TipoEmbarcacao type) {
        if (type == null) {
            throw new IllegalArgumentException("O tipo da embarcacao e obrigatorio.");
        }
        this.type = type;
    }

    public TipoEmbarcacao getType() {
        return type;
    }

    public void receiveHit() {
        if (!isSunk()) {
            hits++;
        }
    }

    public boolean isSunk() {
        return hits == type.getLength();
    }
}
