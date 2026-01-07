// package com.lsptddi.silsp.repository;

// import com.lsptddi.silsp.model.SuratTugas;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.stereotype.Repository;

// @Repository
// public interface SuratTugasRepository extends JpaRepository<SuratTugas, Long> {
//     // Hitung jumlah surat pada bulan dan tahun tertentu untuk auto-number
//     @Query("SELECT COUNT(s) FROM SuratTugas s WHERE s.bulanRomawi = :bulan AND s.tahun = :tahun")
//     Long countByBulanAndTahun(String bulan, int tahun);
// }

package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.Schedule;
import com.lsptddi.silsp.model.SuratTugas;
import com.lsptddi.silsp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SuratTugasRepository extends JpaRepository<SuratTugas, Long> {

    // Hitung nomor urut (Logic sebelumnya)
    @Query("SELECT COUNT(s) FROM SuratTugas s WHERE s.bulanRomawi = :bulan AND s.tahun = :tahun")
    Long countByBulanAndTahun(@Param("bulan") String bulan, @Param("tahun") int tahun);

    // CEK DUPLIKASI: Apakah Asesor X sudah punya surat di Jadwal Y?
    boolean existsByJadwalAndAsesor(Schedule jadwal, User asesor);
}