package br.ufrn.imd.modelo;

import br.ufrn.imd.controle.CelulaInvalidaException;
import java.util.ArrayList;
import java.util.List;

public abstract class Ship implements IShip {

    /*@ spec_public @*/ protected int size;
    /*@ spec_public @*/ private List<CellButton> position;
    /*@ spec_public @*/ protected boolean isSunk;

    /*@
      @ public invariant position != null;
      @*/

    /*@ public normal_behavior
      @   ensures position != null && !isSunk;
      @   ensures size >= 0;
      @*/
    public Ship() {
        this.position = new ArrayList<CellButton>();
        this.isSunk = false;
        this.size = 0;
    }

    /*@ also
      @ public behavior
      @   requires position != null;
      @   assignable \everything;
      @*/
    public void place() {
        for (int i = 0; i < position.size(); i++) {
            CellButton c = position.get(i);
            c.setState(CellButton.State.SHIP);
        }
    }

    /*@ also
      @ public behavior
      @   requires position != null;
      @   assignable isSunk;
      @*/
    public boolean isAlive() {
        boolean existsNotHit = false;
        for (int i = 0; i < position.size(); i++) {
            if (!position.get(i).isHit()) {
                existsNotHit = true;
            }
        }
        if (position.size() > 0 && !existsNotHit) {
            isSunk = true;
            return false;
        }
        isSunk = false;
        return true;
    }

    /*@ also
      @ public behavior
      @   requires position != null;
      @   requires 0 <= row && 0 <= col;
      @   spec_pure
      @*/
    /*@ nullable @*/
    public CellButton buscaCell(int row, int col) {
        for (int i = 0; i < position.size(); i++) {
            CellButton c = position.get(i);
            if (c.getRow() == row && c.getCol() == col) return c;
        }
        return null;
    }

    /*@ spec_pure @*/ 
    public boolean isSunk() { return isSunk; }

    /*@ spec_pure @*/ 
    public int getSize() { return size; }

    /*@ also
      @ public normal_behavior
      @   ensures \result == position;
      @   spec_pure
      @*/
    public List<CellButton> getPosition() {
        return position;
    }

    /*@ also
      @ public behavior
      @   assignable position, size;
      @   signals (CelulaInvalidaException e) true;
      @*/
    public void setPosition(List<CellButton> posicoes) throws CelulaInvalidaException {
        if (posicoes == null) {
            this.position = new ArrayList<CellButton>();
            this.size = 0;
            return;
        }

        // valida sobreposição
        for (int i = 0; i < posicoes.size(); i++) {
            CellButton cell = posicoes.get(i);
            if (cell.getState() == CellButton.State.SHIP) {
                throw new CelulaInvalidaException("Posição inválida.");
            }
        }

        // copia as posições para a lista interna
        this.position = new ArrayList<CellButton>(posicoes);
        this.size = posicoes.size();

        // marca as células como SHIP
        for (int i = 0; i < position.size(); i++) {
            position.get(i).setState(CellButton.State.SHIP);
        }
    }

    /*@ also
      @ public behavior
      @ requires row >= 0 && col >= 0;
      @ assignable \nothing;
      @*/
    public abstract List<CellButton> attack(int row, int col);
}
