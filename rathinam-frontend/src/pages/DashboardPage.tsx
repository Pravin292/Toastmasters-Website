import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { AppLayout } from '../components/layout/AppLayout';
import { meetingApi } from '../api/meetingApi';
import { rankingApi } from '../api/rankingApi';
import { analyticsApi } from '../api/analyticsApi';
import { achievementApi } from '../api/achievementApi';
import { pointApi } from '../api/pointApi';
import { 
  Meeting, 
  MemberRanking, 
  MemberAnalytics, 
  Achievement, 
  Certificate, 
  MemberPointsSummary 
} from '../types';
import { 
  Trophy, 
  Calendar, 
  Award, 
  CheckCircle2, 
  Star, 
  History, 
  ShieldCheck, 
  Users,
  ChevronRight
} from 'lucide-react';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorMessage } from '../components/common/ErrorMessage';
import '../styles/DashboardPage.css';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Data States
  const [rankInfo, setRankInfo] = useState<MemberRanking | null>(null);
  const [analytics, setAnalytics] = useState<MemberAnalytics | null>(null);
  const [meetings, setMeetings] = useState<Meeting[]>([]);
  const [achievements, setAchievements] = useState<Achievement[]>([]);
  const [certificates, setCertificates] = useState<Certificate[]>([]);
  const [pointsSummary, setPointsSummary] = useState<MemberPointsSummary | null>(null);

  useEffect(() => {
    const loadDashboardData = async () => {
      setLoading(true);
      setError(null);
      try {
        const meetingsRes = await meetingApi.getMeetings(0, 5);
        setMeetings(meetingsRes.content || []);

        if (user?.memberId) {
          const [rankRes, analyticsRes, achRes, certRes, pointsRes] = await Promise.all([
            rankingApi.getMemberRank(user.memberId).catch(() => null),
            analyticsApi.getMemberAnalytics(user.memberId).catch(() => null),
            achievementApi.getMemberAchievements(user.memberId).catch(() => []),
            achievementApi.getMemberCertificates(user.memberId).catch(() => []),
            pointApi.getMemberPointEvents(user.memberId).catch(() => null),
          ]);

          setRankInfo(rankRes);
          setAnalytics(analyticsRes);
          setAchievements(achRes);
          setCertificates(certRes);
          setPointsSummary(pointsRes);
        }
      } catch (err: any) {
        setError(err.message || 'Failed to load dashboard data');
      } finally {
        setLoading(false);
      }
    };

    loadDashboardData();
  }, [user]);

  if (loading) {
    return (
      <AppLayout title="Dashboard">
        <div style={{ display: 'flex', justifyContent: 'center', padding: '60px 0' }}>
          <LoadingSpinner />
        </div>
      </AppLayout>
    );
  }

  const memberDisplayName = analytics?.displayName || user?.firstName || user?.email?.split('@')[0] || 'Member';
  const pointEvents = pointsSummary?.events?.content || [];

  return (
    <AppLayout title="Dashboard">
      <div className="dashboard-page-container">
        {error && <ErrorMessage message={error} />}

        {/* Compact Welcome Header */}
        <div className="dashboard-welcome-header">
          <h1 className="dashboard-welcome-title">Welcome back, {memberDisplayName} 👋</h1>
          <p className="dashboard-welcome-subtitle">
            Here is your Toastmasters performance overview & activity log.
          </p>
        </div>

        {/* Six Metric Cards Grid (Strictly Identical 6 Equal Cards) */}
        <div className="dashboard-metrics-grid">
          {/* Card 1: Total Points */}
          <div className="metric-card">
            <div className="metric-card-header">
              <span className="metric-card-title">Total Points</span>
              <div className="metric-card-icon-box">
                <Star size={16} />
              </div>
            </div>
            <div className="metric-card-body">
              <span className="metric-card-value">
                {pointsSummary?.totalPoints ?? analytics?.totalPoints ?? 0}
              </span>
              <span className="metric-card-subtitle">earned</span>
            </div>
          </div>

          {/* Card 2: Club Rank */}
          <div className="metric-card">
            <div className="metric-card-header">
              <span className="metric-card-title">Club Rank</span>
              <div className="metric-card-icon-box">
                <Trophy size={16} color="#FBBF24" />
              </div>
            </div>
            <div className="metric-card-body">
              <span className="metric-card-value" style={{ color: '#FBBF24' }}>
                {rankInfo?.rank ? `#${rankInfo.rank}` : 'N/A'}
              </span>
              <span className="metric-card-subtitle">leaderboard</span>
            </div>
          </div>

          {/* Card 3: Attendance Rate */}
          <div className="metric-card">
            <div className="metric-card-header">
              <span className="metric-card-title">Attendance</span>
              <div className="metric-card-icon-box">
                <CheckCircle2 size={16} color="#34D399" />
              </div>
            </div>
            <div className="metric-card-body">
              <span className="metric-card-value" style={{ color: '#34D399' }}>
                {analytics?.attendancePercentage ? `${Math.round(analytics.attendancePercentage)}%` : '100%'}
              </span>
              <span className="metric-card-subtitle">rate</span>
            </div>
          </div>

          {/* Card 4: Roles Performed */}
          <div className="metric-card">
            <div className="metric-card-header">
              <span className="metric-card-title">Roles</span>
              <div className="metric-card-icon-box">
                <Users size={16} color="#60A5FA" />
              </div>
            </div>
            <div className="metric-card-body">
              <span className="metric-card-value">
                {analytics?.totalRolesPerformed || 0}
              </span>
              <span className="metric-card-subtitle">performed</span>
            </div>
          </div>

          {/* Card 5: Badges Earned */}
          <div className="metric-card">
            <div className="metric-card-header">
              <span className="metric-card-title">Badges</span>
              <div className="metric-card-icon-box">
                <Award size={16} color="#A78BFA" />
              </div>
            </div>
            <div className="metric-card-body">
              <span className="metric-card-value">
                {achievements.length}
              </span>
              <span className="metric-card-subtitle">unlocked</span>
            </div>
          </div>

          {/* Card 6: Certificates */}
          <div className="metric-card">
            <div className="metric-card-header">
              <span className="metric-card-title">Certificates</span>
              <div className="metric-card-icon-box">
                <ShieldCheck size={16} color="#F472B6" />
              </div>
            </div>
            <div className="metric-card-body">
              <span className="metric-card-value">
                {certificates.length}
              </span>
              <span className="metric-card-subtitle">issued</span>
            </div>
          </div>
        </div>

        {/* Section 1: Recent & Upcoming Meetings */}
        <div className="dashboard-section-card">
          <div className="dashboard-section-header">
            <div>
              <h2 className="dashboard-section-title">
                <Calendar size={18} color="#38BDF8" />
                <span>Recent & Upcoming Meetings</span>
              </h2>
              <p className="dashboard-section-desc">Scheduled club sessions and meeting details</p>
            </div>
          </div>

          {meetings.length > 0 ? (
            <div className="dashboard-table-wrapper">
              <table className="dashboard-table">
                <thead>
                  <tr>
                    <th>Meeting #</th>
                    <th>Theme</th>
                    <th>Type</th>
                    <th>Status</th>
                    <th>Date & Time</th>
                    <th style={{ textAlign: 'right' }}>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {meetings.map((m) => (
                    <tr key={m.id}>
                      <td>
                        <strong>#{m.meetingNumber}</strong>
                      </td>
                      <td>{m.theme || 'Regular Club Meeting'}</td>
                      <td>{m.meetingType || 'REGULAR'}</td>
                      <td>
                        <span className={`status-pill ${m.status?.toLowerCase()}`}>
                          {m.status}
                        </span>
                      </td>
                      <td>{new Date(m.meetingStart).toLocaleString()}</td>
                      <td style={{ textAlign: 'right' }}>
                        <button
                          className="btn-table-action"
                          onClick={() => navigate(`/meetings/${m.id}`)}
                        >
                          View Details
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="empty-dashboard-state">
              No meetings recorded yet.
            </div>
          )}
        </div>

        {/* Section 2: Points History & Audit Log */}
        <div className="dashboard-section-card">
          <div className="dashboard-section-header">
            <div>
              <h2 className="dashboard-section-title">
                <History size={18} color="#FBBF24" />
                <span>Points History & Audit Log</span>
              </h2>
              <p className="dashboard-section-desc">Recent point events and performance credits</p>
            </div>
          </div>

          {pointEvents.length > 0 ? (
            <div className="dashboard-table-wrapper">
              <table className="dashboard-table">
                <thead>
                  <tr>
                    <th>Points</th>
                    <th>Reason / Activity</th>
                    <th>Source Type</th>
                    <th>Meeting #</th>
                    <th>Date & Time</th>
                  </tr>
                </thead>
                <tbody>
                  {pointEvents.slice(0, 5).map((ev) => (
                    <tr key={ev.id}>
                      <td>
                        <span style={{ color: '#34D399', fontWeight: 800 }}>+{ev.points} pts</span>
                      </td>
                      <td>{ev.reason || ev.pointRuleName || 'Activity Participation'}</td>
                      <td>{ev.sourceType || 'AUTOMATIC'}</td>
                      <td>{ev.meetingNumber ? `#${ev.meetingNumber}` : '—'}</td>
                      <td>{new Date(ev.createdAt).toLocaleString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="empty-dashboard-state">
              No point events recorded yet.
            </div>
          )}
        </div>

        {/* Section 3: My Badges & Achievements */}
        <div className="dashboard-section-card">
          <div className="dashboard-section-header">
            <div>
              <h2 className="dashboard-section-title">
                <Award size={18} color="#A78BFA" />
                <span>My Badges & Achievements</span>
              </h2>
              <p className="dashboard-section-desc">Milestones unlocked in your public speaking journey</p>
            </div>
          </div>

          {achievements.length > 0 ? (
            <div className="dashboard-badges-grid">
              {achievements.map((ach) => (
                <div key={ach.id} className="dashboard-badge-card">
                  <div className="dashboard-badge-icon">
                    {ach.icon || '🏆'}
                  </div>
                  <div>
                    <div className="dashboard-badge-name">{ach.achievementName || ach.name}</div>
                    <div className="dashboard-badge-desc">{ach.description || 'Milestone achieved'}</div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="empty-dashboard-state">
              No achievements earned yet. Keep attending meetings and taking on roles!
            </div>
          )}
        </div>
      </div>
    </AppLayout>
  );
};
