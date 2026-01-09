package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "jadwal_ujiskema")
@Data
public class ScheduleSchema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_jadwal")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "id_skema")
    private Skema schema;
}