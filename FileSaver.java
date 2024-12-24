import javax.swing.*;
import java.io.File;

public class FileSaver extends FileHandler {
    @Override
    protected File handleFile() {
        fileChooser.setDialogTitle("Pilih lokasi dan nama untuk menyimpan file");

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            // Pastikan file memiliki ekstensi ".png"
            if (!file.getAbsolutePath().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            return file;
        } else {
            showMessage("Proses penyimpanan dibatalkan.",
                    "Gagal disimpan!",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    public void saveSukses(){
        // Tampilkan konfirmasi path yang dipilih
        JOptionPane.showMessageDialog(null,
                "File berhasil disimpan di: " + file.getAbsolutePath(),
                "Berhasil disimpan!",
                JOptionPane.INFORMATION_MESSAGE);
    }
}