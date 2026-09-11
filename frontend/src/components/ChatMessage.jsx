import React, { useState } from 'react';
import { 
  Bot, 
  User, 
  ShieldCheck, 
  FileText, 
  ExternalLink, 
  Copy, 
  Check, 
  Bookmark, 
  BookmarkCheck, 
  AlertCircle, 
  Eye, 
  CheckCircle2 
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { FormattedMarkdown } from './FormattedMarkdown';

export default function ChatMessage({ message }) {
  const { toggleSaveAnswer, savedAnswers, setActiveStandardModal, t } = useApp();
  const [copied, setCopied] = useState(false);

  const isUser = message.role === 'user';
  const isSaved = savedAnswers.some(a => a.id === message.id);
  const standard = message.matchedStandard;

  const handleCopy = () => {
    let copyText = message.content;
    if (standard) {
      copyText += `\n\nRelevant Standard: ${standard.isNumber} - ${standard.title}\nScheme: ${standard.scheme}\nSource: ${message.sourceReference?.document || 'BIS Repository'}`;
    }
    navigator.clipboard.writeText(copyText);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  if (isUser) {
    return (
      <div className="chat-msg-row user">
        <div className="msg-avatar user">
          <User size={18} />
        </div>
        <div className="msg-bubble-container user">
          <div className="user-bubble">
            {message.content}
          </div>
          <span className="msg-timestamp user">
            {message.timestamp}
          </span>
        </div>
      </div>
    );
  }

  return (
    <div className="chat-msg-row ai">
      <div className="msg-avatar ai">
        <Bot size={18} />
      </div>

      <div className="msg-bubble-container ai">
        <div className="ai-response-box">
          {/* Header Status Bar */}
          <div className="ai-header-status">
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', flexWrap: 'wrap' }}>
              <span style={{ color: 'var(--text-main)', fontWeight: 700, fontSize: '0.88rem' }}>
                {message.category || "BIS Standards Assistant"}
              </span>
            </div>

            <div className="ai-disclaimer-pill" title="AI synthesized from authorized BIS Gazettes">
              <ShieldCheck size={13} color="var(--primary-600)" />
              <span>BIS Grounded AI</span>
            </div>
          </div>

          {/* Natural Language Answer Body with Rich Markdown Renderer */}
          <div className="ai-natural-answer">
            <FormattedMarkdown content={message.content} />
          </div>

          {/* Structured "Relevant Standard" Card */}
          {standard && (
            <div className="relevant-standard-card">
              <div className="standard-card-header">
                <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', flexWrap: 'wrap' }}>
                  <span className="is-number-tag">{standard.isNumber}</span>
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

                <button
                  className="btn btn-outline btn-sm"
                  onClick={() => setActiveStandardModal(standard)}
                  style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}
                >
                  <Eye size={13} />
                  <span>{t.viewDetails}</span>
                </button>
              </div>

              {/* Standard Title */}
              <div className="standard-title-display">
                {standard.title}
              </div>

              {/* Meta Grid */}
              <div className="standard-meta-grid">
                <div className="meta-field">
                  <span className="meta-field-label">Division & Committee</span>
                  <span className="meta-field-val">{standard.division} • {standard.category}</span>
                </div>
                <div className="meta-field">
                  <span className="meta-field-label">Conformity Scheme</span>
                  <span className="meta-field-val">{standard.scheme}</span>
                </div>
              </div>

              {/* Applicability Note */}
              <div className="standard-applicability-note">
                <strong>Applicability:</strong> {standard.scope}
              </div>

              {/* Key Testing Requirements */}
              {standard.testingRequirements && standard.testingRequirements.length > 0 && (
                <div className="requirements-list-box">
                  <div className="req-box-title">
                    <CheckCircle2 size={14} color="var(--primary-600)" />
                    <span>{t.testingReqs}</span>
                  </div>
                  <ul className="req-bullet-list">
                    {standard.testingRequirements.slice(0, 3).map((req, idx) => (
                      <li key={idx} className="req-bullet-item">{req}</li>
                    ))}
                  </ul>
                </div>
              )}

              {/* Related Standards */}
              {standard.relatedStandards && (
                <div style={{ fontSize: '0.78rem', color: 'var(--text-subtle)', display: 'flex', alignItems: 'center', gap: '0.5rem', flexWrap: 'wrap' }}>
                  <span style={{ fontWeight: 600 }}>Related Norms:</span>
                  {standard.relatedStandards.map((rel, idx) => (
                    <span key={idx} style={{ background: 'var(--bg-subtle)', padding: '0.15rem 0.45rem', borderRadius: '4px', border: '1px solid var(--border-light)' }}>
                      {rel}
                    </span>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* Official Source Reference Card (Responsive) */}
          {message.sourceReference && (
            <div className="source-ref-box">
              <div className="source-left">
                <div className="source-icon-wrap">
                  <FileText size={18} color="var(--primary-600)" />
                </div>
                <div className="source-text-block">
                  <div className="source-doc-name">{message.sourceReference.document}</div>
                  <div className="source-clause-row">
                    <span className="source-clause-label">Clause / Reference:</span>
                    <span className="source-clause-badge">{message.sourceReference.clause}</span>
                  </div>
                </div>
              </div>

              {(message.sourceReference.portalUrl || message.sourceReference.sourceUrl) && (
                <a
                  href={(message.sourceReference.portalUrl || message.sourceReference.sourceUrl).trim()}
                  target="_blank"
                  rel="noreferrer"
                  className="source-view-link"
                >
                  <span>View Source</span>
                  <ExternalLink size={13} />
                </a>
              )}
            </div>
          )}

          {/* Disclaimer Banner */}
          <div className="ai-disclaimer-banner">
            <AlertCircle size={14} color="var(--text-subtle)" style={{ flexShrink: 0, marginTop: '1px' }} />
            <span>{t.aiGeneratedDisclaimer}</span>
          </div>

          {/* AI Action Strip */}
          <div className="ai-actions-strip">
            <span className="msg-timestamp">{message.timestamp}</span>

            <div className="ai-action-buttons">
              <button
                className="icon-action-btn"
                onClick={handleCopy}
                title={copied ? "Copied!" : "Copy Response"}
                aria-label="Copy response"
              >
                {copied ? <Check size={15} color="var(--emerald-600)" /> : <Copy size={15} />}
              </button>

              <button
                className="icon-action-btn"
                onClick={() => toggleSaveAnswer(message)}
                title={isSaved ? "Saved in Workspace" : "Bookmark Answer"}
                aria-label="Bookmark answer"
              >
                {isSaved ? <BookmarkCheck size={15} color="var(--primary-600)" /> : <Bookmark size={15} />}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
