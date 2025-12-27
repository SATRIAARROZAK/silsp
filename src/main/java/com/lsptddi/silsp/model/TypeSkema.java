package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "type_skema")
@Data
public class TypeSkema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jenis_skema")
    private Long id;

    // @Column(nullable = false, unique = true)
    @Column(name = "nama_jenis_skema", nullable = false, unique = true) // Tambah nama kolom
    private String name; // Contoh: KKNI, OKUPASI, KLASTER
}