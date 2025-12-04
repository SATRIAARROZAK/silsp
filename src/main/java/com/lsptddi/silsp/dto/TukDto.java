package com.lsptddi.silsp.dto;
import lombok.Data;

@Data
public class TukDto {
    private Long id; // Tambahkan ini
    private String namaTuk;
    private String kodeTuk;
    // Kode TUK digenerate di backend, tidak perlu dikirim dari form (atau bisa hidden)
    private Long jenisTukId; // ID RefTukType
    
    private String noTelp;
    private String email;
    private String alamat;
    
    private String provinceId;
    private String cityId;
}