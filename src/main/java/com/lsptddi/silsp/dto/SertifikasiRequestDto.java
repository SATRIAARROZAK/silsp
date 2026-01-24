package com.lsptddi.silsp.dto;

import lombok.Data;
import java.util.List;

@Data
public class SertifikasiRequestDto {
    // Tab 1 & 6 (Info Skema)
    private Long skemaId;
    private Long jadwalId;
    private Long sumberAnggaranId;
    private Long pemberiAnggaranId;
    private String tujuanAsesmen;

    // Tab 2 (Profil Asesi - Update)
    private UserProfileDto dataPemohon;

    // Tab 7 (APL-02 / Asesmen Mandiri)
    private List<Apl02ItemDto> asesmenMandiri;

    @Data
    public static class Apl02ItemDto {
        private Long elemenId; // ID Elemen (bukan KUK)
        private String status; // "K" atau "BK"
        private List<String> buktiIds; // List ID sementara file (misal: "portofolio_1")
    }
}