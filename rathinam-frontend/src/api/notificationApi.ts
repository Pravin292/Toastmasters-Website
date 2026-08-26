import { request } from './client';
import { Notification } from '../types';
import { PageResponse } from './meetingApi';

export const notificationApi = {
  getNotifications: (page = 0, size = 20) =>
    request<PageResponse<Notification>>(`/notifications?page=${page}&size=${size}`, { method: 'GET' }),

  getUnreadNotifications: (page = 0, size = 20) =>
    request<PageResponse<Notification>>(`/notifications/unread?page=${page}&size=${size}`, { method: 'GET' }),

  getUnreadCount: () =>
    request<{ unreadCount: number }>('/notifications/unread/count', { method: 'GET' }),

  markAsRead: (id: string) =>
    request<Notification>(`/notifications/${id}/read`, { method: 'PATCH' }),

  markAllAsRead: () =>
    request<string>('/notifications/read-all', { method: 'PATCH' }),
};
