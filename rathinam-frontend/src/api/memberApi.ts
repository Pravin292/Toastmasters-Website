import { request } from './client';
import { Member } from '../types';

export const memberApi = {
  getMembers: () =>
    request<Member[]>('/members', { method: 'GET' }),

  getMemberById: (id: string) =>
    request<Member>(`/members/${id}`, { method: 'GET' }),

  createMember: (data: { firstName: string; lastName: string; email: string; joinDate: string }) =>
    request<Member>('/members', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  updateMember: (id: string, data: Partial<Member>) =>
    request<Member>(`/members/${id}`, {
      method: 'PATCH',
      body: JSON.stringify(data),
    }),
};
