import React, { useState, useRef, useEffect } from 'react';
import { NavLink, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { notificationApi } from '../../api/notificationApi';
import { Notification } from '../../types';
import { 
  LayoutDashboard, 
  Calendar, 
  Users, 
  Mic, 
  BookOpen, 
  Trophy, 
  BarChart3, 
  TrendingUp, 
  MoreHorizontal, 
  Search, 
  Bell, 
  ChevronDown, 
  LogOut, 
  ShieldCheck, 
  Award, 
  CheckCircle2, 
  X,
  Menu
} from 'lucide-react';
import '../../styles/GlobalNavbar.css';

export const GlobalNavbar: React.FC = () => {
  const { user, isOfficer, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [profileDropdownOpen, setProfileDropdownOpen] = useState(false);
  const [moreDropdownOpen, setMoreDropdownOpen] = useState(false);
  const [notificationOpen, setNotificationOpen] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [searchModalOpen, setSearchModalOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState<number>(0);

  const profileRef = useRef<HTMLDivElement>(null);
  const moreRef = useRef<HTMLDivElement>(null);
  const notifRef = useRef<HTMLDivElement>(null);

  // Fetch unread notifications
  const fetchNotifications = async () => {
    try {
      const [resList, resCount] = await Promise.all([
        notificationApi.getUnreadNotifications(0, 5).catch(() => ({ content: [] })),
        notificationApi.getUnreadCount().catch(() => ({ unreadCount: 0 })),
      ]);
      setNotifications(resList.content || []);
      setUnreadCount(resCount.unreadCount || 0);
    } catch {
      // Quiet fail if guest
    }
  };

  useEffect(() => {
    if (user) {
      fetchNotifications();
    }
  }, [user]);

  // Click outside listener
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as Node;
      if (profileRef.current && !profileRef.current.contains(target)) {
        setProfileDropdownOpen(false);
      }
      if (moreRef.current && !moreRef.current.contains(target)) {
        setMoreDropdownOpen(false);
      }
      if (notifRef.current && !notifRef.current.contains(target)) {
        setNotificationOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleMarkAllRead = async () => {
    try {
      await notificationApi.markAllAsRead();
      setNotifications([]);
      setUnreadCount(0);
    } catch (err) {
      console.error(err);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  // Derive user display details
  const displayName = user?.email?.split('@')[0] || 'Nishhz';
  const formattedName = displayName.charAt(0).toUpperCase() + displayName.slice(1);
  const initial = formattedName.charAt(0).toUpperCase();
  const userRole = isOfficer ? 'Club Officer' : (user?.role === 'ADMIN' ? 'President' : 'Club Member');

  const navItems = [
    { label: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { label: 'Meetings', path: '/meetings', icon: Calendar },
    { label: 'Members', path: '/members', icon: Users },
    { label: 'Roles & Signups', path: '/meetings', icon: Mic },
    { label: 'Pathways', path: '/achievements', icon: BookOpen },
    { label: 'Achievements', path: '/achievements', icon: Trophy },
    { label: 'Leaderboard', path: '/rankings', icon: BarChart3 },
    { label: 'Analytics', path: '/analytics', icon: TrendingUp },
  ];

  return (
    <header className="tm-navbar-header">
      <div className="tm-navbar-inner">
        {/* Brand Logo & Tagline */}
        <NavLink to="/dashboard" className="tm-brand-link">
          <img 
            src="/assets/toastmasters-logo.png" 
            alt="Toastmasters International" 
            className="tm-brand-logo" 
          />
          <div className="tm-brand-text">
            <span className="tm-brand-name">RATHINAM TOASTMASTERS</span>
            <span className="tm-brand-motto">Speak • Lead • Grow</span>
          </div>
        </NavLink>

        {/* Center Desktop Navigation Links */}
        <nav className="tm-nav-links">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.path && 
              (item.label === 'Members' ? location.pathname.startsWith('/members') : 
               item.label === 'Meetings' ? location.pathname === '/meetings' : true);

            return (
              <NavLink
                key={item.label}
                to={item.path}
                className={({ isActive: matchActive }) => 
                  `tm-nav-item ${matchActive && (item.label !== 'Roles & Signups' && item.label !== 'Pathways') ? 'active' : ''}`
                }
              >
                <Icon size={16} className="tm-nav-icon" />
                <span>{item.label}</span>
              </NavLink>
            );
          })}

          {/* More Menu Dropdown */}
          <div className="tm-more-container" ref={moreRef}>
            <button 
              type="button"
              className={`tm-nav-item tm-more-btn ${moreDropdownOpen ? 'active' : ''}`}
              onClick={() => setMoreDropdownOpen(!moreDropdownOpen)}
            >
              <MoreHorizontal size={16} className="tm-nav-icon" />
              <span>More</span>
            </button>

            {moreDropdownOpen && (
              <div className="tm-dropdown-menu tm-more-dropdown">
                <NavLink 
                  to="/certificates" 
                  className="tm-dropdown-item"
                  onClick={() => setMoreDropdownOpen(false)}
                >
                  <Award size={16} />
                  <span>Certificates & Awards</span>
                </NavLink>

                {isOfficer && (
                  <NavLink 
                    to="/admin" 
                    className="tm-dropdown-item"
                    onClick={() => setMoreDropdownOpen(false)}
                  >
                    <ShieldCheck size={16} />
                    <span>Officer Admin Portal</span>
                  </NavLink>
                )}

                <NavLink 
                  to="/notifications" 
                  className="tm-dropdown-item"
                  onClick={() => setMoreDropdownOpen(false)}
                >
                  <Bell size={16} />
                  <span>Notification Center</span>
                </NavLink>
              </div>
            )}
          </div>
        </nav>

        {/* Right Utility Actions */}
        <div className="tm-navbar-actions">
          {/* Search Trigger */}
          <button 
            type="button" 
            className="tm-action-btn"
            title="Search members or meetings"
            onClick={() => setSearchModalOpen(true)}
          >
            <Search size={18} />
          </button>

          {/* Notifications Bell */}
          <div className="tm-notif-container" ref={notifRef}>
            <button 
              type="button" 
              className="tm-action-btn tm-notif-btn"
              title="Notifications"
              onClick={() => setNotificationOpen(!notificationOpen)}
            >
              <Bell size={18} />
              {unreadCount > 0 && <span className="tm-notif-badge" />}
            </button>

            {notificationOpen && (
              <div className="tm-dropdown-menu tm-notif-dropdown">
                <div className="tm-notif-header">
                  <span className="tm-notif-title">Notifications</span>
                  {unreadCount > 0 && (
                    <button type="button" className="tm-mark-read-btn" onClick={handleMarkAllRead}>
                      Mark all as read
                    </button>
                  )}
                </div>

                <div className="tm-notif-list">
                  {notifications.length === 0 ? (
                    <div className="tm-notif-empty">
                      <CheckCircle2 size={24} color="#10B981" />
                      <p>You're all caught up!</p>
                    </div>
                  ) : (
                    notifications.map((n) => (
                      <div key={n.id} className="tm-notif-item">
                        <div className="tm-notif-item-title">{n.title}</div>
                        <div className="tm-notif-item-msg">{n.message}</div>
                      </div>
                    ))
                  )}
                </div>

                <NavLink 
                  to="/notifications" 
                  className="tm-notif-footer"
                  onClick={() => setNotificationOpen(false)}
                >
                  View all notifications
                </NavLink>
              </div>
            )}
          </div>

          {/* User Profile Chip */}
          <div className="tm-profile-container" ref={profileRef}>
            <button 
              type="button" 
              className="tm-profile-chip"
              onClick={() => setProfileDropdownOpen(!profileDropdownOpen)}
            >
              <div className="tm-profile-avatar">
                {initial}
              </div>
              <div className="tm-profile-meta">
                <span className="tm-profile-name">{formattedName}</span>
                <span className="tm-profile-role">{userRole}</span>
              </div>
              <ChevronDown size={14} className="tm-profile-caret" />
            </button>

            {profileDropdownOpen && (
              <div className="tm-dropdown-menu tm-profile-dropdown">
                <div className="tm-profile-menu-header">
                  <div className="tm-menu-avatar">{initial}</div>
                  <div>
                    <div className="tm-menu-name">{formattedName}</div>
                    <div className="tm-menu-email">{user?.email}</div>
                  </div>
                </div>

                <div className="tm-dropdown-divider" />

                <NavLink 
                  to="/members" 
                  className="tm-dropdown-item"
                  onClick={() => setProfileDropdownOpen(false)}
                >
                  <Users size={16} />
                  <span>Club Directory</span>
                </NavLink>

                <NavLink 
                  to="/achievements" 
                  className="tm-dropdown-item"
                  onClick={() => setProfileDropdownOpen(false)}
                >
                  <Trophy size={16} />
                  <span>My Achievements</span>
                </NavLink>

                {isOfficer && (
                  <NavLink 
                    to="/admin" 
                    className="tm-dropdown-item"
                    onClick={() => setProfileDropdownOpen(false)}
                  >
                    <ShieldCheck size={16} />
                    <span>Officer Portal</span>
                  </NavLink>
                )}

                <div className="tm-dropdown-divider" />

                <button 
                  type="button" 
                  className="tm-dropdown-item tm-logout-item"
                  onClick={handleLogout}
                >
                  <LogOut size={16} />
                  <span>Sign Out</span>
                </button>
              </div>
            )}
          </div>

          {/* Mobile Hamburger Toggle */}
          <button 
            type="button"
            className="tm-mobile-toggle"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          >
            {mobileMenuOpen ? <X size={22} /> : <Menu size={22} />}
          </button>
        </div>
      </div>

      {/* Mobile Drawer Menu */}
      {mobileMenuOpen && (
        <div className="tm-mobile-menu">
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.label}
                to={item.path}
                className="tm-mobile-item"
                onClick={() => setMobileMenuOpen(false)}
              >
                <Icon size={18} />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
          <div className="tm-dropdown-divider" />
          <button type="button" className="tm-mobile-item tm-logout-item" onClick={handleLogout}>
            <LogOut size={18} />
            <span>Sign Out</span>
          </button>
        </div>
      )}

      {/* Quick Search Modal */}
      {searchModalOpen && (
        <div className="tm-search-backdrop" onClick={() => setSearchModalOpen(false)}>
          <div className="tm-search-modal" onClick={(e) => e.stopPropagation()}>
            <div className="tm-search-input-wrap">
              <Search size={20} color="#64748B" />
              <input 
                type="text"
                autoFocus
                placeholder="Quick search members, meetings, or pathways..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') {
                    setSearchModalOpen(false);
                    navigate(`/members?search=${encodeURIComponent(searchQuery)}`);
                  }
                }}
              />
              <button 
                type="button" 
                className="tm-search-close"
                onClick={() => setSearchModalOpen(false)}
              >
                <X size={18} />
              </button>
            </div>
          </div>
        </div>
      )}
    </header>
  );
};
