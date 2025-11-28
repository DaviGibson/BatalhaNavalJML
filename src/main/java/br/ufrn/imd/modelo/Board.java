package br.ufrn.imd.modelo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Board {
  	
	/*@ 
    @ // --- Invariantes globais do tabuleiro ---
    @ public invariant cells != null && cells.length == 10 
    @          && cells[0].length == 10;
    @ public invariant ships != null;
    @ public invariant numShips == ships.size();
    @ public invariant 
    @   (\forall int r, c; 0 <= r && r < 10 && 0 <= c && c < 10;
    @       cells[r][c] != null);
    @*/

    private CellButton[][] cells;
    private List<Ship> ships;
    private int numShips;

    /*@ public normal_behavior
      @   ensures cells.length == 10 && cells[0].length == 10;
      @   ensures ships.isEmpty();
      @   ensures numShips == 0;
      @   ensures (\forall int r, c; 0 <= r && r < 10 && 0 <= c && c < 10;
      @               cells[r][c].getRow() == r 
      @            && cells[r][c].getCol() == c
      @            && cells[r][c].getState() == CellButton.State.WATER);
      @ assignable this.cells, this.ships, this.numShips;
      @*/
    public Board() {
        cells = new CellButton[10][10];
        ships = new ArrayList<>();
        numShips = 0;

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                cells[row][col] = new CellButton(row, col);
            }
        }
    }

    /*@ public normal_behavior
      @   requires ship != null;
      @   requires cellIni != null;
      @   ensures ships.contains(ship);
      @   ensures numShips == \old(numShips) + 1;
      @   ensures (\forall CellButton c; ship.getPosition().contains(c);
      @                 c.getState() == CellButton.State.SHIP);
      @ assignable ships, numShips,
      @            (\forall int i; 0 <= i && i < ship.getPosition().size();
      @                ship.getPosition().get(i).state);
      @*/
    public void placeShip(Ship ship, CellButton cellIni) {
        ship.place();
        ships.add(ship);
        numShips++;
    }


    /*@ public normal_behavior
      @   requires 0 <= row && row < 10 && 0 <= col && col < 10;
      @   ensures cells[row][col].isHit();
      @   ensures (\exists Ship s; ships.contains(s);
      @               !s.isAlive()) ==> numShips == \old(numShips) - 1;
      @ assignable cells[row][col].isHit, numShips;
      @*/
    public void hitCells(int row, int col) {
        CellButton cell = cells[row][col];
        cell.hit();

        for (Ship ship : ships) {
            if (!ship.isAlive()) {
                numShips--;
            }
        }
    }


    /*@ public normal_behavior
      @   requires 0 <= coluna && coluna < 10;
      @   requires 0 <= altura && altura < 10;
      @   ensures (\exists Ship s; ships.contains(s);
      @               (\exists CellButton c; s.getPosition().contains(c);
      @                   c.getRow() == altura && c.getCol() == coluna))
      @          ==> cells[altura][coluna].isHit();
      @ assignable 
      @   (\forall int r, c; 0 <= r && r < 10 && 0 <= c && c < 10;
      @        cells[r][c].isHit);
      @*/
    public void buscarCellNavio(int coluna, int altura) {
        for (Ship ship : ships) {
            for (CellButton cell : ship.getPosition()) {
                if (cell.getCol() == coluna && cell.getRow() == altura) {
                    cell.hit();
                }
            }
        }
    }


    /*@ public normal_behavior
      @   ensures (\forall Ship s; ships.contains(s); s.isAlive());
      @   ensures numShips == ships.size();
      @ assignable ships, numShips;
      @*/
    public void attListaNavios() {
        Iterator<Ship> iterator = ships.iterator();
        while (iterator.hasNext()) {
            Ship ship = iterator.next();
            if (!ship.isAlive()) {
                iterator.remove();
            }
        }
        numShips = ships.size();
    }


    /*@ public normal_behavior
      @   requires 0 <= row && row < 10 && 0 <= col && col < 10;
      @   ensures \result == cells[row][col];
      @ pure
      @ also
      @ exceptional_behavior
      @   requires row < 0 || row >= 10 || col < 0 || col >= 10;
      @   signals_only ArrayIndexOutOfBoundsException;
      @*/
    public CellButton getCell(int row, int col) {
        if (row >= 10 || col >= 10 || row < 0 || col < 0) {
            throw new ArrayIndexOutOfBoundsException("Você mirou numa célula fora do alcance do tabuleiro");
        } else {
            return cells[row][col];
        }
    }


    /*@ public normal_behavior
      @   ensures this.numShips == numShips;
      @ assignable this.numShips;
      @*/
    public void setNumShips(int numShips) {
        this.numShips = numShips;
    }

    /*@ public normal_behavior
      @   ensures \result == ships;
      @ pure
      @*/
    public List<Ship> getShips() {
        return ships;
    }

    /*@ public normal_behavior
      @   ensures \result == numShips;
      @ pure
      @*/
    public int getNumShips() {
        return numShips;
    }
}
