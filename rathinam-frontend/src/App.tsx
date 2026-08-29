import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { LoginPage } from './pages/LoginPage';
import { DashboardPage } from './pages/DashboardPage';
import { MeetingsPage } from './pages/MeetingsPage';
import { MeetingDetailPage } from './pages/MeetingDetailPage';
import { MembersPage } from './pages/MembersPage';
import { LeaderboardPage } from './pages/LeaderboardPage';
import { AchievementsPage } from './pages/AchievementsPage';
import { AnalyticsPage } from './pages/AnalyticsPage';
import { CertificatesPage } from './pages/CertificatesPage';
import { NotificationsPage } from './pages/NotificationsPage';
import { AdminPage } from './pages/AdminPage';
import { LoadingSpinner } from './components/common/LoadingSpinner';

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { token, loading } = useAuth();
  if (loading) return <LoadingSpinner />;
  if (!token) return <Navigate to="/login" replace />;
  return <>{children}</>;
};

const OfficerRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isOfficer, loading } = useAuth();
  if (loading) return <LoadingSpinner />;
  if (!isOfficer) return <Navigate to="/dashboard" replace />;
  return <>{children}</>;
};

export const App: React.FC = () => {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
          <Route path="/meetings" element={<ProtectedRoute><MeetingsPage /></ProtectedRoute>} />
          <Route path="/meetings/:id" element={<ProtectedRoute><MeetingDetailPage /></ProtectedRoute>} />
          <Route path="/members" element={<ProtectedRoute><MembersPage /></ProtectedRoute>} />
          <Route path="/rankings" element={<ProtectedRoute><LeaderboardPage /></ProtectedRoute>} />
          <Route path="/achievements" element={<ProtectedRoute><AchievementsPage /></ProtectedRoute>} />
          <Route path="/analytics" element={<ProtectedRoute><AnalyticsPage /></ProtectedRoute>} />
          <Route path="/certificates" element={<ProtectedRoute><CertificatesPage /></ProtectedRoute>} />
          <Route path="/notifications" element={<ProtectedRoute><NotificationsPage /></ProtectedRoute>} />
          <Route path="/admin" element={<ProtectedRoute><OfficerRoute><AdminPage /></OfficerRoute></ProtectedRoute>} />

          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
};

export default App;
