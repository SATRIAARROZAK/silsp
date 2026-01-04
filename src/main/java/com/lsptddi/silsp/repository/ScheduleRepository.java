package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findTopByOrderByIdDesc();
}