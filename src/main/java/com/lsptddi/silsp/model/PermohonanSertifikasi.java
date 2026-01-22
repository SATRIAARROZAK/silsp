package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_permohonan_sertifikasi")
@Data
public class PermohonanSertifikasi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User asesi;

    @ManyToOne
    @JoinColumn(name = "skema_id")
    private Skema skema;

    @ManyToOne
    @JoinColumn(name = "jadwal_id")
    private Schedule jadwal;

    // --- PERBAIKAN 1: Gunakan Relasi ke Master Data ---
    @ManyToOne
    @JoinColumn(name = "sumber_anggaran_id")
    private TypeSumberAnggaran sumberAnggaran;

    @ManyToOne
    @JoinColumn(name = "pemberi_anggaran_id")
    private TypePemberiAnggaran pemberiAnggaran;

    @Column(name = "tujuan_asesmen")
    private String tujuanAsesmen;

    @Column(name = "tanggal_permohonan")
    private LocalDateTime tanggalPermohonan = LocalDateTime.now();

    @Column(name = "status_permohonan")
    private String status = "SUBMITTED";

    // Relasi ke Child Tables (Biarkan Tetap)
    @OneToMany(mappedBy = "permohonan", cascade = CascadeType.ALL)
    private List<BuktiPersyaratan> persyaratanList;

    @OneToMany(mappedBy = "permohonan", cascade = CascadeType.ALL)
    private List<BuktiAdministrasi> buktiAdminList;

    @OneToMany(mappedBy = "permohonan", cascade = CascadeType.ALL)
    private List<BuktiPortofolio> portofolioList;

    @OneToMany(mappedBy = "permohonan", cascade = CascadeType.ALL)
    private List<AsesmenMandiri> asesmenMandiriList;
}

// package com.lsptddi.silsp.model;

// import jakarta.persistence.*;
// import lombok.Data;
// import java.time.LocalDateTime;
// import java.util.List;

// @Entity
// @Table(name = "tb_permohonan_sertifikasi")
// @Data
// public class PermohonanSertifikasi {
// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long id;

// // Relasi ke Asesi (User)
// @ManyToOne
// @JoinColumn(name = "user_id")
// private User asesi;

// // Tab 1: Pengajuan Skema
// @ManyToOne
// @JoinColumn(name = "skema_id")
// private Skema skema;

// @ManyToOne
// @JoinColumn(name = "jadwal_id")
// private Schedule jadwal;

// @Column(name = "sumber_anggaran")
// private String sumberAnggaran;

// @Column(name = "pemberi_anggaran")
// private String pemberiAnggaran;

// // Tab 6: Tujuan Asesmen (Sertifikasi, PKT, RPL, Lainnya)
// @Column(name = "tujuan_asesmen")
// private String tujuanAsesmen;

// @Column(name = "tanggal_permohonan")
// private LocalDateTime tanggalPermohonan = LocalDateTime.now();

// @Column(name = "status_permohonan") // SUBMITTED, VERIFIED, ACCEPTED,
// REJECTED
// private String status = "SUBMITTED";

// // Relasi ke Child Tables
// @OneToMany(mappedBy = "permohonan", cascade = CascadeType.ALL)
// private List<BuktiPersyaratan> persyaratanList;

// @OneToMany(mappedBy = "permohonan", cascade = CascadeType.ALL)
// private List<BuktiAdministrasi> buktiAdminList;

// @OneToMany(mappedBy = "permohonan", cascade = CascadeType.ALL)
// private List<BuktiPortofolio> portofolioList;

// @OneToMany(mappedBy = "permohonan", cascade = CascadeType.ALL)
// private List<AsesmenMandiri> asesmenMandiriList;
// }