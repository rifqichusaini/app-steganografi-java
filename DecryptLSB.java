import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class DecryptLSB {
    private static String pass;

    public static String Decrypt(File newImageFile, String pass) {
        BufferedImage image;
        DecryptLSB.pass = pass;

        if (newImageFile != null) {
            try {
                image = ImageIO.read(newImageFile);
                Pixel[] pixels = GetPixelArray(image);
                String decodedMessage = DecodeMessageFromPixels(pixels);

                // Validasi pesan dengan flag
                if (isValidMessage(decodedMessage)) {
                    return extractActualMessage(decodedMessage);
                } else {
                    return "null";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Terjadi kesalahan saat mendekripsi gambar.";
            }
        }
        return "";
    }

    // Method untuk validasi pesan
    private static boolean isValidMessage(String message) {
        return message != null &&
                message.startsWith(pass) &&
                message.endsWith(pass);
    }

    // Method untuk mengekstrak pesan asli
    private static String extractActualMessage(String message) {
        return message.substring(
                pass.length(),
                message.length() - pass.length()
        );
    }

    private static Pixel[] GetPixelArray(BufferedImage imageToEncrypt) {
        int height = imageToEncrypt.getHeight();
        int width = imageToEncrypt.getWidth();
        Pixel[] pixels = new Pixel[height * width];

        int count = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color colorToAdd = new Color(imageToEncrypt.getRGB(x, y));
                pixels[count] = new Pixel(x, y, colorToAdd);
                count++;
            }
        }
        return pixels;
    }

    private static String DecodeMessageFromPixels(Pixel[] pixels) {
        boolean completed = false;
        int pixelArrayIndex = 0;
        StringBuilder messageBuilder = new StringBuilder("");

        // akan terus loop sampai semua pesan sampai fungsi IsEndOfMessage mengembalikan true
        while (!completed && pixelArrayIndex + 3 <= pixels.length) {
            // mengcopy 3 pixel gambar yg ingin didecode ke pixelToRead
            // disini menggunakan 3, karna akan membaca per karakter
            // 1 karakter membutuhkan 3 pixel
            Pixel[] pixelsToRead = new Pixel[3];
            for (int i = 0; i < 3; i++) {
                pixelsToRead[i] = pixels[pixelArrayIndex];
                // pixelArrayIndex akan terus bertambah sampai completed == true
                pixelArrayIndex++;
            }

            // menambahkan karakter yang ditemukan kedalam massage builder
            // append digunakan untuk menambahkan setiap karakter yg ditemukan ke stringBuilder
            // ConvertPixelsToCharacter berfungsi untuk mengkonversi pixel ke karakter
            messageBuilder.append(ConvertPixelsToCharacter(pixelsToRead));

            // mengecek apakah semua pesan sudah dibaca dan selesai
            if (IsEndOfMessage(pixelsToRead[2])) {
                completed = true;
            }
        }
        return messageBuilder.toString();
    }

    // mengkonversi 3 pixel jadi 1 karakter
    private static char ConvertPixelsToCharacter(Pixel[] pixelsToRead) {
        // membuat arrayList bertipe string, jika di cpp namanya vector
        ArrayList<String> binaryValues = new ArrayList<String>();

        // loping sebanyak pixelsToRead.length (3) karna akan membaca 3 pixel
        for (int i = 0; i < pixelsToRead.length; i++) {
            // kita akan mengambil data warna yang ada di pixelToRead, dan mengubahnya menjadi biner
            // menggunakan fungsi TurnPixelIntegersToBinary
            String[] currentBinary = TurnPixelIntegersToBinary(pixelsToRead[i]);

            // setiap current binary merupakan bilangan biner yang akan dimasukkan ke vector
            binaryValues.add(currentBinary[0]);
            binaryValues.add(currentBinary[1]);
            binaryValues.add(currentBinary[2]);
        }

        // pada akhirnya binary values berisi 9 element.
        // karna setiap piksel ada 3 warna (RGB), dan kita punya 3 pixel.
        return ConvertBinaryValuesToCharacter(binaryValues);
    }

    // method yang digunakan untuk mengambil data warna dan diubah ke binary.
    private static String[] TurnPixelIntegersToBinary(Pixel pixel) {
        // membuat string values sebanyak 3, karna 1 piksel 3 warna
        String[] values = new String[3];

        // mengambil data pixel.getColor(), dan meletakkan pada masing" index
        // index 0 = merah, 1 = hijau, 2 = biru
        // pixel.getColor adalah int dengan rentan 0 - 255, kita akan merubah jadi biner
        values[0] = Integer.toBinaryString(pixel.getColor().getRed());
        values[1] = Integer.toBinaryString(pixel.getColor().getGreen());
        values[2] = Integer.toBinaryString(pixel.getColor().getBlue());

        //isi dari values merupakan bilangan biner
        return values;
    }

    // fungsi untuk mengecek apakah string sudah habis
    private static boolean IsEndOfMessage(Pixel pixel) {
        // jika warna terakhir dari 3 pixel (9 elemnent) == 0, maka itu adalah end dari pesan yg disembunyikan
        if (TurnPixelIntegersToBinary(pixel)[2].endsWith("1")) {
            return false;
        }
        return true;
    }

    private static char ConvertBinaryValuesToCharacter(ArrayList<String> binaryValues) {
        // Membuat string builder kosong.
        StringBuilder endBinary = new StringBuilder("");

        // binaryValues.size() - 1, karna kita akan membaca 8 element, menyisakan element ke 9.
        for (int i = 0; i < binaryValues.size() - 1; i++) {
            // mengambil bit terakhir dari masing" element di binaryValues
            // binaryValues.get(i).charAt(binaryValues.get(i).length() - 1)
            // binaryValues.get(i).charAt() -> membaca index ke i, lalu membaca karakter pada posisi :
            // (binaryValues.get(i).length() - 1) -> binaryValues pasti berisi 8 karakter, nah kita mau ngakses index terakhir
            // yang mana index terakhir nya 7, karna dimulai dari 0, jadi kita menggunakan ( -1 )
            // untuk mendapatkan bit terakhir dari setiap element biner
            endBinary.append(binaryValues.get(i).charAt(binaryValues.get(i).length() - 1));
        }
        // setelah mengumpulkan bit terkhir pada setiap pixel, maka endBinary akan diubah ke string dan dimasukkan ke endBinaryString
        String endBinaryString = endBinary.toString();

        // jika kita mengingat pada encode, bahwa kita mengubah binary menjadi 8bit
        // kita menambahkan 0 pada kiri bilangan biner, maka kita harus menghapusnya agar mendapatkan nilai actual dari biner yg akan di decode
        // contoh isi dari endBinaryString = 0110 0001, nah ini diawali dengan 0, maka kita harus menghapusnya dan hasil akhirnya = 1100001
        // contoh isi dari endBinaryString = 0000 1001, nah ini diawali dengan 0, maka kita harus menghapusnya dan hasil akhirnya = 1001
        String noZeros = RemovePaddedZeros(endBinaryString);

        // kondisi dimana tidak ada karakter yang bisa dibaca, atau isi dari endBinaryString = 0000 0000
        if (noZeros.isEmpty()) {
            return '\0'; // atau throw exception
        }

        // mengubah nilai noZeros dengan radix (basis) 2, menjadi int
        int ascii = Integer.parseInt(noZeros, 2);

        // meretrun ascii yg dicast menjadi char
        return (char) ascii;
    }

    // method untuk menghapus 0 dibagian kiri.
    private static String RemovePaddedZeros(String endBinary) {
        StringBuilder builder = new StringBuilder(endBinary);
        int paddedZeros = 0;
        for (int i = 0; i < builder.length(); i++) {
            // ini akan terus loop jika tidak bertemu 1, jika terus bertemu 0, maka akan terus loop
            // dan terus menambah paddedZeros.
            // jika ketemu 1, maka akan break dari loop
            if (builder.charAt(i) == '0') {
                paddedZeros++;
            } else {
                break;
            }
        }
        for (int i = 0; i < paddedZeros; i++) {
            // menghapus element pertama sampai paddedZeros habis
            builder.deleteCharAt(0);
        }

        // mengembalikan stringbuilder yg di cast jadi string.
        return builder.toString();
    }
}