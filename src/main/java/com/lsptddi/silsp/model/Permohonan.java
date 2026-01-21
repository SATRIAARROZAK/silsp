package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_permohonan")
@Data
public class Permohonan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User asesi;

    @ManyToOne
    @JoinColumn(name = "jadwal_id", nullable = false)
    private Schedule jadwal;

    @ManyToOne
    @JoinColumn(name = "skema_id", nullable = false)
    private Skema skema;

    @Column(name = "tujuan_asesmen")
    private String tujuanAsesmen; // Sertifikasi, PKT, RPL, Lainnya

    @Column(name = "sumber_anggaran")
    private String sumberAnggaran;

    @Column(name = "tanggal_permohonan")
    private LocalDateTime tanggalPermohonan = LocalDateTime.now();

    @Column(name = "status")
    private String status; // MENUNGGU_VERIFIKASI, DISETUJUI, DITOLAK

    // Relasi ke Child Tables
    @OneToMany(mappedBy = "permohonan", cascade = CascadeType.ALL)
    private List<PermohonanBukti> listBukti;

    @OneToMany(mappedBy = "permohonan", cascade = CascadeType.ALL)
    private List<PermohonanAsesmen> listAsesmen;
}