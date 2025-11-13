package br.ufrn.imd.modelo;

/**
 * The {@code Game} class represents the main game structure, managing the two players
 * involved in the game. It provides access to the players and initializes them.
 *
 *
 * @since 1.0
 */
public class Game {
    /**
     * The first player in the game.
     */
    private Player player1;

    /**
     * The second player in the game, usually the computer.
     */
    private Player player2;

    /**
     * Constructs a new {@code Game} instance and initializes the two players.
     * <p>
     * The players are automatically created when a new {@code Game} object is instantiated.
     * </p>
     */
    public Game() {
        player1 = new Player();
        player2 = new Player();
    }

    /**
     * Gets the first player in the game.
     *
     * @return the first {@code Player} object.
     */
    public Player getPlayer1() {
        return player1;
    }

    /**
     * Gets the second player in the game.
     *
     * @return the second {@code Player} object.
     */
    public Player getPlayer2() {
        return player2;
    }
}