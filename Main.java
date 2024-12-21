import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Main {

    private File selectedFile; // Variabel global untuk menyimpan file gambar yang dipilih
    private JTextField decryptTextField;
    private JTextField encryptTextField;
    private JFrame frame; // Deklarasi frame
    private JPanel mainPanel; // Deklarasi mainPanel
    private CardLayout cardLayout; // Deklarasi cardLayout

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().App());
    }

    public void kembaliMenu() {
        frame.setSize(400, 300); // Ukuran frame kembali seperti semula
        frame.setLocationRelativeTo(null);
        cardLayout.show(mainPanel, "Menu");
    }

    private void App() {
        frame = new JFrame("Aplikasi Steganografi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Posisi frame di tengah layar
        frame.setLocationRelativeTo(null);

        // Panel utama untuk card layout (switching menu)
        mainPanel = new JPanel(new CardLayout());
        cardLayout = (CardLayout) mainPanel.getLayout(); // Inisialisasi cardLayout

        // Menu utama
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(2, 1, 10, 10));

        JButton btnInputGambar = new JButton("Input Gambar");
        JButton btnBacaGambar = new JButton("Baca Gambar");

        menuPanel.add(btnInputGambar);
        menuPanel.add(btnBacaGambar);

        // Panel untuk input gambar
        JPanel inputGambarPanel = new JPanel();
        inputGambarPanel.setLayout(null);

        JLabel encryptMassage = new JLabel("Masukkan pesan yg ingin disembunyikan");
        encryptMassage.setFont(new Font("Tahoma", Font.BOLD, 15));
        encryptMassage.setBounds(10, 11, 364, 37);
        encryptMassage.setHorizontalAlignment(SwingConstants.CENTER);
        inputGambarPanel.add(encryptMassage);

        encryptTextField = new JTextField();
        encryptTextField.setBounds(10, 59, 364, 20);
        inputGambarPanel.add(encryptTextField);
        encryptTextField.setColumns(10);

        JButton btnProses = new JButton("Proses dan Simpan");
        btnProses.setBounds(10, 90, 170, 30);
        inputGambarPanel.add(btnProses);

        JButton btnKembali1 = new JButton("Kembali ke Menu");
        btnKembali1.setBounds(190, 90, 184, 30);
        inputGambarPanel.add(btnKembali1);

        // Panel untuk baca gambar
        JPanel bacaGambarPanel = new JPanel();
        bacaGambarPanel.setLayout(null);

        JLabel decryptMassage = new JLabel("Isi pesan tersembunyi");
        decryptMassage.setFont(new Font("Tahoma", Font.BOLD, 15));
        decryptMassage.setBounds(10, 11, 364, 37);
        decryptMassage.setHorizontalAlignment(SwingConstants.CENTER);
        bacaGambarPanel.add(decryptMassage);

        decryptTextField = new JTextField();
        decryptTextField.setColumns(10);
        decryptTextField.setBounds(10, 59, 364, 20);
        bacaGambarPanel.add(decryptTextField);
        JButton btnKembali2 = new JButton("Kembali ke Menu");
        btnKembali2.setBounds(10, 90, 364, 30);
        bacaGambarPanel.add(btnKembali2);

        // Tambahkan panel ke mainPanel
        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(inputGambarPanel, "InputGambar");
        mainPanel.add(bacaGambarPanel, "BacaGambar");

        // Tambahkan listener untuk tombol
        btnKembali2.addActionListener(e -> kembaliMenu());

        btnInputGambar.addActionListener(e -> {
            selectedFile = FileChooser.MakeFileChooser();
            if (selectedFile != null) {
                frame.setSize(400, 170); // Mengubah ukuran frame
                frame.setLocationRelativeTo(null); // Tetap di tengah setelah resize
                cardLayout.show(mainPanel, "InputGambar");
            }
        });

        btnBacaGambar.addActionListener(e -> {
            String decryptedMessage = DecryptLSB.Decrypt();
            if (!decryptedMessage.isEmpty() && !decryptedMessage.equals("null")) {
                decryptTextField.setText(decryptedMessage);
                frame.setSize(400, 170); // Mengubah ukuran frame
                frame.setLocationRelativeTo(null); // Tetap di tengah setelah resize
                cardLayout.show(mainPanel, "BacaGambar");
            }
            if (decryptedMessage.equals("null")){
                JOptionPane.showMessageDialog(null,
                "Gambar tidak memiliki pesan tersembunyi.",
                "Error!",
                JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnProses.addActionListener(e -> {
            if (selectedFile != null) {
                EncryptLSB.Encrypt(selectedFile, encryptTextField.getText());
                encryptTextField.setText("");
                kembaliMenu();
            }
        });

        btnKembali1.addActionListener(e -> kembaliMenu());

        // Atur frame
        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

}
