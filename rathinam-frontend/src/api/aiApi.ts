import { request } from './client';
import { AiSummary } from '../types';

export const aiApi = {
  generateMeetingSummary: (meetingId: string) =>
    request<AiSummary>(`/ai/meetings/${meetingId}/summary`, {
      method: 'POST',
      body: JSON.stringify({}),
    }),
};
