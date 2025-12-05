package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.List;
import java.util.ArrayList;

public class Corvette extends Ship {

    /*@ public normal_behavior
      @   assignable \everything;
      @*/
    public Corvette() {
        super();
        this.size = 2;
    }

    /*@
      @ public behavior
      @   assignable \everything;
      @*/
    public Corvette(List<CellButton> posicoes) throws CelulaInvalidaException {
        super();
        this.size = 2;
        this.isSunk = false;
        setPosition(posicoes);
    }

    /*@ also
      @ public normal_behavior
      @    requires row >= 0 && row < 10;
      @    requires col >= 0 && col < 10;
      @    ensures \result != null;
      @    ensures \result.size() == 1;
      @    assignable \nothing;
      @*/
    @Override
    public List<CellButton> attack(int row, int col) {
        List<CellButton> list = new ArrayList<>();
        CellButton cell = new CellButton(row, col);
        list.add(cell);
        return list;
    }

}
