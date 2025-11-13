package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.List;

/**
 * Interface representing a ship in the game.
 */
public interface IShip {

    /**
     * Places the ship on the game board.
     */
    void place();

    /**
     * Checks if the ship is still alive (not sunk).
     *
     * @return true if the ship is still alive, false otherwise.
     */
    boolean isAlive();

    /**
     * Finds a specific cell of the ship on the game board.
     *
     * @param row The row coordinate of the cell to find.
     * @param col The column coordinate of the cell to find.
     * @return The CellButton object representing the found cell.
     */
    CellButton buscaCell(int row, int col);

    /**
     * Checks if the ship is sunk.
     *
     * @return true if the ship is sunk, false otherwise.
     */
    boolean isSunk();

    /**
     * Retrieves the size of the ship.
     *
     * @return The size of the ship.
     */
    int getSize();

    /**
     * Retrieves the positions occupied by the ship on the game board.
     *
     * @return A list of CellButton objects representing the positions of the ship.
     */
    List<CellButton> getPosition();

    /**
     * Sets the positions of the ship on the game board.
     *
     * @param position The list of CellButton positions where the ship will be placed.
     * @throws CelulaInvalidaException If the ship overlaps with another ship on any cell.
     */
    void setPosition(List<CellButton> position) throws CelulaInvalidaException;

    /**
     * Defines the behavior of the ship when it attacks.
     *
     * @param row The row coordinate to attack.
     * @param col The column coordinate to attack.
     * @return A list of CellButton objects representing the attacked cells.
     */
    List<CellButton> attack(int row, int col);
}
