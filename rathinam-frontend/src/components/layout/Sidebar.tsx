import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Calendar, Trophy, Award, BarChart3, Bell, Shield, LogOut } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

interface SidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ isOpen, onClose }) => {
  const { isOfficer, logout } = useAuth();

  return (
    <aside className={`sidebar ${isOpen ? 'open' : ''}`}>
      <div className="sidebar-header">
        <div style={{
          width: 36,
          height: 36,
          borderRadius: 10,
          background: 'linear-gradient(135deg, #77216F 0%, #F2DF74 100%)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#FFF',
          fontWeight: 800,
          fontSize: '1.1rem'
        }}>
          RT
        </div>
        <div>
          <div className="brand-title">Rathinam TM</div>
          <div style={{ fontSize: '0.72rem', color: '#64748B' }}>Toastmasters Digital</div>
        </div>
      </div>

      <nav className="sidebar-nav">
        <NavLink to="/dashboard" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`} onClick={onClose}>
          <LayoutDashboard size={18} />
          <span>Dashboard</span>
        </NavLink>

        <NavLink to="/meetings" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`} onClick={onClose}>
          <Calendar size={18} />
          <span>Meetings & Roles</span>
        </NavLink>

        <NavLink to="/rankings" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`} onClick={onClose}>
          <Trophy size={18} />
          <span>Leaderboard</span>
        </NavLink>

        <NavLink to="/achievements" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`} onClick={onClose}>
          <Award size={18} />
          <span>Badges & Certs</span>
        </NavLink>

        <NavLink to="/analytics" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`} onClick={onClose}>
          <BarChart3 size={18} />
          <span>Analytics</span>
        </NavLink>

        <NavLink to="/notifications" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`} onClick={onClose}>
          <Bell size={18} />
          <span>Notifications</span>
        </NavLink>

        {isOfficer && (
          <NavLink to="/admin" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`} onClick={onClose}>
            <Shield size={18} />
            <span>ExCom Admin</span>
          </NavLink>
        )}
      </nav>

      <div style={{ padding: '16px 12px', borderTop: '1px solid rgba(255, 255, 255, 0.08)' }}>
        <button
          onClick={logout}
          className="nav-item"
          style={{ width: '100%', background: 'none', border: 'none', cursor: 'pointer' }}
        >
          <LogOut size={18} />
          <span>Sign Out</span>
        </button>
      </div>
    </aside>
  );
};
