import javax.swing.*;
import java.io.File;

public class FileSaver {
    private static String filePath;

    public static String getSaveFilePath() {
        // Membuat JFileChooser untuk memilih lokasi dan nama file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih lokasi dan nama untuk menyimpan file");

        // Atur ekstensi file default sebagai PNG
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Pastikan file memiliki ekstensi ".png"
            filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".png")) {
                filePath += ".png";
            }

            return filePath;
        } else {
            JOptionPane.showMessageDialog(null,
                    "Proses penyimpanan dibatalkan.",
                    "Gagal disimpan!",
                    JOptionPane.WARNING_MESSAGE);
            return "";
        }
    }
    public static void saveSukses(){
        // Tampilkan konfirmasi path yang dipilih
        JOptionPane.showMessageDialog(null,
                "File berhasil disimpan di: " + filePath,
                "Berhasil disimpan!",
                JOptionPane.INFORMATION_MESSAGE);
    }
}