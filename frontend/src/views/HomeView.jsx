import React, { useState } from 'react';
import { 
  Bot, 
  Search, 
  Mic, 
  MicOff, 
  ArrowRight, 
  ShieldCheck, 
  Sparkles, 
  FlaskConical, 
  Award, 
  HeartHandshake, 
  Languages, 
  TrendingUp, 
  BookOpen, 
  AlertTriangle
} from 'lucide-react';
import { useApp } from '../context/AppContext';
import { BIS_UPDATES } from '../data/updatesData';

export default function HomeView() {
  const { setActiveTab, launchChatWithQuery, showToast, t } = useApp();
  const [heroInput, setHeroInput] = useState('');
  const [isListening, setIsListening] = useState(false);

  const handleHeroSubmit = (e) => {
    e?.preventDefault();
    if (!heroInput.trim()) return;
    launchChatWithQuery(heroInput);
  };

  const handleVoiceInput = () => {
    if (!('webkitSpeechRecognition' in window || 'SpeechRecognition' in window)) {
      setIsListening(true);
      showToast("Voice microphone active: Simulated speech...", "info");
      setTimeout(() => {
        setHeroInput("Which standard applies to helmets?");
        setIsListening(false);
        showToast("Voice captured: 'Which standard applies to helmets?'", "success");
      }, 1500);
      return;
    }

    try {
      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
      const recognition = new SpeechRecognition();
      recognition.continuous = false;
      recognition.interimResults = false;
      recognition.lang = 'en-IN';

      recognition.onstart = () => {
        setIsListening(true);
        showToast("Listening... speak your query clearly", "info");
      };

      recognition.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        setHeroInput(transcript);
        setIsListening(false);
      };

      recognition.onerror = () => setIsListening(false);
      recognition.onend = () => setIsListening(false);
      recognition.start();
    } catch (e) {
      console.error(e);
      setIsListening(false);
    }
  };

  const simpleChips = [
    "What BIS standard applies to electrical cables?",
    "How do I get BIS certification?",
    "What products require BIS certification?",
    "Which standard applies to helmets?"
  ];

  const featureCards = [
    {
      id: 'f1',
      title: t.features?.f1_title || "Find Applicable Standards",
      desc: t.features?.f1_desc || "Identify relevant Indian Standards based on your product, material, or technical requirement.",
      icon: Search,
      color: 'var(--primary-600)',
      bg: 'var(--primary-50)',
      tab: 'standards'
    },
    {
      id: 'f2',
      title: t.features?.f2_title || "BIS Certification",
      desc: t.features?.f2_desc || "Understand applicable certification schemes, licensing procedures, and documentation checklists.",
      icon: ShieldCheck,
      color: '#059669',
      bg: '#ecfdf5',
      tab: 'services'
    },
    {
      id: 'f3',
      title: t.features?.f3_title || "Testing Requirements",
      desc: t.features?.f3_desc || "Discover accredited testing parameters, laboratory protocols, and turnaround times.",
      icon: FlaskConical,
      color: '#8b5cf6',
      bg: '#f5f3ff',
      tab: 'labs'
    },
    {
      id: 'f4',
      title: t.features?.f4_title || "Hallmarking & Precious Metals",
      desc: t.features?.f4_desc || "Get guidance on 6-digit HUID hallmarking, purity fineness standards, and jeweller registration.",
      icon: Sparkles,
      color: '#ea580c',
      bg: '#fff7ed',
      tab: 'services'
    },
    {
      id: 'f5',
      title: t.features?.f5_title || "Conformity Assessment",
      desc: t.features?.f5_desc || "Explore Scheme-I (ISI Mark), Scheme-II (CRS), and voluntary eco-mark options.",
      icon: HeartHandshake,
      color: '#0284c7',
      bg: '#f0f9ff',
      tab: 'services'
    },
    {
      id: 'f6',
      title: t.features?.f6_title || "Multilingual Support",
      desc: t.features?.f6_desc || "Interact with the AI assistant seamlessly in English, Hindi (हिन्दी), and Telugu (తెలుగు).",
      icon: Languages,
      color: '#ec4899',
      bg: '#fdf2f8',
      tab: 'chat'
    }
  ];

  return (
    <div>
      {/* Hero Section */}
      <section className="hero-wrapper">
        <div className="max-w-7xl" style={{ textAlign: 'center' }}>
          
          {/* Badge */}
          <div className="hero-badge">
            <Sparkles size={14} color="var(--primary-600)" />
            <span>AI-Powered • BIS Standards & Services</span>
          </div>

          {/* Main Heading */}
          <h1 className="hero-title">
            Your Intelligent Assistant for <span className="hero-title-highlight">Indian Standards</span>
          </h1>

          {/* Supporting Text */}
          <p className="hero-subtitle">
            {t.heroSubtitle || "Find applicable standards, certification requirements, testing information, and BIS services through simple conversations."}
          </p>

          {/* Large AI Chat / Search Box */}
          <div className="hero-search-container">
            <form onSubmit={handleHeroSubmit} className="hero-search-box">
              <Search size={22} color="var(--primary-600)" style={{ flexShrink: 0 }} />
              <input
                type="text"
                className="hero-search-input"
                value={heroInput}
                onChange={(e) => setHeroInput(e.target.value)}
                placeholder="Ask about a product, standard, certification, testing requirement..."
              />
              <div className="hero-search-actions">
                <button
                  type="button"
                  className={`mic-action-btn ${isListening ? 'listening' : ''}`}
                  onClick={handleVoiceInput}
                  title="Voice Search"
                >
                  {isListening ? <MicOff size={18} /> : <Mic size={18} />}
                </button>
                <button type="submit" className="search-submit-btn">
                  <Bot size={18} />
                  <span>Ask Assistant</span>
                </button>
              </div>
            </form>

            {/* Simplified Try Asking Section (Requirement 2) */}
            <div className="query-chips-row">
              <span className="chips-label">Try asking:</span>
              {simpleChips.map((chip, idx) => (
                <button
                  key={idx}
                  className="query-chip"
                  onClick={() => launchChatWithQuery(chip)}
                >
                  <span>{chip}</span>
                </button>
              ))}
            </div>

            {/* Sub-note */}
            <div className="hero-footer-note">
              <ShieldCheck size={14} color="var(--emerald-600)" />
              <span>Answers are supported by authorized BIS sources.</span>
            </div>
          </div>

        </div>
      </section>

      {/* Live Stats Strip */}
      <section className="max-w-7xl">
        <div className="stats-strip">
          <div className="stat-item">
            <div className="stat-icon-wrapper" style={{ background: 'var(--primary-50)', color: 'var(--primary-700)' }}>
              <BookOpen size={24} />
            </div>
            <div>
              <div className="stat-val">21,450+</div>
              <div className="stat-label">Indian Standards Indexed</div>
            </div>
          </div>

          <div className="stat-item">
            <div className="stat-icon-wrapper" style={{ background: '#ecfdf5', color: '#059669' }}>
              <FlaskConical size={24} />
            </div>
            <div>
              <div className="stat-val">1,200+</div>
              <div className="stat-label">Recognized Laboratories</div>
            </div>
          </div>

          <div className="stat-item">
            <div className="stat-icon-wrapper" style={{ background: '#fff7ed', color: '#ea580c' }}>
              <Award size={24} />
            </div>
            <div>
              <div className="stat-val">450+</div>
              <div className="stat-label">Mandatory QCO Products</div>
            </div>
          </div>

          <div className="stat-item">
            <div className="stat-icon-wrapper" style={{ background: '#f5f3ff', color: '#8b5cf6' }}>
              <TrendingUp size={24} />
            </div>
            <div>
              <div className="stat-val">80% Rebate</div>
              <div className="stat-label">For Micro & Startups</div>
            </div>
          </div>
        </div>
      </section>

      {/* Live Ticker Strip */}
      <div className="ticker-strip" style={{ marginTop: '3rem' }}>
        <div className="ticker-badge">
          <AlertTriangle size={13} />
          <span>LATEST GAZETTE</span>
        </div>
        <div className="ticker-content">
          <strong>{BIS_UPDATES[0].title}</strong> — Compliance deadline: {BIS_UPDATES[0].deadline}. {BIS_UPDATES[0].excerpt}
        </div>
        <button
          onClick={() => setActiveTab('updates')}
          style={{ background: 'none', border: 'none', color: 'var(--primary-600)', fontWeight: 700, fontSize: '0.82rem', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '0.3rem', flexShrink: 0 }}
        >
          <span>View All</span>
          <ArrowRight size={13} />
        </button>
      </div>

      {/* Features Section */}
      <section className="features-section" style={{ marginBottom: '4rem' }}>
        <div className="max-w-7xl">
          <div className="section-header-center">
            <span className="section-tag">Empowering Indian Industry & Citizens</span>
            <h2 className="section-title">{t.featuresHeading || "Comprehensive BIS Capabilities"}</h2>
            <p className="section-desc">{t.featuresSubheading || "Engineered to empower Indian manufacturers, MSMEs, startups, laboratory networks, and consumers."}</p>
          </div>

          <div className="features-grid">
            {featureCards.map((feat) => {
              const Icon = feat.icon;
              return (
                <div
                  key={feat.id}
                  className="feature-card"
                  onClick={() => setActiveTab(feat.tab)}
                >
                  <div>
                    <div className="feature-icon-box" style={{ background: feat.bg, color: feat.color }}>
                      <Icon size={26} />
                    </div>
                    <h3 className="feature-card-title">{feat.title}</h3>
                    <p className="feature-card-text">{feat.desc}</p>
                  </div>
                  <div className="feature-card-footer">
                    <span>Explore Capability</span>
                    <ArrowRight size={16} />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </section>

    </div>
  );
}
