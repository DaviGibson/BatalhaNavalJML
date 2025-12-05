package br.ufrn.imd.modelo;

import java.util.List;
import java.util.ArrayList;

public class Player {

    /*@ spec_public @*/ private Board board;
    /*@ spec_public @*/ private List<Ship> ships;

    /*@
      @ public invariant board != null;
      @ public invariant ships != null;
      @*/

    /*@ public normal_behavior
      @   ensures board != null;
      @   ensures ships != null && ships.isEmpty();
      @*/
    public Player() {
        this.board = new Board();
        this.ships = new ArrayList<>();
    }

    /**
     * Posiciona um navio no tabuleiro do jogador.
     *
     * @param ship navio a ser posicionado
     * @param row  linha inicial
     * @param col  coluna inicial
     * @param horizontal true para horizontal, false para vertical
     * @throws IllegalArgumentException se o posicionamento for inválido
     */
    /*@ public behavior
      @   requires ship != null;
      @   requires 0 <= row && row < 10;
      @   requires 0 <= col && col < 10;
      @   assignable \everything;
      @*/
    public void placeShip(Ship ship, int row, int col, boolean horizontal) {
        board.placeShip(ship, row, col, horizontal);
        ships.add(ship);
    }

    /*@ public normal_behavior
      @   ensures \result == board;
      @   spec_pure
      @*/
    public Board getBoard() {
        return board;
    }

    /*@ public normal_behavior
      @   ensures \result == ships;
      @   spec_pure
      @*/
    public List<Ship> getShips() {
        return ships;
    }
}
