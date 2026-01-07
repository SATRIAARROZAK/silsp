package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.Schedule;
import com.lsptddi.silsp.model.ScheduleAssessor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleAssessorRepository extends JpaRepository<ScheduleAssessor, Long> {
    List<Schedule> findSchedulesByAsesorId(Long asesorId);
}