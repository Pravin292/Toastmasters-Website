import React, { useEffect, useState } from 'react';
import { AppLayout } from '../components/layout/AppLayout';
import { Card } from '../components/common/Card';
import { Badge } from '../components/common/Badge';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { useAuth } from '../context/AuthContext';
import { meetingApi } from '../api/meetingApi';
import { rankingApi } from '../api/rankingApi';
import { analyticsApi } from '../api/analyticsApi';
import { achievementApi } from '../api/achievementApi';
import { Meeting, MemberRanking, MemberAnalytics, Achievement, Certificate } from '../types';
import { Trophy, Calendar, Award, CheckCircle2, ChevronRight, Star } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [rankInfo, setRankInfo] = useState<MemberRanking | null>(null);
  const [analytics, setAnalytics] = useState<MemberAnalytics | null>(null);
  const [meetings, setMeetings] = useState<Meeting[]>([]);
  const [achievements, setAchievements] = useState<Achievement[]>([]);
  const [certificates, setCertificates] = useState<Certificate[]>([]);

  useEffect(() => {
    const loadDashboardData = async () => {
      setLoading(true);
      setError(null);
      try {
        const meetingsRes = await meetingApi.getMeetings(0, 5);
        setMeetings(meetingsRes.content);

        if (user?.memberId) {
          const [rankRes, analyticsRes, achRes, certRes] = await Promise.all([
            rankingApi.getMemberRank(user.memberId).catch(() => null),
            analyticsApi.getMemberAnalytics(user.memberId).catch(() => null),
            achievementApi.getMemberAchievements(user.memberId).catch(() => []),
            achievementApi.getMemberCertificates(user.memberId).catch(() => []),
          ]);

          setRankInfo(rankRes);
          setAnalytics(analyticsRes);
          setAchievements(achRes);
          setCertificates(certRes);
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
        <LoadingSpinner />
      </AppLayout>
    );
  }

  return (
    <AppLayout title="Dashboard">
      {error && <ErrorMessage message={error} />}

      {/* Top Welcome Banner */}
      <div style={{
        background: 'linear-gradient(135deg, rgba(119, 33, 111, 0.4) 0%, rgba(0, 65, 101, 0.4) 100%)',
        border: '1px solid rgba(255, 255, 255, 0.1)',
        borderRadius: 18,
        padding: '24px 28px',
        marginBottom: 28,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        flexWrap: 'wrap',
        gap: 16
      }}>
        <div>
          <h2 style={{ fontSize: '1.6rem', marginBottom: 4 }}>
            Hello, {user?.email}! 👋
          </h2>
          <p style={{ color: '#94A3B8', fontSize: '0.95rem' }}>
            Welcome to your Rathinam Toastmasters Member Dashboard.
          </p>
        </div>
        <div style={{ display: 'flex', gap: 12 }}>
          <button className="btn btn-gold" onClick={() => navigate('/rankings')}>
            <Trophy size={16} /> View Leaderboard
          </button>
        </div>
      </div>

      {/* Metrics Grid */}
      <div className="card-grid">
        <Card>
          <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Trophy size={18} color="#F2DF74" />
            <span>Leaderboard Rank</span>
          </div>
          <div className="card-value">
            #{rankInfo?.rank || analytics?.rank || 'N/A'}
          </div>
          <div style={{ fontSize: '0.8rem', color: '#64748B', marginTop: 4 }}>
            {rankInfo ? `${rankInfo.totalPoints} Total Points` : 'Monthly Standings'}
          </div>
        </Card>

        <Card>
          <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Star size={18} color="#10B981" />
            <span>Total Points</span>
          </div>
          <div className="card-value" style={{ color: '#10B981' }}>
            {analytics?.totalPoints || rankInfo?.totalPoints || 0}
          </div>
          <div style={{ fontSize: '0.8rem', color: '#64748B', marginTop: 4 }}>
            Earned via Meetings & Roles
          </div>
        </Card>

        <Card>
          <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <CheckCircle2 size={18} color="#60A5FA" />
            <span>Attendance Rate</span>
          </div>
          <div className="card-value" style={{ color: '#60A5FA' }}>
            {analytics ? `${Math.round(analytics.attendanceRate)}%` : '100%'}
          </div>
          <div style={{ fontSize: '0.8rem', color: '#64748B', marginTop: 4 }}>
            Meeting Participation
          </div>
        </Card>

        <Card>
          <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Award size={18} color="#E9D5FF" />
            <span>Certificates & Badges</span>
          </div>
          <div className="card-value" style={{ color: '#E9D5FF' }}>
            {certificates.length + achievements.length}
          </div>
          <div style={{ fontSize: '0.8rem', color: '#64748B', marginTop: 4 }}>
            Recognitions Earned
          </div>
        </Card>
      </div>

      {/* Main Content Sections: Meetings + Badges */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: 24 }}>
        {/* Recent Meetings */}
        <Card style={{ gridColumn: 'span 2' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 18 }}>
            <h3 style={{ fontSize: '1.1rem', display: 'flex', alignItems: 'center', gap: 8 }}>
              <Calendar size={20} color="#F2DF74" />
              <span>Recent & Upcoming Meetings</span>
            </h3>
            <button
              onClick={() => navigate('/meetings')}
              style={{ background: 'none', border: 'none', color: '#F2DF74', cursor: 'pointer', fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: 4 }}
            >
              <span>View All</span> <ChevronRight size={16} />
            </button>
          </div>

          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Meeting #</th>
                  <th>Theme</th>
                  <th>Type</th>
                  <th>Status</th>
                  <th>Date & Time</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {meetings.map((meeting) => (
                  <tr key={meeting.id}>
                    <td style={{ fontWeight: 700 }}>Meeting #{meeting.meetingNumber}</td>
                    <td>{meeting.theme || 'Regular Meeting'}</td>
                    <td>{meeting.meetingType}</td>
                    <td>
                      <Badge variant={meeting.status.toLowerCase() as any}>
                        {meeting.status}
                      </Badge>
                    </td>
                    <td style={{ fontSize: '0.85rem', color: '#94A3B8' }}>
                      {new Date(meeting.meetingStart).toLocaleString()}
                    </td>
                    <td>
                      <button
                        className="btn btn-secondary"
                        style={{ padding: '6px 12px', fontSize: '0.8rem' }}
                        onClick={() => navigate(`/meetings/${meeting.id}`)}
                      >
                        Details
                      </button>
                    </td>
                  </tr>
                ))}
                {meetings.length === 0 && (
                  <tr>
                    <td colSpan={6} style={{ textAlign: 'center', color: '#64748B', padding: 24 }}>
                      No meetings scheduled yet.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </Card>

        {/* Badges / Achievements Preview */}
        <Card>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 18 }}>
            <h3 style={{ fontSize: '1.1rem', display: 'flex', alignItems: 'center', gap: 8 }}>
              <Award size={20} color="#E9D5FF" />
              <span>My Achievements</span>
            </h3>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
            {achievements.map((ach) => (
              <div
                key={ach.id}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: 12,
                  background: 'rgba(255, 255, 255, 0.03)',
                  padding: '12px 14px',
                  borderRadius: 12,
                  border: '1px solid rgba(255, 255, 255, 0.06)'
                }}
              >
                <div style={{ fontSize: '1.6rem' }}>{ach.icon || '🏅'}</div>
                <div>
                  <div style={{ fontWeight: 600, fontSize: '0.92rem' }}>{ach.name}</div>
                  <div style={{ fontSize: '0.78rem', color: '#94A3B8' }}>{ach.description}</div>
                </div>
              </div>
            ))}

            {achievements.length === 0 && (
              <div style={{ textAlign: 'center', color: '#64748B', padding: '30px 0' }}>
                Complete meeting roles & attend meetings to unlock achievements!
              </div>
            )}
          </div>
        </Card>
      </div>
    </AppLayout>
  );
};
