package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.Schedule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findTopByOrderByIdDesc();

    @Query("SELECT s FROM Schedule s WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Schedule> searchSchedule(@Param("keyword") String keyword, Pageable pageable);

}