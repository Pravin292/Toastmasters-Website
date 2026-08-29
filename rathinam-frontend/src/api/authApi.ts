import { request } from './client';
import { AuthResponse } from '../types';

export const authApi = {
  login: (email: string, password: string) =>
    request<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),

  register: (data: { firstName: string; lastName: string; email: string; password?: string }) =>
    request<any>('/members', {
      method: 'POST',
      body: JSON.stringify({
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        joinDate: new Date().toISOString().split('T')[0]
      }),
    }),

  getCurrentUser: () =>
    request<AuthResponse>('/auth/me', {
      method: 'GET',
    }),
};
