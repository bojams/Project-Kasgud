// File: src/model/User.java
package model;

import util.Hash;

// Akun pengguna. Password tidak disimpan apa adanya, tapi disimpan dalam
// bentuk hash + salt, jadi tidak bisa dibaca langsung dari source code.
public class User {

    private final String username;
    private final String nama;
    private final Peran peran;

    private final String salt;
    private String hashSandi;

    public User(String username, String nama, String sandi, Peran peran) {
        this.username = username;
        this.nama = nama;
        this.peran = peran;
        this.salt = Hash.acakSalt();      // salt dibuat sekali saat akun dibuat
        this.hashSandi = Hash.sha256(sandi, salt);
    }

    public String getUsername() {
        return username;
    }

    public String getNama() {
        return nama;
    }

    public Peran getPeran() {
        return peran;
    }

    // dipakai untuk teks di panel atas: "Rina Kusuma  (Administrator)"
    public String getNamaLengkap() {
        return nama + "  (" + peran.getLabel() + ")";
    }

    // salt sengaja dipakai ulang, jadi hash lama otomatis tidak berlaku lagi
    public void setSandi(String sandiBaru) {
        this.hashSandi = Hash.sha256(sandiBaru, salt);
    }

    public boolean cocokSandi(String sandi) {
        if (sandi == null) {
            return false;
        }
        return Hash.sama(hashSandi, Hash.sha256(sandi, salt));
    }
}