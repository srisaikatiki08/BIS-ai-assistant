import React, { useState } from 'react';
import { ShieldCheck, Lock, Mail, ArrowRight, AlertCircle, Loader2, Sparkles, Languages, Moon, Sun } from 'lucide-react';
import { useApp } from '../context/AppContext';

export default function LoginView() {
  const { login, setAuthView, showToast, theme, setTheme, language, setLanguage, t } = useApp();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');

    if (!email.trim() || !password) {
      setErrorMessage('Please enter both email and password.');
      return;
    }

    setLoading(true);
    const result = await login(email, password);
    setLoading(false);

    if (result.success) {
      showToast('Welcome back! Successfully logged in.', 'success');
    } else {
      setErrorMessage(result.error || 'Invalid email or password.');
    }
  };

  const languagesList = [
    { code: 'en', label: 'English' },
    { code: 'hi', label: 'हिन्दी' },
    { code: 'te', label: 'తెలుగు' }
  ];

  return (
    <div className="auth-page-wrapper">
      {/* Top Gov Tricolor Bar */}
      <div className="gov-top-bar" />

      {/* Top Utility Bar (Language & Theme) */}
      <div className="auth-utility-bar">
        <div className="auth-emblem-tag">
          <ShieldCheck size={14} color="var(--primary-600)" />
          <span>Bureau of Indian Standards • Ministry of Consumer Affairs</span>
        </div>

        <div className="auth-controls">
          {/* Language Selector */}
          <div className="auth-lang-pills">
            {languagesList.map((lang) => (
              <button
                key={lang.code}
                type="button"
                className={`auth-lang-btn ${language === lang.code ? 'active' : ''}`}
                onClick={() => setLanguage(lang.code)}
              >
                {lang.label}
              </button>
            ))}
          </div>

          {/* Theme Toggle */}
          <button
            type="button"
            className="theme-toggle-btn"
            onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}
            title={`Switch to ${theme === 'light' ? 'Dark' : 'Light'} Mode`}
            aria-label="Toggle theme"
          >
            {theme === 'light' ? <Moon size={16} /> : <Sun size={16} />}
          </button>
        </div>
      </div>

      {/* Main Container */}
      <div className="auth-content-container">
        <div className="auth-card">
          {/* Header & Logo */}
          <div className="auth-header">
            <div className="auth-logo-badge">
              <span className="auth-logo-text">BIS</span>
            </div>
            <h1 className="auth-title">Sign In to BIS Assistant</h1>
            <p className="auth-subtitle">
              Access Bureau of Indian Standards compliance advisory, standards search, and AI assistant workspace.
            </p>
          </div>

          {/* Error Alert */}
          {errorMessage && (
            <div className="auth-error-alert" role="alert">
              <AlertCircle size={18} style={{ flexShrink: 0 }} />
              <span>{errorMessage}</span>
            </div>
          )}

          {/* Form */}
          <form onSubmit={handleSubmit} className="auth-form" noValidate>
            <div className="auth-field-group">
              <label className="auth-label" htmlFor="login-email">
                Official or Personal Email
              </label>
              <div className="auth-input-wrapper">
                <Mail size={17} className="auth-input-icon" />
                <input
                  id="login-email"
                  type="email"
                  className="auth-input"
                  placeholder="name@organization.gov.in"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  autoComplete="email"
                  required
                  autoFocus
                />
              </div>
            </div>

            <div className="auth-field-group">
              <label className="auth-label" htmlFor="login-password">
                Password
              </label>
              <div className="auth-input-wrapper">
                <Lock size={17} className="auth-input-icon" />
                <input
                  id="login-password"
                  type="password"
                  className="auth-input"
                  placeholder="Enter your password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  autoComplete="current-password"
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              className="btn btn-primary btn-lg auth-submit-btn"
              disabled={loading}
            >
              {loading ? (
                <>
                  <Loader2 size={18} className="spinner-icon" />
                  <span>Verifying Credentials...</span>
                </>
              ) : (
                <>
                  <span>Sign In</span>
                  <ArrowRight size={16} />
                </>
              )}
            </button>
          </form>

          {/* Switch to Signup */}
          <div className="auth-footer-switch">
            <span>Don't have an account?</span>
            <button
              type="button"
              className="auth-switch-link"
              onClick={() => setAuthView('signup')}
            >
              Create an Account
            </button>
          </div>
        </div>

        {/* Security / Compliance Footnote */}
        <div className="auth-security-notice">
          <ShieldCheck size={14} />
          <span>Protected with 256-bit JWT Encryption • Secure Enterprise Portal</span>
        </div>
      </div>
    </div>
  );
}
