import React from 'react';
import { Bell, Calendar, AlertTriangle, FileText, ArrowRight, Bot } from 'lucide-react';
import { useApp } from '../context/AppContext';

export default function UpdateCard({ update }) {
  const { launchChatWithQuery } = useApp();

  const isHighUrgency = update.urgency.includes("Mandatory") || update.urgency.includes("QCO");

  return (
    <div className="card-base" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', gap: '1rem' }}>
      <div>
        {/* Top Header: Category & Date */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.75rem', flexWrap: 'wrap', gap: '0.5rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span style={{ background: isHighUrgency ? 'var(--badge-qco-bg)' : 'var(--primary-50)', color: isHighUrgency ? 'var(--badge-qco-text)' : 'var(--primary-700)', fontSize: '0.75rem', fontWeight: 700, padding: '0.2rem 0.6rem', borderRadius: 'var(--radius-full)', border: isHighUrgency ? '1px solid var(--badge-qco-border)' : '1px solid var(--primary-100)' }}>
              {update.category}
            </span>
            <span style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--text-subtle)' }}>
              {update.urgency}
            </span>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', fontSize: '0.78rem', color: 'var(--text-subtle)' }}>
            <Calendar size={13} />
            <span>{update.date}</span>
          </div>
        </div>

        {/* Title */}
        <h4 style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--text-main)', lineHeight: 1.35, marginBottom: '0.6rem' }}>
          {update.title}
        </h4>

        {/* Excerpt */}
        <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)', lineHeight: 1.5, marginBottom: '1rem' }}>
          {update.excerpt}
        </p>

        {/* Affected Industries & Deadline */}
        <div style={{ background: 'var(--bg-subtle)', padding: '0.85rem 1rem', borderRadius: '8px', border: '1px solid var(--border-light)', fontSize: '0.84rem', display: 'flex', flexDirection: 'column', gap: '0.5rem', marginBottom: '0.85rem', textDecoration: 'none' }}>
          <div style={{ textDecoration: 'none' }}>
            <strong style={{ color: 'var(--text-main)', fontWeight: 700 }}>Affected Sectors:</strong> <span style={{ color: 'var(--text-muted)' }}>{update.affectedIndustries}</span>
          </div>
          <div style={{ textDecoration: 'none' }}>
            <strong style={{ color: '#dc2626', fontWeight: 700 }}>Compliance Deadline:</strong> <span style={{ color: 'var(--text-main)', fontWeight: 600 }}>{update.deadline}</span>
          </div>
          <div style={{ textDecoration: 'none' }}>
            <strong style={{ color: 'var(--text-main)', fontWeight: 700 }}>Action Required:</strong> <span style={{ color: 'var(--text-muted)', textDecoration: 'none' }}>{update.actionRequired}</span>
          </div>
        </div>

        {/* Gazette Reference */}
        <div style={{ fontSize: '0.75rem', color: 'var(--text-subtle)', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
          <FileText size={13} />
          <span>Gazette Ref: <code>{update.gazetteRef}</code></span>
        </div>
      </div>

      {/* Footer Ask AI Button */}
      <div style={{ paddingTop: '0.85rem', borderTop: '1px solid var(--border-light)' }}>
        <button
          className="btn btn-outline btn-sm"
          style={{ width: '100%', justifyContent: 'center' }}
          onClick={() => launchChatWithQuery(`What are the compliance requirements for: ${update.title}?`)}
        >
          <Bot size={14} />
          <span>Ask Assistant How This Affects My Business</span>
        </button>
      </div>
    </div>
  );
}
