package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "schema_units")
@Data
public class SchemaUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code; // Kode Unit
    private String title; // Judul Unit

    @ManyToOne
    @JoinColumn(name = "schema_id")
    private Schema schema;
}