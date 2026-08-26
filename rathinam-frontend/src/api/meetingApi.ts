import { request } from './client';
import { Meeting, MeetingWorkflow } from '../types';

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export const meetingApi = {
  getMeetings: (page = 0, size = 10) =>
    request<PageResponse<Meeting>>(`/meetings?page=${page}&size=${size}`, { method: 'GET' }),

  getMeetingById: (id: string) =>
    request<Meeting>(`/meetings/${id}`, { method: 'GET' }),

  createMeeting: (data: { meetingNumber: number; meetingStart: string; theme?: string; meetingType: string; location?: string; meetingUrl?: string; description?: string }) =>
    request<Meeting>('/meetings', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  updateMeeting: (id: string, data: Partial<Meeting>) =>
    request<Meeting>(`/meetings/${id}`, {
      method: 'PATCH',
      body: JSON.stringify(data),
    }),

  getMeetingWorkflow: (id: string) =>
    request<MeetingWorkflow>(`/meetings/${id}/workflow`, { method: 'GET' }),

  startMeeting: (id: string) =>
    request<Meeting>(`/meetings/${id}/start`, { method: 'POST' }),

  completeMeeting: (id: string) =>
    request<Meeting>(`/meetings/${id}/complete`, { method: 'POST' }),
};
