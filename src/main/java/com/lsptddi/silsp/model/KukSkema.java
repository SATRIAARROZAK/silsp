package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_kuk_skema")
@Data
public class KukSkema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nama_kuk", columnDefinition = "TEXT")
    private String namaKuk;

    // Relasi ke Elemen (Parent)
    @ManyToOne
    @JoinColumn(name = "id_elemen")
    private UnitElemenSkema schemaElement;
}