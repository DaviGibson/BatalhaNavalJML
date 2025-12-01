package br.ufrn.imd.controle;

/**
 * Exception lançada quando ocorre uma tentativa de posicionar um navio emcélulas onde já se tem outro navio.
 */
public class NaviosSobrepostosException extends Exception{
	
	/*@ public normal_behavior
      @   requires message != null;
      @   ensures getMessage() == message;
      @*/
    public NaviosSobrepostosException(String message) {
        super(message);
    }
}
