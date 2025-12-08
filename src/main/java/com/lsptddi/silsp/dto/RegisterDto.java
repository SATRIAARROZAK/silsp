package com.lsptddi.silsp.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@Data
public class RegisterDto {
    // --- AKUN ---
    private String username;
    private String email;
    private String password;
    private String confirmPassword; // Untuk validasi
    private String role; // "ASESI" atau "ASESOR"

    // --- DATA PRIBADI UMUM ---
    private String fullName;
    private String nik;
    private String phoneNumber;
    private String birthPlace;
    private LocalDate birthDate;
    private String gender; // "L" atau "P"
    private String address;
    
    // Wilayah
    private String provinceId;
    private String cityId;
    private String districtId;
    private String subDistrictId;

    // --- KHUSUS ASESI ---
    private String citizenship; // Kewarganegaraan
    private Long lastEducationId; // Pendidikan Terakhir
    private Long jobTypeId; // Jenis Pekerjaan (Bekerja/Tidak)
    // Detail Kantor (Jika Bekerja)
    private String companyName;
    private String position;
    private String officePhone;
    private String officeEmail;
    private String officeAddress;

    // --- KHUSUS ASESOR ---
    private String noMet; // No. Registrasi MET
    
    // Tanda Tangan (Base64 String dari Canvas)
    private String signatureBase64; 
}