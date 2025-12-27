package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "type_tuk")
@Data
public class TypeTuk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jenis_tuk")
    private Long id;

    @Column(name = "jenis_tuk", nullable = false, unique = true)
    private String name; // Mandiri, Sewaktu, Tempat Kerja
}