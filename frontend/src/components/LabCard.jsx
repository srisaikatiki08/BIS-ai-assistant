import React from 'react';
import { MapPin, Phone, Mail, Clock, CheckCircle2, Bot, ExternalLink, User } from 'lucide-react';
import { useApp } from '../context/AppContext';

export default function LabCard({ lab }) {
  const { launchChatWithQuery } = useApp();

  return (
    <div className="lab-card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
      <div>
        {/* Type Badge & NABL Cert */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.75rem', flexWrap: 'wrap', gap: '0.4rem' }}>
          <span style={{ background: 'var(--primary-50)', color: 'var(--primary-700)', fontSize: '0.75rem', fontWeight: 700, padding: '0.2rem 0.6rem', borderRadius: 'var(--radius-full)', border: '1px solid var(--primary-100)' }}>
            {lab.type}
          </span>
          <span style={{ background: 'var(--emerald-50)', color: 'var(--emerald-600)', fontSize: '0.72rem', fontWeight: 700, padding: '0.15rem 0.5rem', borderRadius: 'var(--radius-full)', border: '1px solid var(--emerald-100)' }}>
            {lab.nablCert}
          </span>
        </div>

        {/* Lab Name */}
        <h3 style={{ fontSize: '1.15rem', fontWeight: 800, color: 'var(--text-main)', lineHeight: 1.35, marginBottom: '0.5rem' }}>
          {lab.name}
        </h3>

        {/* Address */}
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: '0.5rem', fontSize: '0.84rem', color: 'var(--text-muted)', marginBottom: '0.85rem' }}>
          <MapPin size={16} color="var(--primary-600)" style={{ flexShrink: 0, marginTop: '2px' }} />
          <span style={{ lineHeight: 1.45 }}>{lab.address}</span>
        </div>

        {/* Contact Person (if present) */}
        {lab.contactPerson && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '0.6rem' }}>
            <User size={13} color="var(--text-subtle)" />
            <span><strong>Contact:</strong> {lab.contactPerson}</span>
          </div>
        )}

        {/* Disciplines Chips */}
        <div style={{ marginBottom: '1rem' }}>
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--text-subtle)', textTransform: 'uppercase', marginBottom: '0.35rem' }}>
            Testing Disciplines
          </div>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.35rem' }}>
            {lab.disciplines?.map((d, idx) => (
              <span key={idx} style={{ background: 'var(--bg-subtle)', color: 'var(--text-main)', fontSize: '0.75rem', padding: '0.2rem 0.5rem', borderRadius: '4px', border: '1px solid var(--border-light)', fontWeight: 500 }}>
                {d}
              </span>
            ))}
          </div>
        </div>

        {/* Major Tested Standards */}
        {lab.majorStandards && lab.majorStandards.length > 0 && (
          <div style={{ background: 'var(--bg-subtle)', padding: '0.75rem', borderRadius: '6px', border: '1px solid var(--border-light)', fontSize: '0.8rem', marginBottom: '1rem' }}>
            <div style={{ fontWeight: 700, color: 'var(--text-muted)', marginBottom: '0.3rem', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
              <CheckCircle2 size={13} color="var(--primary-600)" />
              <span>Accredited Standards Scope:</span>
            </div>
            <div style={{ color: 'var(--text-main)', lineHeight: 1.4 }}>
              {lab.majorStandards.join(", ")}
            </div>
          </div>
        )}

        {/* Contact Strip */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem', fontSize: '0.78rem', color: 'var(--text-subtle)', marginBottom: '0.85rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
            <Phone size={13} />
            <span>{lab.phone}</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
            <Clock size={13} />
            <span>TAT: {lab.sampleTurnaround || "5-10 days"}</span>
          </div>
          {lab.email && (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem', gridColumn: 'span 2' }}>
              <Mail size={13} />
              <span>{lab.email}</span>
            </div>
          )}
        </div>
      </div>

      {/* Footer Actions */}
      <div style={{ paddingTop: '0.85rem', borderTop: '1px solid var(--border-light)', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
        {lab.googleMapsDirectionsUrl && (
          <a
            href={lab.googleMapsDirectionsUrl}
            target="_blank"
            rel="noopener noreferrer"
            className="btn btn-primary btn-sm"
            style={{
              width: '100%',
              justifyContent: 'center',
              textDecoration: 'none',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '0.4rem',
              fontWeight: 600,
              padding: '0.5rem 0.85rem'
            }}
          >
            <span>🧭 Get Directions</span>
            <ExternalLink size={13} style={{ opacity: 0.85, marginLeft: '4px' }} />
          </a>
        )}
        <button
          className="btn btn-outline btn-sm"
          style={{ width: '100%', justifyContent: 'center', display: 'inline-flex', alignItems: 'center', gap: '0.4rem' }}
          onClick={() => launchChatWithQuery(`How to submit product samples for testing at ${lab.name}? What are the charges and turnaround times?`)}
        >
          <Bot size={14} />
          <span>Consult Lab Workflow with AI</span>
        </button>
      </div>
    </div>
  );
}
