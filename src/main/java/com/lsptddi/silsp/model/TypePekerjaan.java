package com.lsptddi.silsp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "type_pekerjaan")
public class TypePekerjaan {
    @Id
    @Column(name = "id_jenis_pekerjaan")
    private Long id; // ID Manual (1 - 89 sesuai JSON)

    @Column(name = "jenis_pekerjaan", nullable = false)
    private String name;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}