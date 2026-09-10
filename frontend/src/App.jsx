import React from 'react';
import { useApp } from './context/AppContext';
import Header from './components/Header';
import Footer from './components/Footer';
import Toast from './components/Toast';
import StandardDetailModal from './components/StandardDetailModal';
import { ShieldCheck, Loader2 } from 'lucide-react';

// Views
import HomeView from './views/HomeView';
import ChatView from './views/ChatView';
import StandardsSearchView from './views/StandardsSearchView';
import ServicesView from './views/ServicesView';
import LabFinderView from './views/LabFinderView';
import UpdatesView from './views/UpdatesView';
import DashboardView from './views/DashboardView';
import DocumentationView from './views/DocumentationView';
import LoginView from './views/LoginView';
import SignupView from './views/SignupView';

export default function App() {
  const { activeTab, isAuthenticated, authLoading, authView } = useApp();

  // 1. Initial Authentication Loading State
  if (authLoading) {
    return (
      <div className="auth-loading-screen">
        <div className="auth-loading-card">
          <div className="auth-logo-badge" style={{ margin: '0 auto 1rem' }}>
            <span className="auth-logo-text">BIS</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.6rem', color: 'var(--primary-700)', fontWeight: 700, fontSize: '1rem', marginBottom: '0.4rem' }}>
            <Loader2 size={20} className="spinner-icon" />
            <span>Verifying Secure Session...</span>
          </div>
          <p style={{ fontSize: '0.82rem', color: 'var(--text-subtle)', textAlign: 'center' }}>
            Bureau of Indian Standards Intelligent Assistant
          </p>
        </div>
      </div>
    );
  }

  // 2. Unauthenticated Gate (Login / Signup)
  if (!isAuthenticated) {
    return (
      <>
        {authView === 'signup' ? <SignupView /> : <LoginView />}
        <Toast />
      </>
    );
  }

  // 3. Authenticated Application Views
  const renderActiveView = () => {
    switch (activeTab) {
      case 'home':
        return <HomeView />;
      case 'chat':
        return <ChatView />;
      case 'standards':
        return <StandardsSearchView />;
      case 'documentation':
        return <DocumentationView />;
      case 'services':
        return <ServicesView />;
      case 'labs':
        return <LabFinderView />;
      case 'updates':
        return <UpdatesView />;
      case 'dashboard':
        return <DashboardView />;
      default:
        return <HomeView />;
    }
  };

  return (
    <div className="app-container">
      <Header />
      <div className="main-content">
        {renderActiveView()}
      </div>
      {activeTab !== 'chat' && <Footer />}
      <StandardDetailModal />
      <Toast />
    </div>
  );
}
