package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "schema_requirements")
@Data
public class SchemaRequirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String description; // Isi Summernote

    @ManyToOne
    @JoinColumn(name = "schema_id")
    private Schema schema;
}