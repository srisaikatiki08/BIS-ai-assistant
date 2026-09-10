import React from 'react';
import { ExternalLink, ShieldCheck, HelpCircle } from 'lucide-react';
import { useApp } from '../context/AppContext';

export default function Footer() {
  const { setActiveTab, t } = useApp();

  return (
    <footer style={{ background: 'var(--primary-900)', color: '#cbd5e1', paddingTop: '3.5rem', paddingBottom: '2rem', borderTop: '1px solid rgba(255,255,255,0.1)' }}>
      <div className="max-w-7xl">
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '2.5rem', marginBottom: '3rem' }}>
          
          {/* Col 1: Brand Info */}
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
              <div style={{ width: '38px', height: '38px', borderRadius: '8px', background: 'var(--primary-700)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'white', fontWeight: 800, border: '1px solid rgba(255,255,255,0.2)' }}>
                BIS
              </div>
              <div>
                <h4 style={{ color: '#ffffff', fontSize: '1.1rem', fontWeight: 700 }}>{t.brand}</h4>
                <p style={{ fontSize: '0.75rem', color: '#94a3b8' }}>AI Platform for Indian Standards</p>
              </div>
            </div>
            <p style={{ fontSize: '0.85rem', color: '#94a3b8', lineHeight: 1.6, marginBottom: '1.25rem' }}>
              An intelligent conversational assistant designed to simplify standardisation, QCO compliance, testing procedures, and BIS certification for Indian enterprises, MSMEs, startups, and citizens.
            </p>
          </div>

          {/* Col 2: Quick Links */}
          <div>
            <h5 style={{ color: '#ffffff', fontSize: '0.95rem', fontWeight: 700, marginBottom: '1.1rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Platform Features
            </h5>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.6rem', fontSize: '0.88rem' }}>
              <li>
                <button onClick={() => setActiveTab('chat')} style={{ background: 'none', border: 'none', color: '#94a3b8', cursor: 'pointer', padding: 0, transition: 'color 150ms' }} onMouseEnter={(e) => e.target.style.color = '#ffffff'} onMouseLeave={(e) => e.target.style.color = '#94a3b8'}>
                  AI Assistant
                </button>
              </li>
              <li>
                <button onClick={() => setActiveTab('standards')} style={{ background: 'none', border: 'none', color: '#94a3b8', cursor: 'pointer', padding: 0 }} onMouseEnter={(e) => e.target.style.color = '#ffffff'} onMouseLeave={(e) => e.target.style.color = '#94a3b8'}>
                  Indian Standards Catalog
                </button>
              </li>
              <li>
                <button onClick={() => setActiveTab('documentation')} style={{ background: 'none', border: 'none', color: '#94a3b8', cursor: 'pointer', padding: 0 }} onMouseEnter={(e) => e.target.style.color = '#ffffff'} onMouseLeave={(e) => e.target.style.color = '#94a3b8'}>
                  Documentation & Guidelines
                </button>
              </li>
              <li>
                <button onClick={() => setActiveTab('services')} style={{ background: 'none', border: 'none', color: '#94a3b8', cursor: 'pointer', padding: 0 }} onMouseEnter={(e) => e.target.style.color = '#ffffff'} onMouseLeave={(e) => e.target.style.color = '#94a3b8'}>
                  Conformity Assessment Schemes
                </button>
              </li>
              <li>
                <button onClick={() => setActiveTab('labs')} style={{ background: 'none', border: 'none', color: '#94a3b8', cursor: 'pointer', padding: 0 }} onMouseEnter={(e) => e.target.style.color = '#ffffff'} onMouseLeave={(e) => e.target.style.color = '#94a3b8'}>
                  Recognized Testing Laboratories
                </button>
              </li>
              <li>
                <button onClick={() => setActiveTab('updates')} style={{ background: 'none', border: 'none', color: '#94a3b8', cursor: 'pointer', padding: 0 }} onMouseEnter={(e) => e.target.style.color = '#ffffff'} onMouseLeave={(e) => e.target.style.color = '#94a3b8'}>
                  Gazette & QCO Updates
                </button>
              </li>
            </ul>
          </div>

          {/* Col 3: Official BIS Portals */}
          <div>
            <h5 style={{ color: '#ffffff', fontSize: '0.95rem', fontWeight: 700, marginBottom: '1.1rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Official Portals
            </h5>
            <ul style={{ listStyle: 'none', display: 'flex', flexDirection: 'column', gap: '0.6rem', fontSize: '0.88rem' }}>
              <li>
                <a href="https://manakonline.in" target="_blank" rel="noreferrer" style={{ color: '#94a3b8', display: 'inline-flex', alignItems: 'center', gap: '0.35rem' }}>
                  Manakonline e-BIS <ExternalLink size={12} />
                </a>
              </li>
              <li>
                <a href="https://standardsbis.bsbedge.com" target="_blank" rel="noreferrer" style={{ color: '#94a3b8', display: 'inline-flex', alignItems: 'center', gap: '0.35rem' }}>
                  BIS Standards e-Sale <ExternalLink size={12} />
                </a>
              </li>
              <li>
                <a href="https://www.crsbis.in" target="_blank" rel="noreferrer" style={{ color: '#94a3b8', display: 'inline-flex', alignItems: 'center', gap: '0.35rem' }}>
                  Electronics CRS Portal <ExternalLink size={12} />
                </a>
              </li>
              <li>
                <a href="https://bis.gov.in" target="_blank" rel="noreferrer" style={{ color: '#94a3b8', display: 'inline-flex', alignItems: 'center', gap: '0.35rem' }}>
                  Bureau of Indian Standards HQ <ExternalLink size={12} />
                </a>
              </li>
            </ul>
          </div>

          {/* Col 4: Quality & Standards Guidance */}
          <div>
            <h5 style={{ color: '#ffffff', fontSize: '0.95rem', fontWeight: 700, marginBottom: '1.1rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Standards & Compliance
            </h5>
            <p style={{ fontSize: '0.82rem', color: '#94a3b8', lineHeight: 1.6, marginBottom: '1rem' }}>
              Indian Standards are formulated through technical consensus by BIS Committees and notified under Quality Control Orders (QCOs) for public safety and consumer welfare.
            </p>
            <div style={{ background: 'rgba(255,255,255,0.05)', padding: '0.75rem', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.1)', display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#93c5fd', fontSize: '0.82rem' }}>
              <ShieldCheck size={16} color="var(--primary-400)" />
              <span>National Standards Body of India</span>
            </div>
          </div>

        </div>

        {/* Bottom Strip */}
        <div style={{ paddingTop: '1.5rem', borderTop: '1px solid rgba(255,255,255,0.08)', display: 'flex', flexWrap: 'wrap', alignItems: 'center', justifyContent: 'space-between', gap: '1rem', fontSize: '0.78rem', color: '#64748b' }}>
          <div>
            © 2026 Bureau of Indian Standards (BIS) • AI Assistant. All standard codes, gazettes and trademarks belong to their respective authorities.
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <span>Digital Public Infrastructure</span>
          </div>
        </div>
      </div>
    </footer>
  );
}
