package br.ufrn.imd.modelo;

public class Game {

    /*@ spec_public @*/ private Player player1;
    /*@ spec_public @*/ private Player player2;

    // //@ public invariant player1 != null;
    // //@ public invariant player2 != null;

    /*@ public normal_behavior
      @   ensures player1 != null;
      @   ensures player2 != null;
      @*/
    public Game() {
        player1 = new Player();
        player2 = new Player();
    }

    /*@ public normal_behavior
      @   ensures \result == player1;
      @   ensures \result != null;
      @   spec_pure
      @*/
    public Player getPlayer1() {
        return player1;
    }

    /*@ public normal_behavior
      @   ensures \result == player2;
      @   ensures \result != null;
      @   spec_pure
      @*/
    public Player getPlayer2() {
        return player2;
    }
}
