
package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "type_skema_pengajuan")
@Data
public class TypeSkemaPengajuan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pengajuan_skema")
    private Long id;

    @Column(name = "nama_pengajuan_skema", nullable = false, unique = true)
    private String name; // Contoh: MANDIRI, REFERENSI_BNSP
}