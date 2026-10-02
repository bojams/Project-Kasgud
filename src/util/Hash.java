// File: src/util/Hash.java
package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Hash {

    private static final SecureRandom ACAK = new SecureRandom();

    // salt = angka acak yang dicampur ke password sebelum di-hash.
    // Gunanya supaya dua password sama tidak menghasilkan hash yang sama,
    // jadi daftar user yang bocor tidak langsung bisa ditebak balik.
    public static String acakSalt() {
        byte[] b = new byte[16];
        ACAK.nextBytes(b);
        return Base64.getEncoder().withoutPadding().encodeToString(b);
    }

    public static String sha256(String sandi, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            md.update(sandi.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().withoutPadding().encodeToString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            // practically impossible, SHA-256 selalu ada di setiap JVM
            throw new IllegalStateException("Algoritma SHA-256 tidak tersedia", e);
        }
    }

    // MessageDigest.isEqual membandingkan tanpa waktu tetap, jadi selisih
    // waktu proses tidak bisa dipakai untuk menebak karakter hash
    public static boolean sama(String a, String b) {
        if (a == null || b == null) {
            return a == null && b == null;
        }
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}