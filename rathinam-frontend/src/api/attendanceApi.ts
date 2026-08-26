import { request } from './client';
import { Attendance } from '../types';

export const attendanceApi = {
  getMeetingAttendance: (meetingId: string) =>
    request<Attendance[]>(`/meetings/${meetingId}/attendance`, { method: 'GET' }),

  recordAttendance: (meetingId: string, memberId: string, status: 'PRESENT' | 'ABSENT' | 'EXCUSED') =>
    request<Attendance>(`/meetings/${meetingId}/attendance`, {
      method: 'POST',
      body: JSON.stringify({ memberId, status }),
    }),

  updateAttendance: (attendanceId: string, status: 'PRESENT' | 'ABSENT' | 'EXCUSED') =>
    request<Attendance>(`/attendance/${attendanceId}`, {
      method: 'PATCH',
      body: JSON.stringify({ status }),
    }),
};
