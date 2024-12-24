import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooser extends FileHandler {
	@Override
	protected File handleFile() {
		fileChooser.setDialogTitle("Pilih lokasi gambar yang ingin dipilih");

		int option = fileChooser.showOpenDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
			// Validasi ekstensi file
			if (file.getName().toLowerCase().endsWith(".png")) {
				return file;
			} else {
				showMessage("Hanya file PNG yang diperbolehkan.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			showMessage("Tidak ada file yang dipilih.",
					"Informasi",
					JOptionPane.INFORMATION_MESSAGE);
		}
		return null;
	}
}