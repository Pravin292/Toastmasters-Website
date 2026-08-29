import React, { useState, useRef, useEffect } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { notificationApi } from '../../api/notificationApi';
import { Notification } from '../../types';
import { 
  LayoutDashboard, 
  Calendar, 
  Users, 
  Trophy, 
  Award, 
  BarChart3, 
  Settings, 
  ShieldCheck, 
  LogOut, 
  Bell, 
  Menu, 
  X,
  CheckCircle2
} from 'lucide-react';
import '../../styles/GlobalNavbar.css';

export const GlobalNavbar: React.FC = () => {
  const { user, isOfficer, logout } = useAuth();
  const navigate = useNavigate();

  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState<number>(0);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Fetch unread notifications for profile dropdown
  const fetchNotifications = async () => {
    try {
      const [resList, resCount] = await Promise.all([
        notificationApi.getUnreadNotifications(0, 4).catch(() => ({ content: [] })),
        notificationApi.getUnreadCount().catch(() => ({ unreadCount: 0 })),
      ]);
      setNotifications(resList.content || []);
      setUnreadCount(resCount.unreadCount || 0);
    } catch {
      // Quiet fail if guest/unauthenticated
    }
  };

  useEffect(() => {
    if (user) {
      fetchNotifications();
    }
  }, [user]);

  // Close dropdown on outside click or Escape key press
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setDropdownOpen(false);
      }
    };
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        setDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    document.addEventListener('keydown', handleKeyDown);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, []);

  const handleMarkAllAsRead = async () => {
    try {
      await notificationApi.markAllAsRead();
      setNotifications([]);
      setUnreadCount(0);
    } catch (err) {
      console.error('Failed to mark notifications as read', err);
    }
  };

  const handleLogout = () => {
    setDropdownOpen(false);
    logout();
    navigate('/login');
  };

  // Derive dynamic user initial
  const userInitial = user?.firstName
    ? user.firstName.charAt(0).toUpperCase()
    : user?.email
    ? user.email.charAt(0).toUpperCase()
    : 'A';

  const displayName = user?.firstName
    ? `${user.firstName} ${user.lastName || ''}`.trim()
    : user?.email
    ? user.email.split('@')[0]
    : 'Authenticated User';

  const roleBadgeClass = user?.role === 'ADMIN' ? 'admin' : isOfficer ? 'officer' : 'member';

  return (
    <nav className="global-navbar-container">
      <div className="global-navbar-inner">
        {/* Brand Section */}
        <div className="global-brand" onClick={() => navigate('/dashboard')}>
          <img
            src="/assets/rathinam-logo.png"
            alt="Rathinam Group"
            className="global-brand-logo"
          />
          <div>
            <div className="global-brand-title">Rathinam Toastmasters</div>
          </div>
          <span className="global-brand-tag">RTC — District 230</span>
        </div>

        {/* Desktop Horizontal Navigation Links */}
        <div className="global-nav-links">
          <NavLink to="/dashboard" className={({ isActive }) => `global-nav-link ${isActive ? 'active' : ''}`}>
            <LayoutDashboard size={16} />
            <span>Dashboard</span>
          </NavLink>

          <NavLink to="/meetings" className={({ isActive }) => `global-nav-link ${isActive ? 'active' : ''}`}>
            <Calendar size={16} />
            <span>Meetings</span>
          </NavLink>

          <NavLink to="/members" className={({ isActive }) => `global-nav-link ${isActive ? 'active' : ''}`}>
            <Users size={16} />
            <span>Members</span>
          </NavLink>

          <NavLink to="/rankings" className={({ isActive }) => `global-nav-link ${isActive ? 'active' : ''}`}>
            <Trophy size={16} />
            <span>Leaderboard</span>
          </NavLink>

          <NavLink to="/achievements" className={({ isActive }) => `global-nav-link ${isActive ? 'active' : ''}`}>
            <Award size={16} />
            <span>Achievements</span>
          </NavLink>

          <NavLink to="/analytics" className={({ isActive }) => `global-nav-link ${isActive ? 'active' : ''}`}>
            <BarChart3 size={16} />
            <span>Analytics</span>
          </NavLink>

          {isOfficer && (
            <NavLink to="/admin" className={({ isActive }) => `global-nav-link ${isActive ? 'active' : ''}`}>
              <Settings size={16} />
              <span>Club Management</span>
            </NavLink>
          )}

          <NavLink to="/certificates" className={({ isActive }) => `global-nav-link ${isActive ? 'active' : ''}`}>
            <ShieldCheck size={16} />
            <span>Certificates</span>
          </NavLink>
        </div>

        {/* Top-Right Profile Control (Small Circle Only) */}
        <div className="global-profile-container" ref={dropdownRef}>
          <button
            className="global-profile-avatar-btn"
            onClick={() => {
              setDropdownOpen(!dropdownOpen);
              if (!dropdownOpen) fetchNotifications();
            }}
            aria-label="User Profile and Notifications Menu"
            aria-expanded={dropdownOpen}
          >
            <span>{userInitial}</span>
            {unreadCount > 0 && <span className="global-avatar-badge-dot" />}
          </button>

          {/* Integrated Profile & Notifications Dropdown */}
          {dropdownOpen && (
            <div className="global-profile-dropdown" role="menu">
              {/* Section 1: User Profile Header */}
              <div className="dropdown-user-header">
                <div className="dropdown-user-avatar">{userInitial}</div>
                <div>
                  <div className="dropdown-user-name">{displayName}</div>
                  <span className={`dropdown-user-role-pill ${roleBadgeClass}`}>
                    {user?.role || 'MEMBER'}
                  </span>
                </div>
              </div>

              {/* User Email & Account Details */}
              <div className="dropdown-user-meta">
                <div className="dropdown-meta-row">
                  <span>Email:</span>
                  <span className="dropdown-meta-val">{user?.email}</span>
                </div>
                {user?.memberId && (
                  <div className="dropdown-meta-row">
                    <span>Member ID:</span>
                    <span className="dropdown-meta-val">{user.memberId.substring(0, 12)}...</span>
                  </div>
                )}
              </div>

              {/* Section 2: Integrated Notifications */}
              <div className="dropdown-notifications-block">
                <div className="dropdown-notifications-header">
                  <span className="dropdown-notifications-title">
                    <Bell size={14} color="#38BDF8" />
                    <span>Notifications ({unreadCount})</span>
                  </span>
                  {unreadCount > 0 && (
                    <button className="dropdown-read-all-btn" onClick={handleMarkAllAsRead}>
                      Mark all as read
                    </button>
                  )}
                </div>

                <div className="dropdown-notifications-list">
                  {notifications.slice(0, 3).map((n) => (
                    <div key={n.id} className={`dropdown-notification-item ${!n.read ? 'unread' : ''}`}>
                      <div className="dropdown-notification-title">{n.title}</div>
                      <div className="dropdown-notification-msg">{n.message}</div>
                    </div>
                  ))}

                  {notifications.length === 0 && (
                    <div style={{ textAlign: 'center', color: '#64748B', fontSize: '0.78rem', padding: '8px 0' }}>
                      No unread notifications.
                    </div>
                  )}
                </div>

                <NavLink
                  to="/notifications"
                  className="dropdown-view-all-link"
                  onClick={() => setDropdownOpen(false)}
                >
                  View all notifications →
                </NavLink>
              </div>

              {/* Section 3: Logout Action */}
              <button className="dropdown-logout-btn" onClick={handleLogout}>
                <LogOut size={16} />
                <span>Sign Out</span>
              </button>
            </div>
          )}

          {/* Mobile Navigation Drawer Toggle */}
          <button
            className="mobile-nav-toggle"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            aria-label="Toggle navigation menu"
          >
            {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>
      </div>

      {/* Mobile Drawer Menu */}
      {mobileMenuOpen && (
        <div style={{
          background: 'rgba(10, 16, 28, 0.98)',
          borderBottom: '1px solid rgba(255, 255, 255, 0.1)',
          padding: '16px 24px',
          display: 'flex',
          flexDirection: 'column',
          gap: 10
        }}>
          <NavLink to="/dashboard" className="global-nav-link" onClick={() => setMobileMenuOpen(false)}>
            <LayoutDashboard size={18} /> <span>Dashboard</span>
          </NavLink>

          <NavLink to="/meetings" className="global-nav-link" onClick={() => setMobileMenuOpen(false)}>
            <Calendar size={18} /> <span>Meetings</span>
          </NavLink>

          <NavLink to="/members" className="global-nav-link" onClick={() => setMobileMenuOpen(false)}>
            <Users size={18} /> <span>Members</span>
          </NavLink>

          <NavLink to="/rankings" className="global-nav-link" onClick={() => setMobileMenuOpen(false)}>
            <Trophy size={18} /> <span>Leaderboard</span>
          </NavLink>

          <NavLink to="/achievements" className="global-nav-link" onClick={() => setMobileMenuOpen(false)}>
            <Award size={18} /> <span>Achievements</span>
          </NavLink>

          <NavLink to="/analytics" className="global-nav-link" onClick={() => setMobileMenuOpen(false)}>
            <BarChart3 size={18} /> <span>Analytics</span>
          </NavLink>

          {isOfficer && (
            <NavLink to="/admin" className="global-nav-link" onClick={() => setMobileMenuOpen(false)}>
              <Settings size={18} /> <span>Club Management</span>
            </NavLink>
          )}

          <NavLink to="/certificates" className="global-nav-link" onClick={() => setMobileMenuOpen(false)}>
            <ShieldCheck size={18} /> <span>Certificates</span>
          </NavLink>
        </div>
      )}
    </nav>
  );
};
