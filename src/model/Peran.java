// File: src/model/Peran.java
package model;

public enum Peran {

    ADMIN("Administrator", true),
    KASIR("Kasir", false);

    private final String label;
    private final boolean bolehGudang;

    Peran(String label, boolean bolehGudang) {
        this.label = label;
        this.bolehGudang = bolehGudang;
    }

    public String getLabel() {
        return label;
    }

    // tab GUDANG hanya untuk admin, kasir cukup transaksi & lihat laporan
    public boolean bolehBukaGudang() {
        return bolehGudang;
    }
}