package br.ufrn.imd.controle;

/**
 * Exception lançada quando ocorre uma tentativa de posicionar
 * um navio fora dos limites do tabuleiro.
 */
public class NavioForaDoMapaException extends Exception {

    /*@ public normal_behavior
      @   requires message != null;
      @   ensures getMessage() == message;
      @*/
    public NavioForaDoMapaException(String message) {
        super(message);
    }
}
