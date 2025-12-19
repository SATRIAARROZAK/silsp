package com.lsptddi.silsp.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

@Data
public class UserDto {
    private Long id;   
    private String password; // Biarkan ini, nanti di controller kita cek isinya
    private String username;
    private String email;
    private List<String> roles; // List ID Role (ADMIN, ASESI, dll)

    private String fullName;
    private String birthPlace;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    private String gender;
    private String nik;
    private String phoneNumber;

    // --- YANG DITANGKAP DARI FORM ADALAH ID (Angka/String) ---
    private Long educationId;   
    private Long jobTypeId;     
    
    private String provinceId;
    private String cityId;
    private String districtId;
    // private String subDistrictId;

    private String address;
    private String postalCode;
    private String citizenship;
    private String noMet;

    private String companyName;
    private String position;
    private String officePhone;
    private String officeEmail;
    private String officeFax;
    private String officeAddress;

    private String signatureBase64;
}