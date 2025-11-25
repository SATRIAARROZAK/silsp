package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- DATA AKUN ---
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    // Relasi ke Role (Anggap Anda sudah punya entity Role)
    // @ManyToMany(fetch = FetchType.EAGER)
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) // <--- PASTIKAN ADA cascade = CascadeType.ALL
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // --- DATA PRIBADI ---
    private String fullName;
    private String birthPlace;
    private LocalDate birthDate; // Gunakan LocalDate
    private String gender;
    private String nik;
    private String phoneNumber;

    // --- RELASI 3NF (UBAH BAGIAN INI) ---
    // Menyimpan ID Pendidikan (Relasi ke tabel ref_educations)
    @ManyToOne
    @JoinColumn(name = "education_id")
    private RefEducation educationId;

    // Menyimpan ID Pekerjaan (Relasi ke tabel ref_job_types)
    @ManyToOne
    @JoinColumn(name = "job_type_id")
    private RefJobType jobTypeId;

    // --- WILAYAH (Disimpan String ID-nya saja) ---
    private String provinceId;
    private String cityId;
    private String districtId;
    private String subDistrictId;

    private String address;
    private String postalCode;
    private String citizenship;
    private String noMet;

    // --- DETAIL PEKERJAAN ---
    private String companyName;
    private String position;
    private String officePhone;
    private String officeEmail;
    private String officeFax;
    private String officeAddress;

    // --- TANDA TANGAN ---
    @Lob
    @Column(columnDefinition = "TEXT") // Agar muat string panjang
    private String signatureBase64;

}