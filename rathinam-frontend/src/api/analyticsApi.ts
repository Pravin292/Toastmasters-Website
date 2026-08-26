import { request } from './client';
import { MemberAnalytics } from '../types';

export const analyticsApi = {
  getMemberAnalytics: (memberId: string) =>
    request<MemberAnalytics>(`/analytics/members/${memberId}`, { method: 'GET' }),

  getClubOverview: () =>
    request<any>('/analytics/overview', { method: 'GET' }),

  getMemberPerformanceTrend: (memberId: string, months = 6) =>
    request<any[]>(`/analytics/members/${memberId}/performance?months=${months}`, { method: 'GET' }),

  getMonthlyReport: (year: number, month: number) =>
    request<any>(`/analytics/reports/monthly/${year}/${month}`, { method: 'GET' }),
};
