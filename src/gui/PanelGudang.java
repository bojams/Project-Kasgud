// File: src/gui/PanelGudang.java
package gui;

import model.Barang;
import service.GudangService;
import util.Fmt;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
import java.util.List;

public class PanelGudang extends JPanel {

    private final GudangService gudang;
    private Runnable onDataBerubah = () -> {
    };

    private final JTextField txtCari = new JTextField(22);
    private final JTextField txtKode = new JTextField(8);
    private final JTextField txtNama = new JTextField(20);
    private final JTextField txtHarga = new JTextField(9);
    private final JTextField txtStok = new JTextField(7);

    private final JButton btnTambah = new JButton("TAMBAH");
    private final JButton btnUpdate = new JButton("UPDATE");
    private final JButton btnHapus = new JButton("HAPUS");
    private final JButton btnTambahStok = new JButton("TAMBAH STOK");
    private final JButton btnRefresh = new JButton("REFRESH");

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Kode", "Nama", "Harga", "Stok"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tblBarang = new JTable(model);

    public PanelGudang(GudangService gudang) {
        this.gudang = gudang;
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(panelCari(), BorderLayout.NORTH);
        add(panelTabel(), BorderLayout.CENTER);
        add(panelForm(), BorderLayout.SOUTH);
        pasangEvent();
        refreshTabel();
    }

    public void setOnDataBerubah(Runnable aksi) {
        this.onDataBerubah = aksi;
    }

    private JPanel panelCari() {
        Tema.warnaTombol(btnRefresh, 0x2196F3);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panel.add(new JLabel("Cari:"));
        panel.add(txtCari);
        panel.add(btnRefresh);
        return panel;
    }

    private JPanel panelTabel() {
        tblBarang.setRowHeight(24);
        tblBarang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Tema.urutRupiah(tblBarang, 2);
        Tema.urutAngka(tblBarang, 3);
        tblBarang.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer kanan = new DefaultTableCellRenderer();
        kanan.setHorizontalAlignment(SwingConstants.RIGHT);
        tblBarang.getColumnModel().getColumn(2).setCellRenderer(kanan);
        tblBarang.getColumnModel().getColumn(3).setCellRenderer(kanan);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tblBarang), BorderLayout.CENTER);
        return panel;
    }

    private JPanel panelForm() {
        Tema.warnaTombol(btnTambah, 0x4CAF50);
        Tema.warnaTombol(btnHapus, 0xF44336);
        Tema.warnaTombol(btnUpdate, 0x2196F3);
        Tema.warnaTombol(btnTambahStok, 0x2196F3);

        setUkuran(txtKode, 90);
        setUkuran(txtNama, 220);
        setUkuran(txtHarga, 100);
        setUkuran(txtStok, 80);

        JPanel panelIsian = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panelIsian.add(new JLabel("Kode:"));
        panelIsian.add(txtKode);
        panelIsian.add(new JLabel("Nama:"));
        panelIsian.add(txtNama);
        panelIsian.add(new JLabel("Harga:"));
        panelIsian.add(txtHarga);
        panelIsian.add(new JLabel("Stok:"));
        panelIsian.add(txtStok);

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        panelTombol.add(btnTambah);
        panelTombol.add(btnUpdate);
        panelTombol.add(btnHapus);
        panelTombol.add(btnTambahStok);
        panelTombol.add(new JLabel("Klik baris tabel untuk mengisi form"));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Data Barang"));
        panel.add(panelIsian, BorderLayout.NORTH);
        panel.add(panelTombol, BorderLayout.CENTER);
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

        tblBarang.getSelectionModel().addListSelectionListener(e -> isiFormDariTabel());
        btnRefresh.addActionListener(e -> refreshTabel());
        btnTambah.addActionListener(e -> aksiTambah());
        btnUpdate.addActionListener(e -> aksiUpdate());
        btnHapus.addActionListener(e -> aksiHapus());
        btnTambahStok.addActionListener(e -> aksiTambahStok());
    }

    public void refreshTabel() {
        List<Barang> list = gudang.getDaftar(txtCari.getText());
        // model dikosongkan dulu baru selection, kalau dibalik RowSorter akan
        // komplain indeks baris yang dipilih sudah tidak ada
        model.setRowCount(0);
        tblBarang.clearSelection();
        for (Barang brg : list) {
            model.addRow(new Object[]{brg.getKode(), brg.getNama(), Fmt.angka(brg.getHarga()), brg.getStok()});
        }
    }

    private void isiFormDariTabel() {
        int baris = barisTerpilih();
        // baris bisa jadi tidak ada lagi kalau tabel di-refresh tepat saat baris dipilih
        if (baris < 0 || baris >= model.getRowCount()) {
            return;
        }
        Barang brg = gudang.cari((String) model.getValueAt(baris, 0));
        if (brg == null) {
            return;
        }
        txtKode.setText(brg.getKode());
        txtNama.setText(brg.getNama());
        txtHarga.setText(String.valueOf((long) brg.getHarga()));
        txtStok.setText(String.valueOf(brg.getStok()));
    }

    // tabel bisa diurutkan, jadi indeks tampilan harus dikembalikan ke indeks model
    private int barisTerpilih() {
        int barisTampilan = tblBarang.getSelectedRow();
        // cek jumlah baris tampilan dulu, selection bisa tertinggal kalau tabel
        // di-refresh tepat saat baris sedang dipilih
        if (barisTampilan < 0 || barisTampilan >= tblBarang.getRowCount()) {
            return -1;
        }
        return tblBarang.convertRowIndexToModel(barisTampilan);
    }

    private void aksiTambah() {
        try {
            gudang.tambah(txtKode.getText(), txtNama.getText(), bacaHarga(), bacaStok());
            pesan("Barang berhasil ditambahkan.");
            bersihkanForm();
            onDataBerubah.run();
        } catch (Exception e) {
            pesanGagal(e);
        }
    }

    private void aksiUpdate() {
        try {
            gudang.update(txtKode.getText(), txtNama.getText(), bacaHarga(), bacaStok());
            pesan("Data barang berhasil diupdate.");
            bersihkanForm();
            onDataBerubah.run();
        } catch (Exception e) {
            pesanGagal(e);
        }
    }

    private void aksiHapus() {
        String kode = kodeUntukAksi();
        if (kode == null) {
            pesan("Pilih barang di tabel atau isi kode barang dulu.");
            return;
        }
        Barang brg = gudang.cari(kode);
        if (brg == null) {
            pesan("Barang dengan kode " + kode + " tidak ditemukan.");
            return;
        }
        int jawab = JOptionPane.showConfirmDialog(this,
                "Hapus barang " + brg.getNama() + " (" + kode + ")?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (jawab != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            gudang.hapus(kode);
            pesan("Barang " + kode + " dihapus.");
            bersihkanForm();
            onDataBerubah.run();
        } catch (Exception e) {
            pesanGagal(e);
        }
    }

    private void aksiTambahStok() {
        String kode = kodeUntukAksi();
        if (kode == null) {
            pesan("Pilih barang di tabel atau isi kode barang dulu.");
            return;
        }
        String input = JOptionPane.showInputDialog(this, "Jumlah stok yang ditambahkan:", "1");
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        try {
            gudang.tambahStok(kode, Fmt.bacaBilangan(input));
            pesan("Stok " + kode + " berhasil ditambah.");
            isiFormDariTabel();
            onDataBerubah.run();
        } catch (Exception e) {
            pesanGagal(e);
        }
    }

    // form diisi lebih diprioritaskan, kalau kosong baru pakai baris tabel yang dipilih
    private String kodeUntukAksi() {
        if (!txtKode.getText().trim().isEmpty()) {
            return txtKode.getText().trim();
        }
        int baris = barisTerpilih();
        return baris < 0 || baris >= model.getRowCount() ? null : (String) model.getValueAt(baris, 0);
    }

    private double bacaHarga() {
        return Fmt.bacaAngka(txtHarga.getText());
    }

    private int bacaStok() {
        return Fmt.bacaBilangan(txtStok.getText());
    }

    private void bersihkanForm() {
        txtKode.setText("");
        txtNama.setText("");
        txtHarga.setText("");
        txtStok.setText("");
        tblBarang.clearSelection();
    }

    private void pesan(String isi) {
        JOptionPane.showMessageDialog(this, isi, "KasGud", JOptionPane.INFORMATION_MESSAGE);
    }

    private void pesanGagal(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Gagal", JOptionPane.ERROR_MESSAGE);
    }

    private void setUkuran(JTextField field, int lebar) {
        field.setPreferredSize(new Dimension(lebar, 25));
    }
}
