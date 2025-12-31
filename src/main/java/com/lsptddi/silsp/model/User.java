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
    @Column(name = "id_user")
    private Long id;

    // --- DATA AKUN ---
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    // Relasi ke Role (Anggap Anda sudah punya entity Role)
    @ManyToMany(fetch = FetchType.EAGER)
    // @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) // <---
    // PASTIKAN ADA cascade = CascadeType.ALL
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "id_user"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // --- DATA PRIBADI ---
    @Column(name = "nama_lengkap")
    private String fullName;

    @Column(name = "tempat_lahir")
    private String birthPlace;

    @Column(name = "tanggal_lahir")
    private LocalDate birthDate; // Gunakan LocalDate

    @Column(name = "jenis_kelamin")
    private String gender;
    @Column(name = "nik")
    private String nik;
    @Column(name = "no_telepon")
    private String phoneNumber;

    @Column(name = "avatar")
    private String avatar;

    // --- RELASI 3NF (UBAH BAGIAN INI) ---
    // Menyimpan ID Pendidikan (Relasi ke tabel ref_educations)
    // @ManyToOne
    // @JoinColumn(name = "education_id")
    // private RefEducation educationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_jenis_pendidikan") // Ini nama kolom di database (MySQL)
    private TypeEducation educationId;

    // ... getter setter ...
    public TypeEducation getEducationId() {
        return educationId;
    }

    public void setEducationId(TypeEducation educationId) {
        this.educationId = educationId;
    }

    // @ManyToOne
    // @JoinColumn(name = "education_id") // Ini nama kolom di database (MySQL)
    // private RefEducation lastEducation;

    // // ... getter setter ...
    // public RefEducation getLastEducation() {
    // return lastEducation;
    // }

    // public void setLastEducation(RefEducation lastEducation) {
    // this.lastEducation = lastEducation;
    // }

    // Menyimpan ID Pekerjaan (Relasi ke tabel ref_job_types)
    @ManyToOne
    @JoinColumn(name = "id_jenis_pekerjaan")
    private TypePekerjaan jobTypeId;

    // ... getter setter ...
    public TypePekerjaan getJobTypeId() {
        return jobTypeId;
    }

    public void setJobTypeId(TypePekerjaan jobTypeId) {
        this.jobTypeId = jobTypeId;
    }

    // --- WILAYAH (Disimpan String ID-nya saja) ---
    @Column(name = "id_provinsi")
    private String provinceId;
    @Column(name = "id_kota")
    private String cityId;
    @Column(name = "id_kecamatan")
    private String districtId;
    // private String subDistrictId;

    // --- DETAIL ALAMAT ---
    @Column(name = "alamat")
    private String address;
    @Column(name = "kode_pos")
    private String postalCode;
    @Column(name = "kewarganegaraan")
    private String citizenship;
    @Column(name = "no_met")
    private String noMet;

    // --- DETAIL PEKERJAAN ---
    @Column(name = "nama_tempat_kerja")
    private String companyName;
    @Column(name = "jabatan")
    private String position;
    @Column(name = "telepon_kantor")
    private String officePhone;
    @Column(name = "email_kantor")
    private String officeEmail;
    @Column(name = "fax_kantor")
    private String officeFax;
    @Column(name = "alamat_kantor")
    private String officeAddress;

    // --- TANDA TANGAN ---
    @Lob
    @Column(name = "tanda_tangan", columnDefinition = "TEXT") // Agar muat string panjang
    private String signatureBase64;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}