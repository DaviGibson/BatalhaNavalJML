package br.ufrn.imd.modelo;

import javafx.scene.Node;

/**
 * Represents a single cell in the game board.
 */
public class CellButton {
    private int row;
    private int col;
    private State state;
    private boolean isHit;
    private boolean isAimed;
    private Node node;

    /**
     * Enumeration representing possible states of a cell button.
     */
    public enum State {
        WATER, SHIP, HIT
    }

    /**
     * Constructs a CellButton with specified row and column coordinates.
     *
     * @param row The row coordinate of the cell button.
     * @param col The column coordinate of the cell button.
     */
    public CellButton(int row, int col) {
        this.row = row;
        this.col = col;
        this.state = State.WATER;
        this.isHit = false;
        this.isAimed = false;
    }

    /**
     * Marks the cell button as hit.
     */
    public void hit() {
        if (!isHit) {
            isHit = true;
        }
    }

    /**
     * Resets the state of the cell button to water and clears hit status.
     */
    public void reset() {
        state = State.WATER;
        isHit = false;
    }
    
    /**
     * Resets the state of the cell button to water and clears hit status.
     */
    public void undoShipPositioning() {
        if (this.getState() != CellButton.State.SHIP) {
        	state = State.WATER;
        }
    }

    /**
     * Sets the state of the cell button.
     *
     * @param state The state to set.
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Returns the current state of the cell button.
     *
     * @return The current state of the cell button.
     */
    public State getState() {
        return state;
    }

    /**
     * Checks if the cell button has been hit.
     *
     * @return True if the cell button is hit, otherwise false.
     */
    public boolean isHit() {
        return isHit;
    }

    /**
     * Sets whether the cell button is aimed.
     *
     * @param aimed True to set the cell button as aimed, false otherwise.
     */
    public void setAimed(boolean aimed) {
        isAimed = aimed;
    }

    /**
     * Checks if the cell button is aimed.
     *
     * @return True if the cell button is aimed, otherwise false.
     */
    public boolean getAimed() {
        return isAimed;
    }

    /**
     * Returns the row coordinate of the cell button.
     *
     * @return The row coordinate of the cell button.
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column coordinate of the cell button.
     *
     * @return The column coordinate of the cell button.
     */
    public int getCol() {
        return col;
    }

    /**
     * Returns the JavaFX Node associated with the cell button.
     *
     * @return The JavaFX Node associated with the cell button.
     */
    public Node getNode() {
        return node;
    }

    /**
     * Sets the JavaFX Node associated with the cell button.
     *
     * @param node The JavaFX Node to associate with the cell button.
     */
    public void setNode(Node node) {
        this.node = node;
    }
}
