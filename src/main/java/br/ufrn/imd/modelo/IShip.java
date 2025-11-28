package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.List;

public interface IShip {

    /*@ public normal_behavior
      @   ensures \result >= 0;
      @   pure
      @*/
    int getSize();

    /*@ public normal_behavior
      @   ensures \result != null;
      @   pure
      @*/
    List<CellButton> getPosition();

    /*@ public normal_behavior
      @   pure
      @*/
    boolean isAlive();

    /*@ public normal_behavior
      @   pure
      @*/
    boolean isSunk();

    /*@ public normal_behavior
      @   requires 0 <= row && 0 <= col;
      @   pure
      @*/
    CellButton buscaCell(int row, int col);

    /*@ public normal_behavior
      @   requires position != null;
      @   requires position.size() == getSize();
      @   ensures true; 
      @*/

    /*@ public exceptional_behavior
      @   requires position != null;
      @   requires position.size() == getSize();
      @   signals_only CelulaInvalidaException;
      @*/
    
    /*@ pure @*/
    void setPosition(List<CellButton> position) throws CelulaInvalidaException;


    /*@ public normal_behavior
      @   requires 0 <= row && 0 <= col;
      @   ensures \result != null;
      @   pure
      @*/
    List<CellButton> attack(int row, int col);

    /*@ public normal_behavior @*/
    void place();
}
