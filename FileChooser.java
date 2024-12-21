import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooser {
	public static File MakeFileChooser() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Pilih lokasi gambar yang ingin dipilih");

		// Tambahkan filter file untuk hanya menerima file PNG
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG Images", "png");
		fileChooser.setFileFilter(filter);

		int option = fileChooser.showOpenDialog(null);
		if (option == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();

			// Validasi ekstensi file untuk memastikan hanya file PNG diterima
			if (file.getName().toLowerCase().endsWith(".png")) {
				return file;
			} else {
				// Tampilkan pesan kesalahan jika file tidak valid
				JOptionPane.showMessageDialog(null, "Hanya file PNG yang diperbolehkan.",
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Tidak ada file yang dipilih.",
					"Informasi", JOptionPane.INFORMATION_MESSAGE);
		}
		return null;
	}
}
