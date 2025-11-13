package br.ufrn.imd.modelo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a game board consisting of cells and ships.
 */
public class Board {
    private CellButton[][] cells;
    private List<Ship> ships;
    private int numShips;

    /**
     * Constructs a Board with a grid of 10x10 cells and initializes ships list.
     */
    public Board() {
        cells = new CellButton[10][10];
        ships = new ArrayList<>();
        numShips = 0;
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                cells[row][col] = new CellButton(row, col);
            }
        }
    }

    /**
     * Places a ship on the board starting from the specified initial cell.
     *
     * @param ship The ship to place on the board.
     * @param cellIni The initial cell where the ship starts.
     */
    public void placeShip(Ship ship, CellButton cellIni) {
        ship.place();
        ships.add(ship);
        numShips++;
    }

    /**
     * Hits the cell at the specified row and column coordinates.
     * Updates the ship's status if any ship is hit.
     *
     * @param row The row coordinate of the cell to hit.
     * @param col The column coordinate of the cell to hit.
     */
    public void hitCells(int row, int col) {
        CellButton cell = cells[row][col];
        cell.hit();
        for (Ship ship : ships) {
            if (!ship.isAlive()) {
                numShips--;
            }
        }
    }

    /**
     * Searches for a ship cell at the specified column and row coordinates.
     * Marks the cell as hit if found.
     *
     * @param coluna The column coordinate to search.
     * @param altura The row coordinate to search.
     */
    public void buscarCellNavio(int coluna, int altura) {
        for (Ship ship : ships) {
            for (CellButton cell : ship.getPosition()) {
                if (cell.getCol() == coluna && cell.getRow() == altura) {
                    cell.hit();
                }
            }
        }
    }

    /**
     * Updates the list of ships, removing ships that are no longer alive.
     */
    public void attListaNavios() {
        Iterator<Ship> iterator = ships.iterator();
        while (iterator.hasNext()) {
            Ship ship = iterator.next();
            if (!ship.isAlive()) {
                iterator.remove();
            }
        }
    }

    /**
     * Retrieves the cell button at the specified row and column coordinates.
     * Throws an exception if the coordinates are out of the board's range.
     *
     * @param row The row coordinate of the cell button.
     * @param col The column coordinate of the cell button.
     * @return The CellButton object at the specified coordinates.
     * @throws ArrayIndexOutOfBoundsException if the coordinates are out of bounds.
     */
    public CellButton getCell(int row, int col) {
        if (row >= 10 || col >= 10 || row < 0 || col < 0) {
            throw new ArrayIndexOutOfBoundsException("Você mirou numa célula fora do alcance do tabuleiro");
        } else {
            return cells[row][col];
        }
    }

    /**
     * Sets the number of ships currently on the board.
     *
     * @param numShips The number of ships to set.
     */
    public void setNumShips(int numShips) {
        this.numShips = numShips;
    }

    /**
     * Retrieves the list of ships currently on the board.
     *
     * @return The list of Ship objects on the board.
     */
    public List<Ship> getShips() {
        return ships;
    }

    /**
     * Retrieves the number of ships currently on the board.
     *
     * @return The number of ships on the board.
     */
    public int getNumShips() {
        return numShips;
    }
}
