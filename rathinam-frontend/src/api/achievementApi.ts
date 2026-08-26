import { request } from './client';
import { Achievement, Certificate } from '../types';

export const achievementApi = {
  getMemberAchievements: (memberId: string) =>
    request<Achievement[]>(`/members/${memberId}/achievements`, { method: 'GET' }),

  getMemberCertificates: (memberId: string) =>
    request<Certificate[]>(`/members/${memberId}/certificates`, { method: 'GET' }),

  issueCertificate: (memberId: string, title: string, certificateType: string) =>
    request<Certificate>('/certificates', {
      method: 'POST',
      body: JSON.stringify({ memberId, title, certificateType }),
    }),
};
