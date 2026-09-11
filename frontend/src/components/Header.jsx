import React, { useState, useRef, useEffect } from 'react';
import { 
  Bot, 
  Search, 
  Layers, 
  FlaskConical, 
  Bell, 
  Bookmark, 
  Languages, 
  Moon, 
  Sun, 
  Menu, 
  X, 
  ShieldCheck, 
  Compass, 
  FileText,
  User as UserIcon,
  LogOut
} from 'lucide-react';
import { useApp } from '../context/AppContext';

export default function Header() {
  const { 
    activeTab, 
    setActiveTab, 
    language, 
    setLanguage, 
    theme, 
    setTheme, 
    user, 
    logout, 
    t 
  } = useApp();

  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [langDropdownOpen, setLangDropdownOpen] = useState(false);
  const [userDropdownOpen, setUserDropdownOpen] = useState(false);

  const langDropdownRef = useRef(null);
  const userDropdownRef = useRef(null);

  const navItems = [
    { id: 'home', label: t.nav.home, icon: Compass },
    { id: 'chat', label: t.nav.chat, icon: Bot, isAi: true },
    { id: 'standards', label: t.nav.standards, icon: Search },
    { id: 'documentation', label: 'Documentation', icon: FileText },
    { id: 'services', label: t.nav.services, icon: Layers },
    { id: 'labs', label: t.nav.labs, icon: FlaskConical },
    { id: 'updates', label: t.nav.updates, icon: Bell },
    { id: 'dashboard', label: t.nav.dashboard, icon: Bookmark }
  ];

  const handleNavClick = (tabId) => {
    setActiveTab(tabId);
    setMobileMenuOpen(false);
  };

  const languagesList = [
    { code: 'en', label: 'English' },
    { code: 'hi', label: 'हिंदी (Hindi)' },
    { code: 'te', label: 'తెలుగు (Telugu)' }
  ];

  // Close dropdowns when clicking outside
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (langDropdownRef.current && !langDropdownRef.current.contains(e.target)) {
        setLangDropdownOpen(false);
      }
      if (userDropdownRef.current && !userDropdownRef.current.contains(e.target)) {
        setUserDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Compute initials
  const getInitials = (name) => {
    if (!name) return 'U';
    const parts = name.trim().split(/\s+/);
    if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  };

  return (
    <>
      {/* Top Gov Tricolor Bar */}
      <div className="gov-top-bar" />

      {/* Sub Bar (Bureau of Indian Standards identity) */}
      <div className="gov-sub-bar">
        <div className="gov-sub-bar-left">
          <div className="gov-emblem-badge">
            <ShieldCheck size={13} />
            <span>Bureau of Indian Standards</span>
          </div>
        </div>

        {user && (
          <div className="gov-sub-bar-right" style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', fontSize: '0.75rem', color: 'var(--text-subtle)' }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '0.3rem' }}>
              <span className="user-status-dot" style={{ width: '6px', height: '6px', borderRadius: '50%', background: '#10b981', display: 'inline-block' }} />
              <span>Signed in as: <strong>{user.fullName || user.email}</strong></span>
            </span>
          </div>
        )}
      </div>

      {/* Main Header Bar */}
      <header className="header-wrapper">
        <div className="max-w-7xl header-container">
          {/* Logo & Subtitle */}
          <div className="brand-logo" onClick={() => handleNavClick('home')}>
            <div className="logo-symbol">
              BIS
            </div>
            <div className="brand-titles">
              <div className="brand-main">
                {t.brand}
              </div>
              <span className="brand-sub">{t.brandSubtitle}</span>
            </div>
          </div>

          {/* Navigation Links (Desktop) */}
          <nav className="nav-links-desktop">
            {navItems.map((item) => {
              const Icon = item.icon;
              const isActive = activeTab === item.id;
              return (
                <button
                  key={item.id}
                  className={`nav-item-btn ${isActive ? 'active' : ''}`}
                  onClick={() => handleNavClick(item.id)}
                >
                  <Icon size={15} style={{ flexShrink: 0 }} />
                  <span>{item.label}</span>
                  {item.isAi && <span className="nav-badge-pill">AI</span>}
                </button>
              );
            })}
          </nav>

          {/* Controls: Language, Theme, User Profile, Mobile Menu */}
          <div className="header-actions">
            {/* Language Switcher (Desktop) */}
            <div className="header-lang-wrapper" style={{ position: 'relative' }} ref={langDropdownRef}>
              <button
                className="lang-selector-btn"
                onClick={() => setLangDropdownOpen(!langDropdownOpen)}
                title="Change Language"
                aria-expanded={langDropdownOpen}
              >
                <Languages size={15} color="var(--primary-600)" />
                <span>
                  {language === 'en' ? 'EN' : language === 'hi' ? 'हिन्दी' : 'తెలుగు'}
                </span>
              </button>

              {langDropdownOpen && (
                <div
                  style={{
                    position: 'absolute',
                    top: 'calc(100% + 6px)',
                    right: 0,
                    background: 'var(--bg-surface)',
                    border: '1px solid var(--border-light)',
                    borderRadius: 'var(--radius-md)',
                    boxShadow: 'var(--shadow-lg)',
                    padding: '0.4rem',
                    zIndex: 150,
                    minWidth: '150px'
                  }}
                >
                  {languagesList.map((lang) => (
                    <button
                      key={lang.code}
                      onClick={() => {
                        setLanguage(lang.code);
                        setLangDropdownOpen(false);
                      }}
                      style={{
                        display: 'block',
                        width: '100%',
                        textAlign: 'left',
                        padding: '0.5rem 0.75rem',
                        fontSize: '0.85rem',
                        background: language === lang.code ? 'var(--primary-50)' : 'transparent',
                        color: language === lang.code ? 'var(--primary-700)' : 'var(--text-main)',
                        fontWeight: language === lang.code ? 700 : 500,
                        border: 'none',
                        borderRadius: 'var(--radius-sm)',
                        cursor: 'pointer'
                      }}
                    >
                      {lang.label}
                    </button>
                  ))}
                </div>
              )}
            </div>

            {/* Dark / Light Mode Toggle */}
            <button
              className="theme-toggle-btn"
              onClick={() => setTheme(theme === 'light' ? 'dark' : 'light')}
              title={`Switch to ${theme === 'light' ? 'Dark' : 'Light'} Mode`}
              aria-label="Toggle theme"
            >
              {theme === 'light' ? <Moon size={18} /> : <Sun size={18} />}
            </button>

            {/* User Account / Profile Menu (Desktop) */}
            {user && (
              <div className="header-user-wrapper" style={{ position: 'relative' }} ref={userDropdownRef}>
                <button
                  className="user-profile-btn"
                  onClick={() => setUserDropdownOpen(!userDropdownOpen)}
                  title={`Account: ${user.fullName || user.email}`}
                  aria-expanded={userDropdownOpen}
                >
                  <div className="user-avatar-circle">
                    {getInitials(user.fullName)}
                  </div>
                  <span className="user-name-label">{user.fullName ? user.fullName.split(' ')[0] : 'Account'}</span>
                </button>

                {userDropdownOpen && (
                  <div
                    className="user-dropdown-menu"
                    style={{
                      position: 'absolute',
                      top: 'calc(100% + 6px)',
                      right: 0,
                      background: 'var(--bg-surface)',
                      border: '1px solid var(--border-light)',
                      borderRadius: 'var(--radius-md)',
                      boxShadow: 'var(--shadow-lg)',
                      padding: '0.75rem',
                      zIndex: 150,
                      minWidth: '220px'
                    }}
                  >
                    <div style={{ marginBottom: '0.75rem', paddingBottom: '0.6rem', borderBottom: '1px solid var(--border-light)' }}>
                      <div style={{ fontWeight: 700, fontSize: '0.9rem', color: 'var(--text-main)' }}>
                        {user.fullName || 'Authorized User'}
                      </div>
                      <div style={{ fontSize: '0.78rem', color: 'var(--text-subtle)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {user.email}
                      </div>
                    </div>

                    <button
                      onClick={() => {
                        setUserDropdownOpen(false);
                        logout();
                      }}
                      className="user-logout-item"
                    >
                      <LogOut size={15} color="#ef4444" />
                      <span>Sign Out</span>
                    </button>
                  </div>
                )}
              </div>
            )}

            {/* Mobile Menu Toggle */}
            <button
              className="mobile-menu-btn"
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              aria-label="Toggle navigation menu"
            >
              {mobileMenuOpen ? <X size={20} /> : <Menu size={20} />}
            </button>
          </div>
        </div>

        {/* Mobile Dropdown Menu */}
        {mobileMenuOpen && (
          <div className="mobile-nav-dropdown">
            {/* User Details on Mobile */}
            {user && (
              <div className="mobile-user-card">
                <div className="user-avatar-circle" style={{ width: '36px', height: '36px', fontSize: '0.85rem' }}>
                  {getInitials(user.fullName)}
                </div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: '0.88rem', fontWeight: 700, color: 'var(--text-main)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                    {user.fullName || 'User'}
                  </div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-subtle)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                    {user.email}
                  </div>
                </div>
              </div>
            )}

            <div className="mobile-nav-links">
              {navItems.map((item) => {
                const Icon = item.icon;
                const isActive = activeTab === item.id;
                return (
                  <button
                    key={item.id}
                    className={`mobile-nav-item ${isActive ? 'active' : ''}`}
                    onClick={() => handleNavClick(item.id)}
                  >
                    <Icon size={18} />
                    <span>{item.label}</span>
                    {item.isAi && <span className="nav-badge-pill" style={{ marginLeft: 'auto' }}>AI</span>}
                  </button>
                );
              })}
            </div>

            {/* Language Selection row on mobile */}
            <div className="mobile-menu-lang-row">
              <span style={{ fontSize: '0.78rem', fontWeight: 600, color: 'var(--text-subtle)', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                <Languages size={14} color="var(--primary-600)" />
                <span>Language:</span>
              </span>
              <div style={{ display: 'flex', gap: '0.4rem' }}>
                {languagesList.map((lang) => (
                  <button
                    key={lang.code}
                    onClick={() => {
                      setLanguage(lang.code);
                    }}
                    style={{
                      padding: '0.3rem 0.6rem',
                      fontSize: '0.78rem',
                      borderRadius: 'var(--radius-sm)',
                      border: language === lang.code ? '1px solid var(--primary-600)' : '1px solid var(--border-light)',
                      background: language === lang.code ? 'var(--primary-50)' : 'var(--bg-subtle)',
                      color: language === lang.code ? 'var(--primary-700)' : 'var(--text-main)',
                      fontWeight: language === lang.code ? 700 : 500,
                      cursor: 'pointer'
                    }}
                  >
                    {lang.code.toUpperCase()}
                  </button>
                ))}
              </div>
            </div>

            {/* Sign Out on Mobile */}
            {user && (
              <div style={{ marginTop: '0.75rem', paddingTop: '0.75rem', borderTop: '1px solid var(--border-light)' }}>
                <button
                  onClick={() => {
                    setMobileMenuOpen(false);
                    logout();
                  }}
                  className="mobile-logout-btn"
                >
                  <LogOut size={16} color="#ef4444" />
                  <span>Sign Out</span>
                </button>
              </div>
            )}
          </div>
        )}
      </header>
    </>
  );
}
