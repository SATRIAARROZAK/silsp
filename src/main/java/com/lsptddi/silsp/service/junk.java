// // // package com.lsptddi.silsp.service;

// // // import com.lsptddi.silsp.dto.UserProfileDto;

// // // import lombok.Data;
// // // import net.sourceforge.tess4j.ITesseract;
// // // import net.sourceforge.tess4j.Tesseract;
// // // import net.sourceforge.tess4j.TesseractException;
// // // import org.apache.commons.text.similarity.LevenshteinDistance;
// // // import org.springframework.stereotype.Service;
// // // import org.springframework.web.multipart.MultipartFile;

// // // import javax.imageio.ImageIO;
// // // import java.awt.*;
// // // import java.awt.image.BufferedImage;
// // // import java.io.File;
// // // import java.io.IOException;
// // // import java.time.format.DateTimeFormatter;
// // // import java.util.regex.Matcher;
// // // import java.util.regex.Pattern;
// // // import java.awt.image.RescaleOp;

// // // @Service
// // // public class OCRService {

// // //     // Lokasi folder tessdata (Sesuaikan dengan struktur projectmu)
// // //     private static final String TESSDATA_PATH = "tessdata";

// // //     // public String extractText(MultipartFile file) throws IOException,
// // //     // TesseractException {

// // //     public String extractText(MultipartFile file) throws IOException, TesseractException {
// // //         // 1. Konversi MultipartFile ke File temporary
// // //         File convFile = File.createTempFile("ocr-", ".jpg");
// // //         file.transferTo(convFile);

// // //         // 2. Pre-processing Gambar (Agar lebih mudah dibaca Tesseract)
// // //         // Kita ubah jadi Grayscale dan perbesar sedikit
// // //         // BufferedImage image = ImageIO.read(convFile);
// // //         // BufferedImage processedImage = preprocessImage(image);

// // //         // 3. Setup Tesseract
// // //         // ITesseract instance = new Tesseract();
// // //         // instance.setDatapath(TESSDATA_PATH);
// // //         // instance.setLanguage("ind"); // Pakai bahasa Indonesia

// // //         // // 4. Eksekusi OCR
// // //         // String result = instance.doOCR(processedImage);

// // //         // // Bersihkan file temp
// // //         // convFile.delete();

// // //         // return result.toUpperCase(); // Biar mudah dibandingin

// // //         // 1. Baca Gambar
// // //         BufferedImage originalImage = ImageIO.read(convFile);

// // //         // 2. PRE-PROCESSING TINGKAT LANJUT (Kunci Akurasi)
// // //         // KTP seringkali resolusinya rendah untuk OCR, kita perbesar 2x-3x
// // //         BufferedImage scaledImage = scaleImage(originalImage, 2.5);
// // //         // Ubah ke Hitam Putih (Binarization) agar teks tajam
// // //         BufferedImage processedImage = processImageForOCR(scaledImage);

// // //         // 3. Setup Tesseract
// // //         ITesseract instance = new Tesseract();
// // //         instance.setDatapath(TESSDATA_PATH);
// // //         instance.setLanguage("ind");
// // //         // Whitelist karakter agar tidak membaca simbol aneh (Optional, tapi membantu)
// // //         // instance.setTessVariable("tessedit_char_whitelist",
// // //         // "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/.-: ");

// // //         String result = instance.doOCR(processedImage);

// // //         // Cleanup
// // //         convFile.delete();
// // //         return result.toUpperCase(); // Biar mudah dibandingin
// // //     }

// // //     // Algoritma Validasi
// // //     public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto inputData) {
// // //         OcrValidationResult result = new OcrValidationResult();
// // //         try {
// // //             String ocrText = extractText(ktpFile).toUpperCase();
// // //             // Bersihkan simbol aneh agar pencarian lebih akurat
// // //             ocrText = ocrText.replaceAll("[^A-Z0-9\\s\\:\\/\\-\\.]", "");

// // //             result.setRawText(ocrText);

// // //             // 1. Ekstrak Teks
// // //             // String ocrText = extractText(ktpFile);
// // //             // result.setRawText(ocrText);

// // //             System.out.println("--- HASIL OCR KTP ---");
// // //             System.out.println(ocrText);
// // //             System.out.println("---------------------");

// // //             // 1. VALIDASI NIK (Strict)
// // //             // Cari 16 digit angka
// // //             // Pattern nikPattern = Pattern.compile("\\b\\d{16}\\b");
// // //             // Matcher nikMatcher = nikPattern.matcher(ocrText);
// // //             // boolean nikMatch = false;
// // //             // while (nikMatcher.find()) {
// // //             // if (nikMatcher.group().equals(inputData.getNik())) {
// // //             // nikMatch = true;
// // //             // break;
// // //             // }
// // //             // }
// // //             // result.setNikValid(nikMatch);
// // //             // 2. VALIDASI NAMA (Fuzzy)
// // //             // LevenshteinDistance levenshtein = new LevenshteinDistance();
// // //             // boolean nameMatch = false;
// // //             // // Cari baris yang mengandung "NAMA"
// // //             // String[] lines = ocrText.split("\\r?\\n");
// // //             // for(String line : lines) {
// // //             // if(line.contains("NAMA")) {
// // //             // String nameLine = line.replace("NAMA", "").replace(":", "").trim();
// // //             // int distance = levenshtein.apply(nameLine,
// // //             // inputData.getFullName().toUpperCase());
// // //             // // Toleransi jarak <= 3
// // //             // if(distance <= 3) {
// // //             // nameMatch = true;
// // //             // }
// // //             // }
// // //             // }
// // //             // // Fallback: Cek keseluruhan teks
// // //             // if(!nameMatch && ocrText.contains(inputData.getFullName().toUpperCase())) {
// // //             // nameMatch = true;
// // //             // }
// // //             // result.setNameValid(nameMatch);

// // //             LevenshteinDistance dist = new LevenshteinDistance();
// // //             String inputNama = inputData.getFullName().toUpperCase();
// // //             boolean namaMatch = false;
// // //             // String inputNama = cleanString(inputData.getFullName());
// // //             // boolean nameMatch = false;
// // //             double maxScore = 0.0;

// // //             // Coba cari per baris (lebih akurat daripada full text)
// // //             for (String line : ocrText.split("\\n")) {
// // //                 // Hapus label "NAMA" dan simbol
// // //                 String cleanLine = line.replace("NAMA", "").replace(":", "").trim();
// // //                 if (cleanLine.isEmpty())
// // //                     continue;

// // //                 // Jika input nama pendek (< 5 char), harus exact match. Jika panjang, toleransi
// // //                 // 3 typo.
// // //                 // int threshold = inputNama.length() < 5 ? 0 : 3;

// // //                 // if (dist.apply(cleanLine, inputNama) <= threshold) {
// // //                 // namaMatch = true;
// // //                 // break;
// // //                 // }
// // //                 // // Cek contains juga (misal: "MUHAMMAD SATRIA ARROZAK" vs "SATRIA ARROZAK")
// // //                 // if (cleanLine.contains(inputNama) || inputNama.contains(cleanLine)) {
// // //                 // namaMatch = true;
// // //                 // break;
// // //                 // }

// // //                 if (cleanLine.length() < 3)
// // //                     continue;

// // //                 // Hitung Similarity (0.0 - 1.0)
// // //                 double score = calculateSimilarity(inputNama, cleanLine);
// // //                 if (score > maxScore)
// // //                     maxScore = score;

// // //                 // Threshold 0.80 (80% Mirip)
// // //                 if (score >= 0.80) {
// // //                     namaMatch = true;
// // //                     break;
// // //                 }
// // //             }
// // //             System.out.println("Input Nama: " + inputNama + " | Max OCR Score: " + maxScore);
// // //             result.setNameValid(namaMatch);

// // //             // 3. VALIDASI TANGGAL LAHIR
// // //             // if(inputData.getBirthDate() != null) {
// // //             // String dobInput =
// // //             // inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
// // //             // if(ocrText.contains(dobInput)) {
// // //             // result.setDobValid(true);
// // //             // }
// // //             // }

// // //             // 3. TEMPAT LAHIR & TGL LAHIR
// // //             // Format KTP: "BEKASI, 20-01-2000"
// // //             boolean tempatMatch = false;
// // //             boolean tglMatch = false;

// // //             String inputTempat = inputData.getBirthPlace().toUpperCase();
// // //             String inputTgl = "";
// // //             if (inputData.getBirthDate() != null) {
// // //                 // Ubah YYYY-MM-DD (Input) jadi dd-MM-yyyy (KTP)
// // //                 inputTgl = inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
// // //             }

// // //             // Cari baris yang mengandung tanggal lahir
// // //             for (String line : ocrText.split("\\n")) {
// // //                 if (line.contains(inputTgl)) {
// // //                     tglMatch = true; // Tanggal ketemu di baris ini

// // //                     // Cek tempat lahir di baris yang SAMA (sebelum tanda koma)
// // //                     if (line.toUpperCase().contains(inputTempat)) {
// // //                         tempatMatch = true;
// // //                     }
// // //                 }
// // //             }
// // //             // Fallback: cari tempat lahir di seluruh teks jika format baris hancur
// // //             if (!tempatMatch && ocrText.contains(inputTempat))
// // //                 tempatMatch = true;

// // //             result.setBirthPlaceValid(tempatMatch);
// // //             result.setBirthDateValid(tglMatch);

// // //             // 4. VALIDASI JENIS KELAMIN
// // //             // String genderInput = inputData.getGender().equals("L") ? "LAKI-LAKI" :
// // //             // "PEREMPUAN";
// // //             // if(ocrText.contains(genderInput)) {
// // //             // result.setGenderValid(true);
// // //             // }
// // //             // } catch (Exception e) {
// // //             // e.printStackTrace();
// // //             // result.setError("Gagal memproses OCR: " + e.getMessage());
// // //             // }
// // //             // return result;

// // //             // 4. JENIS KELAMIN
// // //             // String genderKtp = inputData.getGender().equals("L") ? "LAKI-LAKI" :
// // //             // "PEREMPUAN";
// // //             // result.setGenderValid(ocrText.contains(genderKtp));

// // //             // boolean genderMatch = false;
// // //             // String targetGender = inputData.getGender().equals("L") ? "LAKI" : "PEREM";

// // //             // for (String line : lines) {
// // //             // if (line.contains("KELAMIN")) {
// // //             // // Ambil teks setelah kata "KELAMIN"
// // //             // String value = line.substring(line.indexOf("KELAMIN") +
// // //             // 7).trim().replace(":", "").trim();

// // //             // // Cek Typo: LAKELAKI, LAKI-LAKI, PEREMPUAN, P3REMPUAN
// // //             // if (value.startsWith("L") || value.contains("LAKI") ||
// // //             // value.contains("LAK1")) {
// // //             // if (inputData.getGender().equals("L")) genderMatch = true;
// // //             // }
// // //             // else if (value.startsWith("P") || value.contains("PEREM") ||
// // //             // value.contains("WANITA")) {
// // //             // if (inputData.getGender().equals("P")) genderMatch = true;
// // //             // }
// // //             // }
// // //             // }

// // //             // --- 2. VALIDASI JENIS KELAMIN (FLEXIBLE) ---
// // //             // Cari baris mengandung "KELAMIN"
// // //             boolean genderMatch = false;
// // //             String targetGender = inputData.getGender().equals("L") ? "LAKI" : "PEREM";
// // //             String[] lines = ocrText.split("\\n");

// // //             for (String line : lines) {
// // //                 if (line.contains("KELAMIN")) {
// // //                     // Ambil teks setelah kata "KELAMIN"
// // //                     String value = line.substring(line.indexOf("KELAMIN") + 7).trim().replace(":", "").trim();

// // //                     // Cek Typo: LAKELAKI, LAKI-LAKI, PEREMPUAN, P3REMPUAN
// // //                     if (value.startsWith("L") || value.contains("LAKI") || value.contains("LAK1")) {
// // //                         if (inputData.getGender().equals("L"))
// // //                             genderMatch = true;
// // //                     } else if (value.startsWith("P") || value.contains("PEREM") || value.contains("WANITA")) {
// // //                         if (inputData.getGender().equals("P"))
// // //                             genderMatch = true;
// // //                     }
// // //                 }
// // //             }
// // //             // Fallback: cari kata kunci di seluruh teks jika baris tidak terdeteksi rapi
// // //             if (!genderMatch) {
// // //                 if (inputData.getGender().equals("L") && (ocrText.contains("LAKI") || ocrText.contains("LAK1"))) {
// // //                     genderMatch = true;
// // //                 } else if (inputData.getGender().equals("P")
// // //                         && (ocrText.contains("PEREM") || ocrText.contains("WANITA"))) {
// // //                     genderMatch = true;
// // //                 }
// // //             }
// // //             result.setGenderValid(genderMatch);

// // //         } catch (Exception e) {
// // //             e.printStackTrace();
// // //             result.setError("Gagal baca OCR: " + e.getMessage());
// // //         }
// // //         return result;
// // //         // }
// // //     }

// // //     // public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto
// // //     // inputData) {
// // //     // OcrValidationResult result = new OcrValidationResult();
// // //     // try {
// // //     // // 1. Ekstrak Teks
// // //     // String ocrText = extractText(ktpFile);
// // //     // result.setRawText(ocrText);

// // //     // System.out.println("--- HASIL OCR KTP ---");
// // //     // System.out.println(ocrText);
// // //     // System.out.println("---------------------");

// // //     // // 2. Validasi NIK (Cari 16 digit angka)
// // //     // // Menggunakan Regex yang mentolerir kesalahan baca (misal 'B' jadi '8' jika
// // //     // // perlu, tapi kita strict dulu)
// // //     // String nikRegex = "\\b\\d{16}\\b";
// // //     // Pattern patternNik = Pattern.compile(nikRegex);
// // //     // Matcher matcherNik = patternNik.matcher(ocrText);

// // //     // boolean nikFound = false;
// // //     // while (matcherNik.find()) {
// // //     // String foundNik = matcherNik.group();
// // //     // if (foundNik.equals(inputData.getNik())) {
// // //     // nikFound = true;
// // //     // break;
// // //     // }
// // //     // }
// // //     // result.setNikValid(nikFound);

// // //     // // 3. Validasi Nama (Fuzzy Logic)
// // //     // // Cari baris yang mengandung "NAMA"
// // //     // LevenshteinDistance distance = new LevenshteinDistance();
// // //     // boolean nameMatch = false;

// // //     // // Pecah per baris untuk cari Nama
// // //     // String[] lines = ocrText.split("\\r?\\n");
// // //     // for (String line : lines) {
// // //     // if (line.contains("NAMA")) {
// // //     // String cleanLine = line.replace("NAMA", "").replace(":", "").trim();
// // //     // // Hitung kemiripan
// // //     // int dist = distance.apply(cleanLine, inputData.getFullName().toUpperCase());
// // //     // // Toleransi: Jika perbedaan karakter <= 3, anggap valid
// // //     // if (dist <= 3) {
// // //     // nameMatch = true;
// // //     // }
// // //     // }
// // //     // }
// // //     // // Fallback: Jika tidak ada label "NAMA", cari string yang mirip banget di
// // //     // // seluruh teks
// // //     // if (!nameMatch && ocrText.contains(inputData.getFullName().toUpperCase())) {
// // //     // nameMatch = true;
// // //     // }
// // //     // result.setNameValid(nameMatch);

// // //     // // 4. Validasi Tanggal Lahir
// // //     // // Format Input: YYYY-MM-DD
// // //     // // Format KTP: DD-MM-YYYY
// // //     // if (inputData.getBirthDate() != null) {
// // //     // String tglLahirInput =
// // //     // inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
// // //     // if (ocrText.contains(tglLahirInput)) {
// // //     // result.setDobValid(true);
// // //     // }
// // //     // }

// // //     // // 5. Validasi Jenis Kelamin
// // //     // String genderInput = inputData.getGender().equals("L") ? "LAKI-LAKI" :
// // //     // "PEREMPUAN";
// // //     // if (ocrText.contains(genderInput)) {
// // //     // result.setGenderValid(true);
// // //     // }

// // //     // } catch (Exception e) {
// // //     // e.printStackTrace();
// // //     // result.setError("Gagal memproses OCR: " + e.getMessage());
// // //     // }
// // //     // return result;
// // //     // }

// // //     // Helper: Image Pre-processing sederhana
// // //     // private BufferedImage preprocessImage(BufferedImage original) {
// // //     // // Ubah ke Grayscale
// // //     // BufferedImage grayscale = new BufferedImage(original.getWidth(),
// // //     // original.getHeight(),
// // //     // BufferedImage.TYPE_BYTE_GRAY);
// // //     // Graphics g = grayscale.getGraphics();
// // //     // g.drawImage(original, 0, 0, null);
// // //     // g.dispose();
// // //     // return grayscale;
// // //     // }

// // //     // --- HELPER FUNCTIONS ---

// // //     // 1. Bersihkan String (Hanya Huruf A-Z)
// // //     // private String cleanString(String text) {
// // //     // if (text == null) return "";
// // //     // return text.toUpperCase().replaceAll("[^A-Z]", "");
// // //     // }

// // //     // 2. Hitung Kemiripan String (Jaro-Winkler / Levenshtein Normalized)
// // //     private double calculateSimilarity(String s1, String s2) {
// // //         if (s1 == null || s2 == null)
// // //             return 0.0;
// // //         LevenshteinDistance dist = new LevenshteinDistance();
// // //         int maxLength = Math.max(s1.length(), s2.length());
// // //         if (maxLength == 0)
// // //             return 1.0;
// // //         int distance = dist.apply(s1, s2);
// // //         return 1.0 - ((double) distance / maxLength);
// // //     }

// // //     // 3. Scale Image (Perbesar)
// // //     private BufferedImage scaleImage(BufferedImage original, double factor) {
// // //         int w = (int) (original.getWidth() * factor);
// // //         int h = (int) (original.getHeight() * factor);
// // //         BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
// // //         Graphics2D g = scaled.createGraphics();
// // //         g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
// // //         g.drawImage(original, 0, 0, w, h, null);
// // //         g.dispose();
// // //         return scaled;
// // //     }

// // //     // 4. Preprocess (Grayscale + Thresholding/Binarization)
// // //     private BufferedImage processImageForOCR(BufferedImage original) {
// // //         BufferedImage processed = new BufferedImage(original.getWidth(), original.getHeight(),
// // //                 BufferedImage.TYPE_BYTE_GRAY);
// // //         Graphics2D g = processed.createGraphics();
// // //         g.drawImage(original, 0, 0, null);
// // //         g.dispose();

// // //         // Simple Binarization (Thresholding)
// // //         // Ubah pixel jadi murni Hitam (0) atau Putih (255)
// // //         int width = processed.getWidth();
// // //         int height = processed.getHeight();
// // //         for (int y = 0; y < height; y++) {
// // //             for (int x = 0; x < width; x++) {
// // //                 int rgba = processed.getRGB(x, y);
// // //                 Color col = new Color(rgba, true);
// // //                 // Jika kecerahan > 130 dianggap putih, sisanya hitam (Teks KTP biasanya
// // //                 // hitam/gelap)
// // //                 // Note: KTP indo background biru/merah kadang gelap, perlu tuning.
// // //                 // Threshold 160 biasanya aman untuk teks hitam di background berwarna.
// // //                 if (col.getRed() + col.getGreen() + col.getBlue() > 380) {
// // //                     processed.setRGB(x, y, Color.WHITE.getRGB());
// // //                 } else {
// // //                     processed.setRGB(x, y, Color.BLACK.getRGB());
// // //                 }
// // //             }
// // //         }
// // //         return processed;
// // //     }

// // //     @Data
// // //     public static class OcrValidationResult {
// // //         // private boolean nikValid;
// // //         private boolean nameValid;
// // //         private boolean birthPlaceValid; // Baru
// // //         private boolean birthDateValid; // Baru
// // //         private boolean genderValid;
// // //         private String rawText;
// // //         private String error;

// // //         public boolean isValid() {
// // //             // Semua harus valid
// // //             return nameValid && birthPlaceValid && birthDateValid && genderValid;
// // //         }
// // //     }
// // //     // Inner Class untuk Hasil
// // //     // public static class OcrValidationResult {
// // //     // private boolean nikValid;
// // //     // private boolean nameValid;
// // //     // private boolean dobValid;
// // //     // private boolean genderValid;
// // //     // private String rawText;
// // //     // private String error;

// // //     // public boolean isValid() {
// // //     // // Valid jika NIK Cocok DAN Nama Cocok (Minimal)
// // //     // return nikValid && nameValid;
// // //     // }

// // //     // // Getter Setter
// // //     // public boolean isNikValid() {
// // //     // return nikValid;
// // //     // }

// // //     // public void setNikValid(boolean nikValid) {
// // //     // this.nikValid = nikValid;
// // //     // }

// // //     // public boolean isNameValid() {
// // //     // return nameValid;
// // //     // }

// // //     // public void setNameValid(boolean nameValid) {
// // //     // this.nameValid = nameValid;
// // //     // }

// // //     // public boolean isDobValid() {
// // //     // return dobValid;
// // //     // }

// // //     // public void setDobValid(boolean dobValid) {
// // //     // this.dobValid = dobValid;
// // //     // }

// // //     // public boolean isGenderValid() {
// // //     // return genderValid;
// // //     // }

// // //     // public void setGenderValid(boolean genderValid) {
// // //     // this.genderValid = genderValid;
// // //     // }

// // //     // public String getRawText() {
// // //     // return rawText;
// // //     // }

// // //     // public void setRawText(String rawText) {
// // //     // this.rawText = rawText;
// // //     // }

// // //     // public String getError() {
// // //     // return error;
// // //     // }

// // //     // public void setError(String error) {
// // //     // this.error = error;
// // //     // }
// // //     // }
// // // }

// // package com.lsptddi.silsp.service;

// // import com.lsptddi.silsp.dto.UserProfileDto;

// // import lombok.Data;
// // import net.sourceforge.tess4j.ITesseract;
// // import net.sourceforge.tess4j.Tesseract;
// // import net.sourceforge.tess4j.TesseractException;
// // import org.apache.commons.text.similarity.LevenshteinDistance;
// // import org.springframework.stereotype.Service;
// // import org.springframework.web.multipart.MultipartFile;

// // import javax.imageio.ImageIO;
// // import java.awt.*;
// // import java.awt.image.BufferedImage;
// // import java.io.File;
// // import java.io.IOException;
// // import java.time.format.DateTimeFormatter;
// // import java.util.regex.Matcher;
// // import java.util.regex.Pattern;

// // @Service
// // public class OCRService {

// //     // Lokasi folder tessdata (Sesuaikan dengan struktur projectmu)
// //     private static final String TESSDATA_PATH = "tessdata";

// //     public String extractText(MultipartFile file) throws IOException, TesseractException {
// //         // 1. Konversi MultipartFile ke File temporary
// //         File convFile = File.createTempFile("ocr-", ".jpg");
// //         file.transferTo(convFile);

// //         // 2. Pre-processing Gambar (Agar lebih mudah dibaca Tesseract)
// //         // Kita ubah jadi Grayscale dan perbesar sedikit
// //         BufferedImage image = ImageIO.read(convFile);
// //         BufferedImage scaledImage = scaleImage(image, 3.5);
// //         BufferedImage processedImage = preprocessImage(scaledImage);

// //         // 3. Setup Tesseract
// //         ITesseract instance = new Tesseract();
// //         instance.setDatapath(TESSDATA_PATH);
// //         instance.setLanguage("ind"); // Pakai bahasa Indonesia

// //         // 4. Eksekusi OCR
// //         String result = instance.doOCR(processedImage);

// //         // Bersihkan file temp
// //         convFile.delete();

// //         return result.toUpperCase(); // Biar mudah dibandingin

// //     }

// //     // Algoritma Validasi
// //     public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto inputData) {
// //         OcrValidationResult result = new OcrValidationResult();
// //         try {
// //             String ocrText = extractText(ktpFile).toUpperCase();
// //             // Bersihkan simbol aneh agar pencarian lebih akurat
// //             ocrText = ocrText.replaceAll("[^A-Z0-9\\s\\:\\/\\-\\.]", "");

// //             result.setRawText(ocrText);

// //             // 1. Ekstrak Teks
// //             // String ocrText = extractText(ktpFile);
// //             // result.setRawText(ocrText);

// //             System.out.println("--- HASIL OCR KTP ---");
// //             System.out.println(ocrText);
// //             System.out.println("---------------------");

// //             // 1. VALIDASI NIK (Strict)
// //             // Cari 16 digit angka
// //             // Pattern nikPattern = Pattern.compile("\\b\\d{16}\\b");
// //             // Matcher nikMatcher = nikPattern.matcher(ocrText);
// //             // boolean nikMatch = false;
// //             // while (nikMatcher.find()) {
// //             // if (nikMatcher.group().equals(inputData.getNik())) {
// //             // nikMatch = true;
// //             // break;
// //             // }
// //             // }
// //             // result.setNikValid(nikMatch);
// //             // 2. VALIDASI NAMA (Fuzzy)
// //             // LevenshteinDistance levenshtein = new LevenshteinDistance();
// //             // boolean nameMatch = false;
// //             // // Cari baris yang mengandung "NAMA"
// //             // String[] lines = ocrText.split("\\r?\\n");
// //             // for(String line : lines) {
// //             // if(line.contains("NAMA")) {
// //             // String nameLine = line.replace("NAMA", "").replace(":", "").trim();
// //             // int distance = levenshtein.apply(nameLine,
// //             // inputData.getFullName().toUpperCase());
// //             // // Toleransi jarak <= 3
// //             // if(distance <= 3) {
// //             // nameMatch = true;
// //             // }
// //             // }
// //             // }
// //             // // Fallback: Cek keseluruhan teks
// //             // if(!nameMatch && ocrText.contains(inputData.getFullName().toUpperCase())) {
// //             // nameMatch = true;
// //             // }
// //             // result.setNameValid(nameMatch);

// //             // LevenshteinDistance dist = new LevenshteinDistance();
// //             String inputNama = inputData.getFullName().toUpperCase();
// //             boolean namaMatch = false;
// //             double maxScore = 0.0;

// //             // Coba cari per baris (lebih akurat daripada full text)
// //             for (String line : ocrText.split("\\n")) {
// //                 // Hapus label "NAMA" dan simbol
// //                 String cleanLine = line.replace("NAMA", "").replace(":", "").trim();
// //                 // if (cleanLine.isEmpty())
// //                 // continue;

// //                 if (cleanLine.length() < 3)
// //                     continue;
// //                 // Jika input nama pendek (< 5 char), harus exact match. Jika panjang, toleransi
// //                 // 3 typo.
// //                 // int threshold = inputNama.length() < 4 ? 0 : 1;

// //                 // if (dist.apply(cleanLine, inputNama) <= threshold) {
// //                 // namaMatch = true;
// //                 // break;
// //                 // }
// //                 // Cek contains juga (misal: "MUHAMMAD SATRIA ARROZAK" vs "SATRIA ARROZAK")
// //                 if (cleanLine.contains(inputNama) || inputNama.contains(cleanLine)) {
// //                     namaMatch = true;
// //                     break;
// //                 }

// //                 // Hitung Similarity (0.0 - 1.0)
// //                 double score = calculateSimilarity(inputNama, cleanLine);
// //                 if (score > maxScore)
// //                     maxScore = score;

// //                 // Threshold 0.80 (80% Mirip)
// //                 // if (score >= 0.80) {
// //                 // namaMatch = true;
// //                 // break;
// //                 // }
// //             }
// //             System.out.println("Input Nama: " + inputNama + " | Max OCR Score: " + maxScore);
// //             result.setNameValid(namaMatch);

// //             // 3. VALIDASI TANGGAL LAHIR
// //             // if(inputData.getBirthDate() != null) {
// //             // String dobInput =
// //             // inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
// //             // if(ocrText.contains(dobInput)) {
// //             // result.setDobValid(true);
// //             // }
// //             // }

// //             // 3. TEMPAT LAHIR & TGL LAHIR
// //             // Format KTP: "BEKASI, 20-01-2000"
// //             boolean tempatMatch = false;
// //             boolean tglMatch = false;

// //             String inputTempat = inputData.getBirthPlace().toUpperCase();
// //             String inputTgl = "";
// //             if (inputData.getBirthDate() != null) {
// //                 // Ubah YYYY-MM-DD (Input) jadi dd-MM-yyyy (KTP)
// //                 inputTgl = inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
// //             }

// //             // Cari baris yang mengandung tanggal lahir
// //             for (String line : ocrText.split("\\n")) {
// //                 if (line.contains(inputTgl)) {
// //                     tglMatch = true; // Tanggal ketemu di baris ini

// //                     // Cek tempat lahir di baris yang SAMA (sebelum tanda koma)
// //                     if (line.toUpperCase().contains(inputTempat)) {
// //                         tempatMatch = true;
// //                     }
// //                 }
// //             }
// //             // Fallback: cari tempat lahir di seluruh teks jika format baris hancur
// //             if (!tempatMatch && ocrText.contains(inputTempat))
// //                 tempatMatch = true;

// //             result.setBirthPlaceValid(tempatMatch);
// //             result.setBirthDateValid(tglMatch);

// //             // 4. VALIDASI JENIS KELAMIN
// //             // String genderInput = inputData.getGender().equals("L") ? "LAKI-LAKI" :
// //             // "PEREMPUAN";
// //             // if(ocrText.contains(genderInput)) {
// //             // result.setGenderValid(true);
// //             // }
// //             // } catch (Exception e) {
// //             // e.printStackTrace();
// //             // result.setError("Gagal memproses OCR: " + e.getMessage());
// //             // }
// //             // return result;

// //             // 4. JENIS KELAMIN
// //             // String genderKtp = inputData.getGender().equals("L") ? "LAKI-LAKI" :
// //             // "PEREMPUAN";
// //             // result.setGenderValid(ocrText.contains(genderKtp));
// //             boolean genderMatch = false;
// //             String targetGender = inputData.getGender().equals("L") ? "LAKI" : "PEREM";
// //             String[] lines = ocrText.split("\\n");

// //             for (String line : lines) {
// //                 if (line.contains("KELAMIN")) {
// //                     // Ambil teks setelah kata "KELAMIN"
// //                     String value = line.substring(line.indexOf("KELAMIN") + 7).trim().replace(":", "").trim();

// //                     // Cek Typo: LAKELAKI, LAKI-LAKI, PEREMPUAN, P3REMPUAN
// //                     if (value.startsWith("L") || value.contains("LAKI") || value.contains("LAK1")) {
// //                         if (inputData.getGender().equals("L"))
// //                             genderMatch = true;
// //                     } else if (value.startsWith("P") || value.contains("PEREM") || value.contains("WANITA")) {
// //                         if (inputData.getGender().equals("P"))
// //                             genderMatch = true;
// //                     }
// //                 }
// //             }
// //             // Fallback: cari kata kunci di seluruh teks jika baris tidak terdeteksi rapi
// //             if (!genderMatch) {
// //                 if (inputData.getGender().equals("L") && (ocrText.contains("LAKI") || ocrText.contains("LAK1"))) {
// //                     genderMatch = true;
// //                 } else if (inputData.getGender().equals("P")
// //                         && (ocrText.contains("PEREM") || ocrText.contains("WANITA"))) {
// //                     genderMatch = true;
// //                 }
// //             }
// //             result.setGenderValid(genderMatch);

// //         } catch (Exception e) {
// //             e.printStackTrace();
// //             result.setError("Gagal baca OCR: " + e.getMessage());
// //         }
// //         return result;
// //         // }
// //     }

// //     // public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto
// //     // inputData) {
// //     // OcrValidationResult result = new OcrValidationResult();
// //     // try {
// //     // // 1. Ekstrak Teks
// //     // String ocrText = extractText(ktpFile);
// //     // result.setRawText(ocrText);

// //     // System.out.println("--- HASIL OCR KTP ---");
// //     // System.out.println(ocrText);
// //     // System.out.println("---------------------");

// //     // // 2. Validasi NIK (Cari 16 digit angka)
// //     // // Menggunakan Regex yang mentolerir kesalahan baca (misal 'B' jadi '8' jika
// //     // // perlu, tapi kita strict dulu)
// //     // String nikRegex = "\\b\\d{16}\\b";
// //     // Pattern patternNik = Pattern.compile(nikRegex);
// //     // Matcher matcherNik = patternNik.matcher(ocrText);

// //     // boolean nikFound = false;
// //     // while (matcherNik.find()) {
// //     // String foundNik = matcherNik.group();
// //     // if (foundNik.equals(inputData.getNik())) {
// //     // nikFound = true;
// //     // break;
// //     // }
// //     // }
// //     // result.setNikValid(nikFound);

// //     // // 3. Validasi Nama (Fuzzy Logic)
// //     // // Cari baris yang mengandung "NAMA"
// //     // LevenshteinDistance distance = new LevenshteinDistance();
// //     // boolean nameMatch = false;

// //     // // Pecah per baris untuk cari Nama
// //     // String[] lines = ocrText.split("\\r?\\n");
// //     // for (String line : lines) {
// //     // if (line.contains("NAMA")) {
// //     // String cleanLine = line.replace("NAMA", "").replace(":", "").trim();
// //     // // Hitung kemiripan
// //     // int dist = distance.apply(cleanLine, inputData.getFullName().toUpperCase());
// //     // // Toleransi: Jika perbedaan karakter <= 3, anggap valid
// //     // if (dist <= 3) {
// //     // nameMatch = true;
// //     // }
// //     // }
// //     // }
// //     // // Fallback: Jika tidak ada label "NAMA", cari string yang mirip banget di
// //     // // seluruh teks
// //     // if (!nameMatch && ocrText.contains(inputData.getFullName().toUpperCase())) {
// //     // nameMatch = true;
// //     // }
// //     // result.setNameValid(nameMatch);

// //     // // 4. Validasi Tanggal Lahir
// //     // // Format Input: YYYY-MM-DD
// //     // // Format KTP: DD-MM-YYYY
// //     // if (inputData.getBirthDate() != null) {
// //     // String tglLahirInput =
// //     // inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
// //     // if (ocrText.contains(tglLahirInput)) {
// //     // result.setDobValid(true);
// //     // }
// //     // }

// //     // // 5. Validasi Jenis Kelamin
// //     // String genderInput = inputData.getGender().equals("L") ? "LAKI-LAKI" :
// //     // "PEREMPUAN";
// //     // if (ocrText.contains(genderInput)) {
// //     // result.setGenderValid(true);
// //     // }

// //     // } catch (Exception e) {
// //     // e.printStackTrace();
// //     // result.setError("Gagal memproses OCR: " + e.getMessage());
// //     // }
// //     // return result;
// //     // }

// //     // Helper: Image Pre-processing sederhana
// //     private BufferedImage preprocessImage(BufferedImage original) {
// //         // Ubah ke Grayscale
// //         BufferedImage grayscale = new BufferedImage(original.getWidth(), original.getHeight(),
// //                 BufferedImage.TYPE_BYTE_GRAY);
// //         Graphics g = grayscale.getGraphics();
// //         g.drawImage(original, 0, 0, null);
// //         g.dispose();
// //         return grayscale;
// //     }

// //     // --- HELPER FUNCTIONS ---

// //     // // 2. Hitung Kemiripan String (Jaro-Winkler / Levenshtein Normalized)
// //     private double calculateSimilarity(String s1, String s2) {
// //         if (s1 == null || s2 == null)
// //             return 0.0;
// //         LevenshteinDistance dist = new LevenshteinDistance();
// //         int maxLength = Math.max(s1.length(), s2.length());
// //         if (maxLength == 0)
// //             return 1.0;
// //         int distance = dist.apply(s1, s2);
// //         return 1.0 - ((double) distance / maxLength);
// //     }

// //     // 3. Scale Image (Perbesar)
// //     private BufferedImage scaleImage(BufferedImage original, double factor) {
// //         int w = (int) (original.getWidth() * factor);
// //         int h = (int) (original.getHeight() * factor);
// //         BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
// //         Graphics2D g = scaled.createGraphics();
// //         g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
// //         g.drawImage(original, 0, 0, w, h, null);
// //         g.dispose();
// //         return scaled;
// //     }

// //     @Data
// //     public static class OcrValidationResult {
// //         // private boolean nikValid;
// //         private boolean nameValid;
// //         private boolean birthPlaceValid; // Baru
// //         private boolean birthDateValid; // Baru
// //         private boolean genderValid;
// //         private String rawText;
// //         private String error;

// //         public boolean isValid() {
// //             // Semua harus valid
// //             return nameValid && birthPlaceValid && birthDateValid && genderValid;
// //         }
// //     }

// // }











// // =============================================
// //  BATCH 2
// // =============================================



















// package com.lsptddi.silsp.service;

// import com.lsptddi.silsp.dto.UserProfileDto;

// import lombok.Data;
// import net.sourceforge.tess4j.ITesseract;
// import net.sourceforge.tess4j.Tesseract;
// import net.sourceforge.tess4j.TesseractException;
// import org.apache.commons.text.similarity.LevenshteinDistance;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import javax.imageio.ImageIO;
// import java.awt.*;
// import java.awt.image.BufferedImage;
// import java.io.File;
// import java.io.IOException;
// import java.time.format.DateTimeFormatter;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
// import java.awt.image.RescaleOp;

// @Service
// public class OCRService {

//     // Lokasi folder tessdata (Sesuaikan dengan struktur projectmu)
//     private static final String TESSDATA_PATH = "tessdata";

//     public String extractText(MultipartFile file) throws IOException, TesseractException {
//         // 1. Konversi MultipartFile ke File temporary
//         File convFile = File.createTempFile("ocr-", ".jpg");
//         file.transferTo(convFile);

//         // 2. Pre-processing Gambar (Agar lebih mudah dibaca Tesseract)
//         // Kita ubah jadi Grayscale dan perbesar sedikit
//         BufferedImage image = ImageIO.read(convFile);
//         BufferedImage scaledImage = scaleImage(image, 3.5);
//         // BufferedImage processedImage = preprocessImage(scaledImage);

//         // 2. Convert ke Grayscale
//         BufferedImage grayImage = toGrayscale(scaledImage);

//         // 3. Contrast Boosting (Penting untuk Fotokopi Pudar)
//         // Menerangkan yang terang, menggelapkan yang gelap sebelum binarisasi
//         // BufferedImage highContrastImage = boostContrast(grayImage);

//         // 4. Binarization (Hitam Putih) dengan OTSU'S METHOD (Dynamic Threshold)
//         // Ini kunci agar support KTP Warna & Fotokopi sekaligus
//         // BufferedImage processedImage = applyOtsuBinarization(grayImage);
//         // 3. Setup Tesseract
//         ITesseract instance = new Tesseract();
//         instance.setDatapath(TESSDATA_PATH);
//         instance.setLanguage("ind"); // Pakai bahasa Indonesia

//         // Izinkan A-Z, 0-9, spasi, dan simbol umum KTP
//         instance.setTessVariable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/.-:, '");

//         // 4. Eksekusi OCR
//         String result = instance.doOCR(grayImage);

//         // Bersihkan file temp
//         convFile.delete();

//         return result.toUpperCase(); // Biar mudah dibandingin

//     }

//     // Algoritma Validasi
//     public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto inputData) {
//         OcrValidationResult result = new OcrValidationResult();
//         try {
//             String ocrText = extractText(ktpFile).toUpperCase();
//             // Bersihkan simbol aneh agar pencarian lebih akurat
//             // ocrText = ocrText.replaceAll("[^A-Z0-9\\s\\:\\/\\-\\.]", "");

//             result.setRawText(ocrText);

//             // 1. Ekstrak Teks
//             // String ocrText = extractText(ktpFile);
//             // result.setRawText(ocrText);

//             System.out.println("--- HASIL OCR KTP ---");
//             System.out.println(ocrText);
//             System.out.println("---------------------");

//             // LevenshteinDistance dist = new LevenshteinDistance();
//             String inputNama = inputData.getFullName().toUpperCase();
//             // String inputNama = inputData.getFullName();

//             boolean nameMatch = false;
//             double maxScore = 0.0;

//             // Coba cari per baris (lebih akurat daripada full text)
//             for (String line : ocrText.split("\\n")) {
//                 // Hapus label "NAMA" dan simbol
//                 String cleanLine = line.replace("NAMA", "").replace(":", "").trim();
//                 // if (cleanLine.isEmpty())
//                 // continue;

//                 if (cleanLine.length() < 3)
//                     continue;

//                 // Cek contains juga (misal: "MUHAMMAD SATRIA ARROZAK" vs "SATRIA ARROZAK")
//                 if (cleanLine.contains(inputNama) || inputNama.contains(cleanLine)) {
//                     nameMatch = true;
//                     break;
//                 }

//                 // Hitung Similarity (0.0 - 1.0)
//                 double score = calculateSimilarity(inputNama, cleanLine);
//                 if (score > maxScore)
//                     maxScore = score;

//             }

//             // String[] lines = ocrText.split("\\n");

//             // for (int i = 0; i < lines.length; i++) {
//             // String line = lines[i].trim();
//             // for (String line : ocrText.split("\\n")) {

//             // // Cek apakah baris ini adalah label "Nama"?
//             // if (isNameLabel(line)) {
//             // // KASUS 1: "Nama : Muhammad Satria" (Satu baris)
//             // String extractedName = line.replace(".*(NAMA|IVAMA|HAMA|VAMA)\\s*[:]?\\s*",
//             // "").trim();

//             // // KASUS 2: Label di baris ini, Nama di baris bawahnya (Multi-line OCR error)
//             // // Baris: "NAMA"
//             // // Baris+1: "MUHAMMAD SATRIA"
//             // // if (extractedName.length() < 3 && (i + 1) < lines.length) {
//             // // extractedName = lines[i + 1].trim();
//             // // }

//             // if (extractedName.length() < 3)
//             // continue;

//             // // BERSIHKAN HASIL EKSTRAKSI
//             // // Hapus angka dan simbol aneh, sisakan huruf dan spasi
//             // extractedName = cleanNameString(extractedName);

//             // System.out.println("Candidate Name from OCR: " + extractedName);
//             // System.out.println("Input Name: " + inputNama);

//             // // BANDINGKAN (Similarity > 80%)
//             // // if (calculateSimilarity(inputNama, extractedName) >= 0.80) {
//             // // nameMatch = true;
//             // // break;
//             // // }

//             // // Hitung Similarity (0.0 - 1.0)
//             // double score = calculateSimilarity(inputNama, extractedName);
//             // if (score > maxScore)
//             // maxScore = score;

//             // // Cek juga Contains (untuk nama panjang yang terpotong)
//             // if (extractedName.contains(inputNama) || inputNama.contains(extractedName)) {
//             // // Pastikan panjangnya masuk akal (minimal 50% panjang input)
//             // // if (extractedName.length() > (inputNama.length() * 0.5)) {
//             // nameMatch = true;
//             // break;
//             // // }

//             // }

//             // }
//             // }

//             // Fallback: Jika logic label gagal, cari brute force di seluruh teks
//             // if (!nameMatch) {
//             // // Cari exact match atau fuzzy match di setiap baris tanpa peduli label
//             // for (String line : lines) {
//             // String cleanLine = cleanNameString(line);
//             // if (calculateSimilarity(inputNama, cleanLine) >= 0.85) {
//             // nameMatch = true;
//             // break;
//             // }
//             // }
//             // }

//             System.out.println("Input Nama: " + inputNama + " | Max OCR Score: " +
//                     maxScore);
//             result.setNameValid(nameMatch);

//             // 3. TEMPAT LAHIR & TGL LAHIR
//             // Format KTP: "BEKASI, 20-01-2000"
//             boolean tempatMatch = false;
//             boolean tglMatch = false;

//             String inputTempat = inputData.getBirthPlace().toUpperCase();
//             String inputTgl = "";
//             if (inputData.getBirthDate() != null) {
//                 // Ubah YYYY-MM-DD (Input) jadi dd-MM-yyyy (KTP)
//                 inputTgl = inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//             }

//             // Cari baris yang mengandung tanggal lahir
//             for (String line : ocrText.split("\\n")) {
//                 if (line.contains(inputTgl)) {
//                     tglMatch = true; // Tanggal ketemu di baris ini

//                     // Cek tempat lahir di baris yang SAMA (sebelum tanda koma)
//                     if (line.toUpperCase().contains(inputTempat)) {
//                         tempatMatch = true;
//                     }
//                 }
//             }
//             // Fallback: cari tempat lahir di seluruh teks jika format baris hancur
//             if (!tempatMatch && ocrText.contains(inputTempat))
//                 tempatMatch = true;

//             result.setBirthPlaceValid(tempatMatch);
//             result.setBirthDateValid(tglMatch);

//             // --- 3. JENIS KELAMIN (FLEXIBLE) ---
//             boolean genderMatch = false;
//             String cleanText = cleanString(ocrText); // Hapus spasi/simbol untuk deteksi global

//             if (inputData.getGender().equals("L")) {
//                 // Laki-Laki sering terbaca: LAKI, LAK1, LAKL, LAK
//                 if (cleanText.contains("LAKI") || cleanText.contains("LAK1") || cleanText.contains("LAKL")) {
//                     genderMatch = true;
//                 }
//             } else if (inputData.getGender().equals("P")) {
//                 // Perempuan: PEREM, P3REM, WANITA
//                 if (cleanText.contains("PEREM") || cleanText.contains("P3REM") || cleanText.contains("WANITA")) {
//                     genderMatch = true;
//                 }
//             }
//             result.setGenderValid(genderMatch);

//         } catch (Exception e) {
//             e.printStackTrace();
//             result.setError("Gagal baca OCR: " + e.getMessage());
//         }
//         return result;
//         // }
//     }

//     // Deteksi Label "Nama" dengan toleransi typo
//     // private boolean isNameLabel(String line) {
//     // String norm = line.toUpperCase().replace(" ", "");
//     // return norm.contains("NAMA") || norm.contains("IVAMA") ||
//     // norm.contains("HAMA") || norm.contains("NAM:");
//     // }

//     // // Bersihkan String Nama (Hanya A-Z dan Spasi) -> Penting untuk akurasi Nama
//     // private String cleanNameString(String text) {
//     // if (text == null)
//     // return "";
//     // // Ganti angka yang mirip huruf (0->O, 1->I, 5->S)
//     // String fixed = text.toUpperCase()
//     // .replace("0", "O")
//     // .replace("1", "I")
//     // .replace("5", "S")
//     // .replace("8", "B");
//     // // Hapus semua kecuali huruf dan spasi
//     // return fixed.replaceAll("[^A-Z\\s]", " ").trim().replaceAll(" +", " ");
//     // }

//     private String cleanString(String text) {
//         if (text == null)
//             return "";
//         return text.toUpperCase().replaceAll("[^A-Z0-9]", "");
//     }

//     // Helper: Image Pre-processing sederhana
//     private BufferedImage toGrayscale(BufferedImage original) {
//         // Ubah ke Grayscale
//         BufferedImage gray = new BufferedImage(original.getWidth(), original.getHeight(),
//                 BufferedImage.TYPE_BYTE_GRAY);
//         Graphics g = gray.getGraphics();
//         g.drawImage(original, 0, 0, null);
//         g.dispose();

//         // Thresholding Agresif untuk KTP Teks hitam di KTP biasanya sangat kontras jika
//         // background dihilangkan
//         // int width = gray.getWidth();
//         // int height = gray.getHeight();

//         // for (int y = 0; y < height; y++) {
//         //     for (int x = 0; x < width; x++) {
//         //         int rgba = gray.getRGB(x, y);
//         //         Color col = new Color(rgba, true);
//         //         // Threshold 150-170 efektif untuk memisahkan teks hitam dari background
//         //         // biru/biru muda
//         //         if ((col.getRed() + col.getGreen() + col.getBlue()) / 3 > 150) {
//         //             gray.setRGB(x, y, Color.WHITE.getRGB()); // Background jadi putih
//         //         } else {
//         //             gray.setRGB(x, y, Color.BLACK.getRGB()); // Teks jadi hitam
//         //         }
//         //     }
//         // }
//         return gray;
//     }

//     // private BufferedImage applyOtsuBinarization(BufferedImage gray) {
//     //     int threshold = getOtsuThreshold(gray);

//     //     // Failsafe: Jika Otsu gagal (0) atau terlalu ekstrem, gunakan default
//     //     if (threshold <= 0 || threshold >= 255) {
//     //         System.out.println("Otsu gagal (Threshold: " + threshold + "), menggunakan fallback 128.");
//     //         threshold = 128;
//     //     } else {
//     //         System.out.println("Dynamic Otsu Threshold: " + threshold);
//     //     }

//     //     BufferedImage binarized = new BufferedImage(gray.getWidth(),
//     //             gray.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//     //     int w = gray.getWidth();
//     //     int h = gray.getHeight();

//     //     for (int y = 0; y < h; y++) {
//     //         for (int x = 0; x < w; x++) {
//     //             // Ambil nilai RGB
//     //             int rgb = gray.getRGB(x, y);
//     //             int r = (rgb >> 16) & 0xFF;
//     //             // Ambil komponen merah (karena grayscale, R=G=B)
//     //             // int rgba = gray.getRGB(x, y);

//     //             // Terapkan Threshold
//     //             if (r >= threshold) {
//     //                 binarized.setRGB(x, y, Color.WHITE.getRGB()); // Background
//     //             } else {
//     //                 binarized.setRGB(x, y, Color.BLACK.getRGB()); // Teks
//     //             }
//     //         }

//     //     }
//     //     return binarized;
//     // // }

//     // 4. Calculate Otsu Threshold (FIXED CALCULATION)
//     // private int getOtsuThreshold(BufferedImage original) {
//     //     int[] histogram = new int[256];
//     //     int w = original.getWidth();
//     //     int h = original.getHeight();

//     //     // Build Histogram
//     //     for (int y = 0; y < h; y++) {
//     //         for (int x = 0; x < w; x++) {
//     //             int rgb = original.getRGB(x, y);
//     //             int r = (rgb >> 16) & 0xFF; // Ambil nilai 0-255 dengan aman
//     //             histogram[r]++;
//     //         }
//     //     }

//     //     int total = w * h;
//     //     float sum = 0;
//     //     for (int i = 0; i < 256; i++)
//     //         sum += i * histogram[i];

//     //     float sumB = 0;
//     //     int wB = 0;
//     //     int wF = 0;

//     //     float varMax = 0;
//     //     int threshold = 0;

//     //     for (int t = 0; t < 256; t++) {
//     //         wB += histogram[t];
//     //         if (wB == 0)
//     //             continue;
//     //         wF = total - wB;
//     //         if (wF == 0)
//     //             break;

//     //         sumB += (float) (t * histogram[t]);
//     //         float mB = sumB / wB;
//     //         float mF = (sum - sumB) / wF;

//     //         float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

//     //         if (varBetween > varMax) {
//     //             varMax = varBetween;
//     //             threshold = t;
//     //         }
//     //     }
//     //     return threshold;
//     // }

//     // --- HELPER FUNCTIONS ---

//     // // 2. Hitung Kemiripan String (Jaro-Winkler / Levenshtein Normalized)
//     private double calculateSimilarity(String s1, String s2) {
//         if (s1 == null || s2 == null)
//             return 0.0;
//         LevenshteinDistance dist = new LevenshteinDistance();
//         int maxLength = Math.max(s1.length(), s2.length());
//         if (maxLength == 0)
//             return 1.0;
//         int distance = dist.apply(s1, s2);
//         return 1.0 - ((double) distance / maxLength);
//     }

//     // 3. Scale Image (Perbesar)
//     private BufferedImage scaleImage(BufferedImage original, double factor) {
//         int w = (int) (original.getWidth() * factor);
//         int h = (int) (original.getHeight() * factor);
//         BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//         Graphics2D g = scaled.createGraphics();
//         g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//         g.drawImage(original, 0, 0, w, h, null);
//         g.dispose();
//         return scaled;
//     }

//     @Data
//     public static class OcrValidationResult {
//         // private boolean nikValid;
//         private boolean nameValid;
//         private boolean birthPlaceValid; // Baru
//         private boolean birthDateValid; // Baru
//         private boolean genderValid;
//         private String rawText;
//         private String error;

//         public boolean isValid() {
//             // Semua harus valid
//             return nameValid && birthPlaceValid && birthDateValid && genderValid;
//         }
//     }

// }

// // package com.lsptddi.silsp.service;

// // import com.lsptddi.silsp.dto.UserProfileDto;
// // import lombok.Data;
// // import net.sourceforge.tess4j.ITesseract;
// // import net.sourceforge.tess4j.Tesseract;
// // import net.sourceforge.tess4j.TesseractException;
// // import org.apache.commons.text.similarity.LevenshteinDistance;
// // import org.springframework.stereotype.Service;
// // import org.springframework.web.multipart.MultipartFile;

// // import javax.imageio.ImageIO;
// // import java.awt.*;
// // import java.awt.image.BufferedImage;
// // import java.io.File;
// // import java.io.IOException;
// // import java.time.format.DateTimeFormatter;

// // @Service
// // public class OCRService {

// // private static final String TESSDATA_PATH = "tessdata";

// // public String extractText(MultipartFile file) throws IOException,
// // TesseractException {
// // File convFile = File.createTempFile("ocr-", ".jpg");
// // file.transferTo(convFile);

// // // 1. Baca Gambar Asli
// // BufferedImage originalImage = ImageIO.read(convFile);

// // // 2. PRE-PROCESSING (KUNCI AKURASI)
// // // Perbesar gambar 2.5x agar font kecil terbaca
// // BufferedImage scaledImage = scaleImage(originalImage, 2.5);
// // // Ubah jadi Hitam-Putih kontras tinggi (Binarization)
// // BufferedImage processedImage = processImageForOCR(scaledImage);

// // // 3. Setup Tesseract
// // ITesseract instance = new Tesseract();
// // instance.setDatapath(TESSDATA_PATH);
// // instance.setLanguage("ind");
// // // Whitelist agar hanya membaca huruf angka dan simbol dasar (mengurangi
// // noise)
// // instance.setTessVariable("tessedit_char_whitelist",
// // "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/.-: ");

// // // 4. Eksekusi
// // String result = instance.doOCR(processedImage);

// // convFile.delete();
// // return result.toUpperCase();
// // }

// // public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto
// // inputData) {
// // OcrValidationResult result = new OcrValidationResult();
// // try {
// // String ocrText = extractText(ktpFile).toUpperCase();
// // result.setRawText(ocrText);

// // System.out.println("--- HASIL OCR KTP (PROCESSED) ---");
// // System.out.println(ocrText);
// // System.out.println("---------------------------------");

// // // --- 1. VALIDASI NAMA (STRICT SIMILARITY) ---
// // // Menggunakan skor kemiripan (0.0 - 1.0). Minimal 0.85 (85% mirip).
// // String inputNama = cleanString(inputData.getFullName());
// // boolean nameMatch = false;

// // String[] lines = ocrText.split("\\n");
// // for (String line : lines) {
// // // Bersihkan baris dari kata "NAMA" dan simbol
// // String lineClean = line.replaceAll("NAMA", "").replaceAll("[:]", "").trim();
// // lineClean = cleanString(lineClean);

// // if (lineClean.length() < 3) continue;

// // // Hitung Score
// // double score = calculateSimilarity(inputNama, lineClean);

// // // Jika input nama pendek (misal "Ali"), harus exact match.
// // // Jika panjang, boleh ada typo sedikit.
// // if (score >= 0.85) {
// // nameMatch = true;
// // break;
// // }
// // }
// // result.setNameValid(nameMatch);

// // // --- 2. TEMPAT & TGL LAHIR ---
// // boolean tempatMatch = false;
// // boolean tglMatch = false;

// // String inputTempat = cleanString(inputData.getBirthPlace());
// // String inputTgl = "";
// // if (inputData.getBirthDate() != null) {
// // inputTgl =
// // inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
// // }

// // for (String line : lines) {
// // // Cari Tanggal (dd-MM-yyyy)
// // if (line.contains(inputTgl) || line.replace(" ", "").contains(inputTgl)) {
// // tglMatch = true;
// // // Cek Tempat Lahir di baris yang sama atau mirip
// // String lineClean = cleanString(line);
// // if (calculateSimilarity(inputTempat, lineClean) > 0.80 ||
// // lineClean.contains(inputTempat)) {
// // tempatMatch = true;
// // }
// // }
// // }
// // result.setBirthPlaceValid(tempatMatch);
// // result.setBirthDateValid(tglMatch);

// // // --- 3. JENIS KELAMIN (FLEXIBLE) ---
// // // Deteksi "LAKI" atau "PEREM"
// // boolean genderMatch = false;
// // if (inputData.getGender().equals("L")) {
// // if (ocrText.contains("LAKI") || ocrText.contains("LAK1") ||
// // ocrText.contains("LAKl")) {
// // genderMatch = true;
// // }
// // } else if (inputData.getGender().equals("P")) {
// // if (ocrText.contains("PEREM") || ocrText.contains("WANITA") ||
// // ocrText.contains("P3REM")) {
// // genderMatch = true;
// // }
// // }
// // result.setGenderValid(genderMatch);

// // } catch (Exception e) {
// // e.printStackTrace();
// // result.setError("Gagal baca OCR: " + e.getMessage());
// // }
// // return result;
// // }

// // // --- HELPER IMAGE PROCESSING ---

// // // 1. Scale Image (Memperbesar gambar agar font jelas)
// // private BufferedImage scaleImage(BufferedImage original, double factor) {
// // int w = (int) (original.getWidth() * factor);
// // int h = (int) (original.getHeight() * factor);
// // BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
// // Graphics2D g = scaled.createGraphics();
// // g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
// // RenderingHints.VALUE_INTERPOLATION_BICUBIC);
// // g.drawImage(original, 0, 0, w, h, null);
// // g.dispose();
// // return scaled;
// // }

// // // 2. Binarization (Ubah ke Hitam Putih murni untuk kontras)
// // private BufferedImage processImageForOCR(BufferedImage original) {
// // BufferedImage processed = new BufferedImage(original.getWidth(),
// // original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
// // Graphics g = processed.createGraphics();
// // g.drawImage(original, 0, 0, null);
// // g.dispose();

// // int width = processed.getWidth();
// // int height = processed.getHeight();

// // // Thresholding manual (Nilai 160 biasanya bagus untuk dokumen scan)
// // for (int y = 0; y < height; y++) {
// // for (int x = 0; x < width; x++) {
// // int rgba = processed.getRGB(x, y);
// // Color col = new Color(rgba, true);
// // // Jika kecerahan rata-rata > 130, jadikan Putih, selain itu Hitam
// // if ((col.getRed() + col.getGreen() + col.getBlue()) / 3 > 130) {
// // processed.setRGB(x, y, Color.WHITE.getRGB());
// // } else {
// // processed.setRGB(x, y, Color.BLACK.getRGB());
// // }
// // }
// // }
// // return processed;
// // }

// // // 3. Helper String & Similarity
// // private String cleanString(String text) {
// // if (text == null) return "";
// // return text.toUpperCase().replaceAll("[^A-Z]", "");
// // }

// // private double calculateSimilarity(String s1, String s2) {
// // if (s1 == null || s2 == null) return 0.0;
// // LevenshteinDistance dist = new LevenshteinDistance();
// // int maxLength = Math.max(s1.length(), s2.length());
// // if (maxLength == 0) return 1.0;
// // int distance = dist.apply(s1, s2);
// // return 1.0 - ((double) distance / maxLength);
// // }

// // @Data
// // public static class OcrValidationResult {
// // private boolean nameValid;
// // private boolean birthPlaceValid;
// // private boolean birthDateValid;
// // private boolean genderValid;
// // private String rawText;
// // private String error;

// // public boolean isValid() {
// // return nameValid && birthPlaceValid && birthDateValid && genderValid;
// // }
// // }
// // }

// // package com.lsptddi.silsp.service;

// // import com.lsptddi.silsp.dto.UserProfileDto;
// // import lombok.Data;
// // import net.sourceforge.tess4j.ITesseract;
// // import net.sourceforge.tess4j.Tesseract;
// // import net.sourceforge.tess4j.TesseractException;
// // import org.apache.commons.text.similarity.LevenshteinDistance;
// // import org.springframework.stereotype.Service;
// // import org.springframework.web.multipart.MultipartFile;

// // import javax.imageio.ImageIO;
// // import java.awt.*;
// // import java.awt.image.BufferedImage;
// // import java.io.File;
// // import java.io.IOException;
// // import java.time.format.DateTimeFormatter;
// // import java.util.Arrays;

// // @Service
// // public class OCRService {

// // private static final String TESSDATA_PATH = "tessdata";

// // public String extractText(MultipartFile file) throws IOException,
// // TesseractException {
// // File convFile = File.createTempFile("ocr-", ".jpg");
// // file.transferTo(convFile);

// // BufferedImage originalImage = ImageIO.read(convFile);

// // // 1. Scale Up (Perbesar 3x agar teks nama yang kecil terbaca detail)
// // BufferedImage scaledImage = scaleImage(originalImage, 3.5);

// // // 2. Binarization (Hitam Putih dengan Threshold khusus KTP)
// // BufferedImage processedImage = processImageForOCR(scaledImage);

// // ITesseract instance = new Tesseract();
// // instance.setDatapath(TESSDATA_PATH);
// // instance.setLanguage("ind");
// // // Izinkan A-Z, 0-9, spasi, dan simbol umum KTP
// // instance.setTessVariable("tessedit_char_whitelist",
// // "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/.-:, '");

// // String result = instance.doOCR(processedImage);

// // convFile.delete();
// // return result.toUpperCase();
// // }

// // public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto
// // inputData) {
// // OcrValidationResult result = new OcrValidationResult();
// // try {
// // String ocrText = extractText(ktpFile).toUpperCase();
// // result.setRawText(ocrText);

// // System.out.println("--- HASIL OCR KTP ---");
// // System.out.println(ocrText);
// // System.out.println("---------------------");

// // // --- 1. VALIDASI NAMA (ENHANCED FOCUS) ---
// // // Strategi: Cari baris yang mengandung kata kunci label "Nama"
// // // Typo umum label nama: "NAMA", "IVAMA", "VAMA", "HAMA", "NAM A"

// // String inputNama = cleanNameString(inputData.getFullName());
// // boolean nameMatch = false;

// // String[] lines = ocrText.split("\\n");

// // for (int i = 0; i < lines.length; i++) {
// // String line = lines[i].trim();

// // // Cek apakah baris ini adalah label "Nama"?
// // if (isNameLabel(line)) {
// // // KASUS 1: "Nama : Muhammad Satria" (Satu baris)
// // String extractedName =
// // line.replaceAll(".*(NAMA|IVAMA|HAMA|VAMA)\\s*[:]?\\s*", "").trim();

// // // KASUS 2: Label di baris ini, Nama di baris bawahnya (Multi-line OCR error)
// // // Baris: "NAMA"
// // // Baris+1: "MUHAMMAD SATRIA"
// // if (extractedName.length() < 3 && (i + 1) < lines.length) {
// // extractedName = lines[i+1].trim();
// // }

// // // BERSIHKAN HASIL EKSTRAKSI
// // // Hapus angka dan simbol aneh, sisakan huruf dan spasi
// // extractedName = cleanNameString(extractedName);

// // System.out.println("Candidate Name from OCR: " + extractedName);
// // System.out.println("Input Name: " + inputNama);

// // // BANDINGKAN (Similarity > 80%)
// // if (calculateSimilarity(inputNama, extractedName) >= 0.80) {
// // nameMatch = true;
// // break;
// // }

// // // Cek juga Contains (untuk nama panjang yang terpotong)
// // if (extractedName.contains(inputNama) || inputNama.contains(extractedName)) {
// // // Pastikan panjangnya masuk akal (minimal 50% panjang input)
// // if(extractedName.length() > (inputNama.length() * 0.5)) {
// // nameMatch = true;
// // break;
// // }
// // }
// // }
// // }

// // // Fallback: Jika logic label gagal, cari brute force di seluruh teks
// // if (!nameMatch) {
// // // Cari exact match atau fuzzy match di setiap baris tanpa peduli label
// // for (String line : lines) {
// // String cleanLine = cleanNameString(line);
// // if (calculateSimilarity(inputNama, cleanLine) >= 0.85) {
// // nameMatch = true; break;
// // }
// // }
// // }

// // result.setNameValid(nameMatch);

// // // --- 2. TEMPAT & TGL LAHIR ---
// // boolean tempatMatch = false;
// // boolean tglMatch = false;

// // String inputTempat = cleanString(inputData.getBirthPlace());
// // String inputTgl = "";
// // if (inputData.getBirthDate() != null) {
// // inputTgl =
// // inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
// // }

// // for (String line : lines) {
// // // Tgl lahir (dd-MM-yyyy) biasanya angka, jadi kita cari angka
// // String lineNoSpace = line.replace(" ", "");
// // if (lineNoSpace.contains(inputTgl)) {
// // tglMatch = true;
// // // Tempat lahir biasanya satu baris dengan tanggal
// // String lineClean = cleanString(line);
// // if (calculateSimilarity(inputTempat, lineClean) > 0.75 ||
// // lineClean.contains(inputTempat)) {
// // tempatMatch = true;
// // }
// // }
// // }
// // result.setBirthDateValid(tglMatch);
// // result.setBirthPlaceValid(tempatMatch);

// // // --- 3. JENIS KELAMIN (FLEXIBLE) ---
// // boolean genderMatch = false;
// // String cleanText = cleanString(ocrText); // Hapus spasi/simbol untuk deteksi
// // global

// // if (inputData.getGender().equals("L")) {
// // // Laki-Laki sering terbaca: LAKI, LAK1, LAKL, LAK
// // if (cleanText.contains("LAKI") || cleanText.contains("LAK1") ||
// // cleanText.contains("LAKL")) {
// // genderMatch = true;
// // }
// // } else if (inputData.getGender().equals("P")) {
// // // Perempuan: PEREM, P3REM, WANITA
// // if (cleanText.contains("PEREM") || cleanText.contains("P3REM") ||
// // cleanText.contains("WANITA")) {
// // genderMatch = true;
// // }
// // }
// // result.setGenderValid(genderMatch);

// // } catch (Exception e) {
// // e.printStackTrace();
// // result.setError("Gagal baca OCR: " + e.getMessage());
// // }
// // return result;
// // }

// // // --- HELPER FUNCTIONS ---

// // // Deteksi Label "Nama" dengan toleransi typo
// // private boolean isNameLabel(String line) {
// // String norm = line.toUpperCase().replace(" ", "");
// // return norm.contains("NAMA") || norm.contains("IVAMA") ||
// // norm.contains("HAMA") || norm.contains("NAM:");
// // }

// // // Bersihkan String Nama (Hanya A-Z dan Spasi) -> Penting untuk akurasi Nama
// // private String cleanNameString(String text) {
// // if (text == null) return "";
// // // Ganti angka yang mirip huruf (0->O, 1->I, 5->S)
// // String fixed = text.toUpperCase()
// // .replace("0", "O")
// // .replace("1", "I")
// // .replace("5", "S")
// // .replace("8", "B");
// // // Hapus semua kecuali huruf dan spasi
// // return fixed.replaceAll("[^A-Z\\s]", " ").trim().replaceAll(" +", " ");
// // }

// // private String cleanString(String text) {
// // if (text == null) return "";
// // return text.toUpperCase().replaceAll("[^A-Z0-9]", "");
// // }

// // private double calculateSimilarity(String s1, String s2) {
// // if (s1 == null || s2 == null) return 0.0;
// // LevenshteinDistance dist = new LevenshteinDistance();
// // int maxLength = Math.max(s1.length(), s2.length());
// // if (maxLength == 0) return 1.0;
// // int distance = dist.apply(s1, s2);
// // return 1.0 - ((double) distance / maxLength);
// // }

// // private BufferedImage scaleImage(BufferedImage original, double factor) {
// // int w = (int) (original.getWidth() * factor);
// // int h = (int) (original.getHeight() * factor);
// // BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
// // Graphics2D g = scaled.createGraphics();
// // g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
// // RenderingHints.VALUE_INTERPOLATION_BICUBIC);
// // g.drawImage(original, 0, 0, w, h, null);
// // g.dispose();
// // return scaled;
// // }

// // private BufferedImage processImageForOCR(BufferedImage original) {
// // BufferedImage processed = new BufferedImage(original.getWidth(),
// // original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
// // Graphics g = processed.createGraphics();
// // g.drawImage(original, 0, 0, null);
// // g.dispose();

// // // Thresholding Agresif untuk KTP
// // // Teks hitam di KTP biasanya sangat kontras jika background dihilangkan
// // int width = processed.getWidth();
// // int height = processed.getHeight();

// // for (int y = 0; y < height; y++) {
// // for (int x = 0; x < width; x++) {
// // int rgba = processed.getRGB(x, y);
// // Color col = new Color(rgba, true);
// // // Threshold 150-170 efektif untuk memisahkan teks hitam dari background
// // biru/biru muda
// // if ((col.getRed() + col.getGreen() + col.getBlue()) / 3 > 155) {
// // processed.setRGB(x, y, Color.WHITE.getRGB()); // Background jadi putih
// // } else {
// // processed.setRGB(x, y, Color.BLACK.getRGB()); // Teks jadi hitam
// // }
// // }
// // }
// // return processed;
// // }

// // @Data
// // public static class OcrValidationResult {
// // private boolean nameValid;
// // private boolean birthPlaceValid;
// // private boolean birthDateValid;
// // private boolean genderValid;
// // private String rawText;
// // private String error;

// // public boolean isValid() {
// // return nameValid && birthPlaceValid && birthDateValid && genderValid;
// // }
// // }
// // }

// // package com.lsptddi.silsp.service;

// // import com.lsptddi.silsp.dto.UserProfileDto;
// // import lombok.Data;
// // import net.sourceforge.tess4j.ITesseract;
// // import net.sourceforge.tess4j.Tesseract;
// // import net.sourceforge.tess4j.TesseractException;
// // import org.apache.commons.text.similarity.LevenshteinDistance;
// // import org.springframework.stereotype.Service;
// // import org.springframework.web.multipart.MultipartFile;

// // import javax.imageio.ImageIO;
// // import java.awt.*;
// // import java.awt.image.BufferedImage;
// // import java.awt.image.RescaleOp;
// // import java.io.File;
// // import java.io.IOException;
// // import java.time.format.DateTimeFormatter;

// // @Service
// // public class OCRService {

// // private static final String TESSDATA_PATH = "tessdata";

// // public String extractText(MultipartFile file) throws IOException,
// // TesseractException {
// // File convFile = File.createTempFile("ocr-", ".jpg");
// // file.transferTo(convFile);

// // BufferedImage originalImage = ImageIO.read(convFile);

// // // 1. Scale Up (3.0x) - Agar font kecil di fotokopi terlihat jelas
// // BufferedImage scaledImage = scaleImage(originalImage, 3.0);

// // // 2. Convert ke Grayscale
// // BufferedImage grayImage = toGrayscale(scaledImage);

// // // 3. Contrast Boosting (Penting untuk Fotokopi Pudar)
// // // Menerangkan yang terang, menggelapkan yang gelap sebelum binarisasi
// // BufferedImage highContrastImage = boostContrast(grayImage);

// // // 4. Binarization (Hitam Putih) dengan OTSU'S METHOD (Dynamic Threshold)
// // // Ini kunci agar support KTP Warna & Fotokopi sekaligus
// // BufferedImage processedImage = applyOtsuBinarization(highContrastImage);

// // ITesseract instance = new Tesseract();
// // instance.setDatapath(TESSDATA_PATH);
// // instance.setLanguage("ind");
// // // Izinkan karakter standar
// // instance.setTessVariable("tessedit_char_whitelist",
// // "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/.-:, '");

// // String result = instance.doOCR(processedImage);

// // convFile.delete();
// // return result.toUpperCase();
// // }

// // public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto
// // inputData) {
// // OcrValidationResult result = new OcrValidationResult();
// // try {
// // String ocrText = extractText(ktpFile).toUpperCase();
// // result.setRawText(ocrText);

// // System.out.println("--- HASIL OCR KTP ---");
// // System.out.println(ocrText);
// // System.out.println("---------------------");

// // // --- 1. VALIDASI NAMA (ENHANCED) ---
// // String inputNama = cleanNameString(inputData.getFullName());
// // boolean nameMatch = false;

// // String[] lines = ocrText.split("\\n");

// // // Step A: Cari via Label "Nama"
// // for (int i = 0; i < lines.length; i++) {
// // String line = lines[i].trim();

// // if (isNameLabel(line)) {
// // // Coba ambil di baris yang sama
// // String extracted =
// // line.replaceAll(".*(NAMA|IVAMA|HAMA|VAMA|NAM)\\s*[:]?\\s*", "").trim();

// // // Jika kosong/pendek, ambil baris bawahnya (Multi-line error)
// // if (extracted.length() < 3 && (i + 1) < lines.length) {
// // extracted = lines[i+1].trim();
// // }

// // String cleanExtracted = cleanNameString(extracted);

// // // Logika Similarity
// // if (isSimilar(inputNama, cleanExtracted)) {
// // nameMatch = true;
// // break;
// // }
// // }
// // }

// // // Step B: Fallback (Brute Force per baris) jika label tidak terbaca
// // if (!nameMatch) {
// // for (String line : lines) {
// // if (isSimilar(inputNama, cleanNameString(line))) {
// // nameMatch = true; break;
// // }
// // }
// // }
// // result.setNameValid(nameMatch);

// // // --- 2. TEMPAT & TGL LAHIR ---
// // boolean tempatMatch = false;
// // boolean tglMatch = false;

// // String inputTempat = cleanString(inputData.getBirthPlace());
// // String inputTgl = "";
// // if (inputData.getBirthDate() != null) {
// // inputTgl =
// // inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
// // }

// // for (String line : lines) {
// // // Bersihkan spasi di tanggal (kadang fotokopi ada noise spasi di angka)
// // String lineNoSpace = line.replace(" ", "");

// // if (lineNoSpace.contains(inputTgl)) {
// // tglMatch = true;
// // // Cek tempat lahir di baris yang sama (Fuzzy 75%)
// // String lineClean = cleanString(line);
// // if (calculateSimilarity(inputTempat, lineClean) > 0.75 ||
// // lineClean.contains(inputTempat)) {
// // tempatMatch = true;
// // }
// // }
// // }
// // result.setBirthDateValid(tglMatch);
// // result.setBirthPlaceValid(tempatMatch);

// // // --- 3. JENIS KELAMIN ---
// // boolean genderMatch = false;
// // String cleanText = cleanString(ocrText);

// // if (inputData.getGender().equals("L")) {
// // if (cleanText.contains("LAKI") || cleanText.contains("LAK1") ||
// // cleanText.contains("LAKL")) {
// // genderMatch = true;
// // }
// // } else if (inputData.getGender().equals("P")) {
// // if (cleanText.contains("PEREM") || cleanText.contains("P3REM") ||
// // cleanText.contains("WANITA")) {
// // genderMatch = true;
// // }
// // }
// // result.setGenderValid(genderMatch);

// // } catch (Exception e) {
// // e.printStackTrace();
// // result.setError("Gagal baca OCR: " + e.getMessage());
// // }
// // return result;
// // }

// // // --- IMAGE PROCESSING HELPERS ---

// // private BufferedImage scaleImage(BufferedImage original, double factor) {
// // int w = (int) (original.getWidth() * factor);
// // int h = (int) (original.getHeight() * factor);
// // BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
// // Graphics2D g = scaled.createGraphics();
// // g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
// // RenderingHints.VALUE_INTERPOLATION_BICUBIC);
// // g.drawImage(original, 0, 0, w, h, null);
// // g.dispose();
// // return scaled;
// // }

// // private BufferedImage toGrayscale(BufferedImage original) {
// // BufferedImage gray = new BufferedImage(original.getWidth(),
// // original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
// // Graphics g = gray.getGraphics();
// // g.drawImage(original, 0, 0, null);
// // g.dispose();
// // return gray;
// // }

// // // Meningkatkan kontras agar fotokopi pudar jadi lebih jelas
// // private BufferedImage boostContrast(BufferedImage gray) {
// // // Scale factor 1.3f (Naikkan kontras), Offset -20 (Gelapkan sedikit agar
// // text hitam pekat)
// // RescaleOp op = new RescaleOp(1.3f, -20.0f, null);
// // return op.filter(gray, null);
// // }

// // // Algoritma Otsu untuk mencari threshold otomatis (Color vs B&W Compatible)
// // private BufferedImage applyOtsuBinarization(BufferedImage gray) {
// // int threshold = getOtsuThreshold(gray);
// // System.out.println("Dynamic Otsu Threshold: " + threshold); // Debugging

// // BufferedImage binarized = new BufferedImage(gray.getWidth(),
// // gray.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
// // int w = gray.getWidth();
// // int h = gray.getHeight();

// // for (int y = 0; y < h; y++) {
// // for (int x = 0; x < w; x++) {
// // int rgb = gray.getRGB(x, y) & 0xFF; // Ambil nilai gray (0-255)
// // if (rgb >= threshold) {
// // binarized.setRGB(x, y, Color.WHITE.getRGB());
// // } else {
// // binarized.setRGB(x, y, Color.BLACK.getRGB());
// // }
// // }
// // }
// // return binarized;
// // }

// // // Kalkulator Otsu Threshold
// // private int getOtsuThreshold(BufferedImage original) {
// // int[] histogram = new int[256];
// // int totalPixels = original.getWidth() * original.getHeight();

// // // 1. Build Histogram
// // for (int y = 0; y < original.getHeight(); y++) {
// // for (int x = 0; x < original.getWidth(); x++) {
// // int gray = original.getRGB(x, y) & 0xFF;
// // histogram[gray]++;
// // }
// // }

// // // 2. Calculate Otsu
// // float sum = 0;
// // for (int i = 0; i < 256; i++) sum += i * histogram[i];

// // float sumB = 0;
// // int wB = 0;
// // int wF = 0;

// // float varMax = 0;
// // int threshold = 0;

// // for (int t = 0; t < 256; t++) {
// // wB += histogram[t];
// // if (wB == 0) continue;
// // wF = totalPixels - wB;
// // if (wF == 0) break;

// // sumB += (float) (t * histogram[t]);
// // float mB = sumB / wB;
// // float mF = (sum - sumB) / wF;

// // float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

// // if (varBetween > varMax) {
// // varMax = varBetween;
// // threshold = t;
// // }
// // }
// // return threshold;
// // }

// // // --- STRING HELPERS ---

// // private boolean isSimilar(String input, String extracted) {
// // // Nama Input vs Extracted
// // // Jika nama panjang, similarity 80%. Jika pendek, 90%.
// // // Tambahan: Jika extracted mengandung input (Partial Match), anggap benar.

// // if (extracted.contains(input) || input.contains(extracted)) {
// // // Pastikan tidak terlalu pendek match-nya (min 50% panjang input)
// // if (extracted.length() > (input.length() * 0.5)) return true;
// // }
// // return calculateSimilarity(input, extracted) >= 0.80;
// // }

// // private String cleanNameString(String text) {
// // if (text == null) return "";
// // String fixed = text.toUpperCase()
// // .replace("0", "O")
// // .replace("1", "I")
// // .replace("5", "S")
// // .replace("8", "B");
// // return fixed.replaceAll("[^A-Z\\s]", " ").trim().replaceAll(" +", " ");
// // }

// // private boolean isNameLabel(String line) {
// // String norm = line.toUpperCase().replace(" ", "").replace(":", "");
// // return norm.contains("NAMA") || norm.contains("NAM") ||
// // norm.contains("VAMA");
// // }

// // private String cleanString(String text) {
// // if (text == null) return "";
// // return text.toUpperCase().replaceAll("[^A-Z0-9]", "");
// // }

// // private double calculateSimilarity(String s1, String s2) {
// // if (s1 == null || s2 == null) return 0.0;
// // LevenshteinDistance dist = new LevenshteinDistance();
// // int maxLength = Math.max(s1.length(), s2.length());
// // if (maxLength == 0) return 1.0;
// // int distance = dist.apply(s1, s2);
// // return 1.0 - ((double) distance / maxLength);
// // }

// // @Data
// // public static class OcrValidationResult {
// // private boolean nameValid;
// // private boolean birthPlaceValid;
// // private boolean birthDateValid;
// // private boolean genderValid;
// // private String rawText;
// // private String error;

// // public boolean isValid() {
// // return nameValid && birthPlaceValid && birthDateValid && genderValid;
// // }
// // }
// // }


// =======================
// KODE TERBARU
// =======================

// package com.lsptddi.silsp.service;

// import com.lsptddi.silsp.dto.UserProfileDto;

// import lombok.Data;
// import net.sourceforge.tess4j.ITesseract;
// import net.sourceforge.tess4j.Tesseract;
// import net.sourceforge.tess4j.TesseractException;
// import org.apache.commons.text.similarity.LevenshteinDistance;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import javax.imageio.ImageIO;
// import java.awt.*;
// import java.awt.image.BufferedImage;
// import java.io.File;
// import java.io.IOException;
// import java.time.format.DateTimeFormatter;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
// import java.awt.image.RescaleOp;

// @Service
// public class OCRService {

//     // Lokasi folder tessdata (Sesuaikan dengan struktur projectmu)
//     private static final String TESSDATA_PATH = "tessdata";

//     public String extractText(MultipartFile file) throws IOException, TesseractException {
//         // 1. Konversi MultipartFile ke File temporary
//         File convFile = File.createTempFile("ocr-", ".jpg");
//         file.transferTo(convFile);

//         // 2. Pre-processing Gambar (Agar lebih mudah dibaca Tesseract)
//         // Kita ubah jadi Grayscale dan perbesar sedikit
//         BufferedImage image = ImageIO.read(convFile);
//         BufferedImage scaledImage = scaleImage(image, 3.5);
//         // BufferedImage processedImage = preprocessImage(scaledImage);

//         // 2. Convert ke Grayscale
//         BufferedImage grayImage = toGrayscale(scaledImage);

//         // 3. Contrast Boosting (Penting untuk Fotokopi Pudar)
//         // Menerangkan yang terang, menggelapkan yang gelap sebelum binarisasi
//         // BufferedImage highContrastImage = boostContrast(grayImage);

//         // 4. Binarization (Hitam Putih) dengan OTSU'S METHOD (Dynamic Threshold)
//         // Ini kunci agar support KTP Warna & Fotokopi sekaligus
//         // BufferedImage processedImage = applyOtsuBinarization(grayImage);
//         // 3. Setup Tesseract
//         ITesseract instance = new Tesseract();
//         instance.setDatapath(TESSDATA_PATH);
//         instance.setLanguage("ind"); // Pakai bahasa Indonesia

//         // Izinkan A-Z, 0-9, spasi, dan simbol umum KTP
//         instance.setTessVariable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/.-:, '");

//         // 4. Eksekusi OCR
//         String result = instance.doOCR(grayImage);

//         // Bersihkan file temp
//         convFile.delete();

//         return result.toUpperCase(); // Biar mudah dibandingin

//     }

//     // Algoritma Validasi
//     public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto inputData) {
//         OcrValidationResult result = new OcrValidationResult();
//         try {
//             String ocrText = extractText(ktpFile).toUpperCase();
//             // Bersihkan simbol aneh agar pencarian lebih akurat
//             // ocrText = ocrText.replaceAll("[^A-Z0-9\\s\\:\\/\\-\\.]", "");

//             result.setRawText(ocrText);

//             // 1. Ekstrak Teks
//             // String ocrText = extractText(ktpFile);
//             // result.setRawText(ocrText);

//             System.out.println("--- HASIL OCR KTP ---");
//             System.out.println(ocrText);
//             System.out.println("---------------------");

//             // LevenshteinDistance dist = new LevenshteinDistance();
//             String inputNama = inputData.getFullName().toUpperCase();
//             // String inputNama = inputData.getFullName();

//             boolean nameMatch = false;
//             double maxScore = 0.0;

//             // Coba cari per baris (lebih akurat daripada full text)
//             for (String line : ocrText.split("\\n")) {
//                 // Hapus label "NAMA" dan simbol
//                 String cleanLine = line.replace("NAMA", "").replace(":", "").trim();
//                 // if (cleanLine.isEmpty())
//                 // continue;

//                 if (cleanLine.length() < 3)
//                     continue;

//                 // Cek contains juga (misal: "MUHAMMAD SATRIA ARROZAK" vs "SATRIA ARROZAK")
//                 if (cleanLine.contains(inputNama) || inputNama.contains(cleanLine)) {
//                     nameMatch = true;
//                     break;
//                 }

//                 // Hitung Similarity (0.0 - 1.0)
//                 double score = calculateSimilarity(inputNama, cleanLine);
//                 if (score > maxScore)
//                     maxScore = score;

//             }

//             // Threshold kemiripan minimal 0.8 (80%)
//             if (!nameMatch && maxScore >= 0.8)
//                 nameMatch = true;

//             System.out.println("Input Nama: " + inputNama + " | Max OCR Score: " +
//                     maxScore);
//             result.setNameValid(nameMatch);

//             // 3. TEMPAT LAHIR & TGL LAHIR
//             // Format KTP: "BEKASI, 20-01-2000"
//             boolean tempatMatch = false;
//             boolean tglMatch = false;

//             String inputTempat = inputData.getBirthPlace().toUpperCase();
//             String inputTgl = "";
//             if (inputData.getBirthDate() != null) {
//                 // Ubah YYYY-MM-DD (Input) jadi dd-MM-yyyy (KTP)
//                 inputTgl = inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//             }

//             // Cari baris yang mengandung tanggal lahir
//             for (String line : ocrText.split("\\n")) {
//                 if (line.contains(inputTgl)) {
//                     tglMatch = true; // Tanggal ketemu di baris ini

//                     // Cek tempat lahir di baris yang SAMA (sebelum tanda koma)
//                     if (line.toUpperCase().contains(inputTempat)) {
//                         tempatMatch = true;
//                     }
//                 }
//             }
//             // Fallback: cari tempat lahir di seluruh teks jika format baris hancur
//             if (!tempatMatch && ocrText.contains(inputTempat))
//                 tempatMatch = true;

//             result.setBirthPlaceValid(tempatMatch);
//             result.setBirthDateValid(tglMatch);

//             // --- 3. JENIS KELAMIN (FLEXIBLE) ---
//             boolean genderMatch = false;
//             String cleanText = cleanString(ocrText); // Hapus spasi/simbol untuk deteksi global

//             if (inputData.getGender().equals("L")) {
//                 // Laki-Laki sering terbaca: LAKI, LAK1, LAKL, LAK
//                 if (cleanText.contains("LAKI") || cleanText.contains("LAK1") || cleanText.contains("LAKL")) {
//                     genderMatch = true;
//                 }
//             } else if (inputData.getGender().equals("P")) {
//                 // Perempuan: PEREM, P3REM, WANITA
//                 if (cleanText.contains("PEREM") || cleanText.contains("P3REM") || cleanText.contains("WANITA")) {
//                     genderMatch = true;
//                 }
//             }
//             result.setGenderValid(genderMatch);

//         } catch (Exception e) {
//             e.printStackTrace();
//             result.setError("Gagal baca OCR: " + e.getMessage());
//         }
//         return result;
//         // }
//     }

//     private String cleanString(String text) {
//         if (text == null)
//             return "";
//         return text.toUpperCase().replaceAll("[^A-Z0-9]", "");
//     }

//     // Helper: Image Pre-processing sederhana
//     private BufferedImage toGrayscale(BufferedImage original) {
//         // Ubah ke Grayscale
//         BufferedImage gray = new BufferedImage(original.getWidth(), original.getHeight(),
//                 BufferedImage.TYPE_BYTE_GRAY);
//         Graphics g = gray.getGraphics();
//         g.drawImage(original, 0, 0, null);
//         g.dispose();

//         // Thresholding Agresif untuk KTP Teks hitam di KTP biasanya sangat kontras jika
//         // background dihilangkan
//         // int width = gray.getWidth();
//         // int height = gray.getHeight();

//         // for (int y = 0; y < height; y++) {
//         // for (int x = 0; x < width; x++) {
//         // int rgba = gray.getRGB(x, y);
//         // Color col = new Color(rgba, true);
//         // // Threshold 150-170 efektif untuk memisahkan teks hitam dari background
//         // // biru/biru muda
//         // if ((col.getRed() + col.getGreen() + col.getBlue()) / 3 > 150) {
//         // gray.setRGB(x, y, Color.WHITE.getRGB()); // Background jadi putih
//         // } else {
//         // gray.setRGB(x, y, Color.BLACK.getRGB()); // Teks jadi hitam
//         // }
//         // }
//         // }
//         return gray;
//     }

//     // --- HELPER FUNCTIONS ---

//     // // 2. Hitung Kemiripan String (Jaro-Winkler / Levenshtein Normalized)
//     private double calculateSimilarity(String s1, String s2) {
//         if (s1 == null || s2 == null)
//             return 0.0;
//         LevenshteinDistance dist = new LevenshteinDistance();
//         int maxLength = Math.max(s1.length(), s2.length());
//         if (maxLength == 0)
//             return 1.0;
//         int distance = dist.apply(s1, s2);
//         return 1.0 - ((double) distance / maxLength);
//     }

//     // 3. Scale Image (Perbesar)
//     private BufferedImage scaleImage(BufferedImage original, double factor) {
//         int w = (int) (original.getWidth() * factor);
//         int h = (int) (original.getHeight() * factor);
//         BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//         Graphics2D g = scaled.createGraphics();
//         g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//         g.drawImage(original, 0, 0, w, h, null);
//         g.dispose();
//         return scaled;
//     }

//     @Data
//     public static class OcrValidationResult {
//         // private boolean nikValid;
//         private boolean nameValid;
//         private boolean birthPlaceValid; // Baru
//         private boolean birthDateValid; // Baru
//         private boolean genderValid;
//         private String rawText;
//         private String error;

//         public boolean isValid() {
//             // Semua harus valid
//             return nameValid && birthPlaceValid && birthDateValid && genderValid;
//         }
//     }

// }



// =====================================
// KODE Baru (UNTUK REFERENSI)
// =====================================


// package com.lsptddi.silsp.service;

// import com.lsptddi.silsp.dto.UserProfileDto;

// import lombok.Data;
// import net.sourceforge.tess4j.ITesseract;
// import net.sourceforge.tess4j.Tesseract;
// import net.sourceforge.tess4j.TesseractException;
// import org.apache.commons.text.similarity.LevenshteinDistance;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import javax.imageio.ImageIO;
// import java.awt.*;
// import java.awt.image.BufferedImage;
// import java.io.File;
// import java.io.IOException;
// import java.time.format.DateTimeFormatter;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
// import java.awt.image.RescaleOp;

// @Service
// public class OCRService {

//     // Lokasi folder tessdata (Sesuaikan dengan struktur projectmu)
//     private static final String TESSDATA_PATH = "tessdata";

//     public String extractText(MultipartFile file) throws IOException, TesseractException {
//         // 1. Konversi MultipartFile ke File temporary
//         File convFile = File.createTempFile("ocr-", ".jpg");
//         file.transferTo(convFile);

//         // 2. Pre-processing Gambar (Agar lebih mudah dibaca Tesseract)
//         // Kita ubah jadi Grayscale dan perbesar sedikit
//         BufferedImage image = ImageIO.read(convFile);
//         BufferedImage scaledImage = scaleImage(image, 3.0);
//         // BufferedImage processedImage = preprocessImage(scaledImage);

//         // 2. Convert ke Grayscale
//         BufferedImage grayImage = toGrayscale(scaledImage);

//         // 3. Contrast Boosting (Penting untuk Fotokopi Pudar)
//         // Menerangkan yang terang, menggelapkan yang gelap sebelum binarisasi
//         // BufferedImage highContrastImage = boostContrast(grayImage);

//         // 4. Binarization (Hitam Putih) dengan OTSU'S METHOD (Dynamic Threshold)
//         // Ini kunci agar support KTP Warna & Fotokopi sekaligus
//         // BufferedImage processedImage = applyOtsuBinarization(grayImage);
//         // 3. Setup Tesseract
//         ITesseract instance = new Tesseract();
//         instance.setDatapath(TESSDATA_PATH);
//         instance.setLanguage("ind"); // Pakai bahasa Indonesia

//         // Izinkan A-Z, 0-9, spasi, dan simbol umum KTP
//         instance.setTessVariable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/.-:, '");

//         // 4. Eksekusi OCR
//         String result = instance.doOCR(grayImage);

//         // Bersihkan file temp
//         convFile.delete();

//         return result.toUpperCase(); // Biar mudah dibandingin

//     }

//     // Algoritma Validasi
//     public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto inputData) {
//         OcrValidationResult result = new OcrValidationResult();
//         try {
//             // String ocrText = extractText(ktpFile).toUpperCase();
//             // Bersihkan simbol aneh agar pencarian lebih akurat
//             // ocrText = ocrText.replaceAll("[^A-Z0-9\\s\\:\\/\\-\\.]", "");

//             // result.setRawText(ocrText);
//             String rawOcr = extractText(ktpFile).toUpperCase();

//             // CLEANING EKSTRA: Hapus baris kosong & karakter sampah di awal/akhir
//             String ocrText = rawOcr.replaceAll("(?m)^\\s+$", "");

//             // 1. Ekstrak Teks
//             // String ocrText = extractText(ktpFile);
//             // result.setRawText(ocrText);

//             System.out.println("--- HASIL OCR KTP ---");
//             System.out.println(ocrText);
//             System.out.println("---------------------");

//             // LevenshteinDistance dist = new LevenshteinDistance();
//             String inputNama = inputData.getFullName().toUpperCase();
//             // String inputNama = inputData.getFullName();

//             boolean nameMatch = false;
//             double maxScore = 0.0;

//             // Coba cari per baris (lebih akurat daripada full text)
//             for (String line : ocrText.split("\\n")) {
//                 // Hapus label "NAMA" dan simbol
//                 String cleanLine = line.replace("NAMA", "").replace(":", "").trim();
//                 // if (cleanLine.isEmpty())
//                 //     continue;

//                 // Bersihkan anomali karakter di baris nama (Hapus angka/simbol aneh)
//                 // Contoh: "SATR1A" -> "SATRA", "BUDI_ANT0" -> "BUDI ANTO"
//                 cleanLine = cleanNameForComparison(cleanLine);

//                 if (cleanLine.length() < 3)
//                     continue;

//                 // Cek contains juga (misal: "MUHAMMAD SATRIA ARROZAK" vs "SATRIA ARROZAK")
//                 if (cleanLine.contains(inputNama) || inputNama.contains(cleanLine)) {
//                     nameMatch = true;
//                     break;
//                 }

//                 // Hitung Similarity (0.0 - 1.0)
//                 double score = calculateSimilarity(inputNama, cleanLine);
//                 if (score > maxScore)
//                     maxScore = score;

//             }

            

//             // Threshold kemiripan minimal 0.8 (80%)
//             // if (!nameMatch && maxScore >= 0.8)
//             // nameMatch = true;

//             System.out.println("Input Nama: " + inputNama + " | Max OCR Score: " +
//                     maxScore);
//             result.setNameValid(nameMatch);

//             // 3. TEMPAT LAHIR & TGL LAHIR
//             // Format KTP: "BEKASI, 20-01-2000"
//             boolean tempatMatch = false;
//             boolean tglMatch = false;

//             String inputTempat = inputData.getBirthPlace().toUpperCase();
//             String inputTgl = "";
//             if (inputData.getBirthDate() != null) {
//                 // Ubah YYYY-MM-DD (Input) jadi dd-MM-yyyy (KTP)
//                 inputTgl = inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//             }

//             // Cari baris yang mengandung tanggal lahir
//             for (String line : ocrText.split("\\n")) {
//                 if (line.contains(inputTgl)) {
//                     tglMatch = true; // Tanggal ketemu di baris ini

//                     // Cek tempat lahir di baris yang SAMA (sebelum tanda koma)
//                     if (line.toUpperCase().contains(inputTempat)) {
//                         tempatMatch = true;
//                     }
//                 }
//             }
//             // Fallback: cari tempat lahir di seluruh teks jika format baris hancur
//             if (!tempatMatch && ocrText.contains(inputTempat))
//                 tempatMatch = true;

//             result.setBirthPlaceValid(tempatMatch);
//             result.setBirthDateValid(tglMatch);

//             // --- 3. JENIS KELAMIN (FLEXIBLE) ---
//             boolean genderMatch = false;
//             String cleanText = cleanString(ocrText); // Hapus spasi/simbol untuk deteksi global

//             if (inputData.getGender().equals("L")) {
//                 // Laki-Laki sering terbaca: LAKI, LAK1, LAKL, LAK
//                 if (cleanText.contains("LAKI") || cleanText.contains("LAK1") || cleanText.contains("LAKL")) {
//                     genderMatch = true;
//                 }
//             } else if (inputData.getGender().equals("P")) {
//                 // Perempuan: PEREM, P3REM, WANITA
//                 if (cleanText.contains("PEREM") || cleanText.contains("P3REM") || cleanText.contains("WANITA")) {
//                     genderMatch = true;
//                 }
//             }
//             result.setGenderValid(genderMatch);

//         } catch (Exception e) {
//             e.printStackTrace();
//             result.setError("Gagal baca OCR: " + e.getMessage());
//         }
//         return result;
//         // }
//     }

//     private String cleanString(String text) {
//         if (text == null)
//             return "";
//         return text.toUpperCase().replaceAll("[^A-Z0-9]", "");
//     }

//     // Helper: Image Pre-processing sederhana
//     private BufferedImage toGrayscale(BufferedImage original) {
//         // Ubah ke Grayscale
//         BufferedImage gray = new BufferedImage(original.getWidth(), original.getHeight(),
//                 BufferedImage.TYPE_BYTE_GRAY);
//         Graphics g = gray.getGraphics();
//         g.drawImage(original, 0, 0, null);
//         g.dispose();

//         try {
//             RescaleOp rescaleOp = new RescaleOp(1.7f, 15.0f, null);
//             rescaleOp.filter(gray, gray); // Apply filter langsung ke image gray
//         } catch (Exception e) {
//             // Fallback jika gagal filter (jarang terjadi), kembalikan raw grayscale
//             System.out.println("Gagal boost contrast, menggunakan raw grayscale.");
//         }

//         return gray;
//     }

//     // --- HELPER METHODS ---

//     // Membersihkan Nama dari angka/simbol aneh agar perbandingan fair
//     private String cleanNameForComparison(String text) {
//         if (text == null)
//             return "";
//         // Hanya sisakan Huruf A-Z dan Spasi. Angka di Nama dianggap sampah OCR.
//         // Ganti titik (.) dengan spasi jika ada gelar
//         return text.toUpperCase()
//                 .replace(".", " ")
//                 .replaceAll("[^A-Z\\s]", "") // Hapus angka & simbol
//                 .replaceAll("\\s+", " ") // Hapus spasi ganda
//                 .trim();
//     }

//     // --- HELPER FUNCTIONS ---

//     // // 2. Hitung Kemiripan String (Jaro-Winkler / Levenshtein Normalized)
//     private double calculateSimilarity(String s1, String s2) {
//         if (s1 == null || s2 == null)
//             return 0.0;
//         LevenshteinDistance dist = new LevenshteinDistance();
//         int maxLength = Math.max(s1.length(), s2.length());
//         if (maxLength == 0)
//             return 1.0;
//         int distance = dist.apply(s1, s2);
//         return 1.0 - ((double) distance / maxLength);
//     }

//     // 3. Scale Image (Perbesar)
//     private BufferedImage scaleImage(BufferedImage original, double factor) {
//         int w = (int) (original.getWidth() * factor);
//         int h = (int) (original.getHeight() * factor);
//         BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//         Graphics2D g = scaled.createGraphics();
//         g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//         g.drawImage(original, 0, 0, w, h, null);
//         g.dispose();
//         return scaled;
//     }

//     @Data
//     public static class OcrValidationResult {
//         // private boolean nikValid;
//         private boolean nameValid;
//         private boolean birthPlaceValid; // Baru
//         private boolean birthDateValid; // Baru
//         private boolean genderValid;
//         private String rawText;
//         private String error;

//         public boolean isValid() {
//             // Semua harus valid
//             return nameValid && birthPlaceValid && birthDateValid && genderValid;
//         }
//     }

// }




// =====================================
// KODE FINAL TERBARU
// ====================================
// package com.lsptddi.silsp.service;

// import com.lsptddi.silsp.dto.UserProfileDto;

// import lombok.Data;
// import net.sourceforge.tess4j.ITesseract;
// import net.sourceforge.tess4j.Tesseract;
// import net.sourceforge.tess4j.TesseractException;
// import org.apache.commons.text.similarity.LevenshteinDistance;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import javax.imageio.ImageIO;
// import java.awt.*;
// import java.awt.image.BufferedImage;
// import java.io.File;
// import java.io.IOException;
// import java.time.format.DateTimeFormatter;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
// import java.awt.image.RescaleOp;

// @Service
// public class OCRService {

//     // Lokasi folder tessdata (Sesuaikan dengan struktur projectmu)
//     private static final String TESSDATA_PATH = "tessdata";

//     public String extractText(MultipartFile file) throws IOException, TesseractException {
//         // 1. Konversi MultipartFile ke File temporary
//         File convFile = File.createTempFile("ocr-", ".jpg");
//         file.transferTo(convFile);

//         // 2. Pre-processing Gambar (Agar lebih mudah dibaca Tesseract)
//         // Kita ubah jadi Grayscale dan perbesar sedikit
//         BufferedImage image = ImageIO.read(convFile);
//         BufferedImage scaledImage = scaleImage(image, 3.0);
//         // BufferedImage processedImage = preprocessImage(scaledImage);

//         // 2. Convert ke Grayscale
//         BufferedImage grayImage = toGrayscale(scaledImage);

//         // 3. Contrast Boosting (Penting untuk Fotokopi Pudar)
//         // Menerangkan yang terang, menggelapkan yang gelap sebelum binarisasi
//         // BufferedImage highContrastImage = boostContrast(grayImage);

//         // 4. Binarization (Hitam Putih) dengan OTSU'S METHOD (Dynamic Threshold)
//         // Ini kunci agar support KTP Warna & Fotokopi sekaligus
//         // BufferedImage processedImage = applyOtsuBinarization(grayImage);
//         // 3. Setup Tesseract
//         ITesseract instance = new Tesseract();
//         instance.setDatapath(TESSDATA_PATH);
//         instance.setLanguage("ind"); // Pakai bahasa Indonesia

//         // Izinkan A-Z, 0-9, spasi, dan simbol umum KTP
//         instance.setTessVariable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/.-:, '");

//         // 4. Eksekusi OCR
//         String result = instance.doOCR(grayImage);

//         // Bersihkan file temp
//         convFile.delete();

//         return result.toUpperCase(); // Biar mudah dibandingin

//     }

//     // Algoritma Validasi
//     public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto inputData) {
//         OcrValidationResult result = new OcrValidationResult();
//         try {
//             // String ocrText = extractText(ktpFile).toUpperCase();
//             // Bersihkan simbol aneh agar pencarian lebih akurat
//             // ocrText = ocrText.replaceAll("[^A-Z0-9\\s\\:\\/\\-\\.]", "");

//             // result.setRawText(ocrText);
//             String rawOcr = extractText(ktpFile).toUpperCase();

//             // CLEANING EKSTRA: Hapus baris kosong & karakter sampah di awal/akhir
//             String ocrText = rawOcr.replaceAll("(?m)^\\s+$", "");

//             // 1. Ekstrak Teks
//             // String ocrText = extractText(ktpFile);
//             // result.setRawText(ocrText);

//             System.out.println("--- HASIL OCR KTP ---");
//             System.out.println(ocrText);
//             System.out.println("---------------------");

//             // LevenshteinDistance dist = new LevenshteinDistance();
//             String inputNama = inputData.getFullName().toUpperCase();
//             // String inputNama = inputData.getFullName();

//             boolean nameMatch = false;
//             double maxScore = 0.0;

//             for (String line : ocrText.split("\\n")) {
//                 String cleanLine = cleanNameForComparison(line); // Bersih standar
//                 if (cleanLine.length() < 3)
//                     continue;

//                 if (cleanLine.contains(cleanNameForComparison(inputNama))) {
//                     nameMatch = true;
//                     maxScore = 1.0;
//                     break;
//                 }

//                 double score = calculateSimilarity(cleanNameForComparison(inputNama), cleanLine);
//                 if (score > maxScore)
//                     maxScore = score;
//             }

//             // Coba cari per baris (lebih akurat daripada full text)
//             if (!nameMatch && maxScore < 0.85) { // Naikkan sedikit threshold strict agar masuk ke sini
//                 System.out.println(
//                         "Strict check gagal (Score: " + maxScore + "), mencoba Fuzzy Logic Toleransi Tinggi...");

//                 String normalizedInput = normalizeForFuzzyMatch(inputNama);

//                 for (String line : ocrText.split("\\n")) {
//                     // Hapus label sampah
//                     String rawLine = line.replace("NAMA", "").replace(":", "").trim();

//                     // Normalisasi Ekstrem (Hapus spasi, titik, ganti angka mirip huruf)
//                     String normalizedOCR = normalizeForFuzzyMatch(rawLine);

//                     if (normalizedOCR.length() < 3)
//                         continue;

//                     // Hitung similarity berdasarkan string yang sudah dipadatkan
//                     // Input: "R. Ifrianssya" -> "RIFRIANSSYA"
//                     // OCR: "REFRIANSSYA" -> "REFRIANSSYA"
//                     // Beda cuma 1 huruf ('I' vs 'E') -> Score pasti tinggi
//                     double looseScore = calculateSimilarity(normalizedInput, normalizedOCR);

//                     if (looseScore > maxScore) {
//                         maxScore = looseScore;
//                     }

//                     // Jika input pendek (cuma 1 kata), butuh akurasi tinggi (0.85)
//                     // Jika input panjang, toleransi typo lebih besar (0.75)
//                     double threshold = (normalizedInput.length() <= 5) ? 0.85 : 0.75;

//                     if (looseScore >= threshold) {
//                         nameMatch = true;
//                         System.out.println(
//                                 "Match ditemukan di Loose Check! (" + normalizedInput + " vs " + normalizedOCR + ")");
//                         break;
//                     }
//                 }
//             } else if (!nameMatch && maxScore >= 0.80) {
//                 // Fallback untuk strict check yang mendekati benar
//                 nameMatch = true;
//             }

//             // Threshold kemiripan minimal 0.8 (80%)
//             // if (!nameMatch && maxScore >= 0.8)
//             // nameMatch = true;

//             System.out.println("Input Nama: " + inputNama + " | Max OCR Score: " +
//                     maxScore);
//             result.setNameValid(nameMatch);

//             // 3. TEMPAT LAHIR & TGL LAHIR
//             // Format KTP: "BEKASI, 20-01-2000"
//             boolean tempatMatch = false;
//             boolean tglMatch = false;

//             String inputTempat = inputData.getBirthPlace().toUpperCase();
//             String inputTgl = "";
//             if (inputData.getBirthDate() != null) {
//                 // Ubah YYYY-MM-DD (Input) jadi dd-MM-yyyy (KTP)
//                 inputTgl = inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//             }

//             // Cari baris yang mengandung tanggal lahir
//             for (String line : ocrText.split("\\n")) {
//                 if (line.contains(inputTgl)) {
//                     tglMatch = true; // Tanggal ketemu di baris ini

//                     // Cek tempat lahir di baris yang SAMA (sebelum tanda koma)
//                     if (line.toUpperCase().contains(inputTempat)) {
//                         tempatMatch = true;
//                     }
//                 }
//             }
//             // Fallback: cari tempat lahir di seluruh teks jika format baris hancur
//             if (!tempatMatch && ocrText.contains(inputTempat))
//                 tempatMatch = true;

//             result.setBirthPlaceValid(tempatMatch);
//             result.setBirthDateValid(tglMatch);

//             // --- 3. JENIS KELAMIN (FLEXIBLE) ---
//             boolean genderMatch = false;
//             String cleanText = cleanString(ocrText); // Hapus spasi/simbol untuk deteksi global

//             if (inputData.getGender().equals("L")) {
//                 // Laki-Laki sering terbaca: LAKI, LAK1, LAKL, LAK
//                 if (cleanText.contains("LAKI") || cleanText.contains("LAK1") || cleanText.contains("LAKL")) {
//                     genderMatch = true;
//                 }
//             } else if (inputData.getGender().equals("P")) {
//                 // Perempuan: PEREM, P3REM, WANITA
//                 if (cleanText.contains("PEREM") || cleanText.contains("P3REM") || cleanText.contains("WANITA")) {
//                     genderMatch = true;
//                 }
//             }
//             result.setGenderValid(genderMatch);

//         } catch (Exception e) {
//             e.printStackTrace();
//             result.setError("Gagal baca OCR: " + e.getMessage());
//         }
//         return result;
//         // }
//     }

//     private String cleanString(String text) {
//         if (text == null)
//             return "";
//         return text.toUpperCase().replaceAll("[^A-Z0-9]", "");
//     }

//     // Helper: Image Pre-processing sederhana
//     private BufferedImage toGrayscale(BufferedImage original) {
//         // Ubah ke Grayscale
//         BufferedImage gray = new BufferedImage(original.getWidth(), original.getHeight(),
//                 BufferedImage.TYPE_BYTE_GRAY);
//         Graphics g = gray.getGraphics();
//         g.drawImage(original, 0, 0, null);
//         g.dispose();

//         try {
//             RescaleOp rescaleOp = new RescaleOp(1.7f, 15.0f, null);
//             rescaleOp.filter(gray, gray); // Apply filter langsung ke image gray
//         } catch (Exception e) {
//             // Fallback jika gagal filter (jarang terjadi), kembalikan raw grayscale
//             System.out.println("Gagal boost contrast, menggunakan raw grayscale.");
//         }

//         return gray;
//     }

//     // --- HELPER METHODS ---

//     // Membersihkan Nama dari angka/simbol aneh agar perbandingan fair
//     private String cleanNameForComparison(String text) {
//         if (text == null)
//             return "";
//         // Hanya sisakan Huruf A-Z dan Spasi. Angka di Nama dianggap sampah OCR.
//         // Ganti titik (.) dengan spasi jika ada gelar
//         return text.toUpperCase()
//                 .replace(".", " ")
//                 .replaceAll("[^A-Z\\s]", "") // Hapus angka & simbol
//                 .replaceAll("\\s+", " ") // Hapus spasi ganda
//                 .trim();
//     }

//     private String normalizeForFuzzyMatch(String text) {
//         if (text == null)
//             return "";

//         String s = text.toUpperCase();

//         // 1. Ganti Angka/Simbol yang mirip Huruf (OCR Substitution)
//         s = s.replace("0", "O")
//                 .replace("1", "I")
//                 .replace("l", "I")
//                 .replace("5", "S")
//                 .replace("2", "Z")
//                 .replace("4", "A")
//                 .replace("8", "B");

//         // 2. Hapus SEMUA karakter non-huruf (Spasi, Titik, Koma, Strip hilang semua)
//         // "R. Ifrianssya" -> "RIFRIANSSYA"
//         // "REFRIANSSYA" -> "REFRIANSSYA"
//         s = s.replaceAll("[^A-Z]", "");

//         return s;
//     }

//     // --- HELPER FUNCTIONS ---

//     // // 2. Hitung Kemiripan String (Jaro-Winkler / Levenshtein Normalized)
//     private double calculateSimilarity(String s1, String s2) {
//         if (s1 == null || s2 == null)
//             return 0.0;
//         LevenshteinDistance dist = new LevenshteinDistance();
//         int maxLength = Math.max(s1.length(), s2.length());
//         if (maxLength == 0)
//             return 1.0;
//         int distance = dist.apply(s1, s2);
//         return 1.0 - ((double) distance / maxLength);
//     }

//     // 3. Scale Image (Perbesar)
//     private BufferedImage scaleImage(BufferedImage original, double factor) {
//         int w = (int) (original.getWidth() * factor);
//         int h = (int) (original.getHeight() * factor);
//         BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//         Graphics2D g = scaled.createGraphics();
//         g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//         g.drawImage(original, 0, 0, w, h, null);
//         g.dispose();
//         return scaled;
//     }

//     @Data
//     public static class OcrValidationResult {
//         // private boolean nikValid;
//         private boolean nameValid;
//         private boolean birthPlaceValid; // Baru
//         private boolean birthDateValid; // Baru
//         private boolean genderValid;
//         private String rawText;
//         private String error;

//         public boolean isValid() {
//             // Semua harus valid
//             return nameValid && birthPlaceValid && birthDateValid && genderValid;
//         }
//     }

// }

// package com.lsptddi.silsp.service;

// import com.lsptddi.silsp.dto.UserProfileDto;

// import lombok.Data;
// import net.sourceforge.tess4j.ITesseract;
// import net.sourceforge.tess4j.Tesseract;
// import net.sourceforge.tess4j.TesseractException;
// import org.apache.commons.text.similarity.LevenshteinDistance;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import javax.imageio.ImageIO;
// import java.awt.*;
// import java.awt.image.BufferedImage;
// import java.io.File;
// import java.io.IOException;
// import java.time.format.DateTimeFormatter;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
// import java.awt.image.RescaleOp;

// @Service
// public class OCRService {

//     // Lokasi folder tessdata (Sesuaikan dengan struktur projectmu)
//     private static final String TESSDATA_PATH = "tessdata";

//     public String extractText(MultipartFile file) throws IOException,
//             TesseractException {
//         // 1. Konversi MultipartFile ke File temporary
//         File convFile = File.createTempFile("ocr-", ".jpg");
//         file.transferTo(convFile);

//         // 2. Pre-processing Gambar (Agar lebih mudah dibaca Tesseract)
//         // Kita ubah jadi Grayscale dan perbesar sedikit
//         BufferedImage image = ImageIO.read(convFile);
//         BufferedImage scaledImage = scaleImage(image, 3.0);
//         // BufferedImage processedImage = preprocessImage(scaledImage);

//         // 2. Convert ke Grayscale
//         BufferedImage grayImage = toGrayscale(scaledImage);

//         // 3. Contrast Boosting (Penting untuk Fotokopi Pudar)
//         // Menerangkan yang terang, menggelapkan yang gelap sebelum binarisasi
//         // BufferedImage highContrastImage = boostContrast(grayImage);

//         // 4. Binarization (Hitam Putih) dengan OTSU'S METHOD (Dynamic Threshold)
//         // Ini kunci agar support KTP Warna & Fotokopi sekaligus
//         // BufferedImage processedImage = applyOtsuBinarization(grayImage);
//         // 3. Setup Tesseract
//         ITesseract instance = new Tesseract();
//         instance.setDatapath(TESSDATA_PATH);
//         instance.setLanguage("ind"); // Pakai bahasa Indonesia

//         // Izinkan A-Z, 0-9, spasi, dan simbol umum KTP
//         instance.setTessVariable("tessedit_char_whitelist",
//                 "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/.-:, '");

//         // 4. Eksekusi OCR
//         String result = instance.doOCR(grayImage);

//         // Bersihkan file temp
//         convFile.delete();

//         return result.toUpperCase(); // Biar mudah dibandingin

//     }

//     // Algoritma Validasi
// public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto
// inputData) {
// OcrValidationResult result = new OcrValidationResult();
// try {
// String ocrText = extractText(ktpFile).toUpperCase();
// // Bersihkan simbol aneh agar pencarian lebih akurat
// // ocrText = ocrText.replaceAll("[^A-Z0-9\\s\\:\\/\\-\\.]", "");

// result.setRawText(ocrText);

// // 1. Ekstrak Teks
// // String ocrText = extractText(ktpFile);
// // result.setRawText(ocrText);

// System.out.println("--- HASIL OCR KTP ---");
// System.out.println(ocrText);
// System.out.println("---------------------");

// // LevenshteinDistance dist = new LevenshteinDistance();
// String inputNama = inputData.getFullName().toUpperCase();
// // String inputNama = inputData.getFullName();

// boolean nameMatch = false;
// double maxScore = 0.0;

// // Coba cari per baris (lebih akurat daripada full text)
// for (String line : ocrText.split("\\n")) {
// // Hapus label "NAMA" dan simbol
// String cleanLine = line.replace("NAMA", "").replace(":", "").trim();
// // if (cleanLine.isEmpty())
// // continue;

// if (cleanLine.length() < 3)
// continue;

// // Cek contains juga (misal: "MUHAMMAD SATRIA ARROZAK" vs "SATRIA ARROZAK")
// if (cleanLine.contains(inputNama) || inputNama.contains(cleanLine)) {
// nameMatch = true;
// break;
// }

// // Hitung Similarity (0.0 - 1.0)
// double score = calculateSimilarity(inputNama, cleanLine);
// if (score > maxScore)
// maxScore = score;

// }

// // Threshold kemiripan minimal 0.8 (80%)
// if (!nameMatch && maxScore >= 0.8)
// nameMatch = true;

// System.out.println("Input Nama: " + inputNama + " | Max OCR Score: " +
// maxScore);
// result.setNameValid(nameMatch);

// // 3. TEMPAT LAHIR & TGL LAHIR
// // Format KTP: "BEKASI, 20-01-2000"
// boolean tempatMatch = false;
// boolean tglMatch = false;

// String inputTempat = inputData.getBirthPlace().toUpperCase();
// String inputTgl = "";
// if (inputData.getBirthDate() != null) {
// // Ubah YYYY-MM-DD (Input) jadi dd-MM-yyyy (KTP)
// inputTgl =
// inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
// }

// // Cari baris yang mengandung tanggal lahir
// for (String line : ocrText.split("\\n")) {
// if (line.contains(inputTgl)) {
// tglMatch = true; // Tanggal ketemu di baris ini

// // Cek tempat lahir di baris yang SAMA (sebelum tanda koma)
// if (line.toUpperCase().contains(inputTempat)) {
// tempatMatch = true;
// }
// }
// }
// // Fallback: cari tempat lahir di seluruh teks jika format baris hancur
// if (!tempatMatch && ocrText.contains(inputTempat))
// tempatMatch = true;

// result.setBirthPlaceValid(tempatMatch);
// result.setBirthDateValid(tglMatch);

// // --- 3. JENIS KELAMIN (FLEXIBLE) ---
// boolean genderMatch = false;
// String cleanText = cleanString(ocrText); // Hapus spasi/simbol untuk deteksi global

// if (inputData.getGender().equals("L")) {
// // Laki-Laki sering terbaca: LAKI, LAK1, LAKL, LAK
// if (cleanText.contains("LAKI") || cleanText.contains("LAK1") ||
// cleanText.contains("LAKL")) {
// genderMatch = true;
// }
// } else if (inputData.getGender().equals("P")) {
// // Perempuan: PEREM, P3REM, WANITA
// if (cleanText.contains("PEREM") || cleanText.contains("P3REM") ||
// cleanText.contains("WANITA")) {
// genderMatch = true;
// }
// }
// result.setGenderValid(genderMatch);

// } catch (Exception e) {
// e.printStackTrace();
// result.setError("Gagal baca OCR: " + e.getMessage());
// }
// return result;
// // }
// }

//     private String cleanString(String text) {
//         if (text == null)
//             return "";
//         return text.toUpperCase().replaceAll("[^A-Z0-9]", "");
//     }

//     // Helper: Image Pre-processing sederhana
// private BufferedImage toGrayscale(BufferedImage original) {
// // Ubah ke Grayscale
// BufferedImage gray = new BufferedImage(original.getWidth(),
// original.getHeight(),
// BufferedImage.TYPE_BYTE_GRAY);
// Graphics g = gray.getGraphics();
// g.drawImage(original, 0, 0, null);
// g.dispose();

// try {
// RescaleOp rescaleOp = new RescaleOp(1.7f, 15.0f, null);
// rescaleOp.filter(gray, gray); // Apply filter langsung ke image gray
// } catch (Exception e) {
// // Fallback jika gagal filter (jarang terjadi), kembalikan raw grayscale
// System.out.println("Gagal boost contrast, menggunakan raw grayscale.");
// }

// // Thresholding Agresif untuk KTP Teks hitam di KTP biasanya sangat kontras jika
// // background dihilangkan
// // int width = gray.getWidth();
// // int height = gray.getHeight();

// // for (int y = 0; y < height; y++) {
// // for (int x = 0; x < width; x++) {
// // int rgba = gray.getRGB(x, y);
// // Color col = new Color(rgba, true);
// // // Threshold 150-170 efektif untuk memisahkan teks hitam dari background
// // // biru/biru muda
// // if ((col.getRed() + col.getGreen() + col.getBlue()) / 3 > 150) {
// // gray.setRGB(x, y, Color.WHITE.getRGB()); // Background jadi putih
// // } else {
// // gray.setRGB(x, y, Color.BLACK.getRGB()); // Teks jadi hitam
// // }
// // }
// // }
// return gray;
// }

//     // --- HELPER FUNCTIONS ---

//     // // 2. Hitung Kemiripan String (Jaro-Winkler / Levenshtein Normalized)
//     private double calculateSimilarity(String s1, String s2) {
//         if (s1 == null || s2 == null)
//             return 0.0;
//         LevenshteinDistance dist = new LevenshteinDistance();
//         int maxLength = Math.max(s1.length(), s2.length());
//         if (maxLength == 0)
//             return 1.0;
//         int distance = dist.apply(s1, s2);
//         return 1.0 - ((double) distance / maxLength);
//     }

//     // 3. Scale Image (Perbesar)
//     private BufferedImage scaleImage(BufferedImage original, double factor) {
//         int w = (int) (original.getWidth() * factor);
//         int h = (int) (original.getHeight() * factor);
//         BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//         Graphics2D g = scaled.createGraphics();
//         g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                 RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//         g.drawImage(original, 0, 0, w, h, null);
//         g.dispose();
//         return scaled;
//     }

//     @Data
//     public static class OcrValidationResult {
//         // private boolean nikValid;
//         private boolean nameValid;
//         private boolean birthPlaceValid; // Baru
//         private boolean birthDateValid; // Baru
//         private boolean genderValid;
//         private String rawText;
//         private String error;

//         public boolean isValid() {
//             // Semua harus valid
//             return nameValid && birthPlaceValid && birthDateValid && genderValid;
//         }
//     }

// }

// package com.lsptddi.silsp.service;

// import com.lsptddi.silsp.dto.UserProfileDto;
// import lombok.Data;
// import net.sourceforge.tess4j.ITesseract;
// import net.sourceforge.tess4j.Tesseract;
// import net.sourceforge.tess4j.TesseractException;
// import org.apache.commons.text.similarity.LevenshteinDistance;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import javax.imageio.ImageIO;
// import java.awt.*;
// import java.awt.image.BufferedImage;
// import java.io.File;
// import java.io.IOException;
// import java.time.format.DateTimeFormatter;
// import java.awt.image.RescaleOp;

// @Service
// public class OCRService {

//     private static final String TESSDATA_PATH = "tessdata";

//     public String extractText(MultipartFile file) throws IOException, TesseractException {
//         File convFile = File.createTempFile("ocr-", ".jpg");
//         file.transferTo(convFile);

//         BufferedImage image = ImageIO.read(convFile);
//         BufferedImage scaledImage = scaleImage(image, 3.0);
//         BufferedImage processedImage = preprocessImage(scaledImage);

//         ITesseract instance = new Tesseract();
//         instance.setDatapath(TESSDATA_PATH);
//         instance.setLanguage("ind");

//         String result = instance.doOCR(processedImage);
//         convFile.delete();

//         return result.toUpperCase();
//     }

//     // Algoritma Validasi (TANPA NIK)
//     public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto inputData) {
//         OcrValidationResult result = new OcrValidationResult();
//         try {
//             String ocrText = extractText(ktpFile).toUpperCase();
//             // Bersihkan simbol aneh
//             ocrText = ocrText.replaceAll("[^A-Z0-9\\s\\:\\/\\-\\.]", "");

//             result.setRawText(ocrText);

//             System.out.println("--- HASIL OCR KTP ---");
//             System.out.println(ocrText);
//             System.out.println("---------------------");

//             // 1. VALIDASI NAMA (Fuzzy - Levenshtein)
//             LevenshteinDistance dist = new LevenshteinDistance();
//             String inputNama = inputData.getFullName().toUpperCase();
//             boolean namaMatch = false;

//             // Cek per baris
//             for (String line : ocrText.split("\\n")) {
//                 String cleanLine = line.replace("NAMA", "").replace(":", "").trim();
//                 if (cleanLine.isEmpty())
//                     continue;

//                 int threshold = inputNama.length() < 5 ? 0 : 3;

//                 if (dist.apply(cleanLine, inputNama) <= threshold) {
//                 namaMatch = true;
//                 break;
//                 }

//                 // if (cleanLine.length() < 3)
//                 //     continue;

//                 if (cleanLine.contains(inputNama) || inputNama.contains(cleanLine)) {
//                     namaMatch = true;
//                     break;
//                 }
//             }
//             // Fallback global search
//             if (!namaMatch && ocrText.contains(inputNama))
//                 namaMatch = true;

//             result.setNameValid(namaMatch);

//             // 2. TEMPAT LAHIR & TGL LAHIR
//             boolean tempatMatch = false;
//             boolean tglMatch = false;

//             String inputTempat = inputData.getBirthPlace().toUpperCase();
//             String inputTgl = "";
//             if (inputData.getBirthDate() != null) {
//                 inputTgl = inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//             }

//             for (String line : ocrText.split("\\n")) {
//                 if (line.contains(inputTgl)) {
//                     tglMatch = true;
//                     if (line.toUpperCase().contains(inputTempat)) {
//                         tempatMatch = true;
//                     }
//                 }
//             }
//             if (!tempatMatch && ocrText.contains(inputTempat))
//                 tempatMatch = true;
//             if (!tglMatch && ocrText.contains(inputTgl))
//                 tglMatch = true;

//             result.setBirthPlaceValid(tempatMatch);
//             result.setBirthDateValid(tglMatch);

//             // 3. JENIS KELAMIN
//             String genderKtp = inputData.getGender().equals("L") ? "LAKI-LAKI" : "PEREMPUAN";
//             result.setGenderValid(ocrText.contains(genderKtp));

//         } catch (Exception e) {
//             e.printStackTrace();
//             result.setError("Gagal baca OCR: " + e.getMessage());
//         }
//         return result;
//     }

//     private BufferedImage preprocessImage(BufferedImage original) {
//         BufferedImage grayscale = new BufferedImage(original.getWidth(), original.getHeight(),
//                 BufferedImage.TYPE_BYTE_GRAY);
//         Graphics g = grayscale.getGraphics();
//         g.drawImage(original, 0, 0, null);
//         g.dispose();

//         try {
//             RescaleOp rescaleOp = new RescaleOp(1.7f, 15.0f, null);
//             rescaleOp.filter(grayscale, grayscale); // Apply filter langsung ke image gray
//         } catch (Exception e) {
//             // Fallback jika gagal filter (jarang terjadi), kembalikan raw grayscale
//             System.out.println("Gagal boost contrast, menggunakan raw grayscale.");
//         }
//         return grayscale;
//     }

//     private BufferedImage scaleImage(BufferedImage original, double factor) {
//         int w = (int) (original.getWidth() * factor);
//         int h = (int) (original.getHeight() * factor);
//         BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//         Graphics2D g = scaled.createGraphics();
//         g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//         g.drawImage(original, 0, 0, w, h, null);
//         g.dispose();
//         return scaled;
//     }

//     @Data
//     public static class OcrValidationResult {
//         // Hapus nikValid
//         private boolean nameValid;
//         private boolean birthPlaceValid;
//         private boolean birthDateValid;
//         private boolean genderValid;
//         private String rawText;
//         private String error;

//         public boolean isValid() {
//             // Valid jika semua field (kecuali NIK) cocok
//             return nameValid && birthPlaceValid && birthDateValid && genderValid;
//         }
//     }
// }


// package com.lsptddi.silsp.service;

// import com.lsptddi.silsp.dto.UserProfileDto;
// import lombok.Data;
// import net.sourceforge.tess4j.ITesseract;
// import net.sourceforge.tess4j.Tesseract;
// import net.sourceforge.tess4j.TesseractException;
// import org.apache.commons.text.similarity.LevenshteinDistance;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import javax.imageio.ImageIO;
// import java.awt.*;
// import java.awt.image.BufferedImage;
// import java.awt.image.RescaleOp;
// import java.io.File;
// import java.io.IOException;
// import java.time.format.DateTimeFormatter;

// @Service
// public class OCRService {

//     private static final String TESSDATA_PATH = "tessdata";

//     public String extractText(MultipartFile file) throws IOException, TesseractException {
//         File convFile = File.createTempFile("ocr-", ".jpg");
//         file.transferTo(convFile);

//         // 1. Baca Gambar
//         BufferedImage originalImage = ImageIO.read(convFile);
        
//         // 2. PRE-PROCESSING TINGKAT LANJUT (Kunci Akurasi)
//         // KTP seringkali resolusinya rendah untuk OCR, kita perbesar 2x-3x
//         BufferedImage scaledImage = scaleImage(originalImage, 2.5); 
//         // Ubah ke Hitam Putih (Binarization) agar teks tajam
//         BufferedImage processedImage = processImageForOCR(scaledImage);

//         // 3. Setup Tesseract
//         ITesseract instance = new Tesseract();
//         instance.setDatapath(TESSDATA_PATH);
//         instance.setLanguage("ind"); 
//         // Whitelist karakter agar tidak membaca simbol aneh (Optional, tapi membantu)
//         instance.setTessVariable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/.-: ");

//         String result = instance.doOCR(processedImage);
        
//         // Cleanup
//         convFile.delete();
//         return result.toUpperCase();
//     }

//     public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto inputData) {
//         OcrValidationResult result = new OcrValidationResult();
//         try {
//             String ocrText = extractText(ktpFile).toUpperCase();
//             result.setRawText(ocrText);
            
//             System.out.println("--- RAW OCR RESULT ---");
//             System.out.println(ocrText);
//             System.out.println("----------------------");

//             // --- 1. VALIDASI NAMA (STRICT MODE) ---
//             // Syarat: Input User harus mirip > 85% dengan baris Nama di KTP.
//             // "Muh" vs "Muhammad" -> Score rendah -> Gagal.
//             // "Muhamad Satria" vs "Muhammad Satria" -> Score tinggi -> Lolos.
            
//             String inputNama = cleanString(inputData.getFullName());
//             boolean nameMatch = false;
//             double maxScore = 0.0;

//             String[] lines = ocrText.split("\\n");
//             for (String line : lines) {
//                 // Hapus label "NAMA" dan simbol non-huruf
//                 String lineClean = line.replaceAll("NAMA", "").replaceAll("[:]", "").trim();
//                 lineClean = cleanString(lineClean); // Hapus angka/simbol sisa

//                 if (lineClean.length() < 3) continue;

//                 // Hitung Similarity (0.0 - 1.0)
//                 double score = calculateSimilarity(inputNama, lineClean);
//                 if (score > maxScore) maxScore = score;
                
//                 // Threshold 0.80 (80% Mirip)
//                 if (score >= 0.80) {
//                     nameMatch = true;
//                     break;
//                 }
//             }
//             // Debugging
//             System.out.println("Input Nama: " + inputNama + " | Max OCR Score: " + maxScore);
//             result.setNameValid(nameMatch);


//             // --- 2. VALIDASI JENIS KELAMIN (FLEXIBLE) ---
//             // Cari baris mengandung "KELAMIN"
//             boolean genderMatch = false;
//             String targetGender = inputData.getGender().equals("L") ? "LAKI" : "PEREM";
            
//             for (String line : lines) {
//                 if (line.contains("KELAMIN")) {
//                     // Ambil teks setelah kata "KELAMIN"
//                     String value = line.substring(line.indexOf("KELAMIN") + 7).trim().replace(":", "").trim();
                    
//                     // Cek Typo: LAKELAKI, LAKI-LAKI, PEREMPUAN, P3REMPUAN
//                     if (value.startsWith("L") || value.contains("LAKI") || value.contains("LAK1")) {
//                          if (inputData.getGender().equals("L")) genderMatch = true;
//                     }
//                     else if (value.startsWith("P") || value.contains("PEREM") || value.contains("WANITA")) {
//                          if (inputData.getGender().equals("P")) genderMatch = true;
//                     }
//                 }
//             }
//             // Fallback: cari kata kunci di seluruh teks jika baris tidak terdeteksi rapi
//             if (!genderMatch) {
//                 if (inputData.getGender().equals("L") && (ocrText.contains("LAKI") || ocrText.contains("LAK1"))) {
//                     genderMatch = true;
//                 } else if (inputData.getGender().equals("P") && (ocrText.contains("PEREM") || ocrText.contains("WANITA"))) {
//                     genderMatch = true;
//                 }
//             }
//             result.setGenderValid(genderMatch);


//             // --- 3. TEMPAT & TGL LAHIR ---
//             boolean tempatMatch = false;
//             boolean tglMatch = false;
            
//             String inputTempat = cleanString(inputData.getBirthPlace());
//             String inputTgl = "";
//             if (inputData.getBirthDate() != null) {
//                 inputTgl = inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//             }

//             for (String line : lines) {
//                 // Cek Tanggal (Format angka dd-MM-yyyy lebih mudah dideteksi)
//                 if (line.contains(inputTgl) || line.replace(" ", "").contains(inputTgl)) {
//                     tglMatch = true;
                    
//                     // Cek Tempat Lahir di baris yang sama (Fuzzy 80%)
//                     String lineClean = cleanString(line);
//                     if (calculateSimilarity(inputTempat, lineClean) > 0.75 || lineClean.contains(inputTempat)) {
//                         tempatMatch = true;
//                     }
//                 }
//             }
//             result.setBirthDateValid(tglMatch);
//             result.setBirthPlaceValid(tempatMatch);


//         } catch (Exception e) {
//             e.printStackTrace();
//             result.setError("Gagal baca OCR: " + e.getMessage());
//         }
//         return result;
//     }

//     // --- HELPER FUNCTIONS ---

//     // 1. Bersihkan String (Hanya Huruf A-Z)
//     private String cleanString(String text) {
//         if (text == null) return "";
//         return text.toUpperCase().replaceAll("[^A-Z]", "");
//     }

//     // 2. Hitung Kemiripan String (Jaro-Winkler / Levenshtein Normalized)
//     private double calculateSimilarity(String s1, String s2) {
//         if (s1 == null || s2 == null) return 0.0;
//         LevenshteinDistance dist = new LevenshteinDistance();
//         int maxLength = Math.max(s1.length(), s2.length());
//         if (maxLength == 0) return 1.0;
//         int distance = dist.apply(s1, s2);
//         return 1.0 - ((double) distance / maxLength);
//     }

//     // 3. Scale Image (Perbesar)
//     private BufferedImage scaleImage(BufferedImage original, double factor) {
//         int w = (int) (original.getWidth() * factor);
//         int h = (int) (original.getHeight() * factor);
//         BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//         Graphics2D g = scaled.createGraphics();
//         g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//         g.drawImage(original, 0, 0, w, h, null);
//         g.dispose();
//         return scaled;
//     }

//     // 4. Preprocess (Grayscale + Thresholding/Binarization)
//     private BufferedImage processImageForOCR(BufferedImage original) {
//         BufferedImage processed = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//         Graphics2D g = processed.createGraphics();
//         g.drawImage(original, 0, 0, null);
//         g.dispose();

//         // Simple Binarization (Thresholding)
//         // Ubah pixel jadi murni Hitam (0) atau Putih (255)
//         // int width = processed.getWidth();
//         // int height = processed.getHeight();
//         // for (int y = 0; y < height; y++) {
//         //     for (int x = 0; x < width; x++) {
//         //         int rgba = processed.getRGB(x, y);
//         //         Color col = new Color(rgba, true);
//         //         // Jika kecerahan > 130 dianggap putih, sisanya hitam (Teks KTP biasanya hitam/gelap)
//         //         // Note: KTP indo background biru/merah kadang gelap, perlu tuning. 
//         //         // Threshold 160 biasanya aman untuk teks hitam di background berwarna.
//         //         if (col.getRed() + col.getGreen() + col.getBlue() > 380) { 
//         //             processed.setRGB(x, y, Color.WHITE.getRGB());
//         //         } else {
//         //             processed.setRGB(x, y, Color.BLACK.getRGB());
//         //         }
//         //     }
//         // }
//         return processed;
//     }

//     @Data
//     public static class OcrValidationResult {
//         private boolean nameValid;
//         private boolean birthPlaceValid; 
//         private boolean birthDateValid; 
//         private boolean genderValid;
//         private String rawText;
//         private String error;

//         public boolean isValid() {
//             // Semua field ini WAJIB valid
//             return nameValid && birthPlaceValid && birthDateValid && genderValid;
//         }
//     }
// }



// ========================
// kode new
// ========================

// package com.lsptddi.silsp.service;

// import com.lsptddi.silsp.dto.UserProfileDto;
// import lombok.Data;
// import net.sourceforge.tess4j.ITesseract;
// import net.sourceforge.tess4j.Tesseract;
// import net.sourceforge.tess4j.TesseractException;
// import org.apache.commons.text.similarity.LevenshteinDistance;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import javax.imageio.ImageIO;
// import java.awt.*;
// import java.awt.image.BufferedImage;
// import java.awt.image.RescaleOp;
// import java.io.File;
// import java.io.IOException;
// import java.time.format.DateTimeFormatter;

// @Service
// public class OCRService {

//     private static final String TESSDATA_PATH = "tessdata";

//     // --- CORE OCR FUNCTION ---
//     public String extractText(MultipartFile file) throws IOException, TesseractException {
//         File convFile = File.createTempFile("ocr-", ".jpg");
//         file.transferTo(convFile);

//         BufferedImage image = ImageIO.read(convFile);

//         // 1. Scaling: Perbesar gambar 3x lipat
//         BufferedImage scaledImage = scaleImage(image, 3.0);

//         // 2. Grayscale & Contrast Boosting
//         BufferedImage processedImage = toGrayscale(scaledImage);

//         ITesseract instance = new Tesseract();
//         instance.setDatapath(TESSDATA_PATH);
//         instance.setLanguage("ind");

//         // Whitelist: Hanya izinkan huruf, angka, dan simbol KTP dasar
//         instance.setTessVariable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/.-:, '");

//         String result = instance.doOCR(processedImage);
//         convFile.delete();

//         return result.toUpperCase();
//     }

//     // --- LOGIKA VALIDASI UTAMA ---
//     public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto inputData) {
//         OcrValidationResult result = new OcrValidationResult();
//         try {
//             // 1. Ekstrak teks mentah dari gambar
//             String rawOcr = extractText(ktpFile).toUpperCase();
            
//             // Clean baris kosong untuk log yang rapi
//             String ocrText = rawOcr.replaceAll("(?m)^\\s+$", ""); 
//             result.setRawText(ocrText);

//             System.out.println("--- HASIL OCR KTP ---");
//             System.out.println(ocrText);
//             System.out.println("---------------------");

//             // --- A. VALIDASI NAMA (LOGIKA COMPACT STRING) ---
//             // Ubah input nama user menjadi format padat (tanpa spasi/simbol)
//             // Contoh: "Muhammad Satria" -> "MUHAMMADSATRIA"
//             String inputNamaCompact = getCompactString(inputData.getFullName());
            
//             boolean nameMatch = false;
//             double maxScore = 0.0;

//             for (String line : ocrText.split("\\n")) {
//                 // 1. Hapus label "NAMA" dan titik dua ":"
//                 String lineNoLabel = line.replace("NAMA", "").replace(":", "");

//                 // 2. PADATKAN STRING (Hapus semua spasi & simbol aneh)
//                 // Contoh OCR: "S A T R I A" -> "SATRIA"
//                 // Contoh OCR: "BUDI_SANT0SO" -> "BUDISANTOSO" (Angka 0 dibuang jika mode huruf only)
//                 String lineCompact = getCompactString(lineNoLabel);

//                 // 3. Filter Anomali: Jika sisa huruf kurang dari 3, anggap sampah/noise
//                 if (lineCompact.length() < 3) continue;

//                 // 4. Cek Contains (Pencocokan Pasti)
//                 if (lineCompact.contains(inputNamaCompact) || inputNamaCompact.contains(lineCompact)) {
//                     nameMatch = true;
//                     maxScore = 1.0; // Perfect match secara logika
//                     break;
//                 }

//                 // 5. Cek Similarity (Levenshtein pada string padat)
//                 double score = calculateSimilarity(inputNamaCompact, lineCompact);
//                 if (score > maxScore) maxScore = score;
//             }

//             // Ambang batas 80%
//             if (!nameMatch && maxScore >= 0.80) nameMatch = true;

//             System.out.println("Input Compact: " + inputNamaCompact + " | Max Score: " + maxScore);
//             result.setNameValid(nameMatch);


//             // --- B. VALIDASI TEMPAT & TGL LAHIR ---
//             boolean tempatMatch = false;
//             boolean tglMatch = false;
            
//             String inputTempat = inputData.getBirthPlace().toUpperCase();
//             // Format Tanggal: "20-01-2000" atau "20012000" (untuk pencarian fleksibel)
//             String inputTgl = "";
//             String inputTglCompact = ""; 
            
//             if (inputData.getBirthDate() != null) {
//                 inputTgl = inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//                 inputTglCompact = inputTgl.replace("-", ""); // "20012000"
//             }

//             for (String line : ocrText.split("\\n")) {
//                 // Bersihkan spasi di baris ini untuk pencarian tanggal yang terpisah
//                 String lineCompact = line.replaceAll("\\s+", ""); // Hapus semua spasi
                
//                 // Cek Tanggal (Flexible: format pakai strip atau gabung)
//                 if (!inputTgl.isEmpty() && (line.contains(inputTgl) || lineCompact.contains(inputTglCompact))) {
//                      tglMatch = true; 

//                      // Jika tanggal ketemu, cek tempat lahir di baris ASLI (karena tempat lahir butuh spasi kadang)
//                      // Atau cek di baris compact juga bisa
//                      if (line.toUpperCase().contains(inputTempat) || lineCompact.contains(getCompactString(inputTempat))) {
//                          tempatMatch = true;
//                      }
//                 }
//             }
//             // Fallback tempat lahir (global scan)
//             if (!tempatMatch && ocrText.contains(inputTempat)) tempatMatch = true;

//             result.setBirthPlaceValid(tempatMatch);
//             result.setBirthDateValid(tglMatch);


//             // --- C. VALIDASI GENDER (COMPACT CHECK) ---
//             boolean genderMatch = false;
//             // Kita pakai full teks yang dipadatkan (hapus spasi enter dll)
//             String globalTextCompact = getCompactString(ocrText); 

//             if (inputData.getGender().equals("L")) {
//                 // "LAKI-LAKI" -> jadi "LAKILAKI" atau "LAKI"
//                 if (globalTextCompact.contains("LAKI") || globalTextCompact.contains("LAK1")) {
//                     genderMatch = true;
//                 }
//             } else {
//                 // "PEREMPUAN" -> "PEREMPUAN"
//                 if (globalTextCompact.contains("PEREM") || globalTextCompact.contains("WANITA")) {
//                     genderMatch = true;
//                 }
//             }
//             result.setGenderValid(genderMatch);

//         } catch (Exception e) {
//             e.printStackTrace();
//             result.setError("Gagal memproses gambar: " + e.getMessage());
//         }
//         return result;
//     }

//     // --- HELPER METHODS ---

//     /**
//      * FUNGSI KUNCI: Menggabungkan string menjadi 1 kata padat huruf kapital.
//      * Menghapus spasi, angka, dan simbol.
//      * Input: "M. Rizky   Fauzi" -> Output: "MRIZKYFAUZI"
//      * Input: "S A T R I A" -> Output: "SATRIA"
//      */
//     private String getCompactString(String text) {
//         if (text == null) return "";
//         return text.toUpperCase()
//                    .replaceAll("[^A-Z]", ""); // Hapus APA PUN yang bukan huruf A-Z
//     }

//     private double calculateSimilarity(String s1, String s2) {
//         if (s1 == null || s2 == null) return 0.0;
//         LevenshteinDistance dist = new LevenshteinDistance();
//         int maxLength = Math.max(s1.length(), s2.length());
//         if (maxLength == 0) return 1.0;
//         int distance = dist.apply(s1, s2);
//         return 1.0 - ((double) distance / maxLength);
//     }

//     // Pre-processing Gambar
//     private BufferedImage toGrayscale(BufferedImage original) {
//         BufferedImage gray = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//         Graphics g = gray.getGraphics();
//         g.drawImage(original, 0, 0, null);
//         g.dispose();

//         // Contrast Boosting (Untuk Fotokopi/KTP Pudar)
//         try {
//             RescaleOp rescaleOp = new RescaleOp(1.7f, 15.0f, null);
//             rescaleOp.filter(gray, gray);
//         } catch (Exception e) {
//             System.out.println("Gagal boost contrast, fallback ke raw grayscale.");
//         }
//         return gray;
//     }

//     private BufferedImage scaleImage(BufferedImage original, double factor) {
//         int w = (int) (original.getWidth() * factor);
//         int h = (int) (original.getHeight() * factor);
//         BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//         Graphics2D g = scaled.createGraphics();
//         g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//         g.drawImage(original, 0, 0, w, h, null);
//         g.dispose();
//         return scaled;
//     }

//     @Data
//     public static class OcrValidationResult {
//         private boolean nameValid;
//         private boolean birthPlaceValid;
//         private boolean birthDateValid;
//         private boolean genderValid;
//         private String rawText;
//         private String error;
//         public boolean isValid() {
//             return nameValid && birthPlaceValid && birthDateValid && genderValid;
//         }
//     }
// }




// ocr new

// package com.lsptddi.silsp.service;

// import com.lsptddi.silsp.dto.UserProfileDto;
// import lombok.Data;
// import net.sourceforge.tess4j.ITesseract;
// import net.sourceforge.tess4j.Tesseract;
// import net.sourceforge.tess4j.TesseractException;
// import org.apache.commons.text.similarity.LevenshteinDistance;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import javax.imageio.ImageIO;
// import java.awt.*;
// import java.awt.image.BufferedImage;
// import java.io.File;
// import java.io.IOException;
// import java.time.format.DateTimeFormatter;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
// import java.awt.image.RescaleOp;

// @Service
// public class OCRService {

// // Lokasi folder tessdata (Sesuaikan dengan struktur projectmu)
// private static final String TESSDATA_PATH = "tessdata";

// public String extractText(MultipartFile file) throws IOException,
// TesseractException {
// // 1. Konversi MultipartFile ke File temporary
// File convFile = File.createTempFile("ocr-", ".jpg");
// file.transferTo(convFile);

// // 2. Pre-processing Gambar (Agar lebih mudah dibaca Tesseract)
// // Kita ubah jadi Grayscale dan perbesar sedikit
// BufferedImage image = ImageIO.read(convFile);
// BufferedImage scaledImage = scaleImage(image, 3.0);
// // BufferedImage processedImage = preprocessImage(scaledImage);

// // 2. Convert ke Grayscale
// BufferedImage grayImage = toGrayscale(scaledImage);

// // 3. Contrast Boosting (Penting untuk Fotokopi Pudar)
// // Menerangkan yang terang, menggelapkan yang gelap sebelum binarisasi
// // BufferedImage highContrastImage = boostContrast(grayImage);

// // 4. Binarization (Hitam Putih) dengan OTSU'S METHOD (Dynamic Threshold)
// // Ini kunci agar support KTP Warna & Fotokopi sekaligus
// // BufferedImage processedImage = applyOtsuBinarization(grayImage);
// // 3. Setup Tesseract
// ITesseract instance = new Tesseract();
// instance.setDatapath(TESSDATA_PATH);
// instance.setLanguage("ind"); // Pakai bahasa Indonesia

// // Izinkan A-Z, 0-9, spasi, dan simbol umum KTP
// instance.setTessVariable("tessedit_char_whitelist",
// "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/.-:, '");

// // 4. Eksekusi OCR
// String result = instance.doOCR(grayImage);

// // Bersihkan file temp
// convFile.delete();

// return result.toUpperCase(); // Biar mudah dibandingin

// }

// // Algoritma Validasi
// public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto
// inputData) {
// OcrValidationResult result = new OcrValidationResult();
// try {
// // String ocrText = extractText(ktpFile).toUpperCase();
// // Bersihkan simbol aneh agar pencarian lebih akurat
// // ocrText = ocrText.replaceAll("[^A-Z0-9\\s\\:\\/\\-\\.]", "");

// // result.setRawText(ocrText);
// String rawOcr = extractText(ktpFile).toUpperCase();

// // CLEANING EKSTRA: Hapus baris kosong & karakter sampah di awal/akhir
// String ocrText = rawOcr.replaceAll("(?m)^\\s+$", "");

// // 1. Ekstrak Teks
// // String ocrText = extractText(ktpFile);
// // result.setRawText(ocrText);

// System.out.println("--- HASIL OCR KTP ---");
// System.out.println(ocrText);
// System.out.println("---------------------");

// // LevenshteinDistance dist = new LevenshteinDistance();
// String inputNama =
// cleanNameForComparison(inputData.getFullName().toUpperCase());
// // String inputNama = inputData.getFullName();

// boolean nameMatch = false;
// double maxScore = 0.0;

// // Coba cari per baris (lebih akurat daripada full text)
// for (String line : ocrText.split("\\n")) {
// // Hapus label "NAMA" dan simbol
// String cleanLine = line.replace("NAMA", "").replace(":", "").trim();
// // if (cleanLine.isEmpty())
// // continue;

// // Bersihkan anomali karakter di baris nama (Hapus angka/simbol aneh)
// // Contoh: "SATR1A" -> "SATRA", "BUDI_ANT0" -> "BUDI ANTO"
// String cleanLineFinal = cleanNameForComparison(cleanLine);

// if (cleanLineFinal.length() < 3)
// continue;

// // Cek contains juga (misal: "MUHAMMAD SATRIA ARROZAK" vs "SATRIA ARROZAK")
// if (cleanLineFinal.contains(inputNama) || inputNama.contains(cleanLineFinal))
// {
// nameMatch = true;
// maxScore = 1.0; // Perfect match secara logika
// break;
// }

// // Hitung Similarity (0.0 - 1.0)
// double score = calculateSimilarity(inputNama, cleanLineFinal);
// if (score > maxScore) // Ambang batas 90% untuk nama
// maxScore = score;

// }

// // Threshold kemiripan minimal 0.8 (80%)
// if (!nameMatch && maxScore >= 0.99)
// nameMatch = true;

// System.out.println("Input Nama: " + inputNama + " | Max OCR Score: " +
// maxScore);
// result.setNameValid(nameMatch);

// // 3. TEMPAT LAHIR & TGL LAHIR
// // Format KTP: "BEKASI, 20-01-2000"
// boolean tempatMatch = false;
// boolean tglMatch = false;

// String inputTempat = inputData.getBirthPlace().toUpperCase();
// String inputTgl = "";
// if (inputData.getBirthDate() != null) {
// // Ubah YYYY-MM-DD (Input) jadi dd-MM-yyyy (KTP)
// inputTgl =
// inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
// }

// // Cari baris yang mengandung tanggal lahir
// for (String line : ocrText.split("\\n")) {
// if (line.contains(inputTgl)) {
// tglMatch = true; // Tanggal ketemu di baris ini

// // Cek tempat lahir di baris yang SAMA (sebelum tanda koma)
// if (line.toUpperCase().contains(inputTempat)) {
// tempatMatch = true;
// }
// }
// }
// // Fallback: cari tempat lahir di seluruh teks jika format baris hancur
// if (!tempatMatch && ocrText.contains(inputTempat))
// tempatMatch = true;

// result.setBirthPlaceValid(tempatMatch);
// result.setBirthDateValid(tglMatch);

// // --- 3. JENIS KELAMIN (FLEXIBLE) ---
// boolean genderMatch = false;
// String cleanText = cleanString(ocrText); // Hapus spasi/simbol untuk
// deteksiglobal

// if (inputData.getGender().equals("L")) {
// // Laki-Laki sering terbaca: LAKI, LAK1, LAKL, LAK
// if (cleanText.contains("LAKI") || cleanText.contains("LAK1") ||
// cleanText.contains("LAKL")) {
// genderMatch = true;
// }
// } else if (inputData.getGender().equals("P")) {
// // Perempuan: PEREM, P3REM, WANITA
// if (cleanText.contains("PEREM") || cleanText.contains("P3REM") ||
// cleanText.contains("WANITA")) {
// genderMatch = true;
// }
// }
// result.setGenderValid(genderMatch);

// } catch (Exception e) {
// e.printStackTrace();
// result.setError("Gagal baca OCR: " + e.getMessage());
// }
// return result;
// // }
// }

// private String cleanString(String text) {
// if (text == null)
// return "";
// return text.toUpperCase().replaceAll("[^A-Z0-9]", "");
// }

// // Helper: Image Pre-processing sederhana
// private BufferedImage toGrayscale(BufferedImage original) {
// // Ubah ke Grayscale
// BufferedImage gray = new BufferedImage(original.getWidth(),
// original.getHeight(),
// BufferedImage.TYPE_BYTE_GRAY);
// Graphics g = gray.getGraphics();
// g.drawImage(original, 0, 0, null);
// g.dispose();

// try {
// RescaleOp rescaleOp = new RescaleOp(1.7f, 15.0f, null);
// rescaleOp.filter(gray, gray); // Apply filter langsung ke image gray
// } catch (Exception e) {
// // Fallback jika gagal filter (jarang terjadi), kembalikan raw grayscale
// System.out.println("Gagal boost contrast, menggunakan raw grayscale.");
// }

// return gray;
// }

// // --- HELPER METHODS ---

// // Membersihkan Nama dari angka/simbol aneh agar perbandingan fair
// private String cleanNameForComparison(String text) {
// if (text == null)
// return "";
// // Hanya sisakan Huruf A-Z dan Spasi. Angka di Nama dianggap sampah OCR.
// // Ganti titik (.) dengan spasi jika ada gelar
// return text.toUpperCase()
// .replace(".", " ")
// .replaceAll("[^A-Z0-9\\s]", "") // Hapus angka & simbol
// .replaceAll("\\s+", " ") // Hapus spasi ganda
// .trim();
// }

// // --- HELPER FUNCTIONS ---

// // // 2. Hitung Kemiripan String (Jaro-Winkler / Levenshtein Normalized)
// private double calculateSimilarity(String s1, String s2) {
// if (s1 == null || s2 == null)
// return 0.0;
// LevenshteinDistance dist = new LevenshteinDistance();
// int maxLength = Math.max(s1.length(), s2.length());
// if (maxLength == 0)
// return 1.0;
// int distance = dist.apply(s1, s2);
// return 1.0 - ((double) distance / maxLength);
// }

// // 3. Scale Image (Perbesar)
// private BufferedImage scaleImage(BufferedImage original, double factor) {
// int w = (int) (original.getWidth() * factor);
// int h = (int) (original.getHeight() * factor);
// BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
// Graphics2D g = scaled.createGraphics();
// g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
// RenderingHints.VALUE_INTERPOLATION_BICUBIC);
// g.drawImage(original, 0, 0, w, h, null);
// g.dispose();
// return scaled;
// }

// @Data
// public static class OcrValidationResult {
// // private boolean nikValid;
// private boolean nameValid;
// private boolean birthPlaceValid; // Baru
// private boolean birthDateValid; // Baru
// private boolean genderValid;
// private String rawText;
// private String error;

// public boolean isValid() {
// // Semua harus valid
// return nameValid && birthPlaceValid && birthDateValid && genderValid;
// }
// }

// }





 // Membersihkan Nama dari angka/simbol aneh agar perbandingan fair
    // private String getCompactString(String text) {
    // if (text == null)
    // return "";
    // // Hanya sisakan Huruf A-Z dan Spasi. Angka di Nama dianggap sampah OCR.
    // // Ganti titik (.) dengan spasi jika ada gelar
    // return text.toUpperCase()
    // .replace(".", " ")
    // .replaceAll("[^A-Z0-9\\s]", "") // Hapus angka & simbol
    // .replaceAll("\\s+", " ") // Hapus spasi ganda
    // .trim(); // Jangan trim karena spasi di awal/akhir bisa jadi penting untuk
    // deteksi contains
    // }