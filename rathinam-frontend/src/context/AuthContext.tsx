import React, { createContext, useContext, useState, useEffect } from 'react';
import { AccountRole, AuthResponse } from '../types';
import { authApi } from '../api/authApi';

interface AuthContextType {
  user: AuthResponse | null;
  token: string | null;
  loading: boolean;
  login: (email: string, pass: string) => Promise<void>;
  register: (data: { firstName: string; lastName: string; email: string; pass: string }) => Promise<void>;
  logout: () => void;
  hasRole: (roles: AccountRole[]) => boolean;
  isOfficer: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AuthResponse | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('auth_token'));
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchMe = async () => {
      if (!token) {
        setLoading(false);
        return;
      }
      try {
        const authRes = await authApi.getCurrentUser();
        setUser(authRes);
      } catch (err) {
        console.error('Failed to restore session:', err);
        localStorage.removeItem('auth_token');
        setToken(null);
        setUser(null);
      } finally {
        setLoading(false);
      }
    };

    fetchMe();

    const handleUnauthorized = () => {
      setToken(null);
      setUser(null);
    };

    window.addEventListener('auth:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('auth:unauthorized', handleUnauthorized);
  }, [token]);

  const login = async (email: string, pass: string) => {
    setLoading(true);
    try {
      const cleanEmail = email.trim();
      const authRes = await authApi.login(cleanEmail, pass);
      localStorage.setItem('auth_token', authRes.token);
      setToken(authRes.token);
      setUser(authRes);
    } finally {
      setLoading(false);
    }
  };

  const register = async (data: { firstName: string; lastName: string; email: string; pass: string }) => {
    setLoading(true);
    try {
      await authApi.register(data);
      // Attempt login immediately after registration
      try {
        await login(data.email, data.pass);
      } catch {
        // If auto-login fails, state remains ready for manual sign in
      }
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('auth_token');
    setToken(null);
    setUser(null);
  };

  const hasRole = (roles: AccountRole[]) => {
    if (!user) return false;
    return roles.includes(user.role);
  };

  const isOfficer = hasRole(['ADMIN', 'PRESIDENT', 'OFFICER']);

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, hasRole, isOfficer }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
