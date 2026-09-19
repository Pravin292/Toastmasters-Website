import { request } from './client';
import { AuthResponse } from '../types';

export const authApi = {
  login: (email: string, password: string) =>
    request<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),

  register: (data: { firstName: string; lastName: string; email: string; password?: string; pass?: string }) =>
    request<AuthResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify({
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
        password: data.password || data.pass,
      }),
    }),

  getCurrentUser: () =>
    request<AuthResponse>('/auth/me', {
      method: 'GET',
    }),
};
