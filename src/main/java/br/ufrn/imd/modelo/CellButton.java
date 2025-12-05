package br.ufrn.imd.modelo;

public class CellButton {

    

    /*@ spec_public @*/ private int row;
    /*@ spec_public @*/ private int col;
    /*@ spec_public @*/ private State state;
    /*@ spec_public @*/ private boolean isHit;
    /*@ spec_public @*/ private boolean isAimed;
    /*@ spec_public nullable @*/ private Object node;

    public enum State { WATER, SHIP, HIT }

    /*@ public normal_behavior
      @   requires 0 <= r;
      @   requires 0 <= c;
      @   ensures row == r && col == c;
      @   ensures state == State.WATER && !isHit && !isAimed;
      @   pure
      @*/
    public CellButton(int r, int c) {
        this.row = r;
        this.col = c;
        this.state = State.WATER;
        this.isHit = false;
        this.isAimed = false;
    }

    /*@ public normal_behavior
      @   ensures isHit == true;
      @   ensures state == \old(state);
      @   assignable isHit;
      @*/
    public void hit() {
        if (!isHit) isHit = true;
    }

    /*@ public normal_behavior
      @   ensures state == State.WATER && !isHit;
      @   assignable state, isHit;
      @*/
    public void reset() {
        state = State.WATER;
        isHit = false;
    }

    /*@ public normal_behavior
      @   ensures (\old(state) != State.SHIP) ==> state == State.WATER;
      @   ensures (\old(state) == State.SHIP) ==> state == State.SHIP;
      @   assignable state;
      @*/
    public void undoShipPositioning() {
        if (state != State.SHIP) {
            state = State.WATER;
        }
    }

    /*@ public normal_behavior
      @   requires isHit || s == State.WATER || s == State.SHIP;
      @   ensures state == s;
      @   assignable state;
      @*/
    public void setState(State s) { this.state = s; }

    /*@ public normal_behavior
      @   ensures \result == state;
      @   spec_pure
      @*/
    public State getState() { return state; }

    /*@ spec_pure @*/
    public boolean isHit() { return isHit; }

    /*@ public normal_behavior
      @   ensures this.isAimed == a;
      @   assignable isAimed;
      @*/
    public void setAimed(boolean a) { isAimed = a; }

    /*@ spec_pure @*/
    public boolean getAimed() { return isAimed; }

    /*@ spec_pure @*/
    public int getRow() { return row; }

    /*@ spec_pure @*/
    public int getCol() { return col; }

    /*@ public normal_behavior
      @   ensures \result == node;
      @*/
    /*@ spec_pure nullable @*/
    public Object getNode() {
        return node;
    }

    /*@ public normal_behavior
      @   ensures this.node == n;
      @   assignable node;
      @*/
    public void setNode(Object n) { this.node = n; }
}
