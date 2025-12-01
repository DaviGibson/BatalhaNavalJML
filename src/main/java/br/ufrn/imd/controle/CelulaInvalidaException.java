
package br.ufrn.imd.controle;

public class CelulaInvalidaException extends Exception {

    /*@ public normal_behavior
      @   assignable \nothing;
      @*/
    public CelulaInvalidaException(String message) {
        super(message);
    }
}