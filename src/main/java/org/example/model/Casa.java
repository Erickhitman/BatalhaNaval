package org.example.model;

import org.example.enums.EstadoCasa;

public class Casa {
    private Embarcacao ship;
    private EstadoCasa status = EstadoCasa.UNTOUCHED;

    public EstadoCasa getStatus() {
        return status;
    }

    public boolean hasShip() {
        return ship != null;
    }

    Embarcacao getShip() {
        return ship;
    }

    void setShip(Embarcacao ship) {
        this.ship = ship;
    }

    void setStatus(EstadoCasa status) {
        this.status = status;
    }
}
