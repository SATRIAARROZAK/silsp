package com.lsptddi.silsp.dto;

import lombok.Data;
import java.util.List;

@Data
public class PermohonanDto {
    // Tab 1
    private Long skemaId;
    private Long jadwalId;
    
    // Tab 2 (Update Profil User)
    private UserProfileDto dataPemohon;

    // Tab 6
    private String tujuanAsesmen;

    // Tab 3, 4, 5 (List File Upload - Base64 atau ID Temp jika upload terpisah)
    // Disini kita asumsikan FE mengirim data mapping file yang sudah diupload
    // atau FE mengirim Metadata file.
    // Untuk simplifikasi Full Stack di satu request, kita gunakan struktur ini:
    
    private List<BuktiDto> listBukti;       // Gabungan Persyaratan, Admin, Portofolio
    private List<AsesmenDto> listAsesmen;   // Tab 7

    @Data
    public static class BuktiDto {
        private String jenis; // PERSYARATAN, ADMINISTRASI, PORTOFOLIO
        private String nama;  // Nama Dokumen
        private String tempId; // ID referensi dari Frontend (untuk mapping APL-02)
        // Note: File fisik akan diambil dari MultipartRequest di Controller
    }

    @Data
    public static class AsesmenDto {
        private String kodeUnit;
        private String noElemen;
        private String status; // K atau BK
        private List<String> buktiRefIds; // List tempId bukti yang relevan
    }
}