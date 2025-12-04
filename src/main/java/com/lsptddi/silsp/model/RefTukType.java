package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ref_tuk_types")
@Data
public class RefTukType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Mandiri, Sewaktu, Tempat Kerja
}