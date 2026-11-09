package org.example.model;

import org.example.enums.TipoEmbarcacao;
import org.example.enums.EstadoDisparo;

public class ResultadoDisparo {
    private final EstadoDisparo outcome;
    private final TipoEmbarcacao shipType;

    public ResultadoDisparo(EstadoDisparo outcome, TipoEmbarcacao shipType) {
        this.outcome = outcome;
        this.shipType = shipType;
    }

    public EstadoDisparo getOutcome() {
        return outcome;
    }

    public TipoEmbarcacao getShipType() {
        return shipType;
    }
}
