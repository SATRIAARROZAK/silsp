package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "trx_jadwal_asesor")
@Data
public class ScheduleAssessor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_jadwal")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "id_asesor")
    private User asesor; // User dengan role Asesor
}