import React from 'react';
import { 
  X, 
  ShieldCheck, 
  AlertCircle, 
  FileText, 
  CheckCircle2, 
  ExternalLink, 
  Bookmark, 
  BookmarkCheck, 
  Bot, 
  Printer, 
  Layers,
  Clock,
  DollarSign
} from 'lucide-react';
import { useApp } from '../context/AppContext';

export default function StandardDetailModal() {
  const { activeStandardModal, setActiveStandardModal, savedStandards, toggleSaveStandard, launchChatWithQuery, showToast, t } = useApp();

  if (!activeStandardModal) return null;
  const s = activeStandardModal;
  const isSaved = savedStandards.includes(s.id);

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="modal-overlay" onClick={() => setActiveStandardModal(null)}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        {/* Close Button */}
        <button
          className="modal-close-btn"
          onClick={() => setActiveStandardModal(null)}
          aria-label="Close modal"
        >
          <X size={20} />
        </button>

        {/* Modal Header */}
        <div style={{ marginBottom: '1.5rem', paddingRight: '2rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginBottom: '0.5rem', flexWrap: 'wrap' }}>
            <span className="is-number-tag" style={{ fontSize: '1.25rem' }}>{s.isNumber}</span>
            <span className={`badge-pill ${s.isMandatory ? 'badge-mandatory' : 'badge-voluntary'}`}>
              {s.isMandatory ? (
                <>
                  <AlertCircle size={12} />
                  <span>{t.mandatoryQco}</span>
                </>
              ) : (
                <span>{t.voluntaryStandard}</span>
              )}
            </span>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-subtle)', background: 'var(--bg-subtle)', padding: '0.2rem 0.6rem', borderRadius: '4px', border: '1px solid var(--border-light)' }}>
              Year: {s.year}
            </span>
          </div>

          <h3 style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--text-main)', lineHeight: 1.3 }}>
            {s.title}
          </h3>

          <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '0.4rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span><strong>Division:</strong> {s.division}</span>
            <span>•</span>
            <span><strong>Scheme:</strong> {s.scheme}</span>
          </div>
        </div>

        {/* QCO Regulatory Notice if applicable */}
        {s.qcoOrder && (
          <div style={{ background: 'var(--badge-qco-bg)', border: '1px solid var(--badge-qco-border)', color: 'var(--badge-qco-text)', padding: '0.85rem 1.15rem', borderRadius: 'var(--radius-md)', marginBottom: '1.5rem', fontSize: '0.86rem', display: 'flex', alignItems: 'flex-start', gap: '0.6rem' }}>
            <AlertCircle size={18} style={{ flexShrink: 0, marginTop: '2px' }} />
            <div>
              <div style={{ fontWeight: 700 }}>Mandatory Quality Control Order Enforced:</div>
              <div>{s.qcoOrder}</div>
            </div>
          </div>
        )}

        {/* Standard Scope */}
        <div className="form-group">
          <h4 style={{ fontSize: '0.95rem', fontWeight: 700, color: 'var(--text-main)', marginBottom: '0.4rem', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
            Standard Scope & Description
          </h4>
          <p style={{ fontSize: '0.92rem', color: 'var(--text-muted)', lineHeight: 1.6, background: 'var(--bg-subtle)', padding: '1rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)' }}>
            {s.scope}
          </p>
        </div>

        {/* Key Clauses Matrix */}
        {s.keyClauses && s.keyClauses.length > 0 && (
          <div className="form-group">
            <h4 style={{ fontSize: '0.95rem', fontWeight: 700, color: 'var(--text-main)', marginBottom: '0.6rem', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
              Essential Clauses & Technical Limits
            </h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              {s.keyClauses.map((c, idx) => (
                <div key={idx} style={{ background: 'var(--bg-surface)', border: '1px solid var(--border-light)', borderRadius: '6px', padding: '0.75rem 1rem', display: 'flex', alignItems: 'flex-start', gap: '0.75rem' }}>
                  <span className="source-clause-badge" style={{ marginTop: '2px' }}>{c.clause}</span>
                  <div style={{ flex: 1 }}>
                    <div style={{ fontWeight: 700, fontSize: '0.88rem', color: 'var(--text-main)' }}>{c.title}</div>
                    <div style={{ fontSize: '0.84rem', color: 'var(--text-muted)' }}>{c.desc}</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Testing Parameters */}
        {s.testingRequirements && (
          <div className="form-group">
            <h4 style={{ fontSize: '0.95rem', fontWeight: 700, color: 'var(--text-main)', marginBottom: '0.6rem', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
              Mandatory Testing Requirements
            </h4>
            <div style={{ background: 'var(--bg-subtle)', border: '1px solid var(--border-light)', borderRadius: 'var(--radius-md)', padding: '1rem' }}>
              <ul className="req-bullet-list">
                {s.testingRequirements.map((req, idx) => (
                  <li key={idx} className="req-bullet-item">{req}</li>
                ))}
              </ul>
            </div>
          </div>
        )}

        {/* Applicable Products */}
        {s.applicableProducts && (
          <div className="form-group">
            <h4 style={{ fontSize: '0.95rem', fontWeight: 700, color: 'var(--text-main)', marginBottom: '0.4rem', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
              Applicable Product Categories
            </h4>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.4rem' }}>
              {s.applicableProducts.map((p, idx) => (
                <span key={idx} style={{ background: 'var(--primary-50)', color: 'var(--primary-800)', padding: '0.25rem 0.65rem', borderRadius: 'var(--radius-full)', fontSize: '0.82rem', fontWeight: 600, border: '1px solid var(--primary-100)' }}>
                  {p}
                </span>
              ))}
            </div>
          </div>
        )}

        {/* Licensing & Fee Info */}
        <div className="modal-meta-grid" style={{ marginBottom: '1.5rem' }}>
          <div style={{ background: 'var(--bg-subtle)', padding: '0.85rem 1rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)', fontSize: '0.82rem' }}>
            <div style={{ fontWeight: 700, color: 'var(--text-main)', display: 'flex', alignItems: 'center', gap: '0.35rem', marginBottom: '0.25rem' }}>
              <Clock size={14} color="var(--primary-600)" />
              <span>Licensing Workflow</span>
            </div>
            <div style={{ color: 'var(--text-muted)', lineHeight: 1.5 }}>{s.licensingProcess}</div>
          </div>

          <div style={{ background: 'var(--bg-subtle)', padding: '0.85rem 1rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-light)', fontSize: '0.82rem' }}>
            <div style={{ fontWeight: 700, color: 'var(--text-main)', display: 'flex', alignItems: 'center', gap: '0.35rem', marginBottom: '0.25rem' }}>
              <DollarSign size={14} color="var(--emerald-600)" />
              <span>Fee & MSME Concessions</span>
            </div>
            <div style={{ color: 'var(--text-muted)', lineHeight: 1.5 }}>{s.feeCategory}</div>
          </div>
        </div>

        {/* Action Controls */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', paddingTop: '1.25rem', borderTop: '1px solid var(--border-light)', flexWrap: 'wrap', gap: '0.75rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <button
              className="btn btn-primary btn-sm"
              onClick={() => {
                setActiveStandardModal(null);
                launchChatWithQuery(`How to apply for BIS certification under ${s.isNumber}?`);
              }}
            >
              <Bot size={14} />
              <span>Consult AI Assistant</span>
            </button>

            <button
              className="btn btn-outline btn-sm"
              onClick={handlePrint}
            >
              <Printer size={14} />
              <span>Export / Print</span>
            </button>
          </div>

          <button
            className="btn btn-subtle btn-sm"
            onClick={() => toggleSaveStandard(s.id)}
          >
            {isSaved ? <BookmarkCheck size={14} color="var(--primary-600)" /> : <Bookmark size={14} />}
            <span>{isSaved ? "Saved in Workspace" : "Save Standard"}</span>
          </button>
        </div>
      </div>
    </div>
  );
}
