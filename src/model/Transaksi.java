// File: src/model/Transaksi.java
package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Transaksi {
    private String idTrx;
    private List<Barang> items = new ArrayList<>();
    private List<Integer> qty = new ArrayList<>();
    private double total;
    private LocalDateTime tanggal;

    public Transaksi() {
    }

    public Transaksi(String idTrx, List<Barang> items, List<Integer> qty, double total, LocalDateTime tanggal) {
        this.idTrx = idTrx;
        this.items = new ArrayList<>(items);
        this.qty = new ArrayList<>(qty);
        this.total = total;
        this.tanggal = tanggal;
    }

    public String getIdTrx() {
        return idTrx;
    }

    public void setIdTrx(String idTrx) {
        this.idTrx = idTrx;
    }

    public List<Barang> getItems() {
        return items;
    }

    public void setItems(List<Barang> items) {
        this.items = new ArrayList<>(items);
    }

    public List<Integer> getQty() {
        return qty;
    }

    public void setQty(List<Integer> qty) {
        this.qty = new ArrayList<>(qty);
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public LocalDateTime getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDateTime tanggal) {
        this.tanggal = tanggal;
    }

    public int getJumlahItem() {
        return items.size();
    }

    public int getTotalQty() {
        int jml = 0;
        for (Integer q : qty) {
            jml += q;
        }
        return jml;
    }

    @Override
    public String toString() {
        return idTrx + " (" + tanggal + ") Rp" + total;
    }
}
