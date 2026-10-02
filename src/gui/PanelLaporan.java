// File: src/gui/PanelLaporan.java
package gui;

import model.Transaksi;
import service.KasirService;
import util.Fmt;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

public class PanelLaporan extends JPanel {

    private final KasirService kasir;

    private final JLabel lblPendapatan = new JLabel("Total Pendapatan : Rp0", SwingConstants.CENTER);
    private final JLabel lblJumlahTrx = new JLabel("Jumlah Transaksi : 0", SwingConstants.CENTER);
    private final JButton btnRefresh = new JButton("REFRESH");

    private final DefaultTableModel modelTrx = new DefaultTableModel(
            new Object[]{"No", "ID Transaksi", "Tanggal", "Jumlah Item", "Total"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel modelTerlaris = new DefaultTableModel(
            new Object[]{"Peringkat", "Kode", "Nama", "Qty Terjual", "Total"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tblTrx = new JTable(modelTrx);
    private final JTable tblTerlaris = new JTable(modelTerlaris);

    public PanelLaporan(KasirService kasir) {
        this.kasir = kasir;
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(panelRingkasan(), BorderLayout.NORTH);
        add(panelTransaksi(), BorderLayout.CENTER);
        add(panelTerlaris(), BorderLayout.SOUTH);
        pasangEvent();
        refreshData();
    }

    private JPanel panelRingkasan() {
        Tema.warnaTombol(btnRefresh, 0x2196F3);
        lblPendapatan.setFont(lblPendapatan.getFont().deriveFont(Font.BOLD, 18f));
        lblPendapatan.setBorder(BorderFactory.createEmptyBorder(10, 0, 4, 0));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(lblPendapatan, BorderLayout.NORTH);
        panel.add(lblJumlahTrx, BorderLayout.CENTER);
        panel.add(btnRefresh, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel panelTransaksi() {
        tblTrx.setRowHeight(22);
        Tema.urutAngka(tblTrx, 0);
        Tema.urutRupiah(tblTrx, 4);
        tblTrx.getTableHeader().setReorderingAllowed(false);
        setKolomKanan(tblTrx, 4);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Daftar Transaksi"));
        panel.add(new JScrollPane(tblTrx), BorderLayout.CENTER);
        return panel;
    }

    private JPanel panelTerlaris() {
        tblTerlaris.setRowHeight(22);
        tblTerlaris.getTableHeader().setReorderingAllowed(false);
        setKolomKanan(tblTerlaris, 3);
        setKolomKanan(tblTerlaris, 4);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(10, 200));
        panel.setBorder(BorderFactory.createTitledBorder("Barang Terlaris (Top 5)"));
        panel.add(new JScrollPane(tblTerlaris), BorderLayout.CENTER);
        return panel;
    }

    private void pasangEvent() {
        btnRefresh.addActionListener(e -> refreshData());
    }

    public void refreshData() {
        // model dikosongkan dulu baru selection, kalau dibalik RowSorter akan
        // komplain indeks baris yang dipilih sudah tidak ada
        modelTrx.setRowCount(0);
        tblTrx.clearSelection();
        int no = 1;
        for (Transaksi trx : kasir.getDaftarTransaksi()) {
            modelTrx.addRow(new Object[]{no++, trx.getIdTrx(), Fmt.tanggal(trx.getTanggal()),
                    trx.getJumlahItem() + " item / " + trx.getTotalQty() + " pcs", Fmt.rp(trx.getTotal())});
        }

        lblPendapatan.setText("Total Pendapatan : " + Fmt.rp(kasir.getTotalPendapatan()));
        lblJumlahTrx.setText("Jumlah Transaksi : " + kasir.getDaftarTransaksi().size());

        modelTerlaris.setRowCount(0);
        int peringkat = 1;
        for (KasirService.Terlaris t : kasir.getBarangTerlaris()) {
            modelTerlaris.addRow(new Object[]{peringkat++, t.getKode(), t.getNama(),
                    t.getQtyTerjual(), Fmt.rp(t.getTotal())});
        }
    }

    private void setKolomKanan(JTable tabel, int kolom) {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tabel.getColumnModel().getColumn(kolom).setCellRenderer(renderer);
    }
}
