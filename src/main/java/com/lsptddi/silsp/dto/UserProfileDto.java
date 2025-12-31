package com.lsptddi.silsp.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String roles; // List Nama Role (ADMIN, ASESI, dll)
    private String fullName;
    private String birthPlace;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String gender;
    private String nik;
    private String phoneNumber;
    private String address;

    // Khusus Ganti Password
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    // Tanda Tangan & Foto (Opsional)
    private String signatureBase64;
    private MultipartFile avatar; // Jika ingin fitur ganti foto profil

    // --- YANG DITANGKAP DARI FORM ADALAH ID (Angka/String) ---
    private Long educationId;
    private Long jobTypeId;

    private String provinceId;
    private String cityId;
    private String districtId;
    // private String subDistrictId;

    private String postalCode;
    private String citizenship;
    private String noMet;

    private String companyName;
    private String position;
    private String officePhone;
    private String officeEmail;
    private String officeFax;
    private String officeAddress;
}