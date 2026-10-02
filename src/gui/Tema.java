// File: src/gui/Tema.java
package gui;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.TableRowSorter;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.util.Comparator;

// Kumpulan warna, font, dan helper tampilan. Dipakai bersama oleh semua
// jendela supaya ukuran dan warnanya tidak berbeda-beda antar layar.
public final class Tema {

    public static final Color LATAR = new Color(0xF5F6F7);
    public static final Color KARTU = new Color(0xFFFFFF);
    public static final Color GARIS = new Color(0xDCDEE1);
    public static final Color TEKS = new Color(0x1F2328);
    public static final Color TEKS_LUNAK = new Color(0x6B7076);
    public static final Color TEKS_GALAT = new Color(0xC62828);
    public static final Color TOMBOL_UTAMA = new Color(0x2F6FED);
    public static final Color TOMBOL_BAHAYA = new Color(0xE5484D);
    public static final Color TOMBOL_NETRAL = new Color(0x5B6168);

    private Tema() {
    }

    public static Font font(int ukuran, boolean tebal) {
        return new Font(Font.SANS_SERIF, tebal ? Font.BOLD : Font.PLAIN, ukuran);
    }

    public static JLabel label(String teks, int ukuran, boolean tebal, Color warna) {
        JLabel label = new JLabel(teks);
        label.setFont(font(ukuran, tebal));
        label.setForeground(warna);
        return label;
    }

    // garis pemisah tipis di atas panel, dipakai untuk judul jendela dialog
    public static void garisAtas(JPanel panel) {
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, GARIS),
                panel.getBorder()));
    }

    // LAF bawaan (Nimbus/Metal/Windows) selalu melapisi background dengan
    // gradient, jadi UI diganti ke BasicButtonUI supaya warnanya benar-benar polos
    public static void warnaTombol(AbstractButton btn, int rgb) {
        btn.setUI(new BasicButtonUI());
        btn.setBackground(new Color(rgb));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setMargin(new Insets(4, 10, 4, 10));
        btn.setFont(btn.getFont().deriveFont(Font.BOLD));
    }

    // DefaultTableModel tidak tahu tipe kolom, tanpa ini angka diurutkan sebagai teks
    // contoh stok 9, 10, 100, 2 akan urut jadi 10, 100, 2, 9
    public static void urutAngka(JTable tabel, int kolom) {
        urut(tabel, kolom, Comparator.comparingInt(v -> ((Number) v).intValue()));
    }

    // untuk kolom isi berupa teks format seperti "3.500", yang dibandingkan angkanya
    public static void urutRupiah(JTable tabel, int kolom) {
        urut(tabel, kolom, Comparator.comparingLong(v -> {
            String angka = String.valueOf(v).replaceAll("[^0-9]", "");
            return angka.isEmpty() ? 0L : Long.parseLong(angka);
        }));
    }

    private static void urut(JTable tabel, int kolom, Comparator<?> comparator) {
        tabel.setAutoCreateRowSorter(true);
        ((TableRowSorter<?>) tabel.getRowSorter()).setComparator(kolom, comparator);
    }
}