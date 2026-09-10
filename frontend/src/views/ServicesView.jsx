import React, { useState } from 'react';
import { Layers, ShieldCheck, Cpu, Sparkles, Globe, FlaskConical, GraduationCap, HeartHandshake, Search } from 'lucide-react';
import { BIS_SERVICES } from '../data/servicesData';
import ServiceCard from '../components/ServiceCard';
import { useApp } from '../context/AppContext';

export default function ServicesView() {
  const { t } = useApp();
  const [filterCategory, setFilterCategory] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');

  const categories = [
    { id: 'ALL', label: 'All BIS Schemes' },
    { id: 'Industry & MSME', label: 'Industry & MSME' },
    { id: 'Electronics & Tech', label: 'Electronics & IT (CRS)' },
    { id: 'Jewellers & Consumers', label: 'Hallmarking (HUID)' },
    { id: 'Overseas Exporters', label: 'Overseas Exporters (FMCS)' },
    { id: 'Laboratories & R&D', label: 'Testing Laboratories (LRS)' },
    { id: 'Students & Academia', label: 'Standards Clubs' }
  ];

  const filteredServices = BIS_SERVICES.filter(service => {
    const matchesCategory = filterCategory === 'ALL' || service.category === filterCategory;
    const matchesSearch = searchTerm === '' ||
      service.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      service.summary.toLowerCase().includes(searchTerm.toLowerCase()) ||
      service.targetAudience.toLowerCase().includes(searchTerm.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  return (
    <div className="page-container">
      <div className="max-w-7xl">
        {/* Header */}
        <div className="page-header">
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', color: 'var(--primary-600)', fontSize: '0.82rem', fontWeight: 800, textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: '0.4rem' }}>
            <Layers size={14} />
            <span>National Conformity Assessment Schemes</span>
          </div>
          <h1 className="page-title">{t.nav.services}</h1>
          <p className="page-subtitle">
            Comprehensive directory of Bureau of Indian Standards conformity schemes, licensing workflows, document checklists, and citizen portals.
          </p>
        </div>

        {/* Search & Category Filter */}
        <div className="search-filter-panel" style={{ marginBottom: '2.5rem' }}>
          <div className="services-filter-grid">
            <div className="filter-input-wrap">
              <Search size={18} className="filter-input-icon" />
              <input
                type="text"
                className="filter-input-field"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Search BIS schemes, requirements, or target beneficiaries..."
              />
            </div>

            <select
              className="filter-select-field"
              value={filterCategory}
              onChange={(e) => setFilterCategory(e.target.value)}
            >
              {categories.map((c) => (
                <option key={c.id} value={c.id}>{c.label}</option>
              ))}
            </select>
          </div>

          <div className="filter-tags-row">
            <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-subtle)' }}>Filter by Beneficiary:</span>
            {categories.map((c) => (
              <button
                key={c.id}
                className={`filter-tag-pill ${filterCategory === c.id ? 'active' : ''}`}
                onClick={() => setFilterCategory(c.id)}
              >
                {c.label}
              </button>
            ))}
          </div>
        </div>

        {/* Services Grid */}
        <div className="services-grid">
          {filteredServices.map((service) => (
            <ServiceCard key={service.id} service={service} />
          ))}
        </div>
      </div>
    </div>
  );
}
