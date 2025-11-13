package br.ufrn.imd.controle;

/**
 * Exception lançada quando ocorre uma tentativa inválida de posicionar um navio em uma célula já ocupada.
 */
public class CelulaInvalidaException extends Exception {

    /**
     * Construtor que recebe uma mensagem de erro específica.
     *
     * @param message a mensagem de erro detalhando a exceção.
     */
    public CelulaInvalidaException(String message) {
        super(message);
    }
}
