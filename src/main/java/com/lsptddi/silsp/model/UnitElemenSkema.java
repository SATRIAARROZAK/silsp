package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "tb_unit_elemen")
@Data
public class UnitElemenSkema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "no_elemen")
    private String noElemen;

    @Column(name = "nama_elemen", columnDefinition = "TEXT")
    private String namaElemen;

    // Relasi ke Unit (Parent)
    @ManyToOne
    @JoinColumn(name = "id_unit_skema")
    private UnitSkema skemaUnit;

    // Relasi ke KUK (Child)
    @OneToMany(mappedBy = "schemaElement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KukSkema> kuks;
}