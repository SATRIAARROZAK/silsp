package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.Schedule;
import com.lsptddi.silsp.model.SuratTugas;
import com.lsptddi.silsp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SuratTugasRepository extends JpaRepository<SuratTugas, Long> {

    // Hitung nomor urut (Logic sebelumnya)
    @Query("SELECT COUNT(s) FROM SuratTugas s WHERE s.bulanRomawi = :bulan AND s.tahun = :tahun")
    Long countByBulanAndTahun(@Param("bulan") String bulan, @Param("tahun") int tahun);

    // Cek Duplikat (Logic sebelumnya)
    boolean existsByJadwalAndAsesor(Schedule jadwal, User asesor);

    // --- TAMBAHAN BARU: PENCARIAN ---
    @Query("SELECT s FROM SuratTugas s WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(s.nomorSurat) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.asesor.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.jadwal.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<SuratTugas> searchSuratTugas(@Param("keyword") String keyword, Pageable pageable);

    // QUERY KHUSUS ASESOR: Hanya ambil surat tugas milik usernya sendiri
    @Query("SELECT st FROM SuratTugas st WHERE " +
            "st.asesor.username = :username AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(st.jadwal.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(st.skema.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(st.nomorSurat) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<SuratTugas> findMyAssignments(@Param("username") String username,
            @Param("keyword") String keyword,
            Pageable pageable);
}