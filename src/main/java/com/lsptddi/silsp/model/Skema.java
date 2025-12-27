package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skema")
@Data
public class Skema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_skema")
    private Long id;

    // --- TAB 1: Data Skema ---
    @Column(name = "nama_skema")
    private String name;

    @Column(name = "kode_skema")
    private String code;

    @Column(name = "level_skkni")
    private Integer level;
    
    @Column(name = "no_skkni")
    private String noSkkni;

    @Column(name = "tahun_skkni")
    private String skkniYear; // Tahun SKKNI
    
    // Relasi 3NF ke Jenis Skema
    @ManyToOne
    @JoinColumn(name = "id_jenis_skema")
    private TypeSkema schemaType;

    // Relasi 3NF ke Mode Skema
    @ManyToOne
    @JoinColumn(name = "id_pengajuan_skema")
    private TypeSkemaPengajuan schemaMode;

    @Column(name = "tanggal_penetapan")
    private LocalDate establishmentDate; // Tanggal Penetapan

    @Column(name = "file_dokumen", columnDefinition = "TEXT")
    private String documentPath; // Path file PDF

    // --- TAB 2: Unit Skema (One-to-Many) ---
    @OneToMany(mappedBy = "skema", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<UnitSkema> units = new ArrayList<>();

    // --- TAB 3: Persyaratan (One-to-Many) ---
    @OneToMany(mappedBy = "skema", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<PersyaratanSkema> requirements = new ArrayList<>();

    // Helper method untuk set relasi dua arah
    public void addUnit(UnitSkema unit) {
        units.add(unit);
        unit.setSkema(this);
    }

    public void addRequirement(PersyaratanSkema requirement) {
        requirements.add(requirement);
        requirement.setSkema(this);
    }
}