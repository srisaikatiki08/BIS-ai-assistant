import React, { useState } from 'react';
import { ShieldCheck, Lock, Mail, User, ArrowRight, AlertCircle, Loader2, Languages, Moon, Sun, CheckCircle2 } from 'lucide-react';
import { useApp } from '../context/AppContext';

export default function SignupView() {
  const { signup, setAuthView, showToast, theme, setTheme, language, setLanguage } = useApp();

  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage('');

    if (!fullName.trim()) {
      setErrorMessage('Please enter your full name.');
      return;
    }

    if (!email.trim()) {
      setErrorMessage('Please enter a valid email address.');
      return;
    }

    if (!password) {
      setErrorMessage('Please enter a password.');
      return;
    }

    if (password.length < 6) {
      setErrorMessage('Password must be at least 6 characters long.');
      return;
    }

    if (password !== confirmPassword) {
      setErrorMessage('Passwords do not match. Please re-enter.');
      return;
    }

    setLoading(true);
    const result = await signup(fullName, email, password);
    setLoading(false);

    if (result.success) {
      showToast('Account created successfully! Welcome to BIS Assistant.', 'success');
    } else {
      setErrorMessage(result.error || 'Failed to create account.');
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
            <h1 className="auth-title">Create an Account</h1>
            <p className="auth-subtitle">
              Join the official BIS Assistant platform for compliance tracking and Indian Standards verification.
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
              <label className="auth-label" htmlFor="signup-name">
                Full Name
              </label>
              <div className="auth-input-wrapper">
                <User size={17} className="auth-input-icon" />
                <input
                  id="signup-name"
                  type="text"
                  className="auth-input"
                  placeholder="e.g. Priya Sharma"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  autoComplete="name"
                  required
                  autoFocus
                />
              </div>
            </div>

            <div className="auth-field-group">
              <label className="auth-label" htmlFor="signup-email">
                Official or Personal Email
              </label>
              <div className="auth-input-wrapper">
                <Mail size={17} className="auth-input-icon" />
                <input
                  id="signup-email"
                  type="email"
                  className="auth-input"
                  placeholder="name@organization.gov.in"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  autoComplete="email"
                  required
                />
              </div>
            </div>

            <div className="auth-field-group">
              <label className="auth-label" htmlFor="signup-password">
                Password <span style={{ fontSize: '0.75rem', fontWeight: 500, color: 'var(--text-subtle)' }}>(min. 6 characters)</span>
              </label>
              <div className="auth-input-wrapper">
                <Lock size={17} className="auth-input-icon" />
                <input
                  id="signup-password"
                  type="password"
                  className="auth-input"
                  placeholder="Create a secure password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  autoComplete="new-password"
                  required
                />
              </div>
            </div>

            <div className="auth-field-group">
              <label className="auth-label" htmlFor="signup-confirm-password">
                Confirm Password
              </label>
              <div className="auth-input-wrapper">
                <Lock size={17} className="auth-input-icon" />
                <input
                  id="signup-confirm-password"
                  type="password"
                  className="auth-input"
                  placeholder="Confirm your password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  autoComplete="new-password"
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
                  <span>Creating Account...</span>
                </>
              ) : (
                <>
                  <span>Create Account</span>
                  <ArrowRight size={16} />
                </>
              )}
            </button>
          </form>

          {/* Switch to Login */}
          <div className="auth-footer-switch">
            <span>Already have an account?</span>
            <button
              type="button"
              className="auth-switch-link"
              onClick={() => setAuthView('login')}
            >
              Sign In
            </button>
          </div>
        </div>

        {/* Security / Compliance Footnote */}
        <div className="auth-security-notice">
          <ShieldCheck size={14} />
          <span>Encrypted Password Storage • Official Bureau of Indian Standards Infrastructure</span>
        </div>
      </div>
    </div>
  );
}
