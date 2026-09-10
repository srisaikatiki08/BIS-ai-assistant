import React from 'react';
import { useApp } from './context/AppContext';
import Header from './components/Header';
import Footer from './components/Footer';
import Toast from './components/Toast';
import StandardDetailModal from './components/StandardDetailModal';

// Views
import HomeView from './views/HomeView';
import ChatView from './views/ChatView';
import StandardsSearchView from './views/StandardsSearchView';
import ServicesView from './views/ServicesView';
import LabFinderView from './views/LabFinderView';
import UpdatesView from './views/UpdatesView';
import DashboardView from './views/DashboardView';
import DocumentationView from './views/DocumentationView';

export default function App() {
  const { activeTab } = useApp();

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
