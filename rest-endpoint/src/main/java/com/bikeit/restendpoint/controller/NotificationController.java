package com.bikeit.restendpoint.controller;

import com.bikeit.restendpoint.model.Dto.NotificationDto;
import com.bikeit.restendpoint.model.Notification;
import com.bikeit.restendpoint.model.User;
import com.bikeit.restendpoint.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification")
    public ResponseEntity<Void> getNotification(@RequestBody NotificationDto notificationDto) {
        try {
            notificationService.saveNotification(new Notification(notificationDto.getContent()));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping("/notification")
    public ResponseEntity<Notification> sendNotification() {
        try {
            Notification toSend = notificationService.getLastNotification();
            if(toSend == null) {throw new IllegalArgumentException("No notifications in database!");}
            return ResponseEntity.ok().body(toSend);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }
}
