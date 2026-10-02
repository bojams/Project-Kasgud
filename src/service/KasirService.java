// File: src/service/KasirService.java
package service;

import model.Barang;
import model.Transaksi;
import util.Fmt;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KasirService {

    private final GudangService gudang;
    private final List<Transaksi> listTrx = new ArrayList<>();

    // keranjang kasir yang sedang berjalan
    private final List<Barang> keranjang = new ArrayList<>();
    private final List<Integer> keranjangQty = new ArrayList<>();

    private int counterTrx = 0;

    public KasirService(GudangService gudang) {
        this.gudang = gudang;
    }

    public void tambahKeranjang(String kode, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Qty harus lebih besar dari 0");
        }
        Barang brg = gudang.cari(kode);
        if (brg == null) {
            throw new IllegalArgumentException("Barang dengan kode " + kode + " tidak ditemukan");
        }

        int idx = cariIndexKeranjang(brg.getKode());
        // pakai long biar penjumlahan tidak berbalik jadi negatif kalau qty mendekati batas int
        long totalQty = (idx >= 0 ? keranjangQty.get(idx) : 0) + (long) qty;
        if (totalQty > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Qty " + brg.getKode() + " melebihi batas maksimum");
        }
        if (brg.getStok() < totalQty) {
            throw new IllegalArgumentException("Stok " + brg.getNama() + " tidak cukup, sisa " + brg.getStok());
        }

        if (idx >= 0) {
            keranjangQty.set(idx, (int) totalQty);
        } else {
            keranjang.add(brg);
            keranjangQty.add(qty);
        }
    }

    public void hapusItemKeranjang(int index) {
        if (index < 0 || index >= keranjang.size()) {
            throw new IllegalArgumentException("Item keranjang tidak ada");
        }
        keranjang.remove(index);
        keranjangQty.remove(index);
    }

    public void resetKeranjang() {
        keranjang.clear();
        keranjangQty.clear();
    }

    public List<Barang> getKeranjang() {
        return keranjang;
    }

    public List<Integer> getKeranjangQty() {
        return keranjangQty;
    }

    public boolean keranjangKosong() {
        return keranjang.isEmpty();
    }

    public double hitungTotal() {
        double total = 0;
        for (int i = 0; i < keranjang.size(); i++) {
            total += keranjang.get(i).getHarga() * keranjangQty.get(i);
        }
        return total;
    }

    public double hitungSubtotal(int index) {
        return keranjang.get(index).getHarga() * keranjangQty.get(index);
    }

    // proses pembayaran: cek uang, potong stok, simpan transaksi, kosongkan keranjang
    public Transaksi bayar(double uang) {
        if (keranjang.isEmpty()) {
            throw new IllegalArgumentException("Keranjang masih kosong");
        }
        double total = hitungTotal();
        if (Double.isNaN(uang) || Double.isInfinite(uang)) {
            throw new IllegalArgumentException("Nominal uang tidak valid");
        }
        if (uang < total) {
            throw new IllegalArgumentException("Uang kurang " + Fmt.rp(total - uang));
        }

        // semua item dicek dulu baru dipotong, supaya tidak ada barang yang kehilangan
        // stok kalau ada satu item gagal di tengah jalan
        for (int i = 0; i < keranjang.size(); i++) {
            Barang brg = gudang.cari(keranjang.get(i).getKode());
            if (brg == null) {
                throw new IllegalArgumentException("Barang " + keranjang.get(i).getNama()
                        + " sudah tidak ada di gudang, hapus dulu dari keranjang");
            }
            if (brg.getStok() < keranjangQty.get(i)) {
                throw new IllegalArgumentException("Stok " + brg.getNama() + " tidak cukup, sisa " + brg.getStok());
            }
        }
        for (int i = 0; i < keranjang.size(); i++) {
            gudang.kurangiStok(keranjang.get(i).getKode(), keranjangQty.get(i));
        }

        Transaksi trx = new Transaksi(generateIdTrx(), keranjang, keranjangQty, total, LocalDateTime.now());
        simpanTransaksi(trx);
        resetKeranjang();
        return trx;
    }

    public void simpanTransaksi(Transaksi trx) {
        listTrx.add(trx);
    }

    public List<Transaksi> getDaftarTransaksi() {
        return new ArrayList<>(listTrx);
    }

    public double getTotalPendapatan() {
        double total = 0;
        for (Transaksi trx : listTrx) {
            total += trx.getTotal();
        }
        return total;
    }

    public List<Terlaris> getBarangTerlaris() {
        return getBarangTerlaris(5);
    }

    // diambil dari transaksi yang sudah tersimpan, jadi barang yang dihapus dari gudang
    // tetap muncul di laporan dan totalnya memakai harga saat barang itu dibeli
    public List<Terlaris> getBarangTerlaris(int limit) {
        Map<String, Integer> mapQty = new HashMap<>();
        Map<String, Double> mapTotal = new HashMap<>();
        Map<String, Barang> mapBrg = new HashMap<>();
        for (Transaksi trx : listTrx) {
            for (int i = 0; i < trx.getJumlahItem(); i++) {
                Barang brg = trx.getItems().get(i);
                int qty = trx.getQty().get(i);
                mapQty.merge(brg.getKode(), qty, Integer::sum);
                mapTotal.merge(brg.getKode(), brg.getHarga() * qty, Double::sum);
                mapBrg.put(brg.getKode(), brg);
            }
        }

        List<Terlaris> list = new ArrayList<>();
        for (Map.Entry<String, Integer> e : mapQty.entrySet()) {
            list.add(new Terlaris(e.getKey(), mapBrg.get(e.getKey()).getNama(), e.getValue(), mapTotal.get(e.getKey())));
        }
        list.sort(Comparator.comparingInt(Terlaris::getQtyTerjual).reversed()
                .thenComparing(Comparator.comparing(Terlaris::getNama)));
        return list.size() > limit ? list.subList(0, limit) : list;
    }

    public String cetakStruk(Transaksi trx, double uang) {
        StringBuilder s = new StringBuilder();
        s.append("===== STRUK PEMBAYARAN =====\n");
        s.append("ID Transaksi : ").append(trx.getIdTrx()).append('\n');
        s.append("Tanggal      : ").append(Fmt.tanggal(trx.getTanggal())).append('\n');
        s.append("----------------------------\n");
        for (int i = 0; i < trx.getJumlahItem(); i++) {
            Barang brg = trx.getItems().get(i);
            int q = trx.getQty().get(i);
            s.append(String.format("%-20s x%-3d = %s%n", brg.getNama(), q, Fmt.rp(brg.getHarga() * q)));
        }
        s.append("----------------------------\n");
        s.append(String.format("%-14s: %s%n", "TOTAL", Fmt.rp(trx.getTotal())));
        s.append(String.format("%-14s: %s%n", "BAYAR", Fmt.rp(uang)));
        s.append(String.format("%-14s: %s%n", "KEMBALIAN", Fmt.rp(uang - trx.getTotal())));
        s.append("============================\n");
        return s.toString();
    }

    private String generateIdTrx() {
        counterTrx++;
        return String.format("TRX%04d", counterTrx);
    }

    private int cariIndexKeranjang(String kode) {
        for (int i = 0; i < keranjang.size(); i++) {
            if (keranjang.get(i).getKode().equalsIgnoreCase(kode)) {
                return i;
            }
        }
        return -1;
    }

    public static class Terlaris {
        private final String kode;
        private final String nama;
        private final int qtyTerjual;
        private final double total;

        public Terlaris(String kode, String nama, int qtyTerjual, double total) {
            this.kode = kode;
            this.nama = nama;
            this.qtyTerjual = qtyTerjual;
            this.total = total;
        }

        public String getKode() {
            return kode;
        }

        public String getNama() {
            return nama;
        }

        public int getQtyTerjual() {
            return qtyTerjual;
        }

        public double getTotal() {
            return total;
        }
    }
}
