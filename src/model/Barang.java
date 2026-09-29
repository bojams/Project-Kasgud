// File: src/model/Barang.java
package model;

public class Barang {
    private String kode;
    private String nama;
    private double harga;
    private int stok;

    public Barang() {
    }

    public Barang(String kode, String nama, double harga, int stok) {
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
        this.stok = stok;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    // primary key = kode, jadi yang dibandingkan cukup kode saja
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Barang)) return false;
        Barang b = (Barang) o;
        if (kode == null) return b.kode == null;
        return kode.equalsIgnoreCase(b.kode);
    }

    @Override
    public int hashCode() {
        return kode == null ? 0 : kode.toUpperCase().hashCode();
    }

    @Override
    public String toString() {
        return kode + " - " + nama;
    }
}
