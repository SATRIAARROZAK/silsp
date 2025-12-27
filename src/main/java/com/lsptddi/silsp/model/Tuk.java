package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tuk")
@Data
public class Tuk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tuk")
    private Long id;

    // --- PROFILE TUK ---
    @Column(name = "nama_tuk", nullable = false)
    private String name;

    @Column(name = "kode_tuk", unique = true)
    private String code; // TUK-LSPTDDI-001 (Auto Generated)

   

    // --- DETAIL TUK ---

    @Column(name = "no_telepon")
    private String phoneNumber;
    @Column(name = "email")
    private String email;
    @Column(name = "alamat")    
    private String address;

    // Wilayah (Simpan ID)
    @Column(name = "id_provinsi")
    private String provinceId;
    @Column(name = "id_kota")
    private String cityId;

     // Relasi 3NF
    @ManyToOne
    @JoinColumn(name = "id_jenis_tuk")
    private TypeTuk tukType;
}