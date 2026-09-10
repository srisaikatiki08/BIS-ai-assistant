import React, { useState, useRef, useEffect } from 'react';
import {
  FileText,
  Bot,
  Send,
  Loader2,
  CheckCircle2,
  Circle,
  ChevronRight,
  Sparkles,
  Package,
  ClipboardList,
  ArrowRight,
  RefreshCw,
  Download,
  Shield,
  FlaskConical,
  FileCheck,
  Info,
  CheckSquare,
  HelpCircle,
  ExternalLink,
  BookOpen,
  AlertTriangle
} from 'lucide-react';
import { sendChatMessage } from '../services/apiService';
import { useApp } from '../context/AppContext';

const STEPS = {
  IDLE: 'idle',
  ASKING: 'asking',
  LOADING: 'loading',
  RESULT: 'result'
};

function parseChecklistFromMarkdown(text) {
  const sections = [];
  let currentSection = null;
  const lines = text.split('\n');

  for (const line of lines) {
    const trimmed = line.trim();
    if (!trimmed) continue;

    if (trimmed.startsWith('## ') || trimmed.startsWith('### ')) {
      if (currentSection && currentSection.items.length > 0) sections.push(currentSection);
      currentSection = {
        title: trimmed.replace(/^#{2,3}\s+/, '').replace(/\*\*/g, ''),
        icon: detectSectionIcon(trimmed),
        color: detectSectionColor(sections.length),
        items: []
      };
    } else if (trimmed.match(/^[-*]\s+\[[\sx]\]\s+/) || trimmed.match(/^[-*]\s+/) || trimmed.match(/^\d+\.\s+/)) {
      if (!currentSection) {
        currentSection = { title: 'General Steps', icon: 'clipboard', color: 'blue', items: [] };
      }
      const t = trimmed
        .replace(/^[-*]\s+\[[\sx]\]\s+/, '')
        .replace(/^[-*]\s+/, '')
        .replace(/^\d+\.\s+/, '')
        .replace(/\*\*(.*?)\*\*/g, '$1');
      if (t.length > 5) {
        currentSection.items.push({ text: t, done: false, isDoc: isDocumentItem(t) });
      }
    } else if (trimmed.startsWith('**') && trimmed.endsWith('**') && trimmed.length > 4) {
      if (currentSection) {
        const subText = trimmed.replace(/\*\*/g, '');
        if (subText.length > 3) {
          currentSection.items.push({ text: subText, done: false, isSub: true, isDoc: false });
        }
      }
    }
  }
  if (currentSection && currentSection.items.length > 0) sections.push(currentSection);
  return sections;
}

function detectSectionIcon(title) {
  const t = title.toLowerCase();
  if (t.includes('document') || t.includes('doc')) return 'file';
  if (t.includes('test') || t.includes('lab')) return 'flask';
  if (t.includes('certif') || t.includes('licens')) return 'shield';
  if (t.includes('apply') || t.includes('step') || t.includes('process')) return 'arrow';
  if (t.includes('check') || t.includes('verif')) return 'check';
  return 'clipboard';
}

function detectSectionColor(idx) {
  const colors = ['blue', 'saffron', 'emerald', 'purple', 'rose'];
  return colors[idx % colors.length];
}

function isDocumentItem(text) {
  const t = text.toLowerCase();
  return t.includes('form') || t.includes('certificate') || t.includes('document') ||
    t.includes('report') || t.includes('declaration') || t.includes('invoice') ||
    t.includes('letter') || t.includes('application') || t.includes('photo') ||
    t.includes('copy') || t.includes('affidavit') || t.includes('noc') ||
    t.includes('id proof') || t.includes('address proof');
}

const colorMap = {
  blue: { bg: 'rgba(37,99,235,0.08)', border: 'rgba(37,99,235,0.2)', icon: '#2563eb', badge: '#dbeafe', badgeText: '#1e40af' },
  saffron: { bg: 'rgba(249,115,22,0.08)', border: 'rgba(249,115,22,0.2)', icon: '#f97316', badge: '#ffedd5', badgeText: '#c2410c' },
  emerald: { bg: 'rgba(16,185,129,0.08)', border: 'rgba(16,185,129,0.2)', icon: '#10b981', badge: '#d1fae5', badgeText: '#065f46' },
  purple: { bg: 'rgba(139,92,246,0.08)', border: 'rgba(139,92,246,0.2)', icon: '#8b5cf6', badge: '#ede9fe', badgeText: '#5b21b6' },
  rose: { bg: 'rgba(244,63,94,0.08)', border: 'rgba(244,63,94,0.2)', icon: '#f43f5e', badge: '#ffe4e6', badgeText: '#9f1239' }
};

const iconComponents = {
  file: FileCheck,
  flask: FlaskConical,
  shield: Shield,
  arrow: ArrowRight,
  check: CheckSquare,
  clipboard: ClipboardList
};

const SAMPLE_PRODUCTS = [
  'LED Bulb / Luminaire',
  'Lithium-ion Battery Pack',
  'Two-Wheeler Helmet',
  'Mobile Phone / Smartphone',
  'Packaged Drinking Water',
  'Electric Vehicle Charger',
  'Pressure Cooker',
  'Gold Jewellery (Hallmarking)'
];

function fallbackSections(product) {
  return [
    {
      title: 'Step 1: Identify Applicable Indian Standards',
      icon: 'clipboard',
      color: 'blue',
      items: [
        { text: `Search for applicable IS standard for "${product}" on BIS e-Sale portal (esalabis.in)`, done: false, isDoc: false },
        { text: 'Check if product falls under mandatory QCO (Quality Control Order)', done: false, isDoc: false },
        { text: 'Check if product falls under MeitY CRS (Compulsory Registration Scheme)', done: false, isDoc: false },
        { text: 'Identify relevant BIS Technical Division and Sectional Committee', done: false, isDoc: false },
        { text: 'Determine Scheme: Scheme-I (ISI Mark) or Scheme-II (CRS)', done: false, isDoc: false }
      ]
    },
    {
      title: 'Step 2: Documents Required',
      icon: 'file',
      color: 'saffron',
      items: [
        { text: 'Completed BIS application form (available on Manakonline portal)', done: false, isDoc: true },
        { text: 'Valid identity proof of applicant / authorized signatory (Aadhaar/PAN)', done: false, isDoc: true },
        { text: 'Business registration certificate (GST, MSME, IEC as applicable)', done: false, isDoc: true },
        { text: 'Product technical specification / data sheet', done: false, isDoc: true },
        { text: 'Test report from BIS-recognized laboratory (not older than 1 year)', done: false, isDoc: true },
        { text: 'Manufacturing process flow chart', done: false, isDoc: true },
        { text: 'Quality management plan / in-house QC documentation', done: false, isDoc: true },
        { text: 'Factory address proof and ownership documents', done: false, isDoc: true }
      ]
    },
    {
      title: 'Step 3: Testing Requirements',
      icon: 'flask',
      color: 'emerald',
      items: [
        { text: 'Locate a BIS-recognized laboratory via BIS Lab Locator portal', done: false, isDoc: false },
        { text: 'Prepare samples as per IS specification (usually 3–5 samples)', done: false, isDoc: false },
        { text: 'Get product tested for all mandatory parameters in applicable IS', done: false, isDoc: false },
        { text: 'Ensure test report covers safety, performance, and labeling parameters', done: false, isDoc: false },
        { text: 'Collect test report with NABL-accredited laboratory stamp', done: false, isDoc: true }
      ]
    },
    {
      title: 'Step 4: Application & Filing Process',
      icon: 'shield',
      color: 'purple',
      items: [
        { text: 'Register on Manakonline portal (manakonline.in)', done: false, isDoc: false },
        { text: 'Submit online application with all scanned documents', done: false, isDoc: false },
        { text: 'Pay prescribed certification fee (20–80% reduced for MSME units)', done: false, isDoc: false },
        { text: 'Schedule factory inspection visit by BIS officer', done: false, isDoc: false },
        { text: 'Rectify any non-conformances identified during factory audit', done: false, isDoc: false },
        { text: 'Obtain License number / CRS R-number upon successful clearance', done: false, isDoc: false }
      ]
    },
    {
      title: 'Step 5: Post-Certification Compliance',
      icon: 'check',
      color: 'rose',
      items: [
        { text: 'Apply ISI mark / CRS label on every certified product unit', done: false, isDoc: false },
        { text: 'Maintain batch records and test registers for surveillance audit', done: false, isDoc: false },
        { text: 'Undergo periodic BIS surveillance inspections (usually annual)', done: false, isDoc: false },
        { text: 'Apply for license renewal before expiry (typically 1–3 years)', done: false, isDoc: false },
        { text: 'Report any change in product design/manufacturing to BIS immediately', done: false, isDoc: false }
      ]
    }
  ];
}

function getDocumentGuide(text, productName) {
  const t = text.toLowerCase();
  
  if (t.includes('test report') || t.includes('lab') || t.includes('laboratory') || t.includes('sample test')) {
    return {
      title: "Laboratory Test Report (BIS / NABL Accredited)",
      authority: "BIS Recognized / NABL Accredited Testing Laboratory",
      portal: "https://www.manakonline.in/MANAK/labLocator",
      portalLabel: "BIS Lab Locator Portal",
      timeline: "7 to 15 working days",
      cost: "Test parameter dependent (20% to 80% rebate for MSMEs)",
      steps: [
        "Locate a recognized testing laboratory for your IS standard using the BIS Lab Locator portal.",
        "Prepare 3 to 5 random production sample units from your manufacturing facility with batch markings.",
        "Submit testing request form along with manufacturer specifications and product drawings.",
        "Obtain the test report generated with formal NABL stamp and BIS-compliant clause coverage."
      ],
      tip: "Ensure test report is not older than 90 days for new application filings and strictly covers all mandatory clauses."
    };
  }
  
  if (t.includes('gst') || t.includes('tax') || t.includes('business registration') || t.includes('company registration')) {
    return {
      title: "GST Certificate / Business Registration",
      authority: "Goods & Services Tax Network (GSTN) / Ministry of Corporate Affairs",
      portal: "https://www.gst.gov.in",
      portalLabel: "Official GST Portal",
      timeline: "3 to 7 working days",
      cost: "Free (Zero Government Fee)",
      steps: [
        "Visit gst.gov.in and click on 'Services' > 'Registration' > 'New Registration'.",
        "Enter Business PAN, Authorized Signatory Mobile Number, and Email ID for TRN generation.",
        "Upload factory premises proof (Electricity Bill / Registered Lease Deed) and Bank Account proof.",
        "Complete Aadhaar biometric verification to receive GSTIN and download GST Form REG-06 Certificate."
      ],
      tip: "The manufacturing premise address listed on GST registration must strictly match the address provided on your BIS application."
    };
  }

  if (t.includes('msme') || t.includes('udyam') || t.includes('startup') || t.includes('dpiit')) {
    return {
      title: "MSME Udyam Registration Certificate",
      authority: "Ministry of Micro, Small and Medium Enterprises (MoMSME)",
      portal: "https://udyamregistration.gov.in",
      portalLabel: "Udyam Registration Portal",
      timeline: "Instant (1 to 2 working days)",
      cost: "Free (Zero Government Fee)",
      steps: [
        "Visit udyamregistration.gov.in with your Aadhaar Number and Business PAN.",
        "Enter enterprise name, manufacturing activity, plant & machinery investment, and turnover figures.",
        "Verify via OTP and instantly generate your official digital Udyam Registration Certificate."
      ],
      tip: "Holding a valid Udyam certificate gives you a 20% to 80% fee concession on BIS certification and test fees!"
    };
  }

  if (t.includes('factory') || t.includes('premises') || t.includes('lease') || t.includes('address proof') || t.includes('ownership')) {
    return {
      title: "Factory License / Manufacturing Premises Proof",
      authority: "State Directorate of Industrial Safety & Health (DISH) / Local Municipality",
      portal: "https://services.india.gov.in",
      portalLabel: "State Single Window Portal",
      timeline: "5 to 14 working days",
      cost: "Nominal municipal / state fee",
      steps: [
        "Obtain Registered Lease Deed (valid for at least 1–3 years) or Property Ownership / Sale Deed.",
        "Collect latest factory commercial electricity bill and sanctioned load sanction letter.",
        "Submit factory layout plan and machinery layout drawing to local DISH / Municipal Corporation.",
        "Obtain Trade License / Factory Inspectorate NOC for industrial manufacturing operations."
      ],
      tip: "BIS inspecting officers will physically inspect power sanction capacity and machinery layout during audit."
    };
  }

  if (t.includes('identity') || t.includes('id proof') || t.includes('pan') || t.includes('aadhaar') || t.includes('authorized signatory') || t.includes('resolution')) {
    return {
      title: "Identity & Authorization Proof (Board Resolution / POA)",
      authority: "Enterprise Internal / Notary Public",
      portal: "https://manakonline.in",
      portalLabel: "Manakonline Authorization Guide",
      timeline: "1 day",
      cost: "₹100–₹200 for Notary Stamp Paper",
      steps: [
        "For Companies/LLPs: Issue a Board Resolution on letterhead authorizing the director/manager to sign BIS filings.",
        "For Proprietorships/Partnerships: Execute a Power of Attorney (POA) on non-judicial stamp paper.",
        "Collect self-attested copies of Aadhaar Card and PAN Card of the designated authorized signatory."
      ],
      tip: "The designated signatory will receive mobile OTPs during verification and license signing."
    };
  }

  if (t.includes('process') || t.includes('flow chart') || t.includes('manufacturing') || t.includes('quality') || t.includes('qc') || t.includes('manual')) {
    return {
      title: "Manufacturing Process Flowchart & Quality Control (QC) Plan",
      authority: "Internal Production & Quality Assurance Team",
      portal: "https://manakonline.in",
      portalLabel: "BIS Scheme-I Scheme Guide",
      timeline: "1 to 3 days",
      cost: "Internal preparation",
      steps: [
        "Draft a flowchart: Raw Material Inward -> Processing -> In-process Quality Inspection -> Assembly -> Final Quality Testing -> Packaging.",
        "List all in-house testing equipment with serial numbers, capacity, and valid calibration certificates.",
        "Create standard daily quality register formats as mandated in the BIS Scheme of Inspection & Testing (SIT)."
      ],
      tip: "You can click 'Ask BIS AI to Help Draft' below to generate a tailored Quality Control Plan template!"
    };
  }

  if (t.includes('calibration') || t.includes('equipment') || t.includes('instrument')) {
    return {
      title: "In-House Equipment Calibration Certificates",
      authority: "NABL Accredited Calibration Laboratory",
      portal: "https://nabl-india.org",
      portalLabel: "NABL Calibration Directory",
      timeline: "3 to 7 working days",
      cost: "₹500 to ₹2,500 per test instrument",
      steps: [
        "Identify all gauges and test equipment (High Voltage Tester, Vernier, Micrometer, Thermometer, Weighing Scale).",
        "Engage a NABL-accredited calibration laboratory for on-site or laboratory calibration.",
        "Ensure the Calibration Certificate has National Physical Laboratory (NPL) traceability and validity (1 year)."
      ],
      tip: "Calibration certificates must be active and not expired at the time of the BIS officer's factory inspection visit."
    };
  }

  if (t.includes('trademark') || t.includes('brand') || t.includes('logo')) {
    return {
      title: "Trademark Certificate / Brand Authorization Letter",
      authority: "Controller General of Patents, Designs and Trade Marks (CGPDTM)",
      portal: "https://ipindiaonline.gov.in",
      portalLabel: "IP India Portal",
      timeline: "TM-A Application (1 day) / Certificate (6–12 months)",
      cost: "₹4,500 for Startups / MSMEs",
      steps: [
        "File Trademark Application (Form TM-A) on ipindiaonline.gov.in under the appropriate product class.",
        "Obtain TM Application Acknowledgement with serial number.",
        "If manufacturing under brand ownership of a client, execute a formal Brand Authorization Agreement on letterhead."
      ],
      tip: "BIS accepts the TM-A application receipt if the final trademark registration certificate is pending."
    };
  }

  // General fallback guide
  return {
    title: `Compliance Document: ${text.replace(/^[-*]\s+/, '').slice(0, 60)}`,
    authority: "Concerned Government / Technical Authority",
    portal: "https://www.manakonline.in",
    portalLabel: "Manakonline Portal",
    timeline: "2 to 7 working days",
    cost: "Varies by issuing authority",
    steps: [
      "Check the specific clause requirement in the applicable Indian Standard (IS).",
      "Draft the formal certificate or declaration on enterprise letterhead signed by the authorized signatory.",
      "Get necessary third-party attestations (Notary / CA / Chartered Engineer) if mandated.",
      "Scan as clear PDF document (under 2MB) for digital upload on the Manakonline portal."
    ],
    tip: "You can click 'Ask BIS AI to Help Draft' below for tailored drafting guidelines."
  };
}

export default function DocumentationView() {
  const { launchChatWithQuery, showToast } = useApp();
  const [step, setStep] = useState(STEPS.IDLE);
  const [productInput, setProductInput] = useState('');
  const [productName, setProductName] = useState('');
  const [sections, setSections] = useState([]);
  const [checkedItems, setCheckedItems] = useState({});
  const [missingDocKeys, setMissingDocKeys] = useState({});
  const inputRef = useRef(null);

  useEffect(() => {
    if (step === STEPS.ASKING && inputRef.current) {
      setTimeout(() => inputRef.current?.focus(), 100);
    }
  }, [step]);

  const handleSubmitProduct = async (e) => {
    e?.preventDefault();
    const product = productInput.trim();
    if (!product) return;

    setProductName(product);
    setStep(STEPS.LOADING);
    setCheckedItems({});
    setSections([]);

    const prompt = `You are a BIS certification documentation expert for India. The user wants to certify their product under Indian Standards (BIS/QCO/CRS).

Product: "${product}"

Provide a COMPLETE step-by-step documentation and compliance checklist. Use this EXACT markdown structure:

## Step 1: Identify Applicable Indian Standards
- List specific IS numbers, titles, and year
- State if mandatory QCO or CRS applies
- Mention the BIS Technical Division

## Step 2: Documents Required
- Application form (mention form name/number if known)
- Identity and address proof (be specific)
- Business registration certificates
- Technical specification / datasheet
- Laboratory test report (specify which lab type)
- Any declarations, affidavits, or factory documents

## Step 3: Testing & Laboratory Requirements
- Which BIS-recognized lab type to use
- Specific parameters to test with IS clause references
- Number of samples required
- Estimated testing time

## Step 4: Application & Filing Process
- Portal URL and registration steps
- Document upload process
- Fee structure (mention MSME concession)
- Factory inspection steps
- Timeline from application to license

## Step 5: Post-Certification Compliance
- Marking/labeling rules
- Surveillance audit frequency
- License renewal process
- Record-keeping requirements

Make all items specific, actionable, and accurate for 2026 Indian regulatory requirements. Use "- " prefix for all checklist items.`;

    try {
      const result = await sendChatMessage(prompt, 'en', []);
      if (result.success && result.answer) {
        const parsed = parseChecklistFromMarkdown(result.answer);
        setSections(parsed.length > 0 ? parsed : fallbackSections(product));
      } else {
        setSections(fallbackSections(product));
      }
    } catch (err) {
      setSections(fallbackSections(product));
    }
    setStep(STEPS.RESULT);
  };

  const toggleItem = (sIdx, iIdx) => {
    const key = `${sIdx}-${iIdx}`;
    setCheckedItems(prev => ({ ...prev, [key]: !prev[key] }));
  };

  const totalItems = sections.reduce((acc, s) => acc + s.items.filter(i => !i.isSub).length, 0);
  const checkedCount = Object.values(checkedItems).filter(Boolean).length;
  const progress = totalItems > 0 ? Math.round((checkedCount / totalItems) * 100) : 0;

  const handleReset = () => {
    setStep(STEPS.IDLE);
    setProductName('');
    setProductInput('');
    setSections([]);
    setCheckedItems({});
  };

  const handleDownload = () => {
    const text = `BIS Documentation Checklist: ${productName}\nGenerated by BIS Intelligent Assistant\n\n` +
      sections.map(s =>
        `## ${s.title}\n` + s.items.map(i => `${i.isSub ? '  ' : '- [ ] '}${i.text}`).join('\n')
      ).join('\n\n');
    const blob = new Blob([text], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `BIS_Checklist_${productName.replace(/\s+/g, '_')}.txt`;
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div className="doc-view-wrapper">
      <div className="doc-bg-mesh" />
      <div className="doc-view-inner max-w-5xl">

        {/* ─── IDLE ─── */}
        {step === STEPS.IDLE && (
          <div className="doc-landing">
            <div className="doc-landing-icon-wrap">
              <div className="doc-landing-icon">
                <FileText size={40} />
              </div>
              <div className="doc-landing-glow" />
            </div>
            <h1 className="doc-landing-title">
              Documentation <span className="doc-title-accent">Checklist</span> Generator
            </h1>
            <p className="doc-landing-sub">
              AI-powered step-by-step roadmap for BIS product certification. Get the complete
              list of documents, tests, and processes for <strong>any product</strong>.
            </p>

            <div className="doc-sample-section">
              <p className="doc-chips-label">Try with a popular product:</p>
              <div className="doc-chips-row">
                {SAMPLE_PRODUCTS.map(p => (
                  <button key={p} className="doc-chip" onClick={() => { setProductInput(p); setStep(STEPS.ASKING); }}>
                    {p}
                  </button>
                ))}
              </div>
            </div>

            <button className="doc-start-btn" onClick={() => setStep(STEPS.ASKING)}>
              <Bot size={20} />
              <span>Generate My Checklist</span>
              <ChevronRight size={18} />
            </button>

            <div className="doc-features-row">
              <div className="doc-feature-pill"><CheckCircle2 size={13} /><span>AI-Powered</span></div>
              <div className="doc-feature-pill"><Shield size={13} /><span>BIS / QCO / CRS</span></div>
              <div className="doc-feature-pill"><FileText size={13} /><span>Complete Doc List</span></div>
              <div className="doc-feature-pill"><Download size={13} /><span>Downloadable</span></div>
            </div>
          </div>
        )}

        {/* ─── ASKING ─── */}
        {step === STEPS.ASKING && (
          <div className="doc-ask-outer">
            <div className="doc-ask-glass">
              <div className="doc-ask-header">
                <div className="doc-bot-avatar">
                  <Bot size={26} />
                </div>
                <div>
                  <h2 className="doc-ask-title">What product do you want to certify?</h2>
                  <p className="doc-ask-sub">
                    Be specific for best results — e.g. "LED Bulb 10W", "Lithium-ion Battery Pack", "ISI marked Helmet"
                  </p>
                </div>
              </div>

              <form onSubmit={handleSubmitProduct} className="doc-ask-form">
                <div className="doc-ask-input-wrap">
                  <Package size={20} className="doc-input-icon" />
                  <input
                    ref={inputRef}
                    type="text"
                    className="doc-ask-input"
                    placeholder="Enter product name or category..."
                    value={productInput}
                    onChange={e => setProductInput(e.target.value)}
                  />
                  <button type="submit" className="doc-ask-submit" disabled={!productInput.trim()}>
                    <Send size={17} />
                    <span>Generate</span>
                  </button>
                </div>
              </form>

              <div className="doc-ask-chips">
                {SAMPLE_PRODUCTS.slice(0, 6).map(p => (
                  <button key={p} className="doc-chip-sm" onClick={() => setProductInput(p)}>
                    {p}
                  </button>
                ))}
              </div>

              <button className="doc-back-link" onClick={() => setStep(STEPS.IDLE)}>← Back</button>
            </div>
          </div>
        )}

        {/* ─── LOADING ─── */}
        {step === STEPS.LOADING && (
          <div className="doc-loading-outer">
            <div className="doc-loading-glass">
              <div className="doc-loading-orb">
                <Loader2 size={34} className="doc-spin" />
              </div>
              <h2 className="doc-loading-title">
                Generating checklist for <span className="doc-loading-product">"{productName}"</span>
              </h2>
              <p className="doc-loading-sub">
                Analyzing Indian Standards, BIS certification requirements, and documentation rules…
              </p>
              <div className="doc-loading-steps">
                {[
                  'Identifying applicable IS numbers & QCO orders',
                  'Compiling required documents checklist',
                  'Mapping testing & laboratory requirements',
                  'Structuring step-by-step process flow'
                ].map((s, i) => (
                  <div key={i} className="doc-loading-step" style={{ animationDelay: `${i * 0.35}s` }}>
                    <Sparkles size={13} />
                    <span>{s}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* ─── RESULT ─── */}
        {step === STEPS.RESULT && (
          <div className="doc-result-wrapper">
            {/* Header bar */}
            <div className="doc-result-topbar">
              <div className="doc-result-topbar-left">
                <div className="doc-result-product-badge">
                  <Package size={15} />
                  <span>{productName}</span>
                </div>
                <div>
                  <h2 className="doc-result-title">BIS Certification Checklist</h2>
                  <p className="doc-result-sub">AI-generated documentation roadmap • Indian Standards 2026</p>
                </div>
              </div>

              {/* Progress circle */}
              <div className="doc-progress-block">
                <div className="doc-progress-ring-wrap">
                  <svg className="doc-progress-ring" viewBox="0 0 80 80">
                    <circle cx="40" cy="40" r="32" fill="none" stroke="var(--border-light)" strokeWidth="7" />
                    <circle
                      cx="40" cy="40" r="32"
                      fill="none"
                      stroke={progress === 100 ? '#10b981' : '#2563eb'}
                      strokeWidth="7"
                      strokeLinecap="round"
                      strokeDasharray={`${2 * Math.PI * 32}`}
                      strokeDashoffset={`${2 * Math.PI * 32 * (1 - progress / 100)}`}
                      transform="rotate(-90 40 40)"
                      style={{ transition: 'stroke-dashoffset 0.6s cubic-bezier(0.16,1,0.3,1)' }}
                    />
                  </svg>
                  <div className="doc-progress-inner">
                    <span className="doc-progress-pct">{progress}%</span>
                  </div>
                </div>
                <div className="doc-progress-info">
                  <span className="doc-progress-stat">{checkedCount}/{totalItems}</span>
                  <span className="doc-progress-stat-label">tasks done</span>
                </div>
              </div>
            </div>

            {/* Disclaimer */}
            <div className="doc-disclaimer">
              <Info size={13} />
              <span>AI-generated guidance. Verify requirements at <strong>manakonline.in</strong> before official submission.</span>
            </div>

            {/* Checklist sections */}
            <div className="doc-sections-list">
              {sections.map((section, sIdx) => {
                const colors = colorMap[section.color] || colorMap.blue;
                const IconComp = iconComponents[section.icon] || ClipboardList;
                const sectionTotal = section.items.filter(i => !i.isSub).length;
                const sectionChecked = section.items.filter((item, iIdx) => !item.isSub && checkedItems[`${sIdx}-${iIdx}`]).length;

                return (
                  <div key={sIdx} className="doc-section-card">
                    <div className="doc-section-header" style={{ borderLeft: `3px solid ${colors.icon}` }}>
                      <div className="doc-section-icon-wrap" style={{ background: colors.bg }}>
                        <IconComp size={17} style={{ color: colors.icon }} />
                      </div>
                      <div className="doc-section-title-row">
                        <h3 className="doc-section-title">{section.title}</h3>
                        <span className="doc-section-badge" style={{ background: colors.badge, color: colors.badgeText }}>
                          {sectionChecked}/{sectionTotal}
                        </span>
                      </div>
                    </div>

                    <div className="doc-checklist">
                      {section.items.map((item, iIdx) => {
                        const key = `${sIdx}-${iIdx}`;
                        const isDone = !!checkedItems[key];

                        if (item.isSub) {
                          return (
                            <div key={iIdx} className="doc-sub-header">
                              <ChevronRight size={11} />
                              <span>{item.text}</span>
                            </div>
                          );
                        }

                        const hasMissingGuide = missingDocKeys[key];
                        const guide = getDocumentGuide(item.text, productName);

                        return (
                          <div
                            key={iIdx}
                            className={`doc-item ${isDone ? 'doc-item-done' : ''} ${hasMissingGuide ? 'doc-item-expanded' : ''}`}
                            onClick={() => toggleItem(sIdx, iIdx)}
                          >
                            <div className="doc-item-check">
                              {isDone
                                ? <CheckCircle2 size={19} style={{ color: '#10b981' }} />
                                : <Circle size={19} style={{ color: 'var(--border-medium)' }} />
                              }
                            </div>

                            <div className="doc-item-body">
                              <div className="doc-item-main-row">
                                <span className="doc-item-text">{item.text}</span>
                                <div className="doc-item-badges-wrap">
                                  {item.isDoc && (
                                    <span className="doc-item-doc-badge">
                                      <FileText size={9} />
                                      Document Required
                                    </span>
                                  )}
                                  <button
                                    type="button"
                                    className={`doc-need-help-btn ${hasMissingGuide ? 'active' : ''}`}
                                    onClick={(e) => {
                                      e.stopPropagation();
                                      setMissingDocKeys(prev => ({ ...prev, [key]: !prev[key] }));
                                    }}
                                    title="Click to view step-by-step guide on how to obtain this document"
                                  >
                                    <HelpCircle size={12} />
                                    <span>{hasMissingGuide ? "Hide Guide" : "Don't have this document?"}</span>
                                  </button>
                                </div>
                              </div>

                              {/* Interactive Step-by-Step Acquisition Guide */}
                              {hasMissingGuide && (
                                <div
                                  className="doc-guide-box"
                                  onClick={(e) => e.stopPropagation()}
                                >
                                  <div className="doc-guide-header">
                                    <div className="doc-guide-title">
                                      <BookOpen size={15} color="var(--primary-600)" />
                                      <span>{guide.title}</span>
                                    </div>
                                    <span className="doc-guide-timeline-badge">⏱ {guide.timeline}</span>
                                  </div>

                                  <div className="doc-guide-meta-grid">
                                    <div className="doc-guide-meta-item">
                                      <span className="doc-guide-meta-label">Issuing Authority:</span>
                                      <span className="doc-guide-meta-value">{guide.authority}</span>
                                    </div>
                                    <div className="doc-guide-meta-item">
                                      <span className="doc-guide-meta-label">Estimated Fee / Cost:</span>
                                      <span className="doc-guide-meta-value">{guide.cost}</span>
                                    </div>
                                  </div>

                                  <div className="doc-guide-steps-title">
                                    Step-by-Step Acquisition Procedure:
                                  </div>
                                  <ol className="doc-guide-steps-list">
                                    {guide.steps.map((st, sIndex) => (
                                      <li key={sIndex} className="doc-guide-step-item">
                                        <span className="step-num-bubble">{sIndex + 1}</span>
                                        <span>{st}</span>
                                      </li>
                                    ))}
                                  </ol>

                                  {guide.tip && (
                                    <div className="doc-guide-tip">
                                      <AlertTriangle size={14} color="#f97316" style={{ flexShrink: 0, marginTop: '2px' }} />
                                      <span><strong>Pro-Tip for Fast Approval:</strong> {guide.tip}</span>
                                    </div>
                                  )}

                                  <div className="doc-guide-actions">
                                    {guide.portal && (
                                      <a
                                        href={guide.portal}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                        className="doc-guide-btn-portal"
                                      >
                                        <span>Open {guide.portalLabel || 'Official Portal'}</span>
                                        <ExternalLink size={12} />
                                      </a>
                                    )}
                                    <button
                                      type="button"
                                      className="doc-guide-btn-ai"
                                      onClick={() => launchChatWithQuery(`How do I prepare and obtain ${guide.title} for certifying ${productName} under BIS standards?`)}
                                    >
                                      <Bot size={13} />
                                      <span>Ask BIS AI to Help Draft</span>
                                    </button>
                                  </div>
                                </div>
                              )}
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                );
              })}
            </div>

            {/* Actions */}
            <div className="doc-result-actions">
              <button className="doc-btn-primary" onClick={handleReset}>
                <RefreshCw size={15} />
                <span>New Checklist</span>
              </button>
              <button className="doc-btn-outline" onClick={handleDownload}>
                <Download size={15} />
                <span>Download .txt</span>
              </button>
            </div>
          </div>
        )}

      </div>
    </div>
  );
}
