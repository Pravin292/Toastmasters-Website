import { request } from './client';
import { RoleAssignment, RoleDefinition } from '../types';

export const roleApi = {
  getRoleDefinitions: () =>
    request<RoleDefinition[]>('/roles', { method: 'GET' }),

  createRoleDefinition: (data: { name: string; description?: string }) =>
    request<RoleDefinition>('/roles', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  getMeetingRoleAssignments: (meetingId: string) =>
    request<RoleAssignment[]>(`/meetings/${meetingId}/roles`, { method: 'GET' }),

  assignRole: (meetingId: string, roleDefinitionId: string, memberId: string) =>
    request<RoleAssignment>(`/meetings/${meetingId}/roles`, {
      method: 'POST',
      body: JSON.stringify({ roleDefinitionId, memberId }),
    }),
};
