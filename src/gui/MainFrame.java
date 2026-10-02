// File: src/gui/MainFrame.java
package gui;

import model.User;
import service.AuthService;
import service.GudangService;
import service.KasirService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Jendela utama aplikasi. User yang login menentukan tab mana saja yang dibuka.
public class MainFrame extends JFrame {

    // service dibuat sekali di sini lalu dipakai bersama semua tab
    private final GudangService gudang = new GudangService();
    private final KasirService kasir = new KasirService(gudang);
    private final AuthService auth;
    private final Runnable onKeluar;

    private final PanelKasir panelKasir;
    // null kalau user yang login bukan admin, tab GUDANG tidak dibuat
    private final PanelGudang panelGudang;
    private final PanelLaporan panelLaporan;

    private boolean sedangKeluar = false;

    public MainFrame(AuthService auth, Runnable onKeluar) {
        super("KasGud - Aplikasi Kasir & Gudang");
        this.auth = auth;
        this.onKeluar = onKeluar;
        setFontAwal();

        User user = auth.getSesi();

        panelKasir = new PanelKasir(gudang, kasir);
        panelLaporan = new PanelLaporan(kasir);

        JTabbedPane tab = new JTabbedPane();
        tab.addTab("KASIR", panelKasir);
        if (user.getPeran().bolehBukaGudang()) {
            panelGudang = new PanelGudang(gudang);
            tab.addTab("GUDANG", panelGudang);
        } else {
            panelGudang = null;
        }
        tab.addTab("LAPORAN", panelLaporan);
        tab.addChangeListener(e -> refreshData());

        // setiap panel memberitahu MainFrame kalau datanya berubah, biar tab lain ikut ter-update
        panelKasir.setOnDataBerubah(this::refreshData);
        if (panelGudang != null) {
            panelGudang.setOnDataBerubah(this::refreshData);
        }

        JPanel root = new JPanel(new BorderLayout());
        root.add(panelAtas(user), BorderLayout.NORTH);
        root.add(tab, BorderLayout.CENTER);
        setContentPane(root);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                prosesKeluar();
            }
        });

        setMinimumSize(new Dimension(900, 600));
        setSize(1000, 650);
        setLocationRelativeTo(null);

        refreshData();
    }

    // baris atas: nama aplikasi di kiri, identitas user dan tombol di kanan
    private JPanel panelAtas(User user) {
        JButton btnSandi = new JButton("Ganti Password");
        JButton btnKeluar = new JButton("Keluar");
        Tema.warnaTombol(btnSandi, Tema.TOMBOL_NETRAL.getRGB());
        Tema.warnaTombol(btnKeluar, Tema.TOMBOL_BAHAYA.getRGB());
        btnSandi.addActionListener(e -> new DialogGantiSandi(auth).setVisible(true));
        btnKeluar.addActionListener(e -> prosesKeluar());

        JLabel lblApp = Tema.label("KasGud", 16, true, Tema.TEKS);

        JLabel lblUser = Tema.label(user.getNamaLengkap(), 12, false, Tema.TEKS_LUNAK);
        lblUser.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Tema.LATAR);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        kanan.setBackground(Tema.LATAR);
        kanan.add(lblUser);
        kanan.add(btnSandi);
        kanan.add(btnKeluar);

        panel.add(lblApp, BorderLayout.WEST);
        panel.add(kanan, BorderLayout.EAST);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.GARIS),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        return panel;
    }

    private void prosesKeluar() {
        if (sedangKeluar) {
            return;
        }
        int jawab = JOptionPane.showConfirmDialog(this,
                "Keluar dari aplikasi " + auth.getSesi().getNama() + "?",
                "Konfirmasi Keluar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (jawab != JOptionPane.YES_OPTION) {
            return;
        }
        sedangKeluar = true;
        auth.logout();
        dispose();
        onKeluar.run();
    }

    public void refreshData() {
        if (panelGudang != null) {
            panelGudang.refreshTabel();
        }
        panelKasir.refreshTabel();
        panelKasir.refreshKeranjang();
        panelLaporan.refreshData();
    }

    private void setFontAwal() {
        Font font = new Font("SansSerif", Font.PLAIN, 13);
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("PasswordField.font", font);
        UIManager.put("Table.font", font);
        UIManager.put("TableHeader.font", font.deriveFont(Font.BOLD));
        UIManager.put("TabbedPane.font", font);
        UIManager.put("OptionPane.messageFont", font);
        UIManager.put("TitledBorder.font", font.deriveFont(Font.BOLD));
    }
}