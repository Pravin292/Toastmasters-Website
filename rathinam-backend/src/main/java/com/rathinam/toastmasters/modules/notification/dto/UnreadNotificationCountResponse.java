package com.rathinam.toastmasters.modules.notification.dto;

public class UnreadNotificationCountResponse {

    private long unreadCount;

    public UnreadNotificationCountResponse() {
    }

    public UnreadNotificationCountResponse(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
