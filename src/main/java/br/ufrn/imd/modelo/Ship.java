package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.ArrayList;
import java.util.List;

public abstract class Ship implements IShip {

    /*@ spec_public @*/ protected int size;
    /*@ spec_public @*/ protected List<CellButton> position;
    /*@ spec_public @*/ protected boolean isSunk;

    /*@ 
      @ public invariant size > 0;
      @ public invariant position != null;
      @ public invariant position.size() == size;
      @ public invariant 
      @   (\forall int i; 0 <= i && i < position.size();
      @       position.get(i) != null);

      @ public invariant 
      @   (\forall CellButton c; position.contains(c);
      @        c.getState() == CellButton.State.SHIP
      @     || c.getState() == CellButton.State.HIT);

      @ public invariant isSunk ==>
      @   (\forall CellButton c; position.contains(c);
      @        c.isHit());
      @*/

    /*@ public normal_behavior
      @   ensures !isSunk;
      @   ensures position.size() == 0;
      @   assignable position, isSunk;
      @*/
    public Ship() {
        this.position = new ArrayList<>();
        this.isSunk = false;
    }

    /*@ public normal_behavior
      @   requires position.size() == size;
      @   ensures (\forall CellButton c; position.contains(c);
      @               c.getState() == CellButton.State.SHIP);
      @   assignable 
      @       (\forall int i; 0 <= i && i < position.size();
      @            position.get(i).state);
      @*/
    public void place() {
        for (CellButton cell : position) {
            cell.setState(CellButton.State.SHIP);
        }
    }

    /*@ public normal_behavior
      @   ensures \result == !isSunk;
      @   ensures isSunk ==> (\forall CellButton c; position.contains(c);
      @                           c.isHit());
      @   assignable isSunk;
      @*/
    public boolean isAlive() {
        int hit = 0;
        for (CellButton c : position) {
            if (c.isHit()) hit++;
        }
        if (hit == position.size()) {
            isSunk = true;
            return false;
        }
        return true;
    }

    /*@ public normal_behavior
      @   requires row >= 0 && col >= 0;
      @   ensures (\exists CellButton c; position.contains(c);
      @               c.getRow() == row && c.getCol() == col)
      @            ==> \result != null;
      @   ensures (\forall CellButton c; position.contains(c);
      @               !(c.getRow() == row && c.getCol() == col))
      @            ==> \result == null;
      @*/

    /*@ pure @*/
    public CellButton buscaCell(int row, int col) {
        for (CellButton c : position) {
            if (c.getRow() == row && c.getCol() == col) return c;
        }
        return null;
    }

    /*@ pure @*/ public boolean isSunk() { return isSunk; }
    /*@ pure @*/ public int getSize() { return size; }
    /*@ pure @*/ public List<CellButton> getPosition() { return position; }

    /*@ public exceptional_behavior
      @   requires position != null;
      @   requires position.size() == size;
      @   requires (\forall int i; 0 <= i && i < position.size();
      @                position.get(i) != null);

      @   requires (\forall CellButton c; position.contains(c);
      @                 c.getState() != CellButton.State.SHIP);

      @   signals (CelulaInvalidaException e)
      @       (\exists CellButton c; position.contains(c);
      @           c.getState() == CellButton.State.SHIP);
      @*/

    /*@ public normal_behavior
      @   requires (\forall CellButton c; position.contains(c);
      @                 c.getState() != CellButton.State.SHIP);
      @   ensures this.position == position;
      @   ensures (\forall CellButton c; position.contains(c);
      @               c.getState() == CellButton.State.SHIP);
      @   assignable this.position,
      @              (\forall int i; 0 <= i && i < position.size();
      @                   position.get(i).state);
      @*/
    public void setPosition(List<CellButton> position) throws CelulaInvalidaException {
        for (CellButton cell : position) {
            if (cell.getState() == CellButton.State.SHIP) {
                throw new CelulaInvalidaException("Seu navio sobrepôs outro.");
            }
        }
        this.position = position;
        for (CellButton c : position) {
            c.setState(CellButton.State.SHIP);
        }
    }

    /*@ public normal_behavior
      @   requires row >= 0 && col >= 0;
      @   assignable \nothing;
      @   ensures \result != null;
      @*/

    /*@ pure @*/
    abstract public List<CellButton> attack(int row, int col);
}
