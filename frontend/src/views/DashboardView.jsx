import React from 'react';
import { 
  Bookmark, 
  BookmarkCheck, 
  FileText, 
  MessageSquare, 
  Trash2, 
  Eye, 
  Bot, 
  CheckCircle2, 
  ArrowRight, 
  Download, 
  ShieldCheck,
  AlertCircle
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { STANDARDS_DATABASE } from '../data/standardsData';
import StandardCard from '../components/StandardCard';
import { FormattedMarkdown } from '../components/FormattedMarkdown';

export default function DashboardView() {
  const { 
    savedStandards, 
    savedAnswers, 
    toggleSaveStandard, 
    toggleSaveAnswer, 
    setActiveStandardModal, 
    launchChatWithQuery, 
    setActiveTab, 
    t 
  } = useApp();

  const savedStandardObjects = STANDARDS_DATABASE.filter(s => savedStandards.includes(s.id));

  return (
    <div className="page-container">
      <div className="max-w-7xl">
        {/* Header */}
        <div className="page-header">
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', color: 'var(--primary-600)', fontSize: '0.82rem', fontWeight: 800, textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: '0.4rem' }}>
            <Bookmark size={14} />
            <span>Personalized Compliance Workspace</span>
          </div>
          <h1 className="page-title">{t.nav.dashboard || "My Compliance Dashboard"}</h1>
          <p className="page-subtitle">
            Manage your bookmarked Indian Standards, saved AI compliance responses, and track certification dossiers.
          </p>
        </div>

        {/* Quick Summary Metric Cards */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '1.25rem', marginBottom: '2.5rem' }}>
          <div className="card-base" style={{ padding: '1.25rem', display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <div style={{ width: '48px', height: '48px', borderRadius: '12px', background: 'var(--primary-50)', color: 'var(--primary-700)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <BookmarkCheck size={24} />
            </div>
            <div>
              <div style={{ fontSize: '1.6rem', fontWeight: 800, color: 'var(--text-main)', lineHeight: 1.1 }}>{savedStandards.length}</div>
              <div style={{ fontSize: '0.82rem', color: 'var(--text-subtle)', fontWeight: 600 }}>Saved Standards</div>
            </div>
          </div>

          <div className="card-base" style={{ padding: '1.25rem', display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <div style={{ width: '48px', height: '48px', borderRadius: '12px', background: '#ecfdf5', color: '#059669', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <MessageSquare size={24} />
            </div>
            <div>
              <div style={{ fontSize: '1.6rem', fontWeight: 800, color: 'var(--text-main)', lineHeight: 1.1 }}>{savedAnswers.length}</div>
              <div style={{ fontSize: '0.82rem', color: 'var(--text-subtle)', fontWeight: 600 }}>Saved AI Notes</div>
            </div>
          </div>

          <div className="card-base" style={{ padding: '1.25rem', display: 'flex', alignItems: 'center', gap: '1rem' }}>
            <div style={{ width: '48px', height: '48px', borderRadius: '12px', background: '#fff7ed', color: '#ea580c', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <ShieldCheck size={24} />
            </div>
            <div>
              <div style={{ fontSize: '1.6rem', fontWeight: 800, color: 'var(--text-main)', lineHeight: 1.1 }}>Active</div>
              <div style={{ fontSize: '0.82rem', color: 'var(--text-subtle)', fontWeight: 600 }}>QCO Alert Monitoring</div>
            </div>
          </div>
        </div>

        {/* Section 1: Saved Standards */}
        <div style={{ marginBottom: '3.5rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.25rem', flexWrap: 'wrap', gap: '0.5rem' }}>
            <div>
              <h2 style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--text-main)' }}>
                Tracked Indian Standards
              </h2>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                Standards you have bookmarked for compliance monitoring and testing updates.
              </p>
            </div>

            <button className="btn btn-outline btn-sm" onClick={() => setActiveTab('standards')}>
              <span>Browse Catalog</span>
              <ArrowRight size={14} />
            </button>
          </div>

          {savedStandardObjects.length > 0 ? (
            <div className="standards-grid">
              {savedStandardObjects.map((std) => (
                <StandardCard key={std.id} standard={std} />
              ))}
            </div>
          ) : (
            <div style={{ background: 'var(--bg-surface)', padding: '2.5rem', borderRadius: 'var(--radius-lg)', border: '1px dashed var(--border-medium)', textAlign: 'center' }}>
              <Bookmark size={32} color="var(--text-subtle)" style={{ marginBottom: '0.5rem' }} />
              <h4 style={{ fontSize: '1.05rem', fontWeight: 700 }}>No Saved Standards Yet</h4>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>
                Save standards while exploring the catalog or asking the AI assistant to track them here.
              </p>
              <button className="btn btn-primary btn-sm" onClick={() => setActiveTab('standards')}>
                Explore Standards
              </button>
            </div>
          )}
        </div>

        {/* Section 2: Saved AI Answers & Notes */}
        <div>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.25rem', flexWrap: 'wrap', gap: '0.5rem' }}>
            <div>
              <h2 style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--text-main)' }}>
                Saved AI Advisory & Notes
              </h2>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                Key responses, clause citations, and testing protocols bookmarked from your AI conversations.
              </p>
            </div>

            <button className="btn btn-primary btn-sm" onClick={() => setActiveTab('chat')}>
              <Bot size={14} />
              <span>Open AI Assistant</span>
            </button>
          </div>

          {savedAnswers.length > 0 ? (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {savedAnswers.map((answer) => (
                <div key={answer.id} className="card-base" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--primary-700)', background: 'var(--primary-50)', padding: '0.2rem 0.5rem', borderRadius: '4px' }}>
                      {answer.category || "AI Compliance Note"}
                    </span>
                    <button
                      className="icon-action-btn"
                      onClick={() => toggleSaveAnswer(answer)}
                      title="Remove Note"
                    >
                      <Trash2 size={14} color="#ef4444" />
                    </button>
                  </div>

                  <div style={{ fontSize: '0.92rem', color: 'var(--text-main)', lineHeight: 1.6 }}>
                    <FormattedMarkdown content={answer.content} />
                  </div>

                  {answer.matchedStandard && (
                    <div style={{ fontSize: '0.82rem', background: 'var(--bg-subtle)', padding: '0.6rem 0.8rem', borderRadius: '6px', border: '1px solid var(--border-light)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                      <span><strong>Linked Standard:</strong> {answer.matchedStandard.isNumber}</span>
                      <button
                        className="btn btn-subtle btn-sm"
                        onClick={() => setActiveStandardModal(answer.matchedStandard)}
                      >
                        <Eye size={12} />
                        <span>Inspect</span>
                      </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          ) : (
            <div style={{ background: 'var(--bg-surface)', padding: '2.5rem', borderRadius: 'var(--radius-lg)', border: '1px dashed var(--border-medium)', textAlign: 'center' }}>
              <MessageSquare size={32} color="var(--text-subtle)" style={{ marginBottom: '0.5rem' }} />
              <h4 style={{ fontSize: '1.05rem', fontWeight: 700 }}>No Bookmarked AI Answers</h4>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>
                Bookmark useful AI answers during chat sessions to reference them here anytime.
              </p>
              <button className="btn btn-primary btn-sm" onClick={() => setActiveTab('chat')}>
                Start a Conversation
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
