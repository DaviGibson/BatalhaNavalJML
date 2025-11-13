package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a Corvette ship in the game.
 */
public class Corvette extends Ship {

    /**
     * Constructs a Corvette ship with a size of 2.
     */
    public Corvette() {
        super();
        this.size = 2;
    }

    /**
     * Constructs a Corvette ship with a size of 2 and places it on the board at the specified positions.
     *
     * @param posicoes The list of CellButton positions where the ship will be placed.
     * @throws CelulaInvalidaException If the ship overlaps with another ship on any cell.
     */
    public Corvette(List<CellButton> posicoes) throws CelulaInvalidaException {
        super();
        for (CellButton cell : posicoes) {
            if (cell.getState() == CellButton.State.SHIP) {
                throw new CelulaInvalidaException("Você tentou posicionar um navio numa célula onde outro navio já ocupa");
            }
        }
        for (CellButton cell : posicoes) {
            cell.setState(CellButton.State.SHIP);
        }

        this.size = 2;
        position = posicoes;
    }

    /**
     * {@inheritDoc}
     *
     * @param row The row coordinate to attack.
     * @param col The column coordinate to attack.
     * @return A list containing the attacked cell.
     */
    @Override
    public List<CellButton> attack(int row, int col) {
        List<CellButton> list = new ArrayList<>();
        CellButton cell = new CellButton(row, col);
        list.add(cell);
        return list;
    }

}
