import React, { useState } from 'react';
import { 
  ShieldCheck, 
  Cpu, 
  Sparkles, 
  Globe, 
  FlaskConical, 
  GraduationCap, 
  HeartHandshake, 
  ExternalLink, 
  ChevronDown, 
  ChevronUp, 
  FileText, 
  Bot, 
  CheckCircle2, 
  DollarSign 
} from 'lucide-react';
import { useApp } from '../context/AppContext';

export default function ServiceCard({ service }) {
  const { launchChatWithQuery, t } = useApp();
  const [expanded, setExpanded] = useState(false);

  // Icon mapping
  const getIcon = (name) => {
    switch (name) {
      case 'ShieldCheck': return <ShieldCheck size={26} color="var(--primary-600)" />;
      case 'Cpu': return <Cpu size={26} color="#0284c7" />;
      case 'Sparkles': return <Sparkles size={26} color="#eab308" />;
      case 'Globe': return <Globe size={26} color="#10b981" />;
      case 'FlaskConical': return <FlaskConical size={26} color="#8b5cf6" />;
      case 'GraduationCap': return <GraduationCap size={26} color="#ec4899" />;
      default: return <HeartHandshake size={26} color="var(--primary-600)" />;
    }
  };

  return (
    <div className="service-card">
      <div>
        {/* Top Icon & Badge */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
          <div style={{ width: '48px', height: '48px', borderRadius: '12px', background: 'var(--bg-subtle)', display: 'flex', alignItems: 'center', justifyContent: 'center', border: '1px solid var(--border-light)' }}>
            {getIcon(service.icon)}
          </div>
          <span style={{ background: 'var(--primary-50)', color: 'var(--primary-700)', fontSize: '0.75rem', fontWeight: 700, padding: '0.2rem 0.6rem', borderRadius: 'var(--radius-full)', border: '1px solid var(--primary-100)' }}>
            {service.badge}
          </span>
        </div>

        {/* Title & Subtitle */}
        <h3 style={{ fontSize: '1.2rem', fontWeight: 800, color: 'var(--text-main)', marginBottom: '0.35rem' }}>
          {service.title}
        </h3>
        <p style={{ fontSize: '0.82rem', color: 'var(--text-subtle)', fontWeight: 500, marginBottom: '0.85rem' }}>
          {service.subtitle}
        </p>

        {/* Summary Description */}
        <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)', lineHeight: 1.5, marginBottom: '1rem' }}>
          {service.summary}
        </p>

        {/* Target Audience Tag */}
        <div style={{ fontSize: '0.8rem', color: 'var(--text-subtle)', background: 'var(--bg-subtle)', padding: '0.5rem 0.75rem', borderRadius: '6px', border: '1px solid var(--border-light)', marginBottom: '1rem' }}>
          <strong>Who it is for:</strong> {service.targetAudience}
        </div>

        {/* Expandable Document Checklist & Process Workflow */}
        {expanded && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem', marginTop: '1rem', paddingTop: '1rem', borderTop: '1px solid var(--border-light)', animation: 'fadeIn 200ms ease' }}>
            
            {/* Required Documents */}
            <div>
              <h5 style={{ fontSize: '0.82rem', fontWeight: 700, color: 'var(--text-main)', textTransform: 'uppercase', marginBottom: '0.4rem', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                <FileText size={13} color="var(--primary-600)" />
                <span>Required Documentation</span>
              </h5>
              <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.3rem', fontSize: '0.82rem', color: 'var(--text-muted)' }}>
                {service.requiredDocuments.map((doc, idx) => (
                  <li key={idx} style={{ display: 'flex', alignItems: 'flex-start', gap: '0.4rem' }}>
                    <span style={{ color: 'var(--primary-600)', fontWeight: 'bold' }}>✓</span>
                    <span>{doc}</span>
                  </li>
                ))}
              </ul>
            </div>

            {/* Basic Process Steps */}
            <div>
              <h5 style={{ fontSize: '0.82rem', fontWeight: 700, color: 'var(--text-main)', textTransform: 'uppercase', marginBottom: '0.4rem', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                <CheckCircle2 size={13} color="var(--emerald-600)" />
                <span>Application Workflow</span>
              </h5>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.45rem' }}>
                {service.processSteps.map((s, idx) => (
                  <div key={idx} style={{ fontSize: '0.8rem', background: 'var(--bg-subtle)', padding: '0.45rem 0.65rem', borderRadius: '6px', border: '1px solid var(--border-light)' }}>
                    <span style={{ fontWeight: 700, color: 'var(--primary-700)' }}>Step {s.step}: {s.title}</span>
                    <div style={{ color: 'var(--text-muted)' }}>{s.desc}</div>
                  </div>
                ))}
              </div>
            </div>

            {/* Fees Breakdown */}
            <div style={{ background: 'var(--emerald-50)', border: '1px solid var(--emerald-100)', padding: '0.65rem 0.85rem', borderRadius: '6px', fontSize: '0.8rem', color: 'var(--emerald-900)' }}>
              <strong style={{ display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                <DollarSign size={13} /> Fee Structure & Rebates:
              </strong>
              <div>{service.fees}</div>
            </div>

          </div>
        )}
      </div>

      {/* Footer Controls */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingTop: '1rem', borderTop: '1px solid var(--border-light)', marginTop: '0.5rem' }}>
        <button
          className="btn btn-primary btn-sm"
          onClick={() => launchChatWithQuery(`How to apply for ${service.title}? What is the step-by-step procedure and fees?`)}
        >
          <Bot size={14} />
          <span>{t.askAssistant}</span>
        </button>

        <button
          className="btn btn-outline btn-sm"
          onClick={() => setExpanded(!expanded)}
        >
          <span>{expanded ? "Show Less" : "View Checklist"}</span>
          {expanded ? <ChevronUp size={14} /> : <ChevronDown size={14} />}
        </button>
      </div>
    </div>
  );
}
