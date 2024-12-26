import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EncryptLSB {
	private static boolean isSave;

	public static boolean isIsSave() {
		return isSave;
	}

	public static void Encrypt(File imageFile, String message, String pass) {
		// Menambahkan flag ke pesan
		String messageWithFlags = pass + message + pass;

		// instansiasi class FileSaver
		FileHandler saveFile = new FileSaver();

		// memanggil method handleFile
		File newImageFile = saveFile.handleFile();

		if(newImageFile != null){
			/*
			* BufferedImage adalah java object yang memungkinkan untuk memasukkan gambar ke buffer,
			* agar kita mudah memodifikasi gambar nya.
			* kita tidak bisa pake image object karna tidak diizinkan untuk memodifikasi gambar.
			* karna memasukkan gambar ke buffer, maka kita dapat memanipulasi stream gambar nya.
			*/
			BufferedImage image;
			try {
				// membaca gambar lalu memasukkan ke buffer
				image = ImageIO.read(imageFile);

				/*
				* Mencopy gambar yang telah dipilih, karna disini saya tidak akan memodifikasi gambarnya langsung,
				* tapi memodifikasi gambar yang telah dicopy
				*/
				BufferedImage imageToEncrypt = GetImageToEncrypt(image);

				// mengubah pixel yang tadinya 2d ke 1d
				Pixel[] pixels = GetPixelArray(imageToEncrypt);

				// merubah pesan ke binary
				String[] messageInBinary = ConvertMessageToBinary(messageWithFlags); // Sekarang menggunakan pesan dengan flag

				// rangkaian proses encode pesan ke gambar.
				// memasukkan pixel, dan massage yg ingin disusupkan yg sudah berbentuk binary
				EncodeMessageBinaryInPixels(pixels, messageInBinary);

				// merubah gambar yg dibuffer menjadi gambar yg sudah dimodif
				ReplacePixelsInNewBufferedImage(pixels, image);

				// menyimpan gambar ke tempat yg sudah dipilih diawal
				SaveNewFile(image, newImageFile);

				// memunculkan pesan bahwan gambar berhasil di simpan
				saveFile.saveSukses();

				// menandakan bahwa gambar sudah disimpan
				isSave = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			isSave = false;
		}
	}


	/*
	 * Copies the image into a new buffered image.
	 */
	private static BufferedImage GetImageToEncrypt(BufferedImage image) {
		// Mengambil model warna gambar, seperti RGB, ARGB, atau format lainnya.
		ColorModel colorModel = image.getColorModel();

		/*
		 * Memeriksa apakah gambar memiliki alpha premultiplied.
		 * premultiplied alpha, setiap komponen warna dikalikan dengan nilai alpha, lalu dibagi dengan 255
		 * Jika true, berarti nilai warna RGB pada gambar sudah dikalikan dengan nilai alpha (transparansi).
		 */
		boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();

		/*
		* WritableRaster adalah sebuah object yg mewakili array pixel gambar yg dapat dimodif
		* mencopy semua data image ke raster agar perubahan tidak mempengaruhi gambar asli
		*/
		WritableRaster raster = image.copyData(null);

		/*
		* membuat object BufferedImage baru berdasarkan
		* - model warna, raster, isAlphaPremultiplied, null
		* berfungsi untuk membuat gambar baru, sehingga gambar utama tidak terpengaruh
		*/
		return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
	}
	
	/*
	 * Gets two dimensional array of colors from the image to encrypt.
	 */
	private static Pixel[] GetPixelArray(BufferedImage imageToEncrypt){
		int height = imageToEncrypt.getHeight();
		int width = imageToEncrypt.getWidth();
		/*
		* mengkonversi array 2d ke 1d dengan panjang yang sama.
		*/
		Pixel[] pixels = new Pixel[height * width];
		int count = 0;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				/*
				* mengambil warna RGB diposisi (x, y) yang ada di gambar yg akan di enkripsi
				* lalu memasukkannya ke dalam array 1d beserta info posisi x,y dan warna RBG nya
				*/
				Color colorToAdd = new Color(imageToEncrypt.getRGB(x, y));
				// membuat object baru pada index array
				pixels[count] = new Pixel(x, y, colorToAdd);
				count++;
			}
		}
		return pixels;
	};
	
	/*
	 * Converting the message into binary.
	 */
	private static String[] ConvertMessageToBinary(String message) {
		/*
		* Massage akan dikonversi dulu ke acsii, lalu dikonversi ke binary
		*/
		int[] messageInAscii = ConvertMessageToAscii(message);
		String[] binary = ConvertAsciiToBinary(messageInAscii);
		return binary;
	}
	
	/*
	 * Converting the message into ASCII.
	 */
	private static int[] ConvertMessageToAscii(String message) {
		int[] messageCharactersInAscii = new int[message.length()];
		for(int i = 0; i < message.length(); i++) {
			// mengubah karakter pada index ke i, menjadi int
			int asciiValue = (int) message.charAt(i);
			messageCharactersInAscii[i] = asciiValue;
		}
		return messageCharactersInAscii;
	}
	
	/*
	 * Converting the ASCII code to Binary.
	 */
	private static String[] ConvertAsciiToBinary(int[] messageInAscii) {
		String[] messageInBinary = new String[messageInAscii.length];
		for(int i = 0; i < messageInAscii.length; i++) {
			// method dari java yang memungkinkan mengubah int ke binary -> Integer.toBinaryString();
			// namun method ini hanya memberikan nilai konversi digit binary, sehingga kita harus menambahkan 0 di kiri agar menjadi 8 bit
			// contoh jika kita mengubah 9 jadi biner = 1001, kita harus menambahkan 0 agar menjadi 8 bit -> 0000 1001
			// contoh jika kita mengubah 97 jadi biner = 1100001, kita harus menambahkan 0 agar menjadi 8 bit -> 0110 0001
			// menggunakan method LeftPadZeros();
			String asciiBinary = LeftPadZeros(Integer.toBinaryString(messageInAscii[i]));
			messageInBinary[i] = asciiBinary;
		}
		return messageInBinary;
	}
	
	/*
	 * Left padding the binary value with zeros to make an 8 digit string.
	 */
	private static String LeftPadZeros(String value) {
		StringBuilder paddedValue = new StringBuilder("00000000");
		// menentukan panjang string yang ingin ditambahkan
		int offSet = 8 - value.length();

		// melakukan iterasi untuk mengubah paddedValue pada index ke i+offset mengubah jadi
		// karakter pada index ke i pada string value
		for(int i = 0 ; i < value.length(); i++) {
			paddedValue.setCharAt(i+offSet, value.charAt(i));
		}
		return paddedValue.toString();
	}
	
	
	/*
	 * Encoding the message in the pixels.
	 */
	private static void EncodeMessageBinaryInPixels(Pixel[] pixels, String[] messageBinary) {
		int pixelIndex = 0;
		boolean isLastCharacter = false;
		for(int i = 0; i < messageBinary.length; i++) {
			// karna 1 karakter memerlukan 3 pixel, maka kita akan membundle 3 pixel menjadi 1 array yaitu currentPixel
			Pixel[] currentPixels = new Pixel[] {pixels[pixelIndex], pixels[pixelIndex+1], pixels[pixelIndex+2]};

			// mengecek apakah i + 1 sama dengan panjang pesan yg berbentuk binary
			if(i+1 == messageBinary.length) {
				isLastCharacter = true; 
			}

			// mengubah satu persatu karakter pesan.
			ChangePixelsColor(messageBinary[i], currentPixels, isLastCharacter);

			// karna kita sudah mengambil 3, maka akan dilanjutkan 3 setelahnya, yang berarti akan di += 3
			pixelIndex = pixelIndex +3;
		}
	}
	
	private static void ChangePixelsColor(String messageBinary, Pixel[] pixels, boolean isLastCharacter) {
		int messageBinaryIndex = 0;

		// pixels length - 1, karna pada pixel ke 1 dan 2 yang kita gunakan ke 3 warna nya, sedangkan pixel ke 3, hanya digunakan 2 warna saja.
		// akan ada perlakuan khusus untuk pixel ke 3
		for(int i =0; i < pixels.length-1; i++) {

			// mengambil 3 bit pada massageBinary, lalu memasukkannya kedalam massageBinaryChars
			char[] messageBinaryChars = new char[] {messageBinary.charAt(messageBinaryIndex), messageBinary.charAt(messageBinaryIndex+1), messageBinary.charAt(messageBinaryIndex+2)};

			// mengambil warna RGB dan mengubahnya menjadi binary, lalu memasukkan kedalam array pixelRGBBinary
			String[] pixelRGBBinary = GetPixelsRGBBinary(pixels[i], messageBinaryChars);

			// membuat pixel yg tadinya biner, menjadi int lalu memasukkan pixel yg baru kedalam gambar.
			pixels[i].setColor(GetNewPixelColor(pixelRGBBinary));

			// karna sudah diambil 3, maka indexnya akan bertambah 3 ( +=3 )
			messageBinaryIndex = messageBinaryIndex + 3;
		}
		// perlakuan khusus untuk pixel ke 3.
		// jika bukan karakter terakhir
		if(!isLastCharacter) {
			// maka akan mengambil 2 warna saja dan dimasukkan kedalam messageBinaryChars yang bersisa 2, lalu karna ini bukan karakter terakhir,
			// maka warna ke 3 (blue) di set ke 1 untuk tanda bahwa ini bukan karakter terakhir.
			char[] messageBinaryChars = new char[] {messageBinary.charAt(messageBinaryIndex), messageBinary.charAt(messageBinaryIndex+1), '1'};

			// pixels.length-1 karna hanya bersisa 2 karakter, maka yg dirubah hanya R dan G nya saja maka yg dipassing pixel 0 dan 1
			// (panjang pixel = 3)
			String[] pixelRGBBinary = GetPixelsRGBBinary(pixels[pixels.length-1], messageBinaryChars);

			// membuat pixel yg tadinya biner, menjadi int lalu memasukkan pixel yg baru kedalam gambar.
			pixels[pixels.length-1].setColor(GetNewPixelColor(pixelRGBBinary));
		}else {
			// penjelasan sama seperti di section if, bedanya disini karna ini karakter terakhir, maka warna ke 3 di set 0.
			char[] messageBinaryChars = new char[] {messageBinary.charAt(messageBinaryIndex), messageBinary.charAt(messageBinaryIndex+1), '0'};
			String[] pixelRGBBinary = GetPixelsRGBBinary(pixels[pixels.length-1], messageBinaryChars);
			pixels[pixels.length-1].setColor(GetNewPixelColor(pixelRGBBinary));
		}
	}

	// mengubah pixel RGB menjadi integer lalu mengubahnya menjadi binary, dan mengambil bit paling kiri untuk diubah
	private static String[] GetPixelsRGBBinary(Pixel pixel, char[] messageBinaryChars) {

		// karna 1 pixel ada 3 warna, maka kita set pixel rgb sebanyak 3.
		String[] pixelRGBBinary = new String[3];

		// * mengubah ke int lalu diubah lagi menjadi biner, dan memanggil fungsi changePixelBinary untuk mengubah bit terakhir menjadi karakter pesan yg berbentuk binary

		// mengubah warna merah *
		pixelRGBBinary[0] = ChangePixelBinary(Integer.toBinaryString(pixel.getColor().getRed()), messageBinaryChars[0]);

		// mengubah warna Hijau *
		pixelRGBBinary[1] = ChangePixelBinary(Integer.toBinaryString(pixel.getColor().getGreen()), messageBinaryChars[1]);

		// mengubah warna Biru *
		pixelRGBBinary[2] = ChangePixelBinary(Integer.toBinaryString(pixel.getColor().getBlue()), messageBinaryChars[2]);

		// mengembalikan pixel yg sudah dimodifikasi
		return pixelRGBBinary;
	}

	// mengubah pixel RGB yg berbentuk int ke binary, dan mengambil bit paling kiri untuk diubah.
	private static String ChangePixelBinary(String pixelBinary, char messageBinaryChar) {

		// String bersifat immutable, artinya setiap kali kita memodifikasi string, sebenarnya Java membuat salinan baru dari string tersebut.
		// StringBuilder bersifat mutable, sehingga dapat memodifikasi string tanpa membuat objek baru setiap kali perubahan dilakukan.
		StringBuilder sb = new StringBuilder(pixelBinary);

		// mengubah char terakhir (bit paling kiri) pada pixel binary, ke massage yg sudah dibuat jadi biner.
		sb.setCharAt(pixelBinary.length()-1, messageBinaryChar);

		// return sb sebagai string.
		return sb.toString();
	}

	// membuat formulasi warna baru dari pixel yang sudah diubah bit nya.
	private static Color GetNewPixelColor(String[] colorBinary) {
		// colorBinary[*]
		// 0 = red, 1 = green, 2 = blue
		// radix = 2, karna basis bilangan 2 (biner), menunjukan bahwa bilangan biner akan diubah ke decimal (int)
		return new Color(Integer.parseInt(colorBinary[0], 2), Integer.parseInt(colorBinary[1], 2), Integer.parseInt(colorBinary[2], 2));
	}
	
	// mengubah gambar yang ada di buffer ke gambar yang sudah dimodifikasi
	private static void ReplacePixelsInNewBufferedImage(Pixel[] newPixels, BufferedImage newImage) {
		for(int i = 0; i < newPixels.length; i++) {
			// membuat bufferedImage yang dipassing diset x, y dan rgb nya
			newImage.setRGB(newPixels[i].getX(), newPixels[i].getY(), newPixels[i].getColor().getRGB());
		}
	};
	
	private static void SaveNewFile(BufferedImage newImage, File newImageFile) {
		try {
			// membuat gambar baru berformat png
			ImageIO.write(newImage, "png", newImageFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
