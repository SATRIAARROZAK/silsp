package com.lsptddi.silsp.service;

import com.lsptddi.silsp.model.Notification;
import com.lsptddi.silsp.model.User;
import com.lsptddi.silsp.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void createNotification(User user, String title, String message, String url) {
        Notification notif = new Notification();
        notif.setUser(user);
        notif.setTitle(title);
        notif.setMessage(message);
        notif.setTargetUrl(url);
        notificationRepository.save(notif);
    }
}