package br.ufrn.imd.modelo;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a player in the naval battle game.
 * <p>
 * Each player has a board and a list of ships that they control. Players can
 * place ships on their board and retrieve the state of their board and ships.
 * </p>
 *
 * @see Board
 * @see Ship
 * @since 1.0
 */
public class Player {
    private Board board;
    private List<Ship> ships;

    /**
     * Creates a new player with an empty board and an empty list of ships.
     */
    public Player() {
        this.board = new Board();
        this.ships = new ArrayList<>();
    }

    /**
     * Places a ship on the player's board at the specified starting cell.
     * <p>
     * The ship is added to the player's list of ships after being placed on the board.
     * </p>
     *
     * @param ship the ship to be placed on the board
     * @param cellIni the initial cell where the ship will be placed
     * @throws IllegalArgumentException if the ship placement is invalid
     */
    public void placeShip(Ship ship, CellButton cellIni) {
        board.placeShip(ship, cellIni);
        ships.add(ship);
    }

    /**
     * Returns the player's board.
     * <p>
     * The board contains information about the placement of ships and the state of cells.
     * </p>
     *
     * @return the board associated with the player
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns the list of ships owned by the player.
     * <p>
     * The list includes all ships that have been placed on the player's board.
     * </p>
     *
     * @return a list of the player's ships
     */
    public List<Ship> getShips() {
        return ships;
    }
}
