import { request } from './client';
import { AuthResponse } from '../types';

export const authApi = {
  login: (email: string, password: string) =>
    request<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),

  getCurrentUser: () =>
    request<AuthResponse>('/auth/me', {
      method: 'GET',
    }),
};
