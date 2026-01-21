package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_permohonan_asesmen")
@Data
public class PermohonanAsesmen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "permohonan_id")
    private Permohonan permohonan;

    @Column(name = "kode_unit")
    private String kodeUnit;

    @Column(name = "no_elemen")
    private String noElemen;

    // Nilai: "K" atau "BK"
    @Column(name = "status_kompetensi")
    private String statusKompetensi;

    // Menyimpan ID bukti yang dipilih (misal: "1,3" merujuk ke index bukti portofolio)
    @Column(name = "bukti_relevan_ids")
    private String buktiRelevanIds; 
}