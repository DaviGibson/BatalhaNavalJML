package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.List;
import java.util.ArrayList;

public class Corvette extends Ship {

    //@ public invariant size == 2;

    /*@ 
      @ public normal_behavior
      @    ensures this.size == 2;
      @    ensures !isSunk;
      @    assignable size, isSunk;
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
      @   ensures size == 2;
      @   ensures !isSunk;
      @   assignable size, isSunk, posicoes.*;
      @
      @ also
      @ public exceptional_behavior
      @   signals_only CelulaInvalidaException;
      @*/
    public Corvette(List<CellButton> posicoes) throws CelulaInvalidaException {
        super();
        this.size = 2;
        // delega validação e marcação para Ship.setPosition
        setPosition(posicoes);
    }

    /*@ 
      @ public normal_behavior
      @    requires row >= 0 && col >= 0;
      @    ensures \result != null;
      @    ensures \result.size() == 1;
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
