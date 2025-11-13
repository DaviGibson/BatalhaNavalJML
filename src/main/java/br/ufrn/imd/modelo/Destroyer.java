package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a Destroyer ship in the game.
 */
public class Destroyer extends Ship {

    /**
     * Constructs a Destroyer ship with a size of 5.
     */
    public Destroyer() {
        super();
        this.size = 5;
    }

    /**
     * Constructs a Destroyer ship with a size of 5 and places it on the board at the specified positions.
     *
     * @param posicoes The list of CellButton positions where the ship will be placed.
     * @throws CelulaInvalidaException If the ship overlaps with another ship on any cell.
     */
    public Destroyer(List<CellButton> posicoes) throws CelulaInvalidaException {
        super();
        for (CellButton cell : posicoes) {
            if (cell.getState() == CellButton.State.SHIP) {
                throw new CelulaInvalidaException("Você tentou posicionar um navio numa célula onde outro navio já ocupa");
            } else {
                cell.setState(CellButton.State.SHIP);
            }
        }
        this.size = 5;
        position = posicoes;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Destroyer attacks three cells: the clicked cell, the cell to its left, and the cell to its right.
     *
     * @param row The row coordinate to attack.
     * @param col The column coordinate to attack.
     * @return A list containing the attacked cells.
     */
    @Override
    public List<CellButton> attack(int row, int col) {
        List<CellButton> list = new ArrayList<>();
        CellButton cell1 = new CellButton(row, col);
        CellButton cell2 = new CellButton(row, col + 1);
        CellButton cell3 = new CellButton(row, col - 1);
        list.add(cell1);
        list.add(cell2);
        list.add(cell3);
        return list;
    }
}
