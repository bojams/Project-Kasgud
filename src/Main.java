// File: src/Main.java
import gui.DialogLogin;
import gui.MainFrame;
import service.AuthService;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        pasangLookAndFeel();
        // Swing harus dijalankan di thread khusus graphics, bukan thread main
        SwingUtilities.invokeLater(Main::tampilkanLogin);
    }

    // aplikasi tidak langsung dibuka, login dulu. Kalau keluar dari aplikasi
    // maka jalankan lagi dari awal supaya user baru bisa masuk dengan akun lain
    private static void tampilkanLogin() {
        AuthService auth = AuthService.get();

        DialogLogin login = new DialogLogin(auth);
        login.setOnBerhasil(() -> {
            login.dispose();
            SwingUtilities.invokeLater(() -> bukaAplikasi(auth));
        });
        login.setVisible(true);
    }

    private static void bukaAplikasi(AuthService auth) {
        new MainFrame(auth, () -> {
            auth.logout();
            SwingUtilities.invokeLater(Main::tampilkanLogin);
        }).setVisible(true);
    }

    private static void pasangLookAndFeel() {
        try {
            // pakai LAF sistem: Nimbus mengabaikan setBackground sehingga warna tombol
            // tidak konsisten antar tab, sedangkan LAF sistem warnaflat-nya konsisten
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Gagal mengatur tampilan aplikasi, memakai tampilan bawaan.\n" + e.getMessage(),
                    "KasGud", JOptionPane.WARNING_MESSAGE);
        }
    }
}