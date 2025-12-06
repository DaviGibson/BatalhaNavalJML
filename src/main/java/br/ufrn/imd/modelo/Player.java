package br.ufrn.imd.modelo;

import java.util.List;
import java.util.ArrayList;

public class Player {

    /*@ spec_public @*/ private Board board;
    /*@ spec_public @*/ private List<Ship> ships;

    /*@ public normal_behavior
      @   ensures board != null;
      @   ensures ships != null && ships.isEmpty();
      @*/
    public Player() {
        this.board = new Board();
        this.ships = new ArrayList<>();
    }

    /*@ public behavior
      @   requires board != null;
      @   requires ships != null;
      @   requires ship != null;
      @   requires 0 <= row && row < 10;
      @   requires 0 <= col && col < 10;
      @   assignable \everything;
      @*/
    public void placeShip(Ship ship, int row, int col, boolean horizontal) {
        //@ assert board != null;
        //@ assert ships != null;
    	// uso local com assert, para guiar o provador
        List<Ship> localShips = this.ships;
        //@ assert localShips != null;
        board.placeShip(ship, row, col, horizontal);
        localShips.add(ship);
    }

    /*@ public normal_behavior
      @   requires board != null;
      @   ensures \result != null;
      @   spec_pure
      @*/
    public Board getBoard() {
        return board;
    }

    /*@ public normal_behavior
      @   requires ships != null;
      @   ensures \result != null;
      @   spec_pure
      @*/
    public List<Ship> getShips() {
        return ships;
    }
}
