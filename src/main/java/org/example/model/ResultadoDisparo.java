package org.example.model;

import org.example.enums.TipoEmbarcacao;
import org.example.enums.EstadoDisparo;

public class ResultadoDisparo {
    private final EstadoDisparo outcome;
    private final TipoEmbarcacao shipType;
    private final boolean sunk;

    public ResultadoDisparo(EstadoDisparo outcome, TipoEmbarcacao shipType) {
        this(outcome, shipType, false);
    }

    public ResultadoDisparo(EstadoDisparo outcome, TipoEmbarcacao shipType, boolean sunk) {
        this.outcome = outcome;
        this.shipType = shipType;
        this.sunk = sunk;
    }

    public EstadoDisparo getOutcome() {
        return outcome;
    }

    public TipoEmbarcacao getShipType() {
        return shipType;
    }

    public boolean isSunk() {
        return sunk;
    }
}
