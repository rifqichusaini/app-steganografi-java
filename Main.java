import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.Objects;

public class Main {
    private File selectedFile; // Variabel global untuk menyimpan file gambar yang dipilih
    private JTextField decryptTextField;
    private JTextField encryptTextField;
    private String decryptedMessage;
    private JTextField encryptPassword;
    private JTextField decryptPassword;
    private JFrame frame; // Deklarasi frame
    private JPanel mainPanel; // Deklarasi mainPanel
    private CardLayout cardLayout; // Deklarasi cardLayout
    FileHandler chooseFile = new FileChooser();

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

        encryptTextField = new JTextField();
        addPlaceholder(encryptTextField,"Masukkan pesan yg ingin disembunyikan");

        encryptTextField.setColumns(10);
        encryptTextField.setBounds(94, 23, 280, 20);
        inputGambarPanel.add(encryptTextField);

        // Button proses
        JButton btnProses = new JButton("Proses dan Simpan");
        btnProses.setBounds(10, 90, 170, 30);
        inputGambarPanel.add(btnProses);

        // Button kembali
        JButton btnKembali1 = new JButton("Kembali ke Menu");
        btnKembali1.setBounds(190, 90, 184, 30);
        inputGambarPanel.add(btnKembali1);

        // Text field untuk password
        encryptPassword = new JTextField();
        addPlaceholder(encryptPassword,"Masukkan password untuk decryptor");
        encryptPassword.setColumns(10);
        encryptPassword.setBounds(94, 59, 280, 20);
        inputGambarPanel.add(encryptPassword);

        // Label pesan
        JLabel labelPesan = new JLabel("Pesan       :");
        labelPesan.setFont(new Font("Tahoma", Font.BOLD, 13));
        labelPesan.setBounds(10, 23, 74, 20);
        inputGambarPanel.add(labelPesan);

        // Label password
        JLabel labelPassword = new JLabel("Password :");
        labelPassword.setFont(new Font("Tahoma", Font.BOLD, 13));
        labelPassword.setBounds(10, 59, 74, 20);
        inputGambarPanel.add(labelPassword);

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

        JPanel bacaPasswordGambar = new JPanel();
        bacaPasswordGambar.setLayout(null);

        // ===================================================
        JLabel enterPass = new JLabel("Password :");
        enterPass.setFont(new Font("Tahoma", Font.BOLD, 12));
        enterPass.setBounds(10, 11, 67, 20);
        enterPass.setHorizontalAlignment(SwingConstants.LEFT);
        bacaPasswordGambar.add(enterPass);

        decryptPassword = new JTextField();
        decryptPassword.setColumns(10);
        decryptPassword.setBounds(81, 11, 293, 20);
        addPlaceholder(decryptPassword, "Masukkan password gambar!");
        bacaPasswordGambar.add(decryptPassword);

        JButton prosesPassword = new JButton("Proses");
        prosesPassword.setBounds(10, 43, 170, 30);
        bacaPasswordGambar.add(prosesPassword);

        JButton btnKembali3 = new JButton("Kembali ke Menu");
        btnKembali3.setBounds(190, 42, 184, 30);
        bacaPasswordGambar.add(btnKembali3);
        // ===================================================

        // Tambahkan panel ke mainPanel
        mainPanel.add(menuPanel, "Menu");
        mainPanel.add(inputGambarPanel, "InputGambar");
        mainPanel.add(bacaGambarPanel, "BacaGambar");
        mainPanel.add(bacaPasswordGambar, "bacaPasswordGambar");

        btnInputGambar.addActionListener(e -> {
            selectedFile = chooseFile.handleFile();
            if (selectedFile != null) {
                frame.setSize(400, 170); // Mengubah ukuran frame
                frame.setLocationRelativeTo(null); // Tetap di tengah setelah resize
                addPlaceholder(encryptTextField,"Masukkan pesan yg ingin disembunyikan");
                addPlaceholder(encryptPassword,"Masukkan password untuk decryptor");
                btnKembali1.requestFocusInWindow();
                cardLayout.show(mainPanel, "InputGambar");
            }
        });

        btnBacaGambar.addActionListener(e -> {
            FileHandler chooseFile = new FileChooser();
            selectedFile = chooseFile.handleFile();

            if (selectedFile != null) {
                // Pindah ke panel baca password
                frame.setSize(400, 125); // Mengubah ukuran frame
                frame.setLocationRelativeTo(null); // Tetap di tengah setelah resize
                addPlaceholder(decryptPassword, "Masukkan password gambar!");
                btnKembali3.requestFocusInWindow();
                cardLayout.show(mainPanel, "bacaPasswordGambar");
            } else {
                JOptionPane.showMessageDialog(null,
                        "Tidak ada file yang dipilih.",
                        "Error!",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Listener untuk tombol Proses Password
        prosesPassword.addActionListener(e -> {
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(null,
                        "Tidak ada file yang dipilih. Silakan pilih file terlebih dahulu.",
                        "Error!",
                        JOptionPane.WARNING_MESSAGE);
                kembaliMenu(); // Kembali ke menu utama
                return;
            }

            String passwordInput = decryptPassword.getText(); // Ambil password dari text field
            if(!passwordInput.equals("Masukkan password gambar!")){
                decryptedMessage = DecryptLSB.Decrypt(selectedFile, passwordInput);

                if (!decryptedMessage.isEmpty() && !decryptedMessage.equals("null")) {
                    // Tampilkan hasil dekripsi di panel BacaGambar
                    JOptionPane.showMessageDialog(null,
                            "Pesan tersembunyi ditemukan!",
                            "BINGGO!",
                            JOptionPane.INFORMATION_MESSAGE);
                    decryptTextField.setText(decryptedMessage);
                    frame.setSize(400, 170); // Mengubah ukuran frame
                    frame.setLocationRelativeTo(null); // Tetap di tengah setelah resize
                    decryptPassword.setText("");
                    cardLayout.show(mainPanel, "BacaGambar");
                } else {
                    // Tampilkan pesan error jika dekripsi gagal
                    JOptionPane.showMessageDialog(null,
                            "Pesan tersembunyi tidak ditemukan!",
                            "Error!",
                            JOptionPane.ERROR_MESSAGE);
                    addPlaceholder(decryptPassword, "Masukkan password gambar!");
                    btnKembali3.requestFocusInWindow();
                }
            } else{
                JOptionPane.showMessageDialog(null,
                        "Password Tidak boleh kosong!",
                        "Error!",
                        JOptionPane.ERROR_MESSAGE);
            }

        });

        btnProses.addActionListener(e -> {
            if (selectedFile != null) {
                if(Objects.equals(encryptTextField.getText(), "Masukkan pesan yg ingin disembunyikan") || Objects.equals(encryptPassword.getText(), "Masukkan password untuk decryptor")){
                    JOptionPane.showMessageDialog(null,
                            "Pesan atau password tidak boleh kosong!",
                            "Error!",
                            JOptionPane.INFORMATION_MESSAGE);
                } else{
                    EncryptLSB.Encrypt(selectedFile, encryptTextField.getText(), encryptPassword.getText());
                    if(EncryptLSB.isIsSave()){
                        encryptTextField.setText("");
                        encryptPassword.setText("");
                        kembaliMenu();
                    }
                }
            }
        });

        // Tambahkan listener untuk tombol
        btnKembali2.addActionListener(e -> {
            decryptTextField.setText("");
            kembaliMenu();
        });

        btnKembali1.addActionListener(e -> kembaliMenu());
        btnKembali3.addActionListener(e -> {
            encryptTextField.setText("");
            encryptPassword.setText("");
            kembaliMenu();
        });

        // Atur frame
        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
    }

    private static void addPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }

}
