import { request } from './client';
import { MemberAnalytics, MemberMonthlyPerformance, ClubOverviewAnalytics, MonthlyAnalytics } from '../types';

export const analyticsApi = {
  getMemberAnalytics: (memberId: string) =>
    request<MemberAnalytics>(`/analytics/members/${memberId}`, { method: 'GET' }),

  getMemberPerformanceTrend: (memberId: string, months = 6) =>
    request<MemberMonthlyPerformance[]>(`/analytics/members/${memberId}/performance?months=${months}`, { method: 'GET' }),

  getClubOverview: () =>
    request<ClubOverviewAnalytics>('/analytics/overview', { method: 'GET' }),

  getMonthlyAnalytics: (year: number, month: number) =>
    request<MonthlyAnalytics>(`/analytics/monthly/${year}/${month}`, { method: 'GET' }),
};
