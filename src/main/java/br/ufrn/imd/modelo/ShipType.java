package br.ufrn.imd.modelo;

public enum ShipType {

    CORVETA(2),
    SUBMARINO(3),
    FRAGATA(4),
    DESTROYER(5);

    public final int size;

    /*@ 
      @  public invariant size > 0;
      @*/

    ShipType(int size) {
        this.size = size;
    }

    /*@ pure @*/
    public int getSize() {
        return size;
    }
}
