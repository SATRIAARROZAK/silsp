package com.lsptddi.silsp.repository;

import com.lsptddi.silsp.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Ambil notifikasi milik user tertentu, urutkan terbaru
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Hitung jumlah notif yang belum dibaca
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnread(@Param("userId") Long userId);

    // Fitur: Tandai Semua Sudah Dibaca
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
    void markAllAsRead(@Param("userId") Long userId);

    // Fitur: Tandai Satu Sudah Dibaca (Optional, jika ingin per item)
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :notifId")
    void markAsRead(@Param("notifId") Long notifId);
}