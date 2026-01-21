package com.lsptddi.silsp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    // Folder penyimpanan file (Pastikan folder ini ada atau akan dibuat otomatis)
    private final Path rootLocation = Paths.get("uploads/bukti");

    public FileStorageService() {
        try {
            // Buat folder jika belum ada
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Gagal membuat direktori upload!", e);
        }
    }

    public String save(MultipartFile file) {
        try {
            // 1. Cek jika file kosong
            if (file.isEmpty()) {
                throw new RuntimeException("Gagal menyimpan file kosong.");
            }

            // 2. Generate nama file unik agar tidak bentrok (pakai UUID)
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // 3. Simpan file ke folder
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(newFilename)).normalize().toAbsolutePath();

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // 4. Kembalikan nama file untuk disimpan di database
            return newFilename;

        } catch (IOException e) {
            throw new RuntimeException("Gagal menyimpan file.", e);
        }
    }
}