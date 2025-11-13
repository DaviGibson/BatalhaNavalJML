package br.ufrn.imd.controle;

/**
 * Exception lançada quando ocorre uma tentativa de posicionar um navio fora dos limites do tabuleiro.
 */
public class NavioForaDoMapaException extends Exception {

    /**
     * Construtor que recebe uma mensagem de erro específica.
     *
     * @param message a mensagem de erro detalhando a exceção.
     */
    public NavioForaDoMapaException(String message) {
        super(message);
    }
}
