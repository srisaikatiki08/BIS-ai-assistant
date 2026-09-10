import React from 'react';
import { 
  ShieldCheck, 
  AlertCircle, 
  Bookmark, 
  BookmarkCheck, 
  Eye, 
  MessageSquare, 
  Layers, 
  CheckCircle2 
} from 'lucide-react';
import { useApp } from '../context/AppContext';

export default function StandardCard({ standard }) {
  const { savedStandards, toggleSaveStandard, setActiveStandardModal, launchChatWithQuery, t } = useApp();
  const isSaved = savedStandards.includes(standard.id);

  return (
    <div className="standard-card-item">
      {/* Top Header Row */}
      <div>
        <div className="card-top-info">
          <span className="standard-is-code">{standard.isNumber}</span>
          <span className={`badge-pill ${standard.isMandatory ? 'badge-mandatory' : 'badge-voluntary'}`}>
            {standard.isMandatory ? (
              <>
                <AlertCircle size={12} />
                <span>{t.mandatoryQco}</span>
              </>
            ) : (
              <span>{t.voluntaryStandard}</span>
            )}
          </span>
        </div>

        {/* Standard Title */}
        <h4 className="standard-card-name" style={{ marginTop: '0.6rem' }}>
          {standard.title}
        </h4>

        {/* Division & Scheme */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', flexWrap: 'wrap', marginTop: '0.5rem', fontSize: '0.78rem', color: 'var(--text-subtle)' }}>
          <span style={{ background: 'var(--bg-subtle)', padding: '0.15rem 0.5rem', borderRadius: '4px', border: '1px solid var(--border-light)', fontWeight: 600 }}>
            {standard.division}
          </span>
          <span>•</span>
          <span style={{ color: 'var(--primary-600)', fontWeight: 600 }}>
            {standard.scheme}
          </span>
        </div>
      </div>

      {/* Scope Excerpt */}
      <p className="standard-scope-excerpt">
        {standard.scope}
      </p>

      {/* Testing Highlights */}
      {standard.testingRequirements && standard.testingRequirements.length > 0 && (
        <div style={{ background: 'var(--bg-subtle)', padding: '0.65rem 0.85rem', borderRadius: '6px', border: '1px solid var(--border-light)', fontSize: '0.8rem' }}>
          <div style={{ fontWeight: 700, color: 'var(--text-muted)', marginBottom: '0.3rem', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
            <CheckCircle2 size={13} color="var(--primary-600)" />
            <span>Key Testing Methods:</span>
          </div>
          <div style={{ color: 'var(--text-main)', fontSize: '0.78rem' }}>
            {standard.testingRequirements[0]}
          </div>
        </div>
      )}

      {/* Footer Controls */}
      <div className="standard-card-footer">
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <button
            className="btn btn-outline btn-sm"
            onClick={() => setActiveStandardModal(standard)}
            title="Inspect Full Standard Scope & Clauses"
          >
            <Eye size={13} />
            <span>{t.viewDetails}</span>
          </button>

          <button
            className="btn btn-subtle btn-sm"
            onClick={() => launchChatWithQuery(`Explain testing requirements and certification process for ${standard.isNumber}`)}
            title="Ask AI Assistant about this standard"
          >
            <MessageSquare size={13} />
            <span>Ask AI</span>
          </button>
        </div>

        <button
          className="icon-action-btn"
          onClick={() => toggleSaveStandard(standard.id)}
          title={isSaved ? "Saved in Workspace" : "Bookmark Standard"}
        >
          {isSaved ? <BookmarkCheck size={16} color="var(--primary-600)" /> : <Bookmark size={16} />}
        </button>
      </div>
    </div>
  );
}
