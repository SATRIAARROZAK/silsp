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
import java.awt.image.RescaleOp;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class OCRService {

    private static final String TESSDATA_PATH = "tessdata";

    // --- CORE OCR FUNCTION ---
    // public String extractText(MultipartFile file) throws IOException,
    // TesseractException {
    // File convFile = File.createTempFile("ocr-", ".jpg");
    // file.transferTo(convFile);

    // BufferedImage image = ImageIO.read(convFile);
    public String extractText(MultipartFile file) throws IOException, TesseractException {
        // PERBAIKAN: Langsung baca dari InputStream tanpa transferTo()
        // Ini tidak akan merusak file temporary Tomcat, sehingga bisa disimpan ke DB
        // nantinya.
        BufferedImage image;
        try (InputStream inputStream = file.getInputStream()) {
            image = ImageIO.read(inputStream);
        }

        if (image == null) {
            throw new IOException("File tidak dapat dibaca sebagai gambar yang valid.");
        }
        // 1. Scaling: Perbesar gambar 3x lipat
        BufferedImage scaledImage = scaleImage(image, 3.0);

        // 2. Grayscale & Contrast Boosting
        BufferedImage processedImage = toGrayscale(scaledImage);

        ITesseract instance = new Tesseract();
        instance.setDatapath(TESSDATA_PATH);
        instance.setLanguage("ind");

        // Whitelist: Hanya izinkan huruf, angka, dan simbol KTP dasar
        instance.setTessVariable("tessedit_char_whitelist", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789/.-:, '");

        String result = instance.doOCR(processedImage);
        // convFile.delete();

        return result.toUpperCase();
    }

    // --- LOGIKA VALIDASI UTAMA ---
    public OcrValidationResult validateKtp(MultipartFile ktpFile, UserProfileDto inputData) {
        OcrValidationResult result = new OcrValidationResult();
        try {
            // 1. Ekstrak teks mentah dari gambar
            String rawOcr = extractText(ktpFile).toUpperCase();

            // Clean baris kosong untuk log yang rapi
            String ocrText = rawOcr.replaceAll("(?m)^\\s+$", "");
            result.setRawText(ocrText);

            System.out.println("--- HASIL OCR KTP ---");
            System.out.println(ocrText);
            System.out.println("---------------------");

            // --- A. VALIDASI NAMA (LOGIKA COMPACT STRING) ---
            // Ubah input nama user menjadi format padat (tanpa spasi/simbol)
            // Contoh: "Muhammad Satria" -> "MUHAMMADSATRIA"
            String inputNamaCompact = getCompactString(inputData.getFullName());

            boolean nameMatch = false;
            double maxScore = 0.0;

            for (String line : ocrText.split("\\n")) {
                // 1. Hapus label "NAMA" dan titik dua ":"
                String lineNoLabel = line.replace("NAMA", "").replace(":", "").trim();

                // 2. PADATKAN STRING (Hapus semua spasi & simbol aneh)
                // Contoh OCR: "S A T R I A" -> "SATRIA"
                // Contoh OCR: "BUDI_SANT0SO" -> "BUDISANTOSO" (Angka 0 dibuang jika mode huruf
                // only)
                String lineCompact = getCompactString(lineNoLabel);

                // 3. Filter Anomali: Jika sisa huruf kurang dari 3, anggap sampah/noise
                if (lineCompact.length() < 3)
                    continue;

                // 4. Cek Contains (Pencocokan Pasti)
                if (lineCompact.contains(inputNamaCompact) || inputNamaCompact.contains(lineCompact)) {
                    nameMatch = true;
                    maxScore = 1.0; // Perfect match secara logika
                    break;
                }

                // 5. Cek Similarity (Levenshtein pada string padat)
                double score = calculateSimilarity(inputNamaCompact, lineCompact);
                if (score > maxScore)
                    maxScore = score;
            }

            // Ambang batas 99%
            if (!nameMatch && maxScore >= 0.99)
                nameMatch = true;

            System.out.println("Input Compact: " + inputNamaCompact + " | Max Score: " + maxScore);
            result.setNameValid(nameMatch);

            // --- B. VALIDASI TEMPAT & TGL LAHIR ---
            boolean tempatMatch = false;
            boolean tglMatch = false;

            String inputTempat = inputData.getBirthPlace().toUpperCase();
            // Format Tanggal: "20-01-2000" atau "20012000" (untuk pencarian fleksibel)
            String inputTgl = "";
            String inputTglCompact = "";

            if (inputData.getBirthDate() != null) {
                inputTgl = inputData.getBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                inputTglCompact = inputTgl.replace("-", ""); // "20012000"
            }

            for (String line : ocrText.split("\\n")) {
                // Bersihkan spasi di baris ini untuk pencarian tanggal yang terpisah
                String lineCompact = line.replaceAll("\\s+", ""); // Hapus semua spasi

                // Cek Tanggal (Flexible: format pakai strip atau gabung)
                if (!inputTgl.isEmpty() && (line.contains(inputTgl) || lineCompact.contains(inputTglCompact))) {
                    tglMatch = true;

                    // Jika tanggal ketemu, cek tempat lahir di baris ASLI (karena tempat lahir
                    // butuh spasi kadang)
                    // Atau cek di baris compact juga bisa
                    if (line.toUpperCase().contains(inputTempat)
                            || lineCompact.contains(getCompactString(inputTempat))) {
                        tempatMatch = true;
                    }
                }
            }
            // Fallback tempat lahir (global scan)
            if (!tempatMatch && ocrText.contains(inputTempat))
                tempatMatch = true;

            result.setBirthPlaceValid(tempatMatch);
            result.setBirthDateValid(tglMatch);

            // --- 3. JENIS KELAMIN (FLEXIBLE) ---
            boolean genderMatch = false;
            String cleanText = cleanString(ocrText); // Hapus spasi/simbol untuk deteksi global

            if (inputData.getGender().equals("L")) {
                // Laki-Laki sering terbaca: LAKI, LAK1, LAKL, LAK
                if (cleanText.contains("LAKI") || cleanText.contains("LAK1") || cleanText.contains("LAKL")
                        || cleanText.contains("LAK")) {
                    genderMatch = true;
                }
            } else if (inputData.getGender().equals("P")) {
                // Perempuan: PEREM, P3REM, WANITA
                if (cleanText.contains("PEREM") || cleanText.contains("P3REM") || cleanText.contains("WANITA")) {
                    genderMatch = true;
                }
            }
            result.setGenderValid(genderMatch);

        } catch (Exception e) {
            e.printStackTrace();
            result.setError("Gagal baca OCR: " + e.getMessage());
        }
        return result;
        // }
    }

    private String cleanString(String text) {
        if (text == null)
            return "";
        return text.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }

    // --- HELPER METHODS ---

    private String getCompactString(String text) {
        if (text == null)
            return "";
        return text.toUpperCase()
                .replaceAll("[^A-Z0-9]", ""); // Hapus APA PUN yang bukan huruf A-Z
    }

   

    private double calculateSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null)
            return 0.0;
        LevenshteinDistance dist = new LevenshteinDistance();
        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0)
            return 1.0;
        int distance = dist.apply(s1, s2);
        return 1.0 - ((double) distance / maxLength);
    }

    // Pre-processing Gambar
    private BufferedImage toGrayscale(BufferedImage original) {
        BufferedImage gray = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = gray.getGraphics();
        g.drawImage(original, 0, 0, null);
        g.dispose();

        // Contrast Boosting (Untuk Fotokopi/KTP Pudar)
        try {
            RescaleOp rescaleOp = new RescaleOp(1.7f, 15.0f, null);
            rescaleOp.filter(gray, gray);
        } catch (Exception e) {
            System.out.println("Gagal boost contrast, fallback ke raw grayscale.");
        }
        return gray;
    }

    private BufferedImage scaleImage(BufferedImage original, double factor) {
        int w = (int) (original.getWidth() * factor);
        int h = (int) (original.getHeight() * factor);
        BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(original, 0, 0, w, h, null);
        g.dispose();
        return scaled;
    }

    @Data
    public static class OcrValidationResult {
        private boolean nameValid;
        private boolean birthPlaceValid;
        private boolean birthDateValid;
        private boolean genderValid;
        private String rawText;
        private String error;

        public boolean isValid() {
            return nameValid && birthPlaceValid && birthDateValid && genderValid;
        }
    }
}
