package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.List;
import java.util.ArrayList;

public class Frigate extends Ship {

    /*@ public normal_behavior
      @   assignable \everything;
      @*/
    public Frigate() {
        super();
        this.size = 4;
    }

    /*@
      @ public behavior
      @   assignable \everything;
      @*/
    public Frigate(List<CellButton> posicoes) throws CelulaInvalidaException {
        super();
        this.size = 4;
        this.isSunk = false;
        setPosition(posicoes);
    }

    /*@ also
      @ public normal_behavior
      @    requires row >= 0;
      @    requires col >= 0;
      @    requires row < Integer.MAX_VALUE;
      @    requires row > Integer.MIN_VALUE;
      @    ensures \result != null;
      @    ensures \result.size() >= 1 && \result.size() <= 3;
      @    assignable \nothing;
      @*/
    @Override
    public List<CellButton> attack(int row, int col) {
        List<CellButton> list = new ArrayList<>();

        // sempre a célula central
        list.add(new CellButton(row, col));

        // célula abaixo, se fizer sentido dentro do tabuleiro lógico
        if (row + 1 < 10) {
            list.add(new CellButton(row + 1, col));
        }

        // célula acima, se fizer sentido dentro do tabuleiro lógico
        if (row - 1 >= 0) {
            list.add(new CellButton(row - 1, col));
        }

        return list;
    }
}
