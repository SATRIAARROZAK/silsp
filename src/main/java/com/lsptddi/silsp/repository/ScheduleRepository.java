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

    // 1. Method Auto Number
    Optional<Schedule> findTopByOrderByIdDesc();

    // 2. Method Search
    @Query("SELECT s FROM Schedule s WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.bnspCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Schedule> searchSchedule(@Param("keyword") String keyword, Pageable pageable);

    // 3. METHOD YANG HILANG (TAMBAHKAN INI AGAR CONTROLLER TIDAK ERROR)
    @Query("SELECT s FROM Schedule s JOIN s.assessors sa WHERE sa.asesor.id = :asesorId ORDER BY s.id DESC")
    List<Schedule> findSchedulesByAsesorId(@Param("asesorId") Long asesorId);
}