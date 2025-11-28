package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.ArrayList;
import java.util.List;

/*@ 
  @ public invariant position != null;
  @ public invariant size >= 2 && size <= 5;
  @*/
public abstract class Ship implements IShip {

    protected int size;
    protected List<CellButton> position;
    protected boolean isSunk;

    /*@ public normal_behavior
      @   ensures position != null && position.isEmpty();
      @   ensures !isSunk;
      @ assignable position, isSunk;
      @*/
    public Ship() {
        this.position = new ArrayList<>();
        this.isSunk = false;
    }

    /*@ public normal_behavior
      @   ensures (\forall CellButton c; position.contains(c); c.getState() == CellButton.State.SHIP);
      @ assignable (\exists int i; 0 <= i && i < position.size(); position.get(i).state); // explanatory; may need adaptation
      @*/
    public void place() {
        for (CellButton cell : position) {
            cell.setState(CellButton.State.SHIP);
        }
    }

    /*@ public normal_behavior
      @   ensures \result == (\exists CellButton c; position.contains(c); !c.isHit()) ;
      @ pure
      @*/
    public boolean isAlive() {
        int cellsHit = 0;
        for (CellButton cell : position) {
            if (cell.isHit()) {
                cellsHit += 1;
            }
        }
        if (cellsHit == position.size()) {
            isSunk = true;
            return false;
        }
        return true;
    }

    /*@ public normal_behavior
      @   ensures (\forall CellButton c; position.contains(c) ==>
      @               (\result == (c.getRow() == row && c.getCol() == col) ? c : \old(\result)));
      @ pure
      @*/
    public CellButton buscaCell(int row, int col) {
        for (CellButton cell : position) {
            if (cell.getRow() == row && cell.getCol() == col) {
                return cell;
            }
        }
        return null;
    }

    /*@ public normal_behavior
      @   ensures \result == isSunk;
      @ pure
      @*/
    public boolean isSunk() { return isSunk; }

    /*@ public normal_behavior
      @   ensures \result == size;
      @ pure
      @*/
    public int getSize() { return size; }

    /*@ public normal_behavior
      @   ensures \result == position;
      @ pure
      @*/
    public List<CellButton> getPosition() { return position; }

    /*@ public normal_behavior
      @   requires position != null;
      @   requires (\forall CellButton c; position.contains(c) ==> c != null);
      @   requires position.size() == this.size;
      @   also
      @   exceptional_behavior
      @     requires (\exists CellButton c; position.contains(c); c.getState() == CellButton.State.SHIP);
      @     signals_only CelulaInvalidaException;
      @ assignable this.position, (\forall CellButton c; position.contains(c); c.getState());
      @   ensures this.position == position && (\forall CellButton c; position.contains(c); c.getState() == CellButton.State.SHIP);
      @*/
    public void setPosition(List<CellButton> position) throws CelulaInvalidaException {
        int successes = 0;
        for (CellButton cell : position) {
            if (cell.getState() == CellButton.State.SHIP) {
                throw new CelulaInvalidaException("Seu navio sobrepôs outro, posicione-o de novo.");
            } else {
                successes++;
            }
        }
        if (successes == size) {
            this.position = position;
            for (CellButton c : position) {
                c.setState(CellButton.State.SHIP);
            }
        }
    }

    /*@ public normal_behavior
      @   requires 0 <= row && 0 <= col;
      @   ensures \not_specified; // attack is concrete per-subclass: documented in subclass
      @*/
    abstract public List<CellButton> attack(int row, int col);
}
