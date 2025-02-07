package com.bikeit.restendpoint.service;

import com.bikeit.restendpoint.model.Notification;
import com.bikeit.restendpoint.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    public Notification getLastNotification() {
        return notificationRepository.findLatestNotification();
    }
}
