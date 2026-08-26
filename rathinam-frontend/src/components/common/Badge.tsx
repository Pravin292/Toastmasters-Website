import React from 'react';

interface BadgeProps {
  children: React.ReactNode;
  variant?: 'scheduled' | 'in_progress' | 'completed' | 'cancelled' | 'admin' | 'officer' | 'default';
  className?: string;
}

export const Badge: React.FC<BadgeProps> = ({ children, variant = 'default', className = '' }) => {
  return (
    <span className={`badge badge-${variant.toLowerCase()} ${className}`}>
      {children}
    </span>
  );
};
