package com.lsptddi.silsp.service;

import com.lsptddi.silsp.model.Notification;
import com.lsptddi.silsp.model.User;
import com.lsptddi.silsp.repository.*;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    // public void createNotification(User user, String title, String message,
    // String url) {
    // Notification notif = new Notification();
    // notif.setUser(user);
    // notif.setTitle(title);
    // notif.setMessage(message);
    // notif.setTargetUrl(url);
    // notificationRepository.save(notif);
    // }

    public void createNotification(User recipient, String title, String message, String link) {
        Notification notif = new Notification();
        notif.setUser(recipient);
        notif.setTitle(title);
        notif.setMessage(message);
        notif.setTargetUrl(link);
        notif.setCreatedAt(LocalDateTime.now());
        notif.setRead(false);
        notificationRepository.save(notif);
    }

    // Kirim notifikasi ke SEMUA Admin
    public void notifyAllAdmins(String title, String message, String link) {
        List<User> admins = userRepository.findByRolesName("Admin"); // Sesuaikan query role Anda
        for (User admin : admins) {
            createNotification(admin, title, message, link);
        }
    }
}