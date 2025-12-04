package br.ufrn.imd.modelo;

import java.util.List;
import java.util.ArrayList;

public class Player {
    private Board board;
    private List<Ship> ships;

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
    public void placeShip(Ship ship, int row, int col, boolean horizontal) {
        board.placeShip(ship, row, col, horizontal);
        ships.add(ship);
    }

    public Board getBoard() {
        return board;
    }

    public List<Ship> getShips() {
        return ships;
    }
}
