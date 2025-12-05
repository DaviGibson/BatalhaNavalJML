package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.List;

public interface IShip {

    /*@ public normal_behavior
      @   spec_pure
      @*/
    int getSize();

    /*@ public normal_behavior
      @   ensures \result != null;
      @   spec_pure
      @*/
    List<CellButton> getPosition();

    /*@ public behavior @*/
    boolean isAlive();

    /*@ public normal_behavior
      @   spec_pure
      @*/
    boolean isSunk();

    /*@ public behavior
      @   requires 0 <= row && 0 <= col;
      @   spec_pure
      @*/
    /*@ nullable @*/ CellButton buscaCell(int row, int col);

    /*@ public behavior
      @   requires position != null;
      @   signals (CelulaInvalidaException e) true;
      @*/
    void setPosition(List<CellButton> position) throws CelulaInvalidaException;

    /*@ public behavior
      @   requires 0 <= row && row < 10;
      @   requires 0 <= col && col < 10;
      @*/
    List<CellButton> attack(int row, int col);


    /*@ public behavior @*/
    void place();
}
