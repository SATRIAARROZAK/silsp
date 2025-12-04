package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tuk")
@Data
public class Tuk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- PROFILE TUK ---
    private String name;
    
    @Column(unique = true)
    private String code; // TUK-LSPTDDI-001 (Auto Generated)

    // Relasi 3NF
    @ManyToOne
    @JoinColumn(name = "tuk_type_id")
    private RefTukType tukType;

    // --- DETAIL TUK ---
    private String phoneNumber;
    private String email;
    private String address;

    // Wilayah (Simpan ID)
    private String provinceId;
    private String cityId;
}