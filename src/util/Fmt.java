// File: src/util/Fmt.java
package util;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Fmt {

    private static final Locale LOCALE = Locale.forLanguageTag("id-ID");

    private static final DateTimeFormatter FMT_TANGGAL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter FMT_TANGGAL_PENDEK = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_JAM = DateTimeFormatter.ofPattern("HH:mm");

    // pakai Locale id-ID supaya pemisah ribuan titik dan koma desimal
    public static String rp(double angka) {
        return "Rp" + angka(angka);
    }

    public static String angka(double angka) {
        NumberFormat nf = NumberFormat.getNumberInstance(LOCALE);
        nf.setMaximumFractionDigits(0);
        return nf.format(angka);
    }

    public static String tanggal(LocalDateTime waktu) {
        return waktu == null ? "-" : waktu.format(FMT_TANGGAL);
    }

    public static String tanggalPendek(LocalDateTime waktu) {
        return waktu == null ? "-" : waktu.format(FMT_TANGGAL_PENDEK);
    }

    public static String jam(LocalDateTime waktu) {
        return waktu == null ? "-" : waktu.format(FMT_JAM);
    }

    // teks jadi angka: "2.500" = 2500, "2500,50" = 2500,5, "2500.50" = 2500,5
    // koma selalu desimal, titik jadi desimal hanya kalau digit setelahnya 1-2,
    // selain itu titik dibaca sebagai pemisah ribuan
    public static double bacaAngka(String teks) {
        String s = bersihkan(teks);
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Angka tidak boleh kosong");
        }
        s = s.replace(',', '#');
        int titik = s.lastIndexOf('.');
        boolean desimal = titik >= 0 && titik == s.indexOf('.') && s.length() - titik - 1 <= 2;
        s = s.replace(".", "");
        if (desimal) {
            s = s.substring(0, titik) + '.' + s.substring(titik + 1);
        }
        s = s.replace('#', '.');
        try {
            double hasil = Double.parseDouble(s);
            if (Double.isNaN(hasil) || Double.isInfinite(hasil)) {
                throw new IllegalArgumentException("Angka tidak valid: " + teks);
            }
            return hasil;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Angka tidak valid: " + teks);
        }
    }

    // teks jadi bilangan bulat, tanda minus tetap diterima biar service yang menolak
    public static int bacaBilangan(String teks) {
        String s = bersihkan(teks).replace(".", "").replace(",", "");
        boolean minus = s.startsWith("-");
        if (minus) {
            s = s.substring(1);
        }
        if (s.isEmpty()) {
            throw new IllegalArgumentException("Jumlah tidak boleh kosong");
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                throw new IllegalArgumentException("Jumlah harus berupa angka");
            }
        }
        try {
            return minus ? -Integer.parseInt(s) : Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Jumlah terlalu besar: " + teks);
        }
    }

    private static String bersihkan(String teks) {
        return teks == null ? "" : teks.trim().replace(" ", "").replace(" ", "");
    }
}
