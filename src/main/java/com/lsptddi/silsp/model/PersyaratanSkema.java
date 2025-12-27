package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "persyaratan_skema")
@Data
public class PersyaratanSkema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persyaratan_skema")
    private Long id;

    @Column(name = "deskripsi", columnDefinition = "TEXT")
    private String description; // Isi Summernote

    @ManyToOne
    @JoinColumn(name = "id_skema")
    private Skema skema;
}