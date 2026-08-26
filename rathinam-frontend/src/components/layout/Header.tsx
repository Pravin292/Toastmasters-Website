import React from 'react';
import { Menu, User as UserIcon } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { Badge } from '../common/Badge';
import { NotificationBell } from './NotificationBell';

interface HeaderProps {
  onToggleMobileSidebar: () => void;
  title: string;
}

export const Header: React.FC<HeaderProps> = ({ onToggleMobileSidebar, title }) => {
  const { user } = useAuth();

  return (
    <header className="header">
      <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
        <button
          onClick={onToggleMobileSidebar}
          style={{ background: 'none', border: 'none', color: '#FFF', cursor: 'pointer', display: 'flex' }}
          className="mobile-menu-btn"
        >
          <Menu size={22} />
        </button>
        <div className="page-title-area">
          <h2>{title}</h2>
        </div>
      </div>

      <div className="header-actions">
        <NotificationBell />

        {user && (
          <div className="user-badge-container">
            <UserIcon size={16} color="#F2DF74" />
            <span style={{ fontSize: '0.9rem', fontWeight: 600 }}>{user.email}</span>
            <Badge variant={user.role === 'ADMIN' ? 'admin' : user.role === 'OFFICER' || user.role === 'PRESIDENT' ? 'officer' : 'default'}>
              {user.role}
            </Badge>
          </div>
        )}
      </div>
    </header>
  );
};
