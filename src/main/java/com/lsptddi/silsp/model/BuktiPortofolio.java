package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_bukti_portofolio")
@Data
public class BuktiPortofolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "permohonan_id")
    private PermohonanSertifikasi permohonan;

    @Column(name = "nama_dokumen") // Inputan user / nama file
    private String namaDokumen;

    @Column(name = "file_path")
    private String filePath;
    
    // Field sementara untuk mapping ID dari Frontend (Tidak masuk DB)
    @Transient
    private String tempId; 
}