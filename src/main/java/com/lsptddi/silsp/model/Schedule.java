package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.ToString; // Import ini
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "jadwal_ujisertifikasi")
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nama_jadwal", nullable = false)
    private String name;

    @Column(name = "kode_jadwal", unique = true)
    private String code; // Jadwal-LSPTDDI-001-2025

    @Column(name = "kode_jadwal_bnsp")
    private String bnspCode;

    @Column(name = "tanggal_mulai")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    // Jika ada tanggal selesai, tambahkan endDate

    @Column(name = "kuota_peserta")
    private Integer quota;

    // RELASI KE SUMBER ANGGARAN (Ganti String jadi Relasi)
    @ManyToOne
    @JoinColumn(name = "id_sumber_anggaran")
    private TypeSumberAnggaran budgetSource;

    // RELASI KE PEMBERI ANGGARAN
    @ManyToOne
    @JoinColumn(name = "id_pemberi_anggaran")
    private TypePemberiAnggaran budgetProvider;

    // // Data dari API Eksternal (Simpan Namanya/ID string saja)
    // @Column(name = "sumber_anggaran")
    // private String budgetSource;

    // @Column(name = "pemberi_anggaran")
    // private String budgetProvider;

    // Relasi ke TUK (Many to One)
    @ManyToOne
    @JoinColumn(name = "id_tuk", nullable = false)
    private Tuk tuk;

    // Relasi ke Asesor (One to Many via Entity Penghubung atau ManyToMany langsung)
    // Disini kita pakai OneToMany ke tabel penghubung agar lebih fleksibel di 3NF
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleAssessor> assessors;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleSchema> schemas;

    // =================================================================
    // TAMBAHAN BARU: RELASI KE SURAT TUGAS & HELPER METHOD
    // =================================================================

    @OneToMany(mappedBy = "jadwal") // Relasi ke SuratTugas (field 'jadwal')
    @ToString.Exclude // PENTING: Cegah error infinite loop
    private List<SuratTugas> suratTugasList;

    /**
     * Helper Function untuk HTML:
     * Mencari Surat Tugas milik asesor tertentu di jadwal ini.
     */
    public SuratTugas getSuratTugasByAsesor(Long asesorId) {
        if (suratTugasList == null || suratTugasList.isEmpty()) {
            return null;
        }
        // Cari surat yang id_asesor-nya cocok
        return suratTugasList.stream()
                .filter(st -> st.getAsesor().getId().equals(asesorId))
                .findFirst()
                .orElse(null);
    }
}