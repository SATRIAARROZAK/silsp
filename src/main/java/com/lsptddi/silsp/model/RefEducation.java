package com.lsptddi.silsp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ref_educations")
public class RefEducation {
    @Id
    // Kita tidak pakai @GeneratedValue karena kita mau ID sesuai JSON (1, 2, 3,
    // 10...)
    private Long id;

    @Column(nullable = false)
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

