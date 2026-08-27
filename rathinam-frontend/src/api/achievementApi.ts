import { request } from './client';
import { Achievement, Badge, Certificate, IssueCertificateRequest } from '../types';

export const achievementApi = {
  getMemberAchievements: (memberId: string) =>
    request<Achievement[]>(`/members/${memberId}/achievements`, { method: 'GET' }),

  getMemberBadges: (memberId: string) =>
    request<Badge[]>(`/members/${memberId}/badges`, { method: 'GET' }),

  evaluateMemberAchievements: (memberId: string) =>
    request<void>(`/members/${memberId}/achievements/evaluate`, { method: 'POST' }),

  getMemberCertificates: (memberId: string) =>
    request<Certificate[]>(`/members/${memberId}/certificates`, { method: 'GET' }),

  issueCertificate: (requestData: IssueCertificateRequest) =>
    request<Certificate>('/certificates', {
      method: 'POST',
      body: JSON.stringify(requestData),
    }),
};
