package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a ship in the game.
 * Implements basic ship functionalities and defines abstract methods to be implemented by specific ship types.
 */
public abstract class Ship implements IShip {

    /**
     * Size of the ship (number of cells it occupies).
     */
    protected int size;

    /**
     * List of CellButton objects representing the positions occupied by the ship.
     */
    protected List<CellButton> position;

    /**
     * Boolean flag indicating if the ship is sunk.
     */
    protected boolean isSunk;

    /**
     * Constructor initializing the ship with an empty position list and not sunk.
     */
    public Ship() {
        this.position = new ArrayList<>();
        this.isSunk = false;
    }

    /**
     * Places the ship on the game board by setting the state of its cells to SHIP.
     */
    public void place() {
        for (CellButton cell : position) {
            cell.setState(CellButton.State.SHIP);
        }
    }

    /**
     * Checks if the ship is still alive (not sunk).
     *
     * @return true if the ship is still alive, false otherwise.
     */
    public boolean isAlive() {
        int cellsHit = 0;
        for (CellButton cell : position) {
            if (cell.isHit()) {
                cellsHit += 1;
                System.out.println("Cell " + cell.getCol() + " " + cell.getRow() + " Hit: " + cell.isHit());
            }
        }
        if (cellsHit == position.size()) {
            isSunk = true;
            return false;
        }
        return true;
    }

    /**
     * Finds a specific cell of the ship on the game board.
     *
     * @param row The row coordinate of the cell to find.
     * @param col The column coordinate of the cell to find.
     * @return The CellButton object representing the found cell, or null if not found.
     */
    public CellButton buscaCell(int row, int col) {
        for (CellButton cell : position) {
            if (cell.getRow() == row && cell.getCol() == col) {
                return cell;
            }
        }
        return null;
    }

    /**
     * Checks if the ship is sunk.
     *
     * @return true if the ship is sunk, false otherwise.
     */
    public boolean isSunk() {
        return isSunk;
    }

    /**
     * Retrieves the size of the ship.
     *
     * @return The size of the ship.
     */
    public int getSize() {
        return size;
    }

    /**
     * Retrieves the positions occupied by the ship on the game board.
     *
     * @return A list of CellButton objects representing the positions of the ship.
     */
    public List<CellButton> getPosition() {
        return position;
    }

    /**
     * Sets the positions of the ship on the game board.
     *
     * @param position The list of CellButton positions where the ship will be placed.
     * @throws CelulaInvalidaException If the ship overlaps with another ship on any cell.
     */
    public void setPosition(List<CellButton> position) throws CelulaInvalidaException {
        int successes = 0;
        for (CellButton cell : position) {
            if (cell.getState() == CellButton.State.SHIP) {
                throw new CelulaInvalidaException("Seu navio sobrepôs outro, posicione-o de novo.");
            } else {
                successes++;
            }
        }
        if (successes == size) {
            this.position = position;
            for (CellButton c : position) {
                c.setState(CellButton.State.SHIP);
            }
        }
    }

    /**
     * Abstract method to be implemented by subclasses.
     * Defines the behavior of the ship when it attacks.
     *
     * @param row The row coordinate to attack.
     * @param col The column coordinate to attack.
     * @return A list of CellButton objects representing the attacked cells.
     */
    abstract public List<CellButton> attack(int row, int col);
}
