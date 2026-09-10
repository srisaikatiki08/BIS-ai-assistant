import React, { useState, useRef, useEffect } from 'react';
import {
  Bot,
  Plus,
  Settings,
  MessageSquare,
  X,
  Eraser,
  Trash2,
  Menu,
  Sparkles,
  Sun,
  Moon,
  Languages
} from 'lucide-react';

import { useApp } from '../context/AppContext';
import ChatMessage from '../components/ChatMessage';
import ChatInput from '../components/ChatInput';
import LoadingIndicator from '../components/LoadingIndicator';

export default function ChatView() {
  const {
    messages,
    sendMessage,
    isAiThinking,
    startNewChat,
    language,
    setLanguage,
    theme,
    setTheme,
    showToast,
    t,
    setMessages,
    currentSessionId,
    conversations,
    selectConversation,
    deleteChatSession,
    clearAllChatSessions
  } = useApp();

  const [showSettingsModal, setShowSettingsModal] = useState(false);
  const [showClearConfirm, setShowClearConfirm] = useState(false);
  const [mobileSidebarOpen, setMobileSidebarOpen] = useState(false);

  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({
      behavior: 'smooth'
    });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, isAiThinking]);

  // Example Questions with generous whitespace
  const exampleQuestions = [
    "What BIS standard applies to electrical cables?",
    "How do I get BIS certification?",
    "What products require BIS certification?",
    "Which standard applies to helmets?"
  ];

  const handleClearCurrentChat = () => {
    startNewChat();
    setShowClearConfirm(false);
    setMobileSidebarOpen(false);
  };

  const handleDeleteMessage = (msgId) => {
    setMessages((prev) =>
      prev.filter((m) => m.id !== msgId)
    );
  };

  return (
    <div className="chat-layout">

      {/* Mobile Backdrop Overlay */}
      {mobileSidebarOpen && (
        <div
          className="chat-sidebar-backdrop"
          onClick={() => setMobileSidebarOpen(false)}
        />
      )}

      {/* =========================
          LEFT SIDEBAR (CHATGPT-STYLE)
      ========================== */}

      <aside
        className={`chat-sidebar ${
          mobileSidebarOpen ? 'mobile-open' : ''
        }`}
      >

        {/* Sidebar Header: New Chat & Mobile Close */}
        <div className="chat-sidebar-header">
          <button
            className="btn-new-chat"
            onClick={() => {
              startNewChat();
              setMobileSidebarOpen(false);
            }}
          >
            <Plus size={18} />
            <span>New Chat</span>
          </button>

          {/* Close button inside mobile drawer */}
          <button
            className="mobile-sidebar-close-btn"
            onClick={() => setMobileSidebarOpen(false)}
            aria-label="Close sidebar"
          >
            <X size={18} />
          </button>
        </div>

        {/* Live Status Indicator */}
        <div className="chat-sidebar-status">
          <div className="status-badge-inner">
            <span className="live-pulse-dot"></span>
            <span>BIS AI Assistant · Online</span>
          </div>
        </div>

        {/* Conversations History List */}
        <div className="chat-sidebar-section">
          <div className="sidebar-section-title">
            <span>Chat History</span>
            {conversations && conversations.length > 0 && (
              <button
                onClick={clearAllChatSessions}
                className="clear-history-link"
                title="Clear all conversations"
              >
                Clear all
              </button>
            )}
          </div>

          <div className="chat-history-scroll-list">
            {(!conversations || conversations.length === 0) ? (
              <div className="empty-history-text">
                No recent conversations
              </div>
            ) : (
              conversations.map((conv) => {
                const convId = conv.sessionIdentifier || conv.id;
                const isActive = convId === currentSessionId;

                return (
                  <div
                    key={convId}
                    className={`history-item ${isActive ? 'active' : ''}`}
                    onClick={() => {
                      selectConversation(convId);
                      setMobileSidebarOpen(false);
                    }}
                    title={conv.title}
                  >
                    <div className="history-item-content">
                      <div className="history-icon-wrap">
                        <MessageSquare
                          size={15}
                          style={{ flexShrink: 0 }}
                        />
                      </div>
                      <span className="history-item-title">
                        {conv.title || "BIS Inquiry"}
                      </span>
                    </div>

                    <button
                      className="history-delete-btn"
                      onClick={(e) => deleteChatSession(convId, e)}
                      title="Delete conversation"
                      aria-label="Delete conversation"
                    >
                      <Trash2 size={13} />
                    </button>
                  </div>
                );
              })
            )}
          </div>
        </div>

        {/* Sidebar Footer */}
        <div className="chat-sidebar-footer">
          <button
            onClick={() => setShowClearConfirm(true)}
            className="sidebar-footer-btn clear"
            disabled={messages.length <= 1}
            title="Clear current messages"
          >
            <Eraser size={15} />
            <span>Clear Chat</span>
          </button>

          <button
            onClick={() => setShowSettingsModal(true)}
            className="sidebar-footer-btn"
          >
            <Settings size={15} />
            <span>Settings</span>
          </button>
        </div>
      </aside>

      {/* =========================
          MAIN CHAT AREA
      ========================== */}

      <main className="chat-main-area">

        {/* Mobile Top Bar */}
        <div className="chat-mobile-topbar">
          <button
            className="mobile-sidebar-toggle"
            onClick={() => setMobileSidebarOpen(true)}
          >
            <Menu size={18} />
            <span>History</span>
          </button>

          <button
            className="mobile-new-chat-btn"
            onClick={startNewChat}
          >
            <Plus size={16} />
            <span>New Chat</span>
          </button>
        </div>

        {/* Messages Scroll Area */}
        <div className="chat-messages-scroll">

          {/* Welcome Screen / Clean Example Questions (Shown when no user messages yet) */}
          {messages.length <= 1 && (!messages[0] || messages[0].category === "Welcome") ? (
            <div className="chat-welcome-container">
              <div className="chat-bot-avatar-lg">
                <Bot size={36} />
              </div>

              <h2 className="chat-welcome-title">
                BIS Intelligent Assistant
              </h2>

              <p className="chat-welcome-subtitle">
                Ask any question regarding Indian Standards (IS), BIS certification procedures, or testing requirements.
              </p>

              {/* Simplified Try Asking Section */}
              <div className="try-asking-box">
                <div className="try-asking-header">
                  <Sparkles size={14} color="var(--primary-600)" />
                  <span className="try-asking-header-title">Try asking:</span>
                </div>

                <div className="simple-questions-grid">
                  {exampleQuestions.map((q, idx) => (
                    <button
                      key={idx}
                      className="simple-question-chip"
                      onClick={() => sendMessage(q)}
                    >
                      <span className="chip-dot"></span>
                      <span className="chip-text">{q}</span>
                    </button>
                  ))}
                </div>
              </div>
            </div>
          ) : (
            /* Conversation Messages Container */
            <div className="chat-conversation-container">
              {messages.filter(m => m.category !== "Welcome").map((msg) => (
                <ChatMessage
                  key={msg.id}
                  message={msg}
                  onDelete={() => handleDeleteMessage(msg.id)}
                />
              ))}

              {/* AI Thinking Indicator */}
              {isAiThinking && (
                <LoadingIndicator />
              )}
            </div>
          )}

          <div ref={messagesEndRef} />
        </div>

        {/* Chat Input */}
        <ChatInput />
      </main>

      {/* =========================
          CLEAR CHAT CONFIRMATION MODAL
      ========================== */}

      {showClearConfirm && (
        <div
          className="modal-overlay"
          onClick={() => setShowClearConfirm(false)}
        >
          <div
            className="modal-content confirm-modal"
            onClick={(e) => e.stopPropagation()}
            style={{ maxWidth: '400px' }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '0.75rem' }}>
              <div
                style={{
                  width: '40px',
                  height: '40px',
                  borderRadius: '10px',
                  background: '#fee2e2',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  flexShrink: 0
                }}
              >
                <Trash2 size={18} color="#ef4444" />
              </div>

              <h3 style={{ fontSize: '1.1rem', fontWeight: 800 }}>
                Clear this chat?
              </h3>
            </div>

            <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)', marginBottom: '1.5rem', lineHeight: 1.5 }}>
              This will clear the messages in your current conversation view.
            </p>

            <div style={{ display: 'flex', gap: '0.75rem' }}>
              <button
                className="btn btn-outline"
                style={{ flex: 1 }}
                onClick={() => setShowClearConfirm(false)}
              >
                Cancel
              </button>

              <button
                className="btn"
                style={{
                  flex: 1,
                  background: '#ef4444',
                  color: 'white',
                  border: 'none'
                }}
                onClick={handleClearCurrentChat}
              >
                Clear
              </button>
            </div>
          </div>
        </div>
      )}

      {/* =========================
          SIMPLIFIED SETTINGS MODAL
      ========================== */}

      {showSettingsModal && (
        <div
          className="modal-overlay"
          onClick={() => setShowSettingsModal(false)}
        >
          <div
            className="modal-content"
            onClick={(e) => e.stopPropagation()}
            style={{ maxWidth: '440px' }}
          >
            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                marginBottom: '1.25rem'
              }}
            >
              <h3 style={{ fontSize: '1.2rem', fontWeight: 800 }}>
                Settings
              </h3>

              <button
                onClick={() => setShowSettingsModal(false)}
                style={{
                  background: 'none',
                  border: 'none',
                  cursor: 'pointer',
                  color: 'var(--text-muted)',
                  display: 'flex',
                  padding: '4px'
                }}
                aria-label="Close Settings"
              >
                <X size={20} />
              </button>
            </div>

            {/* 1. Language Option */}
            <div className="form-group" style={{ marginBottom: '1.5rem' }}>
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', marginBottom: '0.5rem', fontSize: '0.88rem', fontWeight: 600 }}>
                <Languages size={15} color="var(--primary-600)" />
                <span>Language</span>
              </label>

              <select
                className="filter-select-field"
                value={language}
                onChange={(e) => setLanguage(e.target.value)}
              >
                <option value="en">English</option>
                <option value="hi">हिंदी (Hindi)</option>
                <option value="te">తెలుగు (Telugu)</option>
              </select>
            </div>

            {/* 2. Theme Option */}
            <div className="form-group" style={{ marginBottom: '1.75rem' }}>
              <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', marginBottom: '0.5rem', fontSize: '0.88rem', fontWeight: 600 }}>
                {theme === 'light' ? <Sun size={15} color="#ea580c" /> : <Moon size={15} color="#3b82f6" />}
                <span>Theme</span>
              </label>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
                <button
                  type="button"
                  onClick={() => setTheme('light')}
                  style={{
                    padding: '0.75rem',
                    borderRadius: 'var(--radius-md)',
                    border: `2px solid ${theme === 'light' ? 'var(--primary-600)' : 'var(--border-light)'}`,
                    background: theme === 'light' ? 'var(--primary-50)' : 'var(--bg-surface)',
                    color: theme === 'light' ? 'var(--primary-700)' : 'var(--text-main)',
                    fontWeight: 600,
                    fontSize: '0.88rem',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: '0.4rem',
                    transition: 'var(--transition-fast)'
                  }}
                >
                  <Sun size={16} />
                  <span>Light</span>
                </button>

                <button
                  type="button"
                  onClick={() => setTheme('dark')}
                  style={{
                    padding: '0.75rem',
                    borderRadius: 'var(--radius-md)',
                    border: `2px solid ${theme === 'dark' ? 'var(--primary-600)' : 'var(--border-light)'}`,
                    background: theme === 'dark' ? 'rgba(59, 130, 246, 0.15)' : 'var(--bg-surface)',
                    color: theme === 'dark' ? '#93c5fd' : 'var(--text-main)',
                    fontWeight: 600,
                    fontSize: '0.88rem',
                    cursor: 'pointer',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    gap: '0.4rem',
                    transition: 'var(--transition-fast)'
                  }}
                >
                  <Moon size={16} />
                  <span>Dark</span>
                </button>
              </div>
            </div>

            {/* Done Button */}
            <button
              className="btn btn-primary"
              style={{ width: '100%' }}
              onClick={() => {
                setShowSettingsModal(false);
                showToast('Settings saved', 'success');
              }}
            >
              Done
            </button>
          </div>
        </div>
      )}

    </div>
  );
}