package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "schemas")
@Data
public class Schema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- TAB 1: Data Skema ---
    private String name;
    private String code;
    private Integer level;
    private String noSkkni;
    private String skkniYear; // Tahun SKKNI
    
    // Relasi 3NF ke Jenis Skema
    @ManyToOne
    @JoinColumn(name = "schema_type_id")
    private RefSchemaType schemaType;

    // Relasi 3NF ke Mode Skema
    @ManyToOne
    @JoinColumn(name = "schema_mode_id")
    private RefSchemaMode schemaMode;

    private LocalDate establishmentDate; // Tanggal Penetapan
    private String documentPath; // Path file PDF

    // --- TAB 2: Unit Skema (One-to-Many) ---
    @OneToMany(mappedBy = "schema", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<SchemaUnit> units = new ArrayList<>();

    // --- TAB 3: Persyaratan (One-to-Many) ---
    @OneToMany(mappedBy = "schema", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<SchemaRequirement> requirements = new ArrayList<>();

    // Helper method untuk set relasi dua arah
    public void addUnit(SchemaUnit unit) {
        units.add(unit);
        unit.setSchema(this);
    }

    public void addRequirement(SchemaRequirement requirement) {
        requirements.add(requirement);
        requirement.setSchema(this);
    }
}