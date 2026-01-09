package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.SuratTugas;
import com.lsptddi.silsp.model.Schedule;
import com.lsptddi.silsp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SuratTugasRepository extends JpaRepository<SuratTugas, Long> {

    // 1. Hitung surat di bulan & tahun tertentu untuk urutan nomor (Reset tiap bulan)
    @Query("SELECT COUNT(s) FROM SuratTugas s WHERE s.bulanRomawi = :bulan AND s.tahun = :tahun")
    Long countByBulanAndTahun(@Param("bulan") String bulan, @Param("tahun") int tahun);

    // 2. Cek apakah Asesor SUDAH punya surat tugas di Jadwal ini? (Cegah Duplikat)
    boolean existsByJadwalAndAsesor(Schedule jadwal, User asesor);
}