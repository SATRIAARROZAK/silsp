package com.lsptddi.silsp.service;

import com.lsptddi.silsp.dto.UserProfileDto;

import lombok.Data;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OCRService {

    // Lokasi folder tessdata (Sesuaikan dengan struktur projectmu)
    private static final String TESSDATA_PATH = "tessdata";

    public String extractText(MultipartFile file) throws IOException, TesseractException {
        // 1. Konversi MultipartFile ke File temporary
        File convFile = File.createTempFile("ocr-", ".jpg");
        file.transferTo(convFile);

        // 2. Pre-processing Gambar (Agar lebih mudah dibaca Tesseract)
        // Kita ubah jadi Grayscale dan perbesar sedikit
        BufferedImage image = ImageIO.read(convFile);
        BufferedImage processedImage = preprocessImage(image);

        // 3. Setup Tesseract
        ITesseract instance = new Tesseract();
        instance.setDatapath(TESSDATA_PATH);
        instance.setLanguage("ind"); // Pakai bahasa Indonesia

        // 4. Eksekusi OCR
        String result = instance.doOCR(processedImage);

        // Bersihkan file temp
        convFile.delete();

        return result.toUpperCase(); // Biar mudah dibandingin
    }

    // Algoritma Validasi
    public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto inputData) {
        OcrValidationResult result = new OcrValidationResult();
        try {
            String ocrText = extractText(ktpFile).toUpperCase();
            // Bersihkan simbol aneh agar pencarian lebih akurat
            ocrText = ocrText.replaceAll("[^A-Z0-9\\s\\:\\/\\-\\.]", "");

            result.setRawText(ocrText);

            // 1. Ekstrak Teks
            // String ocrText = extractText(ktpFile);
            // result.setRawText(ocrText);

            System.out.println("--- HASIL OCR KTP ---");
            System.out.println(ocrText);
            System.out.println("---------------------");

            // 1. VALIDASI NIK (Strict)
            // Cari 16 digit angka
            // Pattern nikPattern = Pattern.compile("\\b\\d{16}\\b");
            // Matcher nikMatcher = nikPattern.matcher(ocrText);
            // boolean nikMatch = false;
            // while (nikMatcher.find()) {
            //     if (nikMatcher.group().equals(inputData.getNik())) {
            //         nikMatch = true;
            //         break;
            //     }
            // }
            // result.setNikValid(nikMatch);
            // 2. VALIDASI NAMA (Fuzzy)
            // LevenshteinDistance levenshtein = new LevenshteinDistance();
            // boolean nameMatch = false;
            // // Cari baris yang mengandung "NAMA"
            // String[] lines = ocrText.split("\\r?\\n");
            // for(String line : lines) {
            // if(line.contains("NAMA")) {
            // String nameLine = line.replace("NAMA", "").replace(":", "").trim();
            // int distance = levenshtein.apply(nameLine,
            // inputData.getFullName().toUpperCase());
            // // Toleransi jarak <= 3
            // if(distance <= 3) {
            // nameMatch = true;
            // }
            // }
            // }
            // // Fallback: Cek keseluruhan teks
            // if(!nameMatch && ocrText.contains(inputData.getFullName().toUpperCase())) {
            // nameMatch = true;
            // }
            // result.setNameValid(nameMatch);

            LevenshteinDistance dist = new LevenshteinDistance();
            String inputNama = inputData.getFullName().toUpperCase();
            boolean namaMatch = false;

            // Coba cari per baris (lebih akurat daripada full text)
            for (String line : ocrText.split("\\n")) {
                // Hapus label "NAMA" dan simbol
                String cleanLine = line.replace("NAMA", "").replace(":", "").trim();
                if (cleanLine.isEmpty())
                    continue;

                // Jika input nama pendek (< 5 char), harus exact match. Jika panjang, toleransi
                // 3 typo.
                int threshold = inputNama.length() < 5 ? 0 : 3;

                if (dist.apply(cleanLine, inputNama) <= threshold) {
                    namaMatch = true;
                    break;
                }
                // Cek contains juga (misal: "MUHAMMAD SATRIA ARROZAK" vs "SATRIA ARROZAK")
                if (cleanLine.contains(inputNama) || inputNama.contains(cleanLine)) {
                    namaMatch = true;
                    break;
                }
            }
            result.setNameValid(namaMatch);

            // 3. VALIDASI TANGGAL LAHIR
            // if(inputData.getBirthDate() != null) {
            // String dobInput =
            // inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            // if(ocrText.contains(dobInput)) {
            // result.setDobValid(true);
            // }
            // }

            // 3. TEMPAT LAHIR & TGL LAHIR
            // Format KTP: "BEKASI, 20-01-2000"
            boolean tempatMatch = false;
            boolean tglMatch = false;

            String inputTempat = inputData.getBirthPlace().toUpperCase();
            String inputTgl = "";
            if (inputData.getBirthDate() != null) {
                // Ubah YYYY-MM-DD (Input) jadi dd-MM-yyyy (KTP)
                inputTgl = inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            }

            // Cari baris yang mengandung tanggal lahir
            for (String line : ocrText.split("\\n")) {
                if (line.contains(inputTgl)) {
                    tglMatch = true; // Tanggal ketemu di baris ini

                    // Cek tempat lahir di baris yang SAMA (sebelum tanda koma)
                    if (line.toUpperCase().contains(inputTempat)) {
                        tempatMatch = true;
                    }
                }
            }
            // Fallback: cari tempat lahir di seluruh teks jika format baris hancur
            if (!tempatMatch && ocrText.contains(inputTempat))
                tempatMatch = true;

            result.setBirthPlaceValid(tempatMatch);
            result.setBirthDateValid(tglMatch);

            // 4. VALIDASI JENIS KELAMIN
            // String genderInput = inputData.getGender().equals("L") ? "LAKI-LAKI" :
            // "PEREMPUAN";
            // if(ocrText.contains(genderInput)) {
            // result.setGenderValid(true);
            // }
            // } catch (Exception e) {
            // e.printStackTrace();
            // result.setError("Gagal memproses OCR: " + e.getMessage());
            // }
            // return result;

            // 4. JENIS KELAMIN
            String genderKtp = inputData.getGender().equals("L") ? "LAKI-LAKI" : "PEREMPUAN";
            result.setGenderValid(ocrText.contains(genderKtp));

        } catch (Exception e) {
            e.printStackTrace();
            result.setError("Gagal baca OCR: " + e.getMessage());
        }
        return result;
        // }
    }

    // public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto
    // inputData) {
    // OcrValidationResult result = new OcrValidationResult();
    // try {
    // // 1. Ekstrak Teks
    // String ocrText = extractText(ktpFile);
    // result.setRawText(ocrText);

    // System.out.println("--- HASIL OCR KTP ---");
    // System.out.println(ocrText);
    // System.out.println("---------------------");

    // // 2. Validasi NIK (Cari 16 digit angka)
    // // Menggunakan Regex yang mentolerir kesalahan baca (misal 'B' jadi '8' jika
    // // perlu, tapi kita strict dulu)
    // String nikRegex = "\\b\\d{16}\\b";
    // Pattern patternNik = Pattern.compile(nikRegex);
    // Matcher matcherNik = patternNik.matcher(ocrText);

    // boolean nikFound = false;
    // while (matcherNik.find()) {
    // String foundNik = matcherNik.group();
    // if (foundNik.equals(inputData.getNik())) {
    // nikFound = true;
    // break;
    // }
    // }
    // result.setNikValid(nikFound);

    // // 3. Validasi Nama (Fuzzy Logic)
    // // Cari baris yang mengandung "NAMA"
    // LevenshteinDistance distance = new LevenshteinDistance();
    // boolean nameMatch = false;

    // // Pecah per baris untuk cari Nama
    // String[] lines = ocrText.split("\\r?\\n");
    // for (String line : lines) {
    // if (line.contains("NAMA")) {
    // String cleanLine = line.replace("NAMA", "").replace(":", "").trim();
    // // Hitung kemiripan
    // int dist = distance.apply(cleanLine, inputData.getFullName().toUpperCase());
    // // Toleransi: Jika perbedaan karakter <= 3, anggap valid
    // if (dist <= 3) {
    // nameMatch = true;
    // }
    // }
    // }
    // // Fallback: Jika tidak ada label "NAMA", cari string yang mirip banget di
    // // seluruh teks
    // if (!nameMatch && ocrText.contains(inputData.getFullName().toUpperCase())) {
    // nameMatch = true;
    // }
    // result.setNameValid(nameMatch);

    // // 4. Validasi Tanggal Lahir
    // // Format Input: YYYY-MM-DD
    // // Format KTP: DD-MM-YYYY
    // if (inputData.getBirthDate() != null) {
    // String tglLahirInput =
    // inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    // if (ocrText.contains(tglLahirInput)) {
    // result.setDobValid(true);
    // }
    // }

    // // 5. Validasi Jenis Kelamin
    // String genderInput = inputData.getGender().equals("L") ? "LAKI-LAKI" :
    // "PEREMPUAN";
    // if (ocrText.contains(genderInput)) {
    // result.setGenderValid(true);
    // }

    // } catch (Exception e) {
    // e.printStackTrace();
    // result.setError("Gagal memproses OCR: " + e.getMessage());
    // }
    // return result;
    // }

    // Helper: Image Pre-processing sederhana
    private BufferedImage preprocessImage(BufferedImage original) {
        // Ubah ke Grayscale
        BufferedImage grayscale = new BufferedImage(original.getWidth(), original.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayscale.getGraphics();
        g.drawImage(original, 0, 0, null);
        g.dispose();
        return grayscale;
    }

    @Data
    public static class OcrValidationResult {
        // private boolean nikValid;
        private boolean nameValid;
        private boolean birthPlaceValid; // Baru
        private boolean birthDateValid; // Baru
        private boolean genderValid;
        private String rawText;
        private String error;

        public boolean isValid() {
            // Semua harus valid
            return nameValid && birthPlaceValid && birthDateValid && genderValid;
        }
    }
    // Inner Class untuk Hasil
    // public static class OcrValidationResult {
    // private boolean nikValid;
    // private boolean nameValid;
    // private boolean dobValid;
    // private boolean genderValid;
    // private String rawText;
    // private String error;

    // public boolean isValid() {
    // // Valid jika NIK Cocok DAN Nama Cocok (Minimal)
    // return nikValid && nameValid;
    // }

    // // Getter Setter
    // public boolean isNikValid() {
    // return nikValid;
    // }

    // public void setNikValid(boolean nikValid) {
    // this.nikValid = nikValid;
    // }

    // public boolean isNameValid() {
    // return nameValid;
    // }

    // public void setNameValid(boolean nameValid) {
    // this.nameValid = nameValid;
    // }

    // public boolean isDobValid() {
    // return dobValid;
    // }

    // public void setDobValid(boolean dobValid) {
    // this.dobValid = dobValid;
    // }

    // public boolean isGenderValid() {
    // return genderValid;
    // }

    // public void setGenderValid(boolean genderValid) {
    // this.genderValid = genderValid;
    // }

    // public String getRawText() {
    // return rawText;
    // }

    // public void setRawText(String rawText) {
    // this.rawText = rawText;
    // }

    // public String getError() {
    // return error;
    // }

    // public void setError(String error) {
    // this.error = error;
    // }
    // }
}