package com.rathinam.toastmasters.modules.notification.channel;

import com.rathinam.toastmasters.modules.notification.entity.NotificationEntity;
import com.rathinam.toastmasters.modules.notification.repository.NotificationRepository;
import org.springframework.stereotype.Component;

@Component
public class InAppNotificationChannel implements NotificationChannel {

    private final NotificationRepository notificationRepository;

    public InAppNotificationChannel(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void send(NotificationEntity notification) {
        notificationRepository.save(notification);
    }
}
