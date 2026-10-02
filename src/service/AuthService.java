// File: src/service/AuthService.java
package service;

import model.Peran;
import model.User;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AuthService {

    // salah 3 kali berturut-turut, akunnya dikunci 30 detik
    public static final int MAKS_PERCOBAAN = 3;
    private static final long DURASI_KUNCI_MILLIS = 30_000L;

    private final Map<String, User> daftarUser = new LinkedHashMap<>();
    private final Map<String, Integer> hitungGagal = new HashMap<>();
    private final Map<String, Long> kunciSampai = new HashMap<>();

    // user yang sedang login, null kalau belum ada yang login
    private User sesi;

    private static AuthService instance;

    private AuthService() {
        isiAkunAwal();
    }

    // dipakai Main supaya seluruh jendela login & aplikasi memakai data user yang sama
    public static synchronized AuthService get() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    private void isiAkunAwal() {
        daftarUser.put("admin", new User("admin", "Rina Kusuma", "admin123", Peran.ADMIN));
        daftarUser.put("kasir", new User("kasir", "Dimas Saputra", "kasir123", Peran.KASIR));
    }

    // returns user yang berhasil login, error kalau gagal
    public User login(String username, String sandi) {
        String kunci = normalisasi(username);
        if (kunci.isEmpty()) {
            throw new IllegalArgumentException("Username belum diisi.");
        }
        if (sandi == null || sandi.isEmpty()) {
            throw new IllegalArgumentException("Password belum diisi.");
        }

        int sisaKunci = getSisaKunciDetik(kunci);
        if (sisaKunci > 0) {
            throw new IllegalArgumentException("Akun terkunci, coba lagi dalam " + sisaKunci + " detik.");
        }

        User user = daftarUser.get(kunci);
        if (user == null || !user.cocokSandi(sandi)) {
            catatGagal(kunci);
            throw new IllegalArgumentException(pesanGagal(kunci));
        }

        hitungGagal.remove(kunci);
        kunciSampai.remove(kunci);
        sesi = user;
        return user;
    }

    private String pesanGagal(String kunci) {
        int sisaKunci = getSisaKunciDetik(kunci);
        if (sisaKunci > 0) {
            return "Username atau password salah. Akun terkunci " + DURASI_KUNCI_MILLIS / 1000 + " detik.";
        }
        return "Username atau password salah. Sisa percobaan: " + getSisaPercobaan(kunci) + ".";
    }

    // session diakhiri, tapi catatan percobaan gagal sengaja tidak dihapus
    // supaya mengetik username yang salah terus tidak bisa diulang tanpa batas
    public void logout() {
        sesi = null;
    }

    public User getSesi() {
        return sesi;
    }

    public boolean sudahLogin() {
        return sesi != null;
    }

    public void gantiSandi(String sandiLama, String sandiBaru, String sandiUlang) {
        User user = sesi;
        if (user == null) {
            throw new IllegalStateException("Belum ada user yang login.");
        }
        if (!user.cocokSandi(sandiLama)) {
            throw new IllegalArgumentException("Password lama salah.");
        }
        String baru = sandiBaru == null ? "" : sandiBaru;
        if (baru.isEmpty()) {
            throw new IllegalArgumentException("Password baru belum diisi.");
        }
        if (baru.length() < 4) {
            throw new IllegalArgumentException("Password baru minimal 4 karakter.");
        }
        if (!baru.equals(sandiUlang == null ? "" : sandiUlang)) {
            throw new IllegalArgumentException("Ulangi password tidak sama.");
        }
        user.setSandi(baru);
    }

    public int getSisaKunciDetik(String username) {
        String kunci = normalisasi(username);
        if (kunci.isEmpty()) {
            return 0;
        }
        Long sampai = kunciSampai.get(kunci);
        if (sampai == null) {
            return 0;
        }
        long sisa = sampai - System.currentTimeMillis();
        if (sisa <= 0) {
            kunciSampai.remove(kunci);
            hitungGagal.remove(kunci);
            return 0;
        }
        return (int) Math.ceil(sisa / 1000.0);
    }

    public int getSisaPercobaan(String username) {
        return Math.max(0, MAKS_PERCOBAAN - hitungGagal.getOrDefault(normalisasi(username), 0));
    }

    private void catatGagal(String kunci) {
        int gagal = hitungGagal.getOrDefault(kunci, 0) + 1;
        hitungGagal.put(kunci, gagal);
        if (gagal >= MAKS_PERCOBAAN) {
            kunciSampai.put(kunci, System.currentTimeMillis() + DURASI_KUNCI_MILLIS);
        }
    }

    private String normalisasi(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }
}