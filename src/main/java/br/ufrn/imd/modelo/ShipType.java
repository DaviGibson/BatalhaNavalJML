package br.ufrn.imd.modelo;

public enum ShipType {
    CORVETA(2),
    SUBMARINO(3),
    FRAGATA(4),
    DESTROYER(5);

    private final int size;

    ShipType(int size) {
        this.size = size;
    }

    public int getSize() { return size; }
}

