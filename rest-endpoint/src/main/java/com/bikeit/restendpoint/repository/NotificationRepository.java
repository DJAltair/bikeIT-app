package com.bikeit.restendpoint.repository;

import com.bikeit.restendpoint.model.Map;
import com.bikeit.restendpoint.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n WHERE n.id = (SELECT MAX(n.id) FROM Notification n)")
    Notification findLatestNotification();
}
