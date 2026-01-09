package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "tb_surat_tugas")
@Data
public class SuratTugas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nomor_surat", unique = true)
    private String nomorSurat; // 001/ST/LSPTDDI/I/2026

    @Column(name = "tanggal_surat")
    private LocalDate tanggalSurat;

    @Column(name = "bulan_romawi")
    private String bulanRomawi; // Untuk filtering reset nomor

    @Column(name = "tahun")
    private int tahun; // Untuk filtering reset nomor

    // Relasi ke Asesor
    @ManyToOne
    @JoinColumn(name = "id_asesor")
    private User asesor;

    // Relasi ke Jadwal (Didalamnya sudah ada TUK & Skema)
    @ManyToOne
    @JoinColumn(name = "id_jadwal")
    private Schedule jadwal;

    @ManyToOne
    @JoinColumn(name = "id_skema")
    private Skema skema; // Tambahan: Skema spesifik yang dipilih
}