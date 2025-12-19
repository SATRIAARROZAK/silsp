package com.lsptddi.silsp.dto;

import lombok.Data;
// import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@Data
public class RegisterDto {
    // Akun
    private String username;
    private String email;
    private String password;
    private String roles; // Asesi / Asesor

    // Data Pribadi
    private String fullName;
    private String nik;
    private String birthPlace;
    private LocalDate birthDate;
    private String gender;
    private String citizenship;
    private String noMet; // Khusus Asesor
    private String phoneNumber;
    
    // Relasi ID (Dropdown)
    private Long educationId;
    private Long jobTypeId;

    // Wilayah
    private String provinceId;
    private String cityId;
    private String districtId;
    // private String subDistrictId;
    private String postalCode;
    private String address;

    // Tanda Tangan
    private String signatureBase64;
}