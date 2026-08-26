import { request } from './client';
import { PointEvent, PointRule } from '../types';

export const pointApi = {
  getPointRules: () =>
    request<PointRule[]>('/point-rules', { method: 'GET' }),

  createPointRule: (data: { code: string; name: string; description?: string; points: number }) =>
    request<PointRule>('/point-rules', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  getMemberPointEvents: (memberId: string) =>
    request<{ totalPoints: number; events: PointEvent[] }>(`/members/${memberId}/points`, { method: 'GET' }),

  awardManualPoints: (memberId: string, pointRuleId: string, meetingId?: string, reason?: string) =>
    request<PointEvent>('/points/manual', {
      method: 'POST',
      body: JSON.stringify({ memberId, pointRuleId, meetingId, reason }),
    }),
};
