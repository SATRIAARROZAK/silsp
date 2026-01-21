package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_bukti_administrasi")
@Data
public class BuktiAdministrasi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "permohonan_id")
    private PermohonanSertifikasi permohonan;

    @Column(name = "jenis_bukti") // KTP, Foto, dll
    private String jenisBukti;

    @Column(name = "file_path")
    private String filePath;
}