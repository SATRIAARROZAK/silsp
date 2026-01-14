package com.lsptddi.silsp.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_notification")
@Data
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "judul", nullable = false)
    private String title; // Judul: Admin Telah Membuat Jadwal

    @Column(name = "deskripsi", nullable = false)
    private String message; // Desk: Anda ditugaskan...

    @Column(name = "status")
    private boolean isRead = false; // Status sudah dibaca/belum

    @Column(name = "dibuat")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "link")
    private String targetUrl; // Link jika diklik (misal: /asesor/jadwal-asesmen)

    // Relasi ke User (Penerima Notif)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}