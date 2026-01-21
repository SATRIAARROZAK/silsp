package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_bukti_persyaratan")
@Data
public class BuktiPersyaratan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "permohonan_id")
    private PermohonanSertifikasi permohonan;

    // Link ke Master Persyaratan di Skema
    @ManyToOne
    @JoinColumn(name = "persyaratan_skema_id")
    private PersyaratanSkema persyaratanSkema;

    @Column(name = "file_path")
    private String filePath; // File yang diupload asesi

    @Column(name = "status") // Memenuhi / Tidak Memenuhi (Diisi Admin nanti)
    private String status;
}