import React from 'react';
import { AlertCircle } from 'lucide-react';

interface ErrorMessageProps {
  message: string;
}

export const ErrorMessage: React.FC<ErrorMessageProps> = ({ message }) => {
  return (
    <div style={{
      display: 'flex',
      alignItems: 'center',
      gap: 10,
      background: 'rgba(239, 68, 68, 0.15)',
      border: '1px solid rgba(239, 68, 68, 0.4)',
      color: '#F87171',
      padding: '12px 16px',
      borderRadius: 8,
      margin: '16px 0',
      fontSize: '0.9rem'
    }}>
      <AlertCircle size={18} />
      <span>{message}</span>
    </div>
  );
};
