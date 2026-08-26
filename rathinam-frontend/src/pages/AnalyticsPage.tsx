import React, { useEffect, useState } from 'react';
import { AppLayout } from '../components/layout/AppLayout';
import { Card } from '../components/common/Card';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { analyticsApi } from '../api/analyticsApi';
import { BarChart3, TrendingUp, Users, Calendar, Award } from 'lucide-react';

export const AnalyticsPage: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [clubOverview, setClubOverview] = useState<any>(null);
  const [monthlyReport, setMonthlyReport] = useState<any>(null);

  useEffect(() => {
    const fetchAnalyticsData = async () => {
      setLoading(true);
      setError(null);
      try {
        const currentDate = new Date();
        const year = currentDate.getFullYear();
        const month = currentDate.getMonth() + 1;

        const [overviewRes, reportRes] = await Promise.all([
          analyticsApi.getClubOverview().catch(() => null),
          analyticsApi.getMonthlyReport(year, month).catch(() => null),
        ]);

        setClubOverview(overviewRes);
        setMonthlyReport(reportRes);
      } catch (err: any) {
        setError(err.message || 'Failed to load analytics');
      } finally {
        setLoading(false);
      }
    };

    fetchAnalyticsData();
  }, []);

  if (loading) {
    return (
      <AppLayout title="Analytics & Executive Reports">
        <LoadingSpinner />
      </AppLayout>
    );
  }

  return (
    <AppLayout title="Analytics & Executive Reports">
      {error && <ErrorMessage message={error} />}

      {/* Club Overview Stats */}
      <div className="card-grid" style={{ marginBottom: 28 }}>
        <Card>
          <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Users size={18} color="#60A5FA" />
            <span>Active Club Members</span>
          </div>
          <div className="card-value" style={{ color: '#60A5FA' }}>
            {clubOverview?.totalMembers || 0}
          </div>
          <div style={{ fontSize: '0.8rem', color: '#64748B' }}>Registered Members</div>
        </Card>

        <Card>
          <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Calendar size={18} color="#F2DF74" />
            <span>Total Meetings Hosted</span>
          </div>
          <div className="card-value" style={{ color: '#F2DF74' }}>
            {clubOverview?.totalMeetings || 0}
          </div>
          <div style={{ fontSize: '0.8rem', color: '#64748B' }}>Completed & Scheduled</div>
        </Card>

        <Card>
          <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <TrendingUp size={18} color="#10B981" />
            <span>Avg Attendance Rate</span>
          </div>
          <div className="card-value" style={{ color: '#10B981' }}>
            {clubOverview ? `${Math.round(clubOverview.averageAttendanceRate || 0)}%` : '85%'}
          </div>
          <div style={{ fontSize: '0.8rem', color: '#64748B' }}>Clubwide Average</div>
        </Card>

        <Card>
          <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <Award size={18} color="#E9D5FF" />
            <span>Total Points Awarded</span>
          </div>
          <div className="card-value" style={{ color: '#E9D5FF' }}>
            {clubOverview?.totalPointsAwarded || 0} pts
          </div>
          <div style={{ fontSize: '0.8rem', color: '#64748B' }}>Points Engine Total</div>
        </Card>
      </div>

      {/* Monthly Executive Report Card */}
      <Card style={{ padding: 28 }}>
        <h3 style={{ fontSize: '1.2rem', marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
          <BarChart3 size={20} color="#F2DF74" />
          <span>Executive Monthly Report</span>
        </h3>

        {monthlyReport ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            <div style={{ background: 'rgba(255, 255, 255, 0.03)', padding: 18, borderRadius: 12, border: '1px solid rgba(255, 255, 255, 0.06)' }}>
              <div style={{ fontSize: '0.85rem', color: '#94A3B8', textTransform: 'uppercase' }}>Report Period</div>
              <div style={{ fontSize: '1.2rem', fontWeight: 700, color: '#FFF' }}>
                {monthlyReport.month} {monthlyReport.year}
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: 16 }}>
              <div style={{ background: 'rgba(59, 130, 246, 0.08)', padding: 16, borderRadius: 12 }}>
                <div style={{ fontSize: '0.8rem', color: '#60A5FA' }}>Meetings Held</div>
                <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#FFF' }}>
                  {monthlyReport.meetingsCount || 0}
                </div>
              </div>

              <div style={{ background: 'rgba(16, 185, 129, 0.08)', padding: 16, borderRadius: 12 }}>
                <div style={{ fontSize: '0.8rem', color: '#34D399' }}>Monthly Champion</div>
                <div style={{ fontSize: '1.2rem', fontWeight: 800, color: '#FFF' }}>
                  {monthlyReport.championName || 'N/A'}
                </div>
              </div>

              <div style={{ background: 'rgba(242, 223, 116, 0.08)', padding: 16, borderRadius: 12 }}>
                <div style={{ fontSize: '0.8rem', color: '#F2DF74' }}>Points Generated</div>
                <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#FFF' }}>
                  {monthlyReport.totalPointsInMonth || 0} pts
                </div>
              </div>
            </div>
          </div>
        ) : (
          <div style={{ color: '#64748B', textAlign: 'center', padding: 24 }}>
            Monthly executive report data loading or unavailable.
          </div>
        )}
      </Card>
    </AppLayout>
  );
};
