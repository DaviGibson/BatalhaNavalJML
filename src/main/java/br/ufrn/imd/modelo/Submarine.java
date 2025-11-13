package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a Submarine ship in the game.
 */
public class Submarine extends Ship {

    /**
     * Constructs a Submarine ship with a size of 3.
     */
    public Submarine() {
        super();
        this.size = 3;
    }

    /**
     * Constructs a Submarine ship with a size of 3 and places it on the board at the specified positions.
     *
     * @param posicoes The list of CellButton positions where the ship will be placed.
     * @throws CelulaInvalidaException If the ship overlaps with another ship on any cell.
     */
    public Submarine(List<CellButton> posicoes) throws CelulaInvalidaException {
        super();
        for (CellButton cell : posicoes) {
            if (cell.getState() == CellButton.State.SHIP) {
                throw new CelulaInvalidaException("Você tentou posicionar um navio numa célula onde outro navio já ocupa");
            } else {
                cell.setState(CellButton.State.SHIP);
            }
        }
        this.size = 3;
        position = posicoes;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Submarine attacks two cells: the clicked cell and the cell to its right.
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
        list.add(cell1);
        list.add(cell2);
        return list;
    }
}
