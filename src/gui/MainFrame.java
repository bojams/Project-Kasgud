// File: src/gui/MainFrame.java
package gui;

import service.GudangService;
import service.KasirService;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableRowSorter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.Comparator;
import javax.swing.plaf.basic.BasicButtonUI;

public class MainFrame extends JFrame {

    // service dibuat sekali di sini lalu dipakai bersama semua tab
    private final GudangService gudang = new GudangService();
    private final KasirService kasir = new KasirService(gudang);

    private final PanelKasir panelKasir;
    private final PanelGudang panelGudang;
    private final PanelLaporan panelLaporan;

    public MainFrame() {
        super("KasGud - Aplikasi Kasir & Gudang");
        setFontAwal();

        panelKasir = new PanelKasir(gudang, kasir);
        panelGudang = new PanelGudang(gudang);
        panelLaporan = new PanelLaporan(kasir);

        JTabbedPane tab = new JTabbedPane();
        tab.addTab("KASIR", panelKasir);
        tab.addTab("GUDANG", panelGudang);
        tab.addTab("LAPORAN", panelLaporan);
        tab.addChangeListener(e -> refreshData());

        // setiap panel memberitahu MainFrame kalau datanya berubah, biar tab lain ikut ter-update
        panelKasir.setOnDataBerubah(this::refreshData);
        panelGudang.setOnDataBerubah(this::refreshData);

        setContentPane(tab);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setSize(1000, 650);
        setLocationRelativeTo(null);

        refreshData();
    }

    public void refreshData() {
        panelGudang.refreshTabel();
        panelKasir.refreshTabel();
        panelKasir.refreshKeranjang();
        panelLaporan.refreshData();
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

    public static void warnaTombol(AbstractButton btn, int rgb) {
        // LAF bawaan (Nimbus/Metal/Windows) selalu melapisi background dengan gradient,
        // jadi UI diganti ke BasicButtonUI supaya warnanya benar-benar polos
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

    private void setFontAwal() {
        Font font = new Font("SansSerif", Font.PLAIN, 13);
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font.deriveFont(Font.BOLD));
        UIManager.put("TabbedPane.font", font);
        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("TitledBorder.font", font.deriveFont(Font.BOLD));
    }
}
