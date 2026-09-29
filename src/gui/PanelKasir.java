// File: src/gui/PanelKasir.java
package gui;

import model.Barang;
import model.Transaksi;
import service.GudangService;
import service.KasirService;
import util.Fmt;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

public class PanelKasir extends JPanel {

    private final GudangService gudang;
    private final KasirService kasir;
    private Runnable onDataBerubah = () -> {
    };

    private final JTextField txtCari = new JTextField(20);
    private final JTextField txtQty = new JTextField(5);

    private final JButton btnTambah = new JButton("Tambah ke Keranjang");
    private final JButton btnHapus = new JButton("Hapus Item");
    private final JButton btnReset = new JButton("Reset");
    private final JButton btnBayar = new JButton("BAYAR");

    private final DefaultTableModel modelBarang = new DefaultTableModel(
            new Object[]{"Kode", "Nama", "Harga", "Stok"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel modelKeranjang = new DefaultTableModel(
            new Object[]{"Nama", "Harga", "Qty", "Subtotal"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tblBarang = new JTable(modelBarang);
    private final JTable tblKeranjang = new JTable(modelKeranjang);
    private final JLabel lblTotal = new JLabel("TOTAL : Rp0", SwingConstants.CENTER);

    public PanelKasir(GudangService gudang, KasirService kasir) {
        this.gudang = gudang;
        this.kasir = kasir;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelBarang(), panelKeranjang());
        split.setResizeWeight(0.55);
        split.setDividerLocation(540);
        add(split, BorderLayout.CENTER);

        pasangEvent();
        refreshTabel();
        refreshKeranjang();
    }

    public void setOnDataBerubah(Runnable aksi) {
        this.onDataBerubah = aksi;
    }

    private JPanel panelBarang() {
        MainFrame.warnaTombol(btnTambah, 0x4CAF50);
        txtQty.setPreferredSize(new Dimension(60, 25));
        txtQty.setHorizontalAlignment(JTextField.CENTER);
        txtQty.setText("1");

        tblBarang.setRowHeight(24);
        tblBarang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        MainFrame.urutRupiah(tblBarang, 2);
        MainFrame.urutAngka(tblBarang, 3);
        tblBarang.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer kanan = new DefaultTableCellRenderer();
        kanan.setHorizontalAlignment(SwingConstants.RIGHT);
        tblBarang.getColumnModel().getColumn(2).setCellRenderer(kanan);
        tblBarang.getColumnModel().getColumn(3).setCellRenderer(kanan);

        JPanel panelAtas = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panelAtas.add(new JLabel("Cari:"));
        panelAtas.add(txtCari);
        panelAtas.add(new JLabel("Qty:"));
        panelAtas.add(txtQty);
        panelAtas.add(btnTambah);

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Daftar Barang"));
        panel.add(panelAtas, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblBarang), BorderLayout.CENTER);
        return panel;
    }

    private JPanel panelKeranjang() {
        MainFrame.warnaTombol(btnHapus, 0xF44336);
        MainFrame.warnaTombol(btnReset, 0x2196F3);
        MainFrame.warnaTombol(btnBayar, 0x4CAF50);

        tblKeranjang.setRowHeight(24);
        tblKeranjang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblKeranjang.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer kanan = new DefaultTableCellRenderer();
        kanan.setHorizontalAlignment(SwingConstants.RIGHT);
        tblKeranjang.getColumnModel().getColumn(1).setCellRenderer(kanan);
        tblKeranjang.getColumnModel().getColumn(2).setCellRenderer(kanan);
        tblKeranjang.getColumnModel().getColumn(3).setCellRenderer(kanan);

        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 16f));
        lblTotal.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        panelTombol.add(btnHapus);
        panelTombol.add(btnReset);
        panelTombol.add(btnBayar);

        JPanel panelBawah = new JPanel(new BorderLayout());
        panelBawah.add(lblTotal, BorderLayout.CENTER);
        panelBawah.add(panelTombol, BorderLayout.SOUTH);

        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Keranjang Belanja"));
        panel.add(new JScrollPane(tblKeranjang), BorderLayout.CENTER);
        panel.add(panelBawah, BorderLayout.SOUTH);
        return panel;
    }

    private void pasangEvent() {
        // pencarian real-time, tabel ikut berubah begitu diketik
        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshTabel();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshTabel();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshTabel();
            }
        });

        btnTambah.addActionListener(e -> aksiTambahKeranjang());
        btnHapus.addActionListener(e -> aksiHapusItem());
        btnReset.addActionListener(e -> aksiReset());
        btnBayar.addActionListener(e -> aksiBayar());
    }

    public void refreshTabel() {
        tblBarang.clearSelection();
        modelBarang.setRowCount(0);
        for (Barang brg : gudang.getDaftar(txtCari.getText())) {
            modelBarang.addRow(new Object[]{brg.getKode(), brg.getNama(), Fmt.angka(brg.getHarga()), brg.getStok()});
        }
    }

    public void refreshKeranjang() {
        modelKeranjang.setRowCount(0);
        for (int i = 0; i < kasir.getKeranjang().size(); i++) {
            Barang brg = kasir.getKeranjang().get(i);
            int qty = kasir.getKeranjangQty().get(i);
            modelKeranjang.addRow(new Object[]{
                    brg.getNama(), Fmt.rp(brg.getHarga()), qty, Fmt.rp(kasir.hitungSubtotal(i))});
        }
        lblTotal.setText("TOTAL : " + Fmt.rp(kasir.hitungTotal()));
    }

    private void aksiTambahKeranjang() {
        int barisTampilan = tblBarang.getSelectedRow();
        if (barisTampilan < 0) {
            pesan("Pilih barang di tabel dulu.");
            return;
        }
        String kode = (String) modelBarang.getValueAt(tblBarang.convertRowIndexToModel(barisTampilan), 0);
        try {
            kasir.tambahKeranjang(kode, Fmt.bacaBilangan(txtQty.getText()));
            txtQty.setText("1");
            refreshKeranjang();
            refreshTabel();
        } catch (Exception e) {
            pesanGagal(e);
        }
    }

    private void aksiHapusItem() {
        int baris = tblKeranjang.getSelectedRow();
        if (baris < 0) {
            pesan("Pilih item keranjang dulu.");
            return;
        }
        try {
            kasir.hapusItemKeranjang(baris);
            refreshKeranjang();
            refreshTabel();
        } catch (Exception e) {
            pesanGagal(e);
        }
    }

    private void aksiReset() {
        if (kasir.keranjangKosong()) {
            return;
        }
        int jawab = JOptionPane.showConfirmDialog(this, "Kosongkan keranjang?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (jawab == JOptionPane.YES_OPTION) {
            kasir.resetKeranjang();
            refreshKeranjang();
        }
    }

    private void aksiBayar() {
        if (kasir.keranjangKosong()) {
            pesan("Keranjang masih kosong.");
            return;
        }
        double total = kasir.hitungTotal();
        String input = JOptionPane.showInputDialog(this,
                "Total belanja : " + Fmt.rp(total) + "\nUang yang dibayar :", Fmt.angka(total));
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        double uang;
        try {
            uang = Fmt.bacaAngka(input);
        } catch (Exception e) {
            pesanGagal(e);
            return;
        }

        try {
            Transaksi trx = kasir.bayar(uang);
            tampilStruk(kasir.cetakStruk(trx, uang));
            refreshKeranjang();
            refreshTabel();
            onDataBerubah.run();
        } catch (Exception e) {
            pesanGagal(e);
            // bayar gagal bisa jadi stok sudah terlanjur berubah, tampilan disegarkan biar sinkron
            refreshKeranjang();
            refreshTabel();
        }
    }

    private void tampilStruk(String isi) {
        JTextArea area = new JTextArea(isi);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setEditable(false);
        area.setCaretPosition(0);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(460, 300));
        JOptionPane.showMessageDialog(this, scroll, "Struk Pembayaran", JOptionPane.INFORMATION_MESSAGE);
    }

    private void pesan(String isi) {
        JOptionPane.showMessageDialog(this, isi, "KasGud", JOptionPane.INFORMATION_MESSAGE);
    }

    private void pesanGagal(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE);
    }
}
