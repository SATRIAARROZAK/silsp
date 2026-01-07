package com.lsptddi.silsp.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class SuratTugasDto {
    // Input Manual dari Form
    private String nomorSurat; // Contoh: 01/ST/LSPTDDI/V/2025
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate tanggalSurat;

    // Data Pilihan (Dropdown)
    private Long jadwalId; // Dari jadwal kita bisa dapat Skema & TUK
    private Long asesorId; // Dari user asesor
}