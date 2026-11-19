package org.example.model;

import org.example.enums.EstadoCasa;

public class Casa {
    private Embarcacao ship;
    private EstadoCasa status = EstadoCasa.UNTOUCHED;
    // usado só pra parte visual: saber que pedaço do navio desenhar nessa casa
    private int shipPart;
    private boolean horizontal = true;

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
        setShip(ship, 0, true); // mantive essa versão pra nada que já chamava ela quebrar
    }

    // part 0 é a primeira casa do navio (a da esquerda se for horizontal, a de cima se for vertical)
    void setShip(Embarcacao ship, int part, boolean horizontal) {
        this.ship = ship;
        this.shipPart = part;
        this.horizontal = horizontal;
    }

    int getShipPart() {
        return shipPart;
    }

    boolean isHorizontal() {
        return horizontal;
    }

    void setStatus(EstadoCasa status) {
        this.status = status;
    }
}
