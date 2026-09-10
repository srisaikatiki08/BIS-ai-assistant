import React from 'react';
import { CheckCircle2, AlertCircle, Info, X } from 'lucide-react';
import { useApp } from '../context/AppContext';

export default function Toast() {
  const { toast } = useApp();
  if (!toast) return null;

  const isSuccess = toast.type === 'success';
  const isError = toast.type === 'error';

  return (
    <div
      style={{
        position: 'fixed',
        bottom: '24px',
        right: '24px',
        zIndex: 999,
        background: isSuccess ? '#065f46' : isError ? '#991b1b' : '#1e293b',
        color: '#ffffff',
        padding: '0.85rem 1.25rem',
        borderRadius: '10px',
        display: 'flex',
        alignItems: 'center',
        gap: '0.75rem',
        boxShadow: '0 10px 25px -5px rgba(0,0,0,0.3)',
        fontSize: '0.9rem',
        fontWeight: 500,
        maxWidth: '400px',
        animation: 'fadeIn 200ms ease'
      }}
    >
      {isSuccess && <CheckCircle2 size={20} color="#34d399" />}
      {isError && <AlertCircle size={20} color="#f87171" />}
      {!isSuccess && !isError && <Info size={20} color="#60a5fa" />}
      <span style={{ flex: 1 }}>{toast.message}</span>
    </div>
  );
}
