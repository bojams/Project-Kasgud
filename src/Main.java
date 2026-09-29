// File: src/Main.java
import gui.MainFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        pasangLookAndFeel();
        // Swing harus dijalankan di thread khusus graphics, bukan thread main
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
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
