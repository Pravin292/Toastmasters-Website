import React, { useEffect, useState } from 'react';
import { AppLayout } from '../components/layout/AppLayout';
import { Card } from '../components/common/Card';
import { Badge } from '../components/common/Badge';
import { Button } from '../components/common/Button';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { notificationApi } from '../api/notificationApi';
import { Notification } from '../types';
import { Bell, CheckCheck, Circle } from 'lucide-react';

export const NotificationsPage: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [filterUnread, setFilterUnread] = useState(false);

  const fetchNotifications = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = filterUnread
        ? await notificationApi.getUnreadNotifications(0, 50)
        : await notificationApi.getNotifications(0, 50);
      setNotifications(res.content);
    } catch (err: any) {
      setError(err.message || 'Failed to fetch notifications');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchNotifications();
  }, [filterUnread]);

  const handleMarkAsRead = async (id: string) => {
    try {
      await notificationApi.markAsRead(id);
      fetchNotifications();
    } catch (err: any) {
      setError(err.message || 'Failed to mark notification read');
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationApi.markAllAsRead();
      fetchNotifications();
    } catch (err: any) {
      setError(err.message || 'Failed to mark all notifications read');
    }
  };

  return (
    <AppLayout title="Notifications Center">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24, flexWrap: 'wrap', gap: 16 }}>
        <div style={{ display: 'flex', gap: 8, background: 'rgba(255, 255, 255, 0.05)', padding: 4, borderRadius: 10 }}>
          <button
            onClick={() => setFilterUnread(false)}
            style={{
              padding: '6px 14px',
              borderRadius: 8,
              border: 'none',
              background: !filterUnread ? 'var(--tm-maroon)' : 'transparent',
              color: !filterUnread ? '#FFF' : '#94A3B8',
              fontWeight: 600,
              fontSize: '0.85rem',
              cursor: 'pointer'
            }}
          >
            All Notifications
          </button>
          <button
            onClick={() => setFilterUnread(true)}
            style={{
              padding: '6px 14px',
              borderRadius: 8,
              border: 'none',
              background: filterUnread ? 'var(--tm-maroon)' : 'transparent',
              color: filterUnread ? '#FFF' : '#94A3B8',
              fontWeight: 600,
              fontSize: '0.85rem',
              cursor: 'pointer'
            }}
          >
            Unread Only
          </button>
        </div>

        <Button variant="secondary" onClick={handleMarkAllAsRead}>
          <CheckCheck size={16} /> Mark All as Read
        </Button>
      </div>

      {error && <ErrorMessage message={error} />}

      {loading ? (
        <LoadingSpinner />
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
          {notifications.map((n) => (
            <Card
              key={n.id}
              style={{
                display: 'flex',
                alignItems: 'flex-start',
                justifyContent: 'space-between',
                padding: '18px 22px',
                background: !n.read ? 'rgba(119, 33, 111, 0.12)' : 'var(--bg-card)',
                borderColor: !n.read ? 'rgba(119, 33, 111, 0.4)' : 'var(--bg-card-border)'
              }}
            >
              <div style={{ display: 'flex', gap: 14 }}>
                <div style={{ marginTop: 2 }}>
                  {!n.read ? (
                    <Circle size={14} color="#F2DF74" fill="#F2DF74" />
                  ) : (
                    <Bell size={18} color="#64748B" />
                  )}
                </div>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 4 }}>
                    <h4 style={{ fontSize: '1rem', fontWeight: 700 }}>{n.title}</h4>
                    <Badge variant="scheduled">{n.type}</Badge>
                  </div>
                  <p style={{ fontSize: '0.92rem', color: '#CBD5E1', marginBottom: 6 }}>
                    {n.message}
                  </p>
                  <div style={{ fontSize: '0.78rem', color: '#64748B' }}>
                    {new Date(n.createdAt).toLocaleString()}
                  </div>
                </div>
              </div>

              {!n.read && (
                <button
                  className="btn btn-secondary"
                  style={{ padding: '4px 10px', fontSize: '0.78rem' }}
                  onClick={() => handleMarkAsRead(n.id)}
                >
                  Mark Read
                </button>
              )}
            </Card>
          ))}

          {notifications.length === 0 && (
            <Card style={{ textAlign: 'center', padding: 40, color: '#64748B' }}>
              No notifications found.
            </Card>
          )}
        </div>
      )}
    </AppLayout>
  );
};
