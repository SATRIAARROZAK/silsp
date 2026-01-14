package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 1. Method untuk Auto Number (Ambil ID terakhir)
    Optional<Schedule> findTopByOrderByIdDesc();

    // 2. Method untuk Search Jadwal (PERBAIKAN UTAMA DISINI)
    // Kita tambahkan @Query agar error "No property found" hilang
    @Query("SELECT s FROM Schedule s WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.bnspCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Schedule> searchSchedule(@Param("keyword") String keyword, Pageable pageable);

    // 3. Method untuk Dropdown di Form Surat Tugas (Filter jadwal berdasarkan
    // asesor)
    // Pastikan query ini juga ada agar fitur surat tugas tidak error
    @Query("SELECT s FROM Schedule s JOIN s.assessors sa WHERE sa.asesor.id = :asesorId ORDER BY s.id DESC")
    List<Schedule> findSchedulesByAsesorId(@Param("asesorId") Long asesorId);

    @Query("SELECT s FROM Schedule s " +
            "LEFT JOIN FETCH s.assessors sa " +
            "LEFT JOIN FETCH sa.asesor u " + // Fetch User Asesor
            "LEFT JOIN FETCH s.schemas ss " +
            "LEFT JOIN FETCH ss.schema sc " + // Fetch Data Skema
            "WHERE s.id = :id")
    Optional<Schedule> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT s FROM Schedule s " +
            "JOIN s.assessors sa " +
            "WHERE sa.asesor.username = :username AND " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Schedule> findJadwalByAsesor(@Param("username") String username,
            @Param("keyword") String keyword,
            Pageable pageable);
}