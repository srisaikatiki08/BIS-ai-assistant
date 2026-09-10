import React, { useState } from 'react';
import { FlaskConical, Search, MapPin, Filter, RotateCcw, Building2 } from 'lucide-react';
import { BIS_LABORATORIES, LAB_DISCIPLINES, INDIAN_STATES } from '../data/labsData';
import LabCard from '../components/LabCard';
import { useApp } from '../context/AppContext';

export default function LabFinderView() {
  const { t } = useApp();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedState, setSelectedState] = useState('All States');
  const [selectedDiscipline, setSelectedDiscipline] = useState('All Disciplines');

  const filteredLabs = BIS_LABORATORIES.filter(lab => {
    const s = searchTerm.toLowerCase().trim();
    const matchesSearch = s === '' ||
      lab.name.toLowerCase().includes(s) ||
      lab.city.toLowerCase().includes(s) ||
      (lab.state && lab.state.toLowerCase().includes(s)) ||
      (lab.address && lab.address.toLowerCase().includes(s)) ||
      (lab.majorStandards && lab.majorStandards.some(st => st.toLowerCase().includes(s))) ||
      (lab.disciplines && lab.disciplines.some(d => d.toLowerCase().includes(s)));

    const matchesState = selectedState === 'All States' || lab.state === selectedState;
    const matchesDiscipline = selectedDiscipline === 'All Disciplines' || (lab.disciplines && lab.disciplines.includes(selectedDiscipline));

    return matchesSearch && matchesState && matchesDiscipline;
  });

  const handleReset = () => {
    setSearchTerm('');
    setSelectedState('All States');
    setSelectedDiscipline('All Disciplines');
  };

  return (
    <div className="page-container">
      <div className="max-w-7xl">
        {/* Header */}
        <div className="page-header">
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', color: 'var(--primary-600)', fontSize: '0.82rem', fontWeight: 800, textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: '0.4rem' }}>
            <FlaskConical size={14} />
            <span>Accredited Testing Infrastructure</span>
          </div>
          <h1 className="page-title">{t.findLab || "Find a BIS Recognized Laboratory"}</h1>
          <p className="page-subtitle">
            Locate verified BIS central, regional, and recognized testing laboratories with exact original addresses and 1-click Google Maps Navigation directions.
          </p>
        </div>

        {/* Filter Panel */}
        <div className="search-filter-panel">
          <div className="lab-filter-grid">
            {/* Search Input */}
            <div className="filter-input-wrap">
              <Search size={18} className="filter-input-icon" />
              <input
                type="text"
                className="filter-input-field"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Search by product, standard (IS 4151, IS 10500), city, or lab name..."
              />
            </div>

            {/* State Filter */}
            <select
              className="filter-select-field"
              value={selectedState}
              onChange={(e) => setSelectedState(e.target.value)}
            >
              {INDIAN_STATES.map((st, idx) => (
                <option key={idx} value={st}>{st}</option>
              ))}
            </select>

            {/* Discipline Filter */}
            <select
              className="filter-select-field"
              value={selectedDiscipline}
              onChange={(e) => setSelectedDiscipline(e.target.value)}
            >
              {LAB_DISCIPLINES.map((d, idx) => (
                <option key={idx} value={d}>{d}</option>
              ))}
            </select>
          </div>

          <div className="filter-tags-row">
            <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-subtle)' }}>Common Disciplines:</span>
            {LAB_DISCIPLINES.slice(1, 7).map((d, idx) => (
              <button
                key={idx}
                className={`filter-tag-pill ${selectedDiscipline === d ? 'active' : ''}`}
                onClick={() => setSelectedDiscipline(selectedDiscipline === d ? 'All Disciplines' : d)}
              >
                {d}
              </button>
            ))}

            {(searchTerm || selectedState !== 'All States' || selectedDiscipline !== 'All Disciplines') && (
              <button
                className="btn-subtle btn-sm"
                onClick={handleReset}
                style={{ marginLeft: 'auto', display: 'flex', alignItems: 'center', gap: '0.3rem' }}
              >
                <RotateCcw size={12} />
                <span>Reset Filters</span>
              </button>
            )}
          </div>
        </div>

        {/* Results Counter */}
        <div className="results-counter-bar">
          Showing <strong>{filteredLabs.length}</strong> verified BIS laboratories
        </div>

        {/* Labs Grid */}
        {filteredLabs.length > 0 ? (
          <div className="labs-grid">
            {filteredLabs.map((lab) => (
              <LabCard key={lab.id} lab={lab} />
            ))}
          </div>
        ) : (
          <div style={{ background: 'var(--bg-card)', padding: '3rem 2rem', borderRadius: '12px', border: '1px solid var(--border-light)', textAlign: 'center' }}>
            <Building2 size={40} style={{ color: 'var(--text-subtle)', margin: '0 auto 1rem', opacity: 0.5 }} />
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'var(--text-main)', marginBottom: '0.5rem' }}>No Laboratories Found</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', maxWidth: '400px', margin: '0 auto 1.25rem' }}>
              No verified BIS laboratories match your current filters. Try resetting the filters or searching with different keywords.
            </p>
            <button className="btn btn-primary btn-sm" onClick={handleReset}>
              <RotateCcw size={14} />
              <span>Reset All Filters</span>
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
