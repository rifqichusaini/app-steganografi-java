import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

abstract class FileHandler {
    protected File file;
    protected JFileChooser fileChooser;

    public FileHandler() {
        fileChooser = new JFileChooser();
        // Set filter untuk file PNG
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
        fileChooser.setFileFilter(filter);
    }

    // Method abstract yang akan diimplementasi di child class
    protected abstract File handleFile();

    // Method umum untuk menampilkan pesan
    protected void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(null, message, title, messageType);
    }

    protected void saveSukses(){
        // Tampilkan konfirmasi path yang dipilih
        JOptionPane.showMessageDialog(null,
                "File berhasil disimpan di: " + file.getAbsolutePath(),
                "Berhasil disimpan!",
                JOptionPane.INFORMATION_MESSAGE);
    }
}