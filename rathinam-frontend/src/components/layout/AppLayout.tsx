import React from 'react';
import { GlobalNavbar } from './GlobalNavbar';

interface AppLayoutProps {
  children: React.ReactNode;
  title?: string;
  fluid?: boolean;
}

export const AppLayout: React.FC<AppLayoutProps> = ({ children, fluid = false }) => {
  return (
    <div style={{ minHeight: '100vh', width: '100%', backgroundColor: '#F8FAFC', color: '#0F172A', display: 'flex', flexDirection: 'column' }}>
      <GlobalNavbar />
      <main style={{ flex: 1, width: '100%', margin: '0 auto', maxWidth: fluid ? '100%' : '1440px', padding: fluid ? '0' : '28px 24px' }}>
        {children}
      </main>
    </div>
  );
};
