// File: src/gui/DialogGantiSandi.java
package gui;

import service.AuthService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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

public class DialogGantiSandi extends JDialog {

    private final AuthService auth;

    private final JPasswordField txtLama = new JPasswordField(15);
    private final JPasswordField txtBaru = new JPasswordField(15);
    private final JPasswordField txtUlang = new JPasswordField(15);
    private final JLabel lblGalat = new JLabel(" ");
    private final JButton btnSimpan = new JButton("SIMPAN");

    public DialogGantiSandi(AuthService auth) {
        super((Frame) null, "Ganti Password", true);
        this.auth = auth;

        bangunTampilan();
        pasangEvent();

        setModal(true);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                txtLama.requestFocusInWindow();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void bangunTampilan() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Tema.LATAR);
        root.add(panelJudul(), BorderLayout.NORTH);
        root.add(panelForm(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel panelJudul() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Tema.LATAR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 14, 24));

        JLabel judul = Tema.label("Ganti Password", 18, true, Tema.TEKS);
        JLabel sub = Tema.label(auth.sudahLogin()
                ? "Login sebagai " + auth.getSesi().getNama()
                : "Tidak ada user yang login", 12, false, Tema.TEKS_LUNAK);
        judul.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(judul);
        panel.add(Box.createVerticalStrut(2));
        panel.add(sub);
        return panel;
    }

    private JPanel panelForm() {
        txtLama.setPreferredSize(new Dimension(190, 28));
        txtBaru.setPreferredSize(new Dimension(190, 28));
        txtUlang.setPreferredSize(new Dimension(190, 28));

        lblGalat.setFont(Tema.font(11, false));
        lblGalat.setForeground(Tema.TEKS_GALAT);
        lblGalat.setPreferredSize(new Dimension(220, 16));

        Tema.warnaTombol(btnSimpan, Tema.TOMBOL_UTAMA.getRGB());
        btnSimpan.setPreferredSize(new Dimension(96, 30));

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombol.setBackground(Tema.KARTU);
        tombol.setOpaque(false);
        tombol.add(btnSimpan);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Tema.KARTU);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 24, 20, 24));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(0, 0, 10, 10);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0;
        g.gridy = 0;
        panel.add(Tema.label("Password lama", 13, false, Tema.TEKS), g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtLama, g);

        g.gridx = 0;
        g.gridy = 1;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        panel.add(Tema.label("Password baru", 13, false, Tema.TEKS), g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtBaru, g);

        g.gridx = 0;
        g.gridy = 2;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        panel.add(Tema.label("Ulangi baru", 13, false, Tema.TEKS), g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        panel.add(txtUlang, g);

        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 2;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        panel.add(lblGalat, g);

        g.gridx = 0;
        g.gridy = 4;
        g.insets = new Insets(4, 0, 0, 0);
        panel.add(tombol, g);

        Tema.garisAtas(panel);
        return panel;
    }

    private void pasangEvent() {
        txtLama.addActionListener(e -> txtBaru.requestFocusInWindow());
        txtBaru.addActionListener(e -> txtUlang.requestFocusInWindow());
        txtUlang.addActionListener(e -> aksiSimpan());
        btnSimpan.addActionListener(e -> aksiSimpan());
    }

    private void aksiSimpan() {
        char[] lama = txtLama.getPassword();
        char[] baru = txtBaru.getPassword();
        char[] ulang = txtUlang.getPassword();
        try {
            auth.gantiSandi(new String(lama), new String(baru), new String(ulang));
            JOptionPane.showMessageDialog(this, "Password berhasil diganti.",
                    "KasGud", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            lblGalat.setForeground(Tema.TEKS_GALAT);
            lblGalat.setText(e.getMessage());
        } finally {
            Arrays.fill(lama, '\0');
            Arrays.fill(baru, '\0');
            Arrays.fill(ulang, '\0');
            txtLama.setText("");
            txtBaru.setText("");
            txtUlang.setText("");
        }
    }
}