package br.ufrn.imd.modelo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Board {

    /*@ spec_public @*/ private CellButton[][] cells;
    /*@ spec_public @*/ private List<Ship> ships;
    /*@ spec_public @*/ private int numShips;

    /*@
      @ public invariant cells != null;
      @ public invariant ships != null;
      @ public invariant cells.length == 10;
      @ public invariant (\forall int r; 0 <= r && r < 10; cells[r] != null && cells[r].length == 10);
      @ public invariant (\forall int r,c;
      @                      0 <= r && r < 10 && 0 <= c && c < 10;
      @                      cells[r][c] != null);
      @*/

    /*@ public normal_behavior
      @   ensures cells != null;
      @   ensures ships != null && ships.isEmpty();
      @   ensures numShips == 0;
      @*/
    public Board() {
        cells = new CellButton[10][10];
        ships = new ArrayList<Ship>();
        numShips = 0;

        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                cells[r][c] = new CellButton(r, c);
            }
        }
    }

    /*@ public behavior
      @   requires cells != null;
      @   requires ships != null;
      @   requires ship != null;
      @   requires 0 <= row && row < 10;
      @   requires 0 <= col && col < 10;
      @*/
    public void placeShip(Ship ship, int row, int col, boolean horizontal) {
        int size = ship.getSize();
        if (size <= 0) {
            throw new IllegalArgumentException("Tamanho inválido do navio");
        }

        if (horizontal) {
            if (col + size > 10) {
                throw new IllegalArgumentException("Navio fora do tabuleiro");
            }
        } else {
            if (row + size > 10) {
                throw new IllegalArgumentException("Navio fora do tabuleiro");
            }
        }

        List<CellButton> pos = new ArrayList<CellButton>();
        if (horizontal) {
            for (int c = col; c < col + size; c++) {
                if (cells[row][c].getState() == CellButton.State.SHIP) {
                    throw new IllegalArgumentException("Navio sobreposto");
                }
                pos.add(cells[row][c]);
            }
        } else {
            for (int r = row; r < row + size; r++) {
                if (cells[r][col].getState() == CellButton.State.SHIP) {
                    throw new IllegalArgumentException("Navio sobreposto");
                }
                pos.add(cells[r][col]);
            }
        }

        try {
            ship.setPosition(pos);
        } catch (br.ufrn.imd.controle.CelulaInvalidaException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }

        ships.add(ship);
        //@ assert ships.get(ships.size()-1) != null;
        numShips++;
    }

    /*@ public behavior
      @   requires cells != null;
      @   requires 0 <= row && row < 10;
      @   requires 0 <= col && col < 10;
      @*/
    public void hitCells(int row, int col) {
        CellButton cell = cells[row][col];
        //@ assert cell != null;
        //@ assert cell.state != null;
        cell.hit();

        for (Ship ship : ships) {
            //@ assert ship != null;
            //@ assert ship.position != null;
            if (!ship.isAlive() && numShips > 0) {
                numShips--;
            }
        }
    }

    /*@ public behavior
      @   requires ships != null;
      @   requires 0 <= coluna && coluna < 10;
      @   requires 0 <= altura && altura < 10;
      @*/
    public void buscarCellNavio(int coluna, int altura) {
        for (Ship ship : ships) {
            //@ assert ship != null;
            List<CellButton> pos = ship.getPosition();
            for (CellButton cell : pos) {
                //@ assert cell != null;
                //@ assert cell.getState() != null;
                //@ assert !cell.isHit() ==> (cell.getState() == CellButton.State.WATER || cell.getState() == CellButton.State.SHIP);
                if (cell.getCol() == coluna && cell.getRow() == altura) {
                    cell.hit();
                }
            }
        }
    }

    /*@ public behavior
      @   requires ships != null;
      @*/
    public void attListaNavios() {
        Iterator<Ship> iterator = ships.iterator();
        while (iterator.hasNext()) {
            Ship ship = iterator.next();
            if (!ship.isAlive() && numShips > 0) {
                iterator.remove();
                numShips--;
            }
        }
    }

    /*@ public normal_behavior
      @   requires cells != null;
      @   requires 0 <= row && row < 10;
      @   requires 0 <= col && col < 10;
      @   ensures \result == cells[row][col];
      @   ensures \result != null;
      @   spec_pure
      @*/
    public CellButton getCell(int row, int col) {
        return cells[row][col];
    }

    /*@ public normal_behavior
      @   requires ships != null;
      @   requires (\forall int i;
      @               0 <= i && i < ships.size();
      @               ships.get(i) != null);
      @*/
    public void setNumShips(int numShips) {
        this.numShips = numShips;
    }

    /*@ public normal_behavior
      @   ensures \result == ships;
      @   spec_pure
      @*/
    public List<Ship> getShips() {
        return ships;
    }

    /*@ public normal_behavior
      @   ensures \result == numShips;
      @   spec_pure
      @*/
    public int getNumShips() {
        return numShips;
    }
}
