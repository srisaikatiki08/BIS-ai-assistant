import React, { useState } from 'react';
import { Bell, Filter, Calendar, AlertTriangle, FileText, CheckCircle2, Search } from 'lucide-react';
import { BIS_UPDATES } from '../data/updatesData';
import UpdateCard from '../components/UpdateCard';
import { useApp } from '../context/AppContext';

export default function UpdatesView() {
  const { t } = useApp();
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');

  const categories = [
    { id: 'ALL', label: 'All Gazette Updates' },
    { id: 'Quality Control Order (QCO)', label: 'Mandatory QCOs' },
    { id: 'Standard Revision', label: 'Revised Standards' },
    { id: 'Fee Concession', label: 'MSME Fee Relief' },
    { id: 'Hallmarking Expansion', label: 'Hallmarking' },
    { id: 'Draft Standard', label: 'Drafts for Public Comment' }
  ];

  const filteredUpdates = BIS_UPDATES.filter(u => {
    const matchesCategory = selectedCategory === 'ALL' || u.category === selectedCategory;
    const matchesSearch = searchQuery === '' ||
      u.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.excerpt.toLowerCase().includes(searchQuery.toLowerCase()) ||
      u.affectedIndustries.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  return (
    <div className="page-container">
      <div className="max-w-7xl">
        {/* Header */}
        <div className="page-header">
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', color: 'var(--primary-600)', fontSize: '0.82rem', fontWeight: 800, textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: '0.4rem' }}>
            <Bell size={14} />
            <span>Official Gazette Notifications</span>
          </div>
          <h1 className="page-title">{t.nav.updates || "BIS Updates & Regulatory Orders"}</h1>
          <p className="page-subtitle">
            Real-time feed of new Indian Standards, mandatory Quality Control Orders (QCOs), deadline extensions, and fee revisions.
          </p>
        </div>

        {/* Filter Bar */}
        <div className="search-filter-panel">
          <div className="updates-filter-grid">
            <div className="filter-input-wrap">
              <Search size={18} className="filter-input-icon" />
              <input
                type="text"
                className="filter-input-field"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search regulatory updates by industry, standard, or keyword..."
              />
            </div>

            <select
              className="filter-select-field"
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
            >
              {categories.map((c) => (
                <option key={c.id} value={c.id}>{c.label}</option>
              ))}
            </select>
          </div>

          <div className="filter-tags-row">
            <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-subtle)' }}>Filter Category:</span>
            {categories.map((c) => (
              <button
                key={c.id}
                className={`filter-tag-pill ${selectedCategory === c.id ? 'active' : ''}`}
                onClick={() => setSelectedCategory(c.id)}
              >
                {c.label}
              </button>
            ))}
          </div>
        </div>

        {/* Updates List Grid */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(380px, 1fr))', gap: '1.5rem' }}>
          {filteredUpdates.map((update) => (
            <UpdateCard key={update.id} update={update} />
          ))}
        </div>
      </div>
    </div>
  );
}
