package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.List;
import java.util.ArrayList;

public class Submarine extends Ship {

    /*@ public normal_behavior
      @   assignable \everything;
      @*/
    public Submarine() {
        super();
        this.size = 3;
    }

    /*@
      @ public behavior
      @   assignable \everything;
      @*/
    public Submarine(List<CellButton> posicoes) throws CelulaInvalidaException {
        super();
        this.size = 3;
        this.isSunk = false;
        setPosition(posicoes);
    }

    /*@ also
      @ public normal_behavior
      @    requires row >= 0;
      @    requires col >= 0;
      @    ensures \result != null;
      @    ensures \result.size() >= 1 && \result.size() <= 2;
      @    assignable \nothing;
      @*/
    @Override
    public List<CellButton> attack(int row, int col) {
        List<CellButton> list = new ArrayList<>();
        list.add(new CellButton(row, col));
        // só cria segunda célula se ainda estiver numa coluna válida do jogo
        if (col + 1 < 10) {
            list.add(new CellButton(row, col + 1));
        }
        return list;
    }

}
