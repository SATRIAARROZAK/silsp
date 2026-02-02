package com.lsptddi.silsp.service;

import com.lsptddi.silsp.dto.UserProfileDto;
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
            // 1. Ekstrak Teks
            String ocrText = extractText(ktpFile);
            result.setRawText(ocrText);
            
            System.out.println("--- HASIL OCR KTP ---");
            System.out.println(ocrText);
            System.out.println("---------------------");

            // 2. Validasi NIK (Cari 16 digit angka)
            // Menggunakan Regex yang mentolerir kesalahan baca (misal 'B' jadi '8' jika perlu, tapi kita strict dulu)
            String nikRegex = "\\b\\d{16}\\b"; 
            Pattern patternNik = Pattern.compile(nikRegex);
            Matcher matcherNik = patternNik.matcher(ocrText);
            
            boolean nikFound = false;
            while (matcherNik.find()) {
                String foundNik = matcherNik.group();
                if (foundNik.equals(inputData.getNik())) {
                    nikFound = true;
                    break;
                }
            }
            result.setNikValid(nikFound);

            // 3. Validasi Nama (Fuzzy Logic)
            // Cari baris yang mengandung "NAMA"
            LevenshteinDistance distance = new LevenshteinDistance();
            boolean nameMatch = false;
            
            // Pecah per baris untuk cari Nama
            String[] lines = ocrText.split("\\r?\\n");
            for (String line : lines) {
                if (line.contains("NAMA")) {
                    String cleanLine = line.replace("NAMA", "").replace(":", "").trim();
                    // Hitung kemiripan
                    int dist = distance.apply(cleanLine, inputData.getFullName().toUpperCase());
                    // Toleransi: Jika perbedaan karakter <= 3, anggap valid
                    if (dist <= 3) { 
                        nameMatch = true;
                    }
                }
            }
            // Fallback: Jika tidak ada label "NAMA", cari string yang mirip banget di seluruh teks
            if (!nameMatch && ocrText.contains(inputData.getFullName().toUpperCase())) {
                nameMatch = true;
            }
            result.setNameValid(nameMatch);

            // 4. Validasi Tanggal Lahir
            // Format Input: YYYY-MM-DD
            // Format KTP: DD-MM-YYYY
            if (inputData.getBirthDate() != null) {
                String tglLahirInput = inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                if (ocrText.contains(tglLahirInput)) {
                    result.setDobValid(true);
                }
            }

            // 5. Validasi Jenis Kelamin
            String genderInput = inputData.getGender().equals("L") ? "LAKI-LAKI" : "PEREMPUAN";
            if (ocrText.contains(genderInput)) {
                result.setGenderValid(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.setError("Gagal memproses OCR: " + e.getMessage());
        }
        return result;
    }

    // Helper: Image Pre-processing sederhana
    private BufferedImage preprocessImage(BufferedImage original) {
        // Ubah ke Grayscale
        BufferedImage grayscale = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = grayscale.getGraphics();
        g.drawImage(original, 0, 0, null);
        g.dispose();
        return grayscale;
    }
    
    // Inner Class untuk Hasil
    public static class OcrValidationResult {
        private boolean nikValid;
        private boolean nameValid;
        private boolean dobValid;
        private boolean genderValid;
        private String rawText;
        private String error;

        public boolean isValid() {
            // Valid jika NIK Cocok DAN Nama Cocok (Minimal)
            return nikValid && nameValid;
        }

        // Getter Setter
        public boolean isNikValid() { return nikValid; }
        public void setNikValid(boolean nikValid) { this.nikValid = nikValid; }
        public boolean isNameValid() { return nameValid; }
        public void setNameValid(boolean nameValid) { this.nameValid = nameValid; }
        public boolean isDobValid() { return dobValid; }
        public void setDobValid(boolean dobValid) { this.dobValid = dobValid; }
        public boolean isGenderValid() { return genderValid; }
        public void setGenderValid(boolean genderValid) { this.genderValid = genderValid; }
        public String getRawText() { return rawText; }
        public void setRawText(String rawText) { this.rawText = rawText; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}