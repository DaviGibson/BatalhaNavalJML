package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.List;
import java.util.ArrayList;

public class Destroyer extends Ship {

    /*@ public normal_behavior
      @   assignable \everything;
      @*/
    public Destroyer() {
        super();
        this.size = 5;
    }

    /*@
      @ public behavior
      @   assignable \everything;
      @*/
    public Destroyer(List<CellButton> posicoes) throws CelulaInvalidaException {
        super();
        this.size = 5;
        this.isSunk = false;
        setPosition(posicoes);
    }

    /*@ also
      @ public normal_behavior
      @    requires row >= 0;
      @    requires col >= 0;
      @    ensures \result != null;
      @    ensures \result.size() >= 1 && \result.size() <= 3;
      @    assignable \nothing;
      @*/
    @Override
    public List<CellButton> attack(int row, int col) {
        List<CellButton> list = new ArrayList<>();

        // célula central
        list.add(new CellButton(row, col));

        // célula à direita
        if (col + 1 < 10) {
            list.add(new CellButton(row, col + 1));
        }

        // célula à esquerda
        if (col - 1 >= 0) {
            list.add(new CellButton(row, col - 1));
        }

        return list;
    }
}
