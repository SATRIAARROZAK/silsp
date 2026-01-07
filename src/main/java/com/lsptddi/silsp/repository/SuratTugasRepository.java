package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.SuratTugas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SuratTugasRepository extends JpaRepository<SuratTugas, Long> {
    // Hitung jumlah surat pada bulan dan tahun tertentu untuk auto-number
    @Query("SELECT COUNT(s) FROM SuratTugas s WHERE s.bulanRomawi = :bulan AND s.tahun = :tahun")
    Long countByBulanAndTahun(String bulan, int tahun);
}