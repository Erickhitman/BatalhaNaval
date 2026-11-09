package org.example.model;

import org.example.enums.EstadoCasa;
import org.example.enums.EstadoDisparo;

public class Tabuleiro {
    private final int size;
    private final Casa[][] cells;
    private int shipCount;
    private int remainingShipCells;

    public Tabuleiro(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("O tamanho do tabuleiro deve ser positivo.");
        }
        this.size = size;
        this.cells = new Casa[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                cells[row][column] = new Casa();
            }
        }
    }

    public int getSize() {
        return size;
    }

    public int getShipCount() {
        return shipCount;
    }

    public int getRemainingShipCells() {
        return remainingShipCells;
    }

    public EstadoCasa getCellStatus(int row, int column) {
        return getCell(row, column).getStatus();
    }

    public boolean canPlaceShip(int length, int row, int column, boolean horizontal) {
        if (length <= 0 || length > size || !isInside(row, column)) {
            return false;
        }
        int rowStep = horizontal ? 0 : 1;
        int columnStep = horizontal ? 1 : 0;
        int lastRow = row + rowStep * (length - 1);
        int lastColumn = column + columnStep * (length - 1);
        if (!isInside(lastRow, lastColumn)) {
            return false;
        }
        for (int part = 0; part < length; part++) {
            if (cells[row + rowStep * part][column + columnStep * part].hasShip()) {
                return false;
            }
        }
        return true;
    }

    public void placeShip(Embarcacao ship, int row, int column, boolean horizontal) {
        if (ship == null || !canPlaceShip(ship.getType().getLength(), row, column, horizontal)) {
            throw new IllegalArgumentException("Posicao invalida ou ocupada para a embarcacao.");
        }
        int length = ship.getType().getLength();
        int rowStep = horizontal ? 0 : 1;
        int columnStep = horizontal ? 1 : 0;
        for (int part = 0; part < length; part++) {
            cells[row + rowStep * part][column + columnStep * part].setShip(ship);
        }
        shipCount++;
        remainingShipCells += length;
    }

    public ResultadoDisparo shoot(int row, int column) {
        Casa cell = getCell(row, column);
        if (cell.getStatus() != EstadoCasa.UNTOUCHED) {
            return new ResultadoDisparo(EstadoDisparo.ALREADY_SHOT, null);
        }
        if (!cell.hasShip()) {
            cell.setStatus(EstadoCasa.MISS);
            return new ResultadoDisparo(EstadoDisparo.MISS, null);
        }
        cell.setStatus(EstadoCasa.HIT);
        cell.getShip().receiveHit();
        remainingShipCells--;
        return new ResultadoDisparo(EstadoDisparo.HIT, cell.getShip().getType());
    }

    Casa getCell(int row, int column) {
        if (!isInside(row, column)) {
            throw new IndexOutOfBoundsException("Posicao fora dos limites do tabuleiro.");
        }
        return cells[row][column];
    }

    private boolean isInside(int row, int column) {
        return row >= 0 && row < size && column >= 0 && column < size;
    }
}
