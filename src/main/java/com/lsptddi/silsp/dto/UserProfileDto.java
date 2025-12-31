package com.lsptddi.silsp.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@Data
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String birthPlace;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    private String gender;
    private String nik;
    private String phoneNumber; // Mapping ke phoneNumber di Entity
    private String address;

    // Khusus Ganti Password
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    // Tanda Tangan & Foto
    private String signatureBase64;
    private MultipartFile avatar; 

    // Relasi (Dropdown)
    private Long educationId;
    private Long jobTypeId;

    // Wilayah
    private String provinceId;
    private String cityId;
    private String districtId;
    private String postalCode;

    // Data Khusus Asesi/Asesor
    private String citizenship; // Kewarganegaraan
    private String noMet;       // Khusus Asesor

    // Detail Pekerjaan (Khusus Asesi)
    private String companyName;
    private String position;
    private String officePhone;
    private String officeEmail;
    private String officeFax;
    private String officeAddress;
}