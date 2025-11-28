package br.ufrn.imd.modelo;

/*@ public model import org.jmlspecs.annotation.*; @*/

public enum ShipType {
    CORVETA(2),
    SUBMARINO(3),
    FRAGATA(4),
    DESTROYER(5);

    private final int size;

    private ShipType(int size) { this.size = size; }

    /*@ public normal_behavior
      @   ensures \result == size;
      @*/
    public int getSize() { return size; }

    @Override
    public String toString() { return name(); }
}
