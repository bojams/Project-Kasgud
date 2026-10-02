// File: src/gui/DialogLogin.java
package gui;

import model.User;
import service.AuthService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class DialogLogin extends JDialog {

    private final AuthService auth;

    private final JTextField txtUsername = new JTextField(16);
    private final JPasswordField txtSandi = new JPasswordField(16);
    private final JLabel lblGalat = new JLabel(" ");
    private final JButton btnMasuk = new JButton("MASUK");
    private final JButton btnKeluar = new JButton("KELUAR");

    private final Timer timerKunci;

    private Runnable onBerhasil = () -> {
    };
    private User hasil = null;
    private boolean terkunci = false;

    public DialogLogin(AuthService auth) {
        super((Frame) null, "KasGud - Login", true);
        this.auth = auth;

        timerKunci = new Timer(1000, e -> cekKunci());
        timerKunci.setRepeats(false);

        bangunTampilan();
        pasangEvent();

        setModal(true);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                txtUsername.requestFocusInWindow();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                keluarAplikasi();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    public void setOnBerhasil(Runnable aksi) {
        this.onBerhasil = aksi;
    }

    public User getHasil() {
        return hasil;
    }

    private void bangunTampilan() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Tema.LATAR);
        root.add(panelJudul(), BorderLayout.NORTH);
        root.add(panelForm(), BorderLayout.CENTER);
        root.add(panelKaki(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel panelJudul() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Tema.LATAR);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 16, 24));

        JLabel judul = Tema.label("KasGud", 24, true, Tema.TEKS);
        JLabel sub = Tema.label("Aplikasi Kasir & Gudang", 12, false, Tema.TEKS_LUNAK);
        judul.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(judul);
        panel.add(Box.createVerticalStrut(2));
        panel.add(sub);
        return panel;
    }

    private JPanel panelForm() {
        txtUsername.setPreferredSize(new Dimension(200, 28));
        txtSandi.setPreferredSize(new Dimension(200, 28));

        // tinggi tetap supaya tampilan tidak bergeser setiap muncul pesan error
        lblGalat.setFont(Tema.font(11, false));
        lblGalat.setForeground(Tema.TEKS_GALAT);
        lblGalat.setPreferredSize(new Dimension(240, 16));

        Tema.warnaTombol(btnMasuk, Tema.TOMBOL_UTAMA.getRGB());
        Tema.warnaTombol(btnKeluar, Tema.TOMBOL_NETRAL.getRGB());
        btnMasuk.setPreferredSize(new Dimension(104, 30));
        btnKeluar.setPreferredSize(new Dimension(104, 30));

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombol.setBackground(Tema.KARTU);
        tombol.setOpaque(false);
        tombol.add(btnMasuk);
        tombol.add(btnKeluar);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Tema.KARTU);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 24, 20, 24));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(0, 0, 10, 10);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0;
        g.gridy = 0;
        panel.add(Tema.label("Username", 13, false, Tema.TEKS), g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtUsername, g);

        g.gridx = 0;
        g.gridy = 1;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        panel.add(Tema.label("Password", 13, false, Tema.TEKS), g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtSandi, g);

        g.gridx = 0;
        g.gridy = 2;
        g.gridwidth = 2;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        panel.add(lblGalat, g);

        g.gridx = 0;
        g.gridy = 3;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 0, 0, 0);
        panel.add(tombol, g);

        Tema.garisAtas(panel);
        return panel;
    }

    private JPanel panelKaki() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Tema.LATAR);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 24, 20, 24));

        JLabel hint = Tema.label("Akun bawaan : admin/admin123   kasir/kasir123", 11, false, Tema.TEKS_LUNAK);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(hint);
        return panel;
    }

    private void pasangEvent() {
        // enter di kolom username langsung geser ke kolom password
        txtUsername.addActionListener(e -> txtSandi.requestFocusInWindow());
        // enter di kolom password sama dengan menekan tombol MASUK
        txtSandi.addActionListener(e -> cobaMasuk());
        btnMasuk.addActionListener(e -> cobaMasuk());
        btnKeluar.addActionListener(e -> keluarAplikasi());

        // kalau username diganti saat akun sedang terkunci, hitungan mundur ikut diperbarui
        txtUsername.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                cekKunci();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                cekKunci();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                cekKunci();
            }
        });
    }

    private void cobaMasuk() {
        if (!btnMasuk.isEnabled()) {
            return;
        }
        // isi password dibaca sekali lalu field-nya dikosongkan lagi,
        // supaya tidak tertinggal tampil di layar
        char[] karakter = txtSandi.getPassword();
        String sandi = new String(karakter);
        Arrays.fill(karakter, '\0');

        try {
            hasil = auth.login(txtUsername.getText(), sandi);
            txtSandi.setText("");
            timerKunci.stop();
            onBerhasil.run();
        } catch (Exception e) {
            txtSandi.setText("");
            txtSandi.requestFocusInWindow();
            tampilkanGalat(e.getMessage());
            cekKunci();
        }
    }

    private void cekKunci() {
        int sisa = auth.getSisaKunciDetik(txtUsername.getText());
        if (sisa > 0) {
            if (!terkunci) {
                terkunci = true;
                btnMasuk.setEnabled(false);
                txtSandi.setText("");
            }
            lblGalat.setText("Akun terkunci. Coba lagi dalam " + sisa + " detik.");
            timerKunci.restart();
        } else if (terkunci) {
            terkunci = false;
            btnMasuk.setEnabled(true);
            timerKunci.stop();
            lblGalat.setText(" ");
            txtSandi.requestFocusInWindow();
        }
    }

    private void tampilkanGalat(String pesan) {
        lblGalat.setForeground(Tema.TEKS_GALAT);
        lblGalat.setText(pesan == null || pesan.isEmpty() ? "Login gagal." : pesan);
    }

    private void keluarAplikasi() {
        timerKunci.stop();
        dispose();
        System.exit(0);
    }
}