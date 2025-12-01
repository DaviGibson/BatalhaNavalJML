package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.List;

public interface IShip {

    /*@ public normal_behavior
      @   pure
      @*/
    int getSize();

    /*@ public normal_behavior
      @   ensures \result != null;
      @   pure
      @*/
    List<CellButton> getPosition();

    /*@ public behavior @*/
    boolean isAlive();

    /*@ public normal_behavior
      @   pure
      @*/
    boolean isSunk();

    /*@ public behavior
      @   requires 0 <= row && 0 <= col;
      @   pure
      @*/
    /*@ nullable @*/ CellButton buscaCell(int row, int col);

    /*@ public behavior
      @   requires position != null;
      @   signals (CelulaInvalidaException e) true;
      @*/
    void setPosition(List<CellButton> position) throws CelulaInvalidaException;

    /*@ public behavior
      @   requires 0 <= row && 0 <= col;
      @*/
    List<CellButton> attack(int row, int col);

    /*@ public behavior @*/
    void place();
}
