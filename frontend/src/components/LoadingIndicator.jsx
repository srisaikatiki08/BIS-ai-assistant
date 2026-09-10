import React from 'react';
import { Bot, Sparkles } from 'lucide-react';
import { useApp } from '../context/AppContext';

export default function LoadingIndicator() {
  const { t } = useApp();

  return (
    <div className="chat-msg-row ai" style={{ animation: 'fadeIn 200ms ease' }}>
      <div className="msg-avatar ai">
        <Bot size={20} />
      </div>
      <div className="msg-bubble-container">
        <div className="ai-response-box" style={{ padding: '1rem 1.25rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <div className="loading-dots">
              <span className="loading-dot"></span>
              <span className="loading-dot"></span>
              <span className="loading-dot"></span>
            </div>
            <span style={{ fontSize: '0.86rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
              <Sparkles size={14} color="var(--primary-600)" />
              {t.chat?.thinking || "BIS AI is analyzing Indian Standards database & gazette clauses..."}
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}
