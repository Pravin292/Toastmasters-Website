package com.rathinam.toastmasters.modules.notification.channel;

import com.rathinam.toastmasters.modules.notification.entity.NotificationEntity;

public interface NotificationChannel {
    void send(NotificationEntity notification);
}
