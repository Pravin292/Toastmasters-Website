import React, { useEffect, useState } from 'react';
import { AppLayout } from '../components/layout/AppLayout';
import { Card } from '../components/common/Card';
import { Badge } from '../components/common/Badge';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { useAuth } from '../context/AuthContext';
import { analyticsApi } from '../api/analyticsApi';
import {
  MemberAnalytics,
  MemberMonthlyPerformance,
  ClubOverviewAnalytics,
  MonthlyAnalytics,
} from '../types';
import {
  BarChart3,
  TrendingUp,
  Users,
  Calendar,
  Award,
  Trophy,
  UserCheck,
  Crown,
  Medal,
  Activity,
} from 'lucide-react';

const MONTHS = [
  { value: 1, label: 'January' },
  { value: 2, label: 'February' },
  { value: 3, label: 'March' },
  { value: 4, label: 'April' },
  { value: 5, label: 'May' },
  { value: 6, label: 'June' },
  { value: 7, label: 'July' },
  { value: 8, label: 'August' },
  { value: 9, label: 'September' },
  { value: 10, label: 'October' },
  { value: 11, label: 'November' },
  { value: 12, label: 'December' },
];

export const AnalyticsPage: React.FC = () => {
  const { user } = useAuth();
  const currentDate = new Date();
  const currentYear = currentDate.getFullYear();
  const currentMonth = currentDate.getMonth() + 1;

  const YEARS = [currentYear - 3, currentYear - 2, currentYear - 1, currentYear, currentYear + 1];

  const [selectedYear, setSelectedYear] = useState<number>(currentYear);
  const [selectedMonth, setSelectedMonth] = useState<number>(currentMonth);

  const [loading, setLoading] = useState(true);
  const [monthlyLoading, setMonthlyLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Phase 1 State
  const [memberAnalytics, setMemberAnalytics] = useState<MemberAnalytics | null>(null);
  const [performanceTrend, setPerformanceTrend] = useState<MemberMonthlyPerformance[]>([]);

  // Phase 2 State
  const [clubOverview, setClubOverview] = useState<ClubOverviewAnalytics | null>(null);

  // Phase 3 State
  const [monthlyAnalytics, setMonthlyAnalytics] = useState<MonthlyAnalytics | null>(null);

  useEffect(() => {
    const fetchInitialData = async () => {
      setLoading(true);
      setError(null);
      try {
        const promises: Promise<any>[] = [
          analyticsApi.getClubOverview().catch(() => null),
          analyticsApi.getMonthlyAnalytics(selectedYear, selectedMonth).catch(() => null),
        ];

        if (user?.memberId) {
          promises.push(analyticsApi.getMemberAnalytics(user.memberId).catch(() => null));
          promises.push(analyticsApi.getMemberPerformanceTrend(user.memberId, 6).catch(() => []));
        }

        const [overviewRes, monthlyRes, memberRes, trendRes] = await Promise.all(promises);

        setClubOverview(overviewRes);
        setMonthlyAnalytics(monthlyRes);
        if (memberRes) setMemberAnalytics(memberRes);
        if (trendRes) setPerformanceTrend(trendRes);
      } catch (err: any) {
        setError(err.message || 'Failed to load analytics data');
      } finally {
        setLoading(false);
      }
    };

    fetchInitialData();
  }, [user]);

  // Phase 3 Month/Year Selector Change
  useEffect(() => {
    const fetchMonthlyData = async () => {
      setMonthlyLoading(true);
      try {
        const res = await analyticsApi.getMonthlyAnalytics(selectedYear, selectedMonth);
        setMonthlyAnalytics(res);
      } catch (err: any) {
        setError(err.message || 'Failed to fetch monthly analytics');
      } finally {
        setMonthlyLoading(false);
      }
    };

    if (!loading) {
      fetchMonthlyData();
    }
  }, [selectedYear, selectedMonth]);

  if (loading) {
    return (
      <AppLayout title="Club Performance Analytics & Executive Reporting">
        <LoadingSpinner />
      </AppLayout>
    );
  }

  const selectedMonthLabel = MONTHS.find((m) => m.value === selectedMonth)?.label || '';

  return (
    <AppLayout title="Club Performance Analytics & Executive Reporting">
      {error && <ErrorMessage message={error} />}

      {/* PHASE 1: Personal Performance Analytics (For Logged-In Member) */}
      {memberAnalytics && (
        <div style={{ marginBottom: 36 }}>
          <h3 style={{ fontSize: '1.2rem', marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
            <Activity size={20} color="#10B981" />
            <span>My Performance Analytics ({memberAnalytics.displayName})</span>
          </h3>

          <div className="card-grid" style={{ marginBottom: 20 }}>
            <Card>
              <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <Trophy size={18} color="#F2DF74" />
                <span>Total Club Points</span>
              </div>
              <div className="card-value" style={{ color: '#F2DF74' }}>
                {memberAnalytics.totalPoints} pts
              </div>
              <div style={{ fontSize: '0.8rem', color: '#94A3B8' }}>
                Rank #{memberAnalytics.currentRank || 'N/A'}
              </div>
            </Card>

            <Card>
              <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <Calendar size={18} color="#60A5FA" />
                <span>Meetings Attended</span>
              </div>
              <div className="card-value" style={{ color: '#60A5FA' }}>
                {memberAnalytics.totalMeetingsAttended}
              </div>
              <div style={{ fontSize: '0.8rem', color: '#94A3B8' }}>
                {Math.round(memberAnalytics.attendancePercentage || 0)}% Attendance Rate
              </div>
            </Card>

            <Card>
              <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <UserCheck size={18} color="#10B981" />
                <span>Meeting Roles Taken</span>
              </div>
              <div className="card-value" style={{ color: '#10B981' }}>
                {memberAnalytics.totalRolesPerformed}
              </div>
              <div style={{ fontSize: '0.8rem', color: '#94A3B8' }}>Role Assignments</div>
            </Card>

            <Card>
              <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <Award size={18} color="#E9D5FF" />
                <span>Achievements Unlocked</span>
              </div>
              <div className="card-value" style={{ color: '#E9D5FF' }}>
                {memberAnalytics.achievementsEarned}
              </div>
              <div style={{ fontSize: '0.8rem', color: '#94A3B8' }}>Club Performance Milestones</div>
            </Card>
          </div>

          {/* Member 6-Month Performance Trend Table */}
          {performanceTrend.length > 0 && (
            <Card style={{ padding: 20 }}>
              <h4 style={{ fontSize: '1rem', color: '#FFF', marginBottom: 14, display: 'flex', alignItems: 'center', gap: 8 }}>
                <TrendingUp size={18} color="#60A5FA" />
                <span>Historical Monthly Performance Trend (Last 6 Months)</span>
              </h4>
              <div className="table-container">
                <table>
                  <thead>
                    <tr>
                      <th>Month</th>
                      <th>Points Earned</th>
                      <th>Meetings Attended</th>
                      <th>Roles Performed</th>
                    </tr>
                  </thead>
                  <tbody>
                    {performanceTrend.map((pt, idx) => (
                      <tr key={`${pt.year}-${pt.month}-${idx}`}>
                        <td style={{ fontWeight: 700, color: '#FFF' }}>
                          {MONTHS.find((m) => m.value === pt.month)?.label} {pt.year}
                        </td>
                        <td style={{ fontWeight: 800, color: '#10B981' }}>+{pt.points} pts</td>
                        <td>{pt.attendanceCount} meetings</td>
                        <td>{pt.rolesCount} roles</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </Card>
          )}
        </div>
      )}

      {/* PHASE 2: Club Overview Analytics */}
      <div style={{ marginBottom: 36 }}>
        <h3 style={{ fontSize: '1.2rem', marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
          <Users size={20} color="#60A5FA" />
          <span>Club Performance Overview (Rathinam Toastmasters)</span>
        </h3>

        <div className="card-grid">
          <Card>
            <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <Users size={18} color="#60A5FA" />
              <span>Active Club Members</span>
            </div>
            <div className="card-value" style={{ color: '#60A5FA' }}>
              {clubOverview?.activeMembers || 0}
            </div>
            <div style={{ fontSize: '0.8rem', color: '#94A3B8' }}>
              Total Registered: {clubOverview?.totalMembers || 0}
            </div>
          </Card>

          <Card>
            <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <Calendar size={18} color="#F2DF74" />
              <span>Total Meetings Hosted</span>
            </div>
            <div className="card-value" style={{ color: '#F2DF74' }}>
              {clubOverview?.totalMeetings || 0}
            </div>
            <div style={{ fontSize: '0.8rem', color: '#94A3B8' }}>
              {clubOverview?.totalAttendanceRecords || 0} Total Attendance Records
            </div>
          </Card>

          <Card>
            <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <TrendingUp size={18} color="#10B981" />
              <span>Avg Attendance / Meeting</span>
            </div>
            <div className="card-value" style={{ color: '#10B981' }}>
              {clubOverview ? Math.round(clubOverview.averageAttendancePerMeeting * 10) / 10 : 0}
            </div>
            <div style={{ fontSize: '0.8rem', color: '#94A3B8' }}>Members Per Meeting</div>
          </Card>

          <Card>
            <div className="card-title" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <Award size={18} color="#E9D5FF" />
              <span>Total Club Points Awarded</span>
            </div>
            <div className="card-value" style={{ color: '#E9D5FF' }}>
              {clubOverview?.totalPointsAwarded || 0} pts
            </div>
            <div style={{ fontSize: '0.8rem', color: '#94A3B8' }}>
              {clubOverview?.totalAchievementsEarned || 0} Achievements Awarded
            </div>
          </Card>
        </div>

        {/* Current Monthly Champion Card */}
        {clubOverview?.currentMonthlyChampion && (
          <div style={{
            background: 'linear-gradient(135deg, rgba(242, 223, 116, 0.15) 0%, rgba(119, 33, 111, 0.25) 100%)',
            border: '1px solid rgba(242, 223, 116, 0.4)',
            borderRadius: 16,
            padding: 20,
            marginTop: 20,
            display: 'flex',
            alignItems: 'center',
            gap: 16
          }}>
            <Crown size={32} color="#F2DF74" />
            <div>
              <div style={{ fontSize: '0.8rem', color: '#F2DF74', fontWeight: 700, textTransform: 'uppercase' }}>
                Active Monthly Champion ({clubOverview.currentMonthlyChampion.year}-{String(clubOverview.currentMonthlyChampion.month).padStart(2, '0')})
              </div>
              <div style={{ fontSize: '1.3rem', color: '#FFF', fontWeight: 700 }}>
                {clubOverview.currentMonthlyChampion.displayName}
              </div>
              <div style={{ fontSize: '0.85rem', color: '#94A3B8' }}>
                Leading with <strong style={{ color: '#10B981' }}>{clubOverview.currentMonthlyChampion.points} pts</strong> ({clubOverview.currentMonthlyChampion.email})
              </div>
            </div>
          </div>
        )}
      </div>

      {/* PHASE 3: Monthly Executive Reporting */}
      <div>
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: 16,
          flexWrap: 'wrap',
          gap: 12
        }}>
          <h3 style={{ fontSize: '1.2rem', display: 'flex', alignItems: 'center', gap: 8 }}>
            <BarChart3 size={20} color="#F2DF74" />
            <span>Monthly Executive Report</span>
          </h3>

          {/* Month / Year Selector Controls */}
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <label style={{ fontSize: '0.9rem', color: '#94A3B8', fontWeight: 600 }}>Report Period:</label>
            <select
              className="form-select"
              style={{ width: 140, padding: '6px 12px', fontSize: '0.85rem' }}
              value={selectedMonth}
              onChange={(e) => setSelectedMonth(parseInt(e.target.value))}
            >
              {MONTHS.map((m) => (
                <option key={m.value} value={m.value}>{m.label}</option>
              ))}
            </select>

            <select
              className="form-select"
              style={{ width: 100, padding: '6px 12px', fontSize: '0.85rem' }}
              value={selectedYear}
              onChange={(e) => setSelectedYear(parseInt(e.target.value))}
            >
              {YEARS.map((y) => (
                <option key={y} value={y}>{y}</option>
              ))}
            </select>
          </div>
        </div>

        {monthlyLoading ? (
          <LoadingSpinner />
        ) : (
          <Card style={{ padding: 24 }}>
            <h4 style={{ fontSize: '1.1rem', color: '#F2DF74', marginBottom: 18 }}>
              Executive Report Summary — {selectedMonthLabel} {selectedYear}
            </h4>

            {monthlyAnalytics ? (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 16 }}>
                  <div style={{ background: 'rgba(255, 255, 255, 0.03)', padding: 16, borderRadius: 12, border: '1px solid rgba(255,255,255,0.06)' }}>
                    <div style={{ fontSize: '0.8rem', color: '#94A3B8' }}>Meetings Held</div>
                    <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#60A5FA' }}>
                      {monthlyAnalytics.totalMeetings}
                    </div>
                  </div>

                  <div style={{ background: 'rgba(255, 255, 255, 0.03)', padding: 16, borderRadius: 12, border: '1px solid rgba(255,255,255,0.06)' }}>
                    <div style={{ fontSize: '0.8rem', color: '#94A3B8' }}>Total Attendance</div>
                    <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#10B981' }}>
                      {monthlyAnalytics.totalAttendance}
                    </div>
                    <div style={{ fontSize: '0.75rem', color: '#64748B' }}>
                      Avg {Math.round(monthlyAnalytics.averageAttendance * 10) / 10} / meeting
                    </div>
                  </div>

                  <div style={{ background: 'rgba(255, 255, 255, 0.03)', padding: 16, borderRadius: 12, border: '1px solid rgba(255,255,255,0.06)' }}>
                    <div style={{ fontSize: '0.8rem', color: '#94A3B8' }}>Points Awarded</div>
                    <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#F2DF74' }}>
                      {monthlyAnalytics.totalPointsAwarded} pts
                    </div>
                  </div>

                  <div style={{ background: 'rgba(255, 255, 255, 0.03)', padding: 16, borderRadius: 12, border: '1px solid rgba(255,255,255,0.06)' }}>
                    <div style={{ fontSize: '0.8rem', color: '#94A3B8' }}>Active Participants</div>
                    <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#E9D5FF' }}>
                      {monthlyAnalytics.activeMembersCount}
                    </div>
                  </div>
                </div>

                {/* Monthly Champion for Selected Period */}
                {monthlyAnalytics.monthlyChampion ? (
                  <div style={{
                    background: 'rgba(242, 223, 116, 0.08)',
                    border: '1px solid rgba(242, 223, 116, 0.3)',
                    padding: 18,
                    borderRadius: 12,
                    display: 'flex',
                    alignItems: 'center',
                    gap: 14
                  }}>
                    <Crown size={28} color="#F2DF74" />
                    <div>
                      <div style={{ fontSize: '0.8rem', color: '#F2DF74', fontWeight: 700 }}>
                        Monthly Champion ({selectedMonthLabel} {selectedYear})
                      </div>
                      <div style={{ fontSize: '1.2rem', color: '#FFF', fontWeight: 700 }}>
                        {monthlyAnalytics.monthlyChampion.displayName}
                      </div>
                      <div style={{ fontSize: '0.85rem', color: '#94A3B8' }}>
                        Points: <strong style={{ color: '#10B981' }}>{monthlyAnalytics.monthlyChampion.points} pts</strong> ({monthlyAnalytics.monthlyChampion.email})
                      </div>
                    </div>
                  </div>
                ) : (
                  <div style={{ color: '#94A3B8', fontSize: '0.9rem', fontStyle: 'italic' }}>
                    No Monthly Champion declared for {selectedMonthLabel} {selectedYear}.
                  </div>
                )}

                {/* Top Performers Table */}
                {(monthlyAnalytics.topPerformers || []).length > 0 && (
                  <div>
                    <h5 style={{ fontSize: '1rem', color: '#FFF', marginBottom: 12 }}>
                      Top Performers Leaderboard — {selectedMonthLabel} {selectedYear}
                    </h5>
                    <div className="table-container">
                      <table>
                        <thead>
                          <tr>
                            <th>Rank</th>
                            <th>Member Name</th>
                            <th>Email</th>
                            <th>Points</th>
                          </tr>
                        </thead>
                        <tbody>
                          {monthlyAnalytics.topPerformers?.map((tp, idx) => (
                            <tr key={tp.memberId}>
                              <td style={{ fontWeight: 800 }}>
                                {idx === 0 && <Crown size={16} color="#F2DF74" style={{ marginRight: 4 }} />}
                                {idx === 1 && <Medal size={16} color="#A9B2B1" style={{ marginRight: 4 }} />}
                                #{tp.rank || idx + 1}
                              </td>
                              <td style={{ fontWeight: 600, color: '#F2DF74' }}>{tp.displayName}</td>
                              <td style={{ color: '#60A5FA', fontSize: '0.85rem' }}>{tp.email || '-'}</td>
                              <td style={{ fontWeight: 800, color: '#10B981' }}>{tp.points} pts</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}
              </div>
            ) : (
              <div style={{ color: '#64748B', textAlign: 'center', padding: 28 }}>
                No analytics recorded for {selectedMonthLabel} {selectedYear} yet.
              </div>
            )}
          </Card>
        )}
      </div>
    </AppLayout>
  );
};
