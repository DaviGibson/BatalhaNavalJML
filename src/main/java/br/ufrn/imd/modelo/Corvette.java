package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.List;
import java.util.ArrayList;

public class Corvette extends Ship {
    /*@
      @ public invariant size == 2;
      @*/

    /*@ 
      @ public normal_behavior
      @    ensures this.size == 2;
      @    ensures position != null && position.size() == 0;
      @    ensures !isSunk;
      @    assignable size, position, isSunk;
      @*/
    public Corvette() {
        super();
        this.size = 2;
    }

    /*@ 
      @ public normal_behavior
      @   requires posicoes != null;
      @   requires posicoes.size() == 2;
      @   requires (\forall int i; 0 <= i && i < posicoes.size();
      @                  posicoes.get(i) != null &&
      @                  posicoes.get(i).getState() != CellButton.State.SHIP);
      @
      @   ensures size == 2;
      @   ensures position == posicoes;
      @   ensures !isSunk;
      @   ensures (\forall int i; 0 <= i && i < position.size();
      @                  position.get(i).getState() == CellButton.State.SHIP);
      @   assignable size, position, isSunk, posicoes.*;
      @
      @ also
      @ public exceptional_behavior
      @   signals_only CelulaInvalidaException;
      @   signals (CelulaInvalidaException)
      @       (\exists int i; 0 <= i && i < posicoes.size();
      @           posicoes.get(i) != null &&
      @           posicoes.get(i).getState() == CellButton.State.SHIP);
      @   assignable \nothing;
      @*/
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

    /*@ 
      @ public normal_behavior
      @    requires row >= 0 && col >= 0;
      @    ensures \result != null;
      @    ensures \result.size() == 1;
      @    ensures \result.get(0).row == row && \result.get(0).col == col;
      @    assignable \nothing;
      @    pure
      @*/
    @Override
    public List<CellButton> attack(int row, int col) {
        List<CellButton> list = new ArrayList<>();
        CellButton cell = new CellButton(row, col);
        list.add(cell);
        return list;
    }

}
