package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.PermohonanSertifikasi;
import com.lsptddi.silsp.model.Schedule;
import com.lsptddi.silsp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PermohonanSertifikasiRepository extends JpaRepository<PermohonanSertifikasi, Long> {
    List<PermohonanSertifikasi> findByAsesiOrderByTanggalPermohonanDesc(User asesi);

    // Hitung jumlah pendaftar di jadwal tertentu
    long countByJadwal(Schedule jadwal);

    // Cek apakah user sudah daftar di jadwal ini (Cegah double register di jadwal
    // sama)
    boolean existsByAsesiAndJadwal(User asesi, Schedule jadwal);

    // Cek apakah user sudah daftar di jadwal APAPUN pada tanggal tertentu (Cegah
    // jadwal bentrok)
    // Asumsi: Schedule punya field startDate (LocalDate)
    @Query("SELECT COUNT(p) > 0 FROM PermohonanSertifikasi p WHERE p.asesi = :asesi AND p.jadwal.startDate = :date")
    boolean existsByAsesiAndJadwalDate(@Param("asesi") User asesi, @Param("date") LocalDate date);
}