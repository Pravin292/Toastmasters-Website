import React, { useEffect, useState } from 'react';
import { AppLayout } from '../components/layout/AppLayout';
import { Card } from '../components/common/Card';
import { Badge } from '../components/common/Badge';
import { LoadingSpinner } from '../components/common/LoadingSpinner';
import { ErrorMessage } from '../components/common/ErrorMessage';
import { rankingApi } from '../api/rankingApi';
import { MonthlyRanking } from '../types';
import { Trophy, Crown, Medal, Award, Calendar } from 'lucide-react';

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

export const LeaderboardPage: React.FC = () => {
  const currentDate = new Date();
  const currentYear = currentDate.getFullYear();
  const currentMonth = currentDate.getMonth() + 1;

  const YEARS = [currentYear - 3, currentYear - 2, currentYear - 1, currentYear, currentYear + 1];

  const [selectedYear, setSelectedYear] = useState<number>(currentYear);
  const [selectedMonth, setSelectedMonth] = useState<number>(currentMonth);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [monthlyData, setMonthlyData] = useState<MonthlyRanking | null>(null);

  useEffect(() => {
    const fetchLeaderboardData = async () => {
      setLoading(true);
      setError(null);
      try {
        const isCurrentPeriod = selectedYear === currentYear && selectedMonth === currentMonth;

        const res = isCurrentPeriod
          ? await rankingApi.getCurrentMonthlyChampionship().catch(async () => {
              return await rankingApi.getMonthlyRankings(selectedYear, selectedMonth);
            })
          : await rankingApi.getMonthlyRankings(selectedYear, selectedMonth);

        setMonthlyData(res);
      } catch (err: any) {
        setError(err.message || 'Failed to load leaderboard data');
      } finally {
        setLoading(false);
      }
    };

    fetchLeaderboardData();
  }, [selectedYear, selectedMonth]);

  const rankingList = monthlyData?.leaderboard?.content || [];
  const champion = monthlyData?.champion;
  const selectedMonthLabel = MONTHS.find((m) => m.value === selectedMonth)?.label || '';

  return (
    <AppLayout title="Leaderboard & Monthly Championship">
      {error && <ErrorMessage message={error} />}

      {/* Period Selection Controls */}
      <div style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 20,
        flexWrap: 'wrap',
        gap: 12
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <Calendar size={18} color="#F2DF74" />
          <label style={{ fontSize: '0.9rem', color: '#94A3B8', fontWeight: 600 }}>Period:</label>
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

        {(selectedYear !== currentYear || selectedMonth !== currentMonth) && (
          <button
            type="button"
            className="btn btn-secondary"
            style={{ padding: '6px 12px', fontSize: '0.8rem' }}
            onClick={() => {
              setSelectedYear(currentYear);
              setSelectedMonth(currentMonth);
            }}
          >
            Reset to Current Month
          </button>
        )}
      </div>

      {loading ? (
        <LoadingSpinner />
      ) : (
        <>
          {/* Monthly Champion Hero Banner */}
          {champion ? (
            <div style={{
              background: 'linear-gradient(135deg, rgba(242, 223, 116, 0.2) 0%, rgba(119, 33, 111, 0.3) 100%)',
              border: '1px solid rgba(242, 223, 116, 0.4)',
              borderRadius: 18,
              padding: 28,
              marginBottom: 28,
              display: 'flex',
              alignItems: 'center',
              gap: 24,
              flexWrap: 'wrap'
            }}>
              <div style={{
                width: 64,
                height: 64,
                borderRadius: '50%',
                background: 'linear-gradient(135deg, #F2DF74 0%, #D4BD3A 100%)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                boxShadow: '0 6px 20px rgba(242, 223, 116, 0.4)'
              }}>
                <Crown size={36} color="#0F172A" />
              </div>

              <div>
                <div style={{ fontSize: '0.85rem', textTransform: 'uppercase', letterSpacing: '0.05em', color: '#F2DF74', fontWeight: 700 }}>
                  Monthly Champion ({selectedMonthLabel} {selectedYear})
                </div>
                <h2 style={{ fontSize: '1.8rem', color: '#FFF', margin: '4px 0' }}>
                  {champion.displayName}
                </h2>
                <div style={{ fontSize: '0.95rem', color: '#94A3B8' }}>
                  Leading with <strong style={{ color: '#10B981' }}>{champion.points} pts</strong> ({champion.email})
                </div>
              </div>
            </div>
          ) : (
            <div style={{
              background: 'rgba(255, 255, 255, 0.03)',
              border: '1px solid var(--bg-card-border)',
              borderRadius: 18,
              padding: 24,
              marginBottom: 28,
              display: 'flex',
              alignItems: 'center',
              gap: 16,
              color: '#94A3B8'
            }}>
              <Crown size={28} color="#F2DF74" />
              <div>
                <div style={{ fontWeight: 700, color: '#F2DF74' }}>No Monthly Champion Declared Yet ({selectedMonthLabel} {selectedYear})</div>
                <div style={{ fontSize: '0.85rem' }}>Standings update live as members earn points by attending meetings and taking roles.</div>
              </div>
            </div>
          )}

          {/* Leaderboard Table */}
          <Card>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 18, flexWrap: 'wrap', gap: 12 }}>
              <h3 style={{ fontSize: '1.2rem', display: 'flex', alignItems: 'center', gap: 8 }}>
                <Trophy size={20} color="#F2DF74" />
                <span>Monthly Points Leaderboard — {selectedMonthLabel} {selectedYear}</span>
              </h3>
              {monthlyData?.totalMembers !== undefined && (
                <Badge variant="default">Total Active Participants: {monthlyData.totalMembers}</Badge>
              )}
            </div>

            <div className="table-container">
              <table>
                <thead>
                  <tr>
                    <th>Rank</th>
                    <th>Member Name</th>
                    <th>Email</th>
                    <th>Total Points</th>
                    <th>Badge</th>
                  </tr>
                </thead>
                <tbody>
                  {rankingList.map((r, idx) => {
                    const rankNum = r.rank ?? (idx + 1);
                    return (
                      <tr key={r.memberId} style={{ background: rankNum === 1 ? 'rgba(242, 223, 116, 0.05)' : undefined }}>
                        <td style={{ fontWeight: 800, fontSize: '1.1rem' }}>
                          {rankNum === 1 && <Crown size={18} color="#F2DF74" style={{ marginRight: 6, verticalAlign: 'middle' }} />}
                          {rankNum === 2 && <Medal size={18} color="#A9B2B1" style={{ marginRight: 6, verticalAlign: 'middle' }} />}
                          {rankNum === 3 && <Award size={18} color="#CD7F32" style={{ marginRight: 6, verticalAlign: 'middle' }} />}
                          #{rankNum}
                        </td>
                        <td style={{ fontWeight: 600, color: '#F2DF74' }}>{r.displayName}</td>
                        <td style={{ color: '#60A5FA', fontSize: '0.9rem' }}>{r.email || '-'}</td>
                        <td style={{ fontWeight: 800, color: '#10B981', fontSize: '1.1rem' }}>
                          {r.points} pts
                        </td>
                        <td>
                          {rankNum === 1 && <Badge variant="in_progress">CHAMPION</Badge>}
                          {rankNum <= 3 && rankNum > 1 && <Badge variant="scheduled">TOP PERFORMER</Badge>}
                          {rankNum > 3 && <Badge variant="default">MEMBER</Badge>}
                        </td>
                      </tr>
                    );
                  })}

                  {rankingList.length === 0 && (
                    <tr>
                      <td colSpan={5} style={{ textAlign: 'center', color: '#64748B', padding: 28 }}>
                        No points recorded for {selectedMonthLabel} {selectedYear} yet.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </Card>
        </>
      )}
    </AppLayout>
  );
};
