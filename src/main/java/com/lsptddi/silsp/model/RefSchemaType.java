package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ref_schema_types")
@Data
public class RefSchemaType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    // @Column(name = "name_skema", nullable = false, unique = true) // Tambah nama kolom
    private String name; // Contoh: KKNI, OKUPASI, KLASTER
}