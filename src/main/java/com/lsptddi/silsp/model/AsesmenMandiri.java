package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "tb_asesmen_mandiri")
@Data
public class AsesmenMandiri {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "permohonan_id")
    private PermohonanSertifikasi permohonan;

    // Link ke Unit Elemen Skema
   @ManyToOne
    @JoinColumn(name = "unit_elemen_id")
    private UnitElemenSkema unitElemen;

    @Column(name = "rekomendasi_asesi") // "K" atau "BK"
    private String rekomendasiAsesi;

    // Relasi Many-to-Many ke Bukti Portofolio
    // Satu KUK bisa dibuktikan dengan banyak portofolio
    @ManyToMany
    @JoinTable(
        name = "tb_asesmen_bukti_relasi",
        joinColumns = @JoinColumn(name = "asesmen_id"),
        inverseJoinColumns = @JoinColumn(name = "portofolio_id")
    )
    private List<BuktiPortofolio> buktiRelevan;
}