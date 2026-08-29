import React from 'react';
import { GlobalNavbar } from './GlobalNavbar';

interface AppLayoutProps {
  children: React.ReactNode;
  title: string;
}

export const AppLayout: React.FC<AppLayoutProps> = ({ children, title }) => {
  return (
    <div style={{ minHeight: '100vh', width: '100%', backgroundColor: '#030712', color: '#F8FAFC' }}>
      <GlobalNavbar />
      <main style={{ maxWidth: '1380px', margin: '0 auto', padding: '24px 20px' }}>
        {children}
      </main>
    </div>
  );
};
