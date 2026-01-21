package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tb_permohonan_bukti")
@Data
public class PermohonanBukti {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "permohonan_id")
    private Permohonan permohonan;

    // Jenis: "PERSYARATAN", "ADMINISTRASI", "PORTOFOLIO"
    @Column(name = "jenis_bukti")
    private String jenisBukti; 

    @Column(name = "nama_bukti")
    private String namaBukti; // Misal: "KTP", "Ijazah", "Laporan X"

    @Column(name = "file_path")
    private String filePath;
}