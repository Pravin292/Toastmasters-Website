import { request } from './client';
import { MonthlyRanking, MemberRanking } from '../types';

export const rankingApi = {
  getMonthlyRankings: (year: number, month: number) =>
    request<MonthlyRanking>(`/rankings/monthly/${year}/${month}`, { method: 'GET' }),

  getCurrentMonthlyChampionship: () =>
    request<MonthlyRanking>('/championships/monthly/current', { method: 'GET' }),

  getMemberRank: (memberId: string, year?: number, month?: number) => {
    const query = year && month ? `?year=${year}&month=${month}` : '';
    return request<MemberRanking>(`/rankings/member/${memberId}${query}`, { method: 'GET' });
  },
};
