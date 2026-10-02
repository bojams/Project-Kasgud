// File: src/service/GudangService.java
package service;

import model.Barang;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GudangService {

    // key = kode barang (huruf besar) supaya unik & pencarian by kode cepat
    private final Map<String, Barang> mapBarang = new HashMap<>();

    public GudangService() {
        seedAwal();
    }

    private void seedAwal() {
        mapBarang.put("B001", new Barang("B001", "Indomie Goreng", 3500, 100));
        mapBarang.put("B002", new Barang("B002", "Aqua 600ml", 3000, 50));
        mapBarang.put("B003", new Barang("B003", "Kopi Kapal Api", 1500, 200));
    }

    public void tambah(String kode, String nama, double harga, int stok) {
        String k = cekKode(kode);
        if (mapBarang.containsKey(k)) {
            throw new IllegalArgumentException("Kode barang " + k + " sudah dipakai");
        }
        if (harga <= 0) {
            throw new IllegalArgumentException("Harga harus lebih besar dari 0");
        }
        if (stok < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif");
        }
        mapBarang.put(k, new Barang(k, cekNama(nama), harga, stok));
    }

    public void update(String kode, String nama, double harga, int stok) {
        Barang brg = cari(kode);
        if (brg == null) {
            throw new IllegalArgumentException("Barang dengan kode " + kode + " tidak ditemukan");
        }
        if (harga <= 0) {
            throw new IllegalArgumentException("Harga harus lebih besar dari 0");
        }
        if (stok < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif");
        }
        // kode tidak diubah, jadi primary key tetap aman
        brg.setNama(cekNama(nama));
        brg.setHarga(harga);
        brg.setStok(stok);
    }

    public void hapus(String kode) {
        Barang brg = cari(kode);
        if (brg == null) {
            throw new IllegalArgumentException("Barang dengan kode " + kode + " tidak ditemukan");
        }
        mapBarang.remove(brg.getKode().toUpperCase());
    }

    // pencarian exact by kode, null kalau tidak ada
    public Barang cari(String kode) {
        if (kode == null || kode.trim().isEmpty()) {
            return null;
        }
        return mapBarang.get(kode.trim().toUpperCase());
    }

    public List<Barang> getAll() {
        return getDaftar("");
    }

    // keyword kosong = semua barang. Kalau ketemu kode persis, ambil 1 itu saja,
    // selain itu baru cocokkan sebagian nama/kode.
    public List<Barang> getDaftar(String keyword) {
        String kw = keyword == null ? "" : keyword.trim().toUpperCase();
        List<Barang> hasil = new ArrayList<>();
        for (Barang brg : mapBarang.values()) {
            if (kw.isEmpty() || brg.getKode().toUpperCase().equals(kw) || brg.getNama().toUpperCase().contains(kw)) {
                hasil.add(brg);
            }
        }
        hasil.sort(Comparator.comparing(Barang::getKode));
        return hasil;
    }

    public void kurangiStok(String kode, int jumlah) {
        Barang brg = cari(kode);
        if (brg == null) {
            throw new IllegalArgumentException("Barang dengan kode " + kode + " tidak ditemukan");
        }
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah harus lebih besar dari 0");
        }
        if (brg.getStok() < jumlah) {
            throw new IllegalArgumentException("Stok " + brg.getNama() + " tidak cukup, sisa " + brg.getStok());
        }
        brg.setStok(brg.getStok() - jumlah);
    }

    public void tambahStok(String kode, int jumlah) {
        Barang brg = cari(kode);
        if (brg == null) {
            throw new IllegalArgumentException("Barang dengan kode " + kode + " tidak ditemukan");
        }
        if (jumlah <= 0) {
            throw new IllegalArgumentException("Jumlah harus lebih besar dari 0");
        }
        // dijumlahkan manual, penjumlahan int bisa berbalik jadi negatif kalau sudah maksimum
        if (brg.getStok() > Integer.MAX_VALUE - jumlah) {
            throw new IllegalArgumentException("Stok " + brg.getKode() + " akan melebihi batas maksimum");
        }
        brg.setStok(brg.getStok() + jumlah);
    }

    public void setStok(String kode, int stokBaru) {
        Barang brg = cari(kode);
        if (brg == null) {
            throw new IllegalArgumentException("Barang dengan kode " + kode + " tidak ditemukan");
        }
        if (stokBaru < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif");
        }
        brg.setStok(stokBaru);
    }

    public int getJumlahBarang() {
        return mapBarang.size();
    }

    private String cekKode(String kode) {
        if (kode == null || kode.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode barang tidak boleh kosong");
        }
        String k = kode.trim().toUpperCase();
        if (!k.matches("[A-Z0-9]+")) {
            throw new IllegalArgumentException("Kode barang hanya boleh huruf dan angka");
        }
        return k;
    }

    private String cekNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama barang tidak boleh kosong");
        }
        if (nama.trim().length() > 50) {
            throw new IllegalArgumentException("Nama barang maksimal 50 karakter");
        }
        return nama.trim();
    }
}
