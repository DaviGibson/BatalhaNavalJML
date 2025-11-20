package br.ufrn.imd.controle;

/**
 * Exception lançada quando ocorre uma tentativa de posicionar um navio emcélulas onde já se tem outro navio.
 */
public class NaviosSobrepostosException extends Exception{
	/**
     * Construtor que recebe uma mensagem de erro específica.
     *
     * @param message a mensagem de erro detalhando a exceção.
     */
    public NaviosSobrepostosException(String message) {
        super(message);
    }
}
