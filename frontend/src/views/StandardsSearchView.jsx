import React, { useState, useMemo } from 'react';
import { 
  Search, 
  Filter, 
  Layers, 
  AlertCircle, 
  Sparkles, 
  Grid, 
  List, 
  RotateCcw 
} from 'lucide-react';
import { STANDARDS_DATABASE, STANDARDS_DIVISIONS } from '../data/standardsData';
import StandardCard from '../components/StandardCard';
import { useApp } from '../context/AppContext';

export default function StandardsSearchView() {
  const { searchQuery, setSearchQuery, t } = useApp();
  const [selectedDivision, setSelectedDivision] = useState('ALL');
  const [mandatoryFilter, setMandatoryFilter] = useState('all'); // 'all', 'mandatory', 'voluntary'
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [viewMode, setViewMode] = useState('grid'); // 'grid' or 'table'

  const categoriesList = useMemo(() => {
    const set = new Set(STANDARDS_DATABASE.map(s => s.category));
    return ['ALL', ...Array.from(set)];
  }, []);

  const filteredStandards = useMemo(() => {
    return STANDARDS_DATABASE.filter(item => {
      // Text search
      const matchesSearch = searchQuery === '' || 
        item.isNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.scope.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.category.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (item.applicableProducts && item.applicableProducts.some(p => p.toLowerCase().includes(searchQuery.toLowerCase())));

      // Division filter
      const matchesDivision = selectedDivision === 'ALL' || item.division.includes(selectedDivision);

      // Mandatory filter
      const matchesMandatory = mandatoryFilter === 'all' || 
        (mandatoryFilter === 'mandatory' && item.isMandatory) ||
        (mandatoryFilter === 'voluntary' && !item.isMandatory);

      // Category filter
      const matchesCategory = selectedCategory === 'ALL' || item.category === selectedCategory;

      return matchesSearch && matchesDivision && matchesMandatory && matchesCategory;
    });
  }, [searchQuery, selectedDivision, mandatoryFilter, selectedCategory]);

  const handleResetFilters = () => {
    setSearchQuery('');
    setSelectedDivision('ALL');
    setMandatoryFilter('all');
    setSelectedCategory('ALL');
  };

  return (
    <div className="page-container">
      <div className="max-w-7xl">
        {/* Page Header */}
        <div className="page-header">
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.4rem', color: 'var(--primary-600)', fontSize: '0.82rem', fontWeight: 800, textTransform: 'uppercase', letterSpacing: '0.06em', marginBottom: '0.4rem' }}>
            <Search size={14} />
            <span>National Repository</span>
          </div>
          <h1 className="page-title">{t.searchStandards || "Indian Standards Catalog"}</h1>
          <p className="page-subtitle">
            Explore 21,450+ official Indian Standards (IS), mandatory Quality Control Orders (QCOs), and testing parameters.
          </p>
        </div>

        {/* Search & Filter Panel */}
        <div className="search-filter-panel">
          {/* Top Row Inputs */}
          <div className="filter-row-top">
            <div className="filter-input-wrap">
              <Search size={18} className="filter-input-icon" />
              <input
                type="text"
                className="filter-input-field"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search by IS code (e.g., IS 4151, IS 10500), product name, or keyword..."
              />
            </div>

            {/* Division Council */}
            <select
              className="filter-select-field"
              value={selectedDivision}
              onChange={(e) => setSelectedDivision(e.target.value)}
            >
              {STANDARDS_DIVISIONS.map((div) => (
                <option key={div.id} value={div.code || 'ALL'}>
                  {div.label}
                </option>
              ))}
            </select>

            {/* Regulatory Status */}
            <select
              className="filter-select-field"
              value={mandatoryFilter}
              onChange={(e) => setMandatoryFilter(e.target.value)}
            >
              <option value="all">All Regulatory Statuses</option>
              <option value="mandatory">Mandatory QCO Only</option>
              <option value="voluntary">Voluntary Standards Only</option>
            </select>

            {/* Category Filter */}
            <select
              className="filter-select-field"
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
            >
              {categoriesList.map((cat, idx) => (
                <option key={idx} value={cat}>
                  {cat === 'ALL' ? 'All Product Categories' : cat}
                </option>
              ))}
            </select>
          </div>

          {/* Quick Division Tag Pills */}
          <div className="filter-tags-row">
            <span style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-subtle)' }}>Quick Filter:</span>
            {STANDARDS_DIVISIONS.slice(0, 7).map((d) => (
              <button
                key={d.id}
                className={`filter-tag-pill ${selectedDivision === (d.code || 'ALL') ? 'active' : ''}`}
                onClick={() => setSelectedDivision(d.code || 'ALL')}
              >
                {d.label.split('(')[0]}
              </button>
            ))}

            {(searchQuery || selectedDivision !== 'ALL' || mandatoryFilter !== 'all' || selectedCategory !== 'ALL') && (
              <button
                className="btn-subtle btn-sm"
                onClick={handleResetFilters}
                style={{ marginLeft: 'auto', display: 'flex', alignItems: 'center', gap: '0.3rem' }}
              >
                <RotateCcw size={12} />
                <span>Reset Filters</span>
              </button>
            )}
          </div>
        </div>

        {/* Results Stats & Controls Bar */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '0.75rem' }}>
          <div style={{ fontSize: '0.92rem', color: 'var(--text-muted)' }}>
            Showing <strong>{filteredStandards.length}</strong> matching standards
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-subtle)' }}>View:</span>
            <button
              className={`icon-action-btn ${viewMode === 'grid' ? 'active' : ''}`}
              onClick={() => setViewMode('grid')}
              title="Grid View"
              style={{ background: viewMode === 'grid' ? 'var(--primary-100)' : 'transparent' }}
            >
              <Grid size={16} />
            </button>
            <button
              className={`icon-action-btn ${viewMode === 'table' ? 'active' : ''}`}
              onClick={() => setViewMode('table')}
              title="Table View"
              style={{ background: viewMode === 'table' ? 'var(--primary-100)' : 'transparent' }}
            >
              <List size={16} />
            </button>
          </div>
        </div>

        {/* Results Grid */}
        {filteredStandards.length > 0 ? (
          viewMode === 'grid' ? (
            <div className="standards-grid">
              {filteredStandards.map((std) => (
                <StandardCard key={std.id} standard={std} />
              ))}
            </div>
          ) : (
            <div style={{ background: 'var(--bg-surface)', borderRadius: 'var(--radius-lg)', border: '1px solid var(--border-light)', overflowX: 'auto', boxShadow: 'var(--shadow-sm)' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.88rem' }}>
                <thead>
                  <tr style={{ background: 'var(--bg-subtle)', borderBottom: '1px solid var(--border-light)', textAlign: 'left', color: 'var(--text-subtle)', fontSize: '0.78rem', textTransform: 'uppercase' }}>
                    <th style={{ padding: '1rem' }}>IS Number</th>
                    <th style={{ padding: '1rem' }}>Title & Scope</th>
                    <th style={{ padding: '1rem' }}>Division</th>
                    <th style={{ padding: '1rem' }}>Scheme</th>
                    <th style={{ padding: '1rem' }}>Status</th>
                    <th style={{ padding: '1rem', textAlign: 'right' }}>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredStandards.map((std) => (
                    <tr key={std.id} style={{ borderBottom: '1px solid var(--border-light)', transition: 'background 150ms' }}>
                      <td style={{ padding: '1rem', fontFamily: 'var(--font-mono)', fontWeight: 700, color: 'var(--primary-700)' }}>
                        {std.isNumber}
                      </td>
                      <td style={{ padding: '1rem', maxWidth: '380px' }}>
                        <div style={{ fontWeight: 600, color: 'var(--text-main)', marginBottom: '0.2rem' }}>{std.title}</div>
                        <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>{std.scope.slice(0, 100)}...</div>
                      </td>
                      <td style={{ padding: '1rem', fontSize: '0.82rem', color: 'var(--text-subtle)' }}>
                        {std.division}
                      </td>
                      <td style={{ padding: '1rem', fontSize: '0.82rem', fontWeight: 600, color: 'var(--primary-600)' }}>
                        {std.scheme}
                      </td>
                      <td style={{ padding: '1rem' }}>
                        <span className={`badge-pill ${std.isMandatory ? 'badge-mandatory' : 'badge-voluntary'}`}>
                          {std.isMandatory ? t.mandatoryQco : t.voluntaryStandard}
                        </span>
                      </td>
                      <td style={{ padding: '1rem', textAlign: 'right' }}>
                        <StandardCard standard={std} />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )
        ) : (
          <div style={{ textAlign: 'center', padding: '4rem 1rem', background: 'var(--bg-surface)', borderRadius: 'var(--radius-lg)', border: '1px dashed var(--border-medium)' }}>
            <AlertCircle size={40} color="var(--text-subtle)" style={{ marginBottom: '1rem' }} />
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '0.5rem' }}>No Standards Found</h3>
            <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)', marginBottom: '1.5rem' }}>
              No Indian Standards matched your active search query and filter criteria.
            </p>
            <button className="btn btn-primary" onClick={handleResetFilters}>
              Reset All Filters
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
