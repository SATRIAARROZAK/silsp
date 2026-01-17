package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "unit_skema")
@Data
public class UnitSkema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unit_skema")
    private Long id;

    @Column(name = "kode_unit")
    private String code; // Kode Unit

    @Column(name = "judul_unit")
    private String title; // Judul Unit

    @ManyToOne
    @JoinColumn(name = "id_skema")
    private Skema skema;

    // TAMBAHAN: Relasi ke Element
    // @OneToMany(mappedBy = "skemaUnit", cascade = CascadeType.ALL, orphanRemoval =
    // true)
    // private List<UnitElemenSkema> elements;

    // --- TAMBAHAN RELASI KE ELEMEN ---
    @OneToMany(mappedBy = "skemaUnit", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<UnitElemenSkema> elements = new ArrayList<>();

    // Helper untuk set relasi 2 arah
    public void addElement(UnitElemenSkema element) {
        elements.add(element);
        element.setSkemaUnit(this);
    }
}