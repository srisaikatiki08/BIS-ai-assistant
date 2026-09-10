// Detailed BIS Services & Schemes Data
export const BIS_SERVICES = [
  {
    id: "product-certification",
    title: "Product Certification (ISI Mark Scheme-I)",
    subtitle: "Conformity mark for domestic and industrial products",
    badge: "ISI Mark",
    category: "Industry & MSME",
    icon: "ShieldCheck",
    summary: "The primary scheme enabling manufacturers to certify their products against applicable Indian Standards and use the prestigious ISI mark.",
    targetAudience: "Domestic manufacturers, MSMEs, Large industries, Exporters",
    benefits: [
      "Mandatory for products under Quality Control Orders (QCOs)",
      "Unlocks government e-Marketplace (GeM) public procurement preferences",
      "Instills unshakeable consumer trust and brand credibility",
      "Prevents product seizure, legal penalties, and customs blockages"
    ],
    requiredDocuments: [
      "Factory Registration / Incorporation / Udyam Certificate",
      "Process Flowchart with detailed manufacturing steps",
      "List of In-house Testing Equipment with calibration certificates",
      "Competent Quality Control Personnel qualification records",
      "Plant Layout and Raw Material test certificates"
    ],
    processSteps: [
      { step: 1, title: "Online Application", desc: "Submit Form-I on Manakonline with product details & factory setup." },
      { step: 2, title: "Factory Inspection", desc: "BIS inspecting officer visits to audit production and witness sample testing." },
      { step: 3, title: "Independent Lab Testing", desc: "Counter samples sent to BIS/NABL recognized lab for full type verification." },
      { step: 4, title: "Grant of CM/L Licence", desc: "Licence granted with unique CM/L number to print ISI mark." }
    ],
    fees: "Application fee ₹1,000 + Audit fee ₹7,000/man-day + Annual minimum marking fee (MSMEs get 20% rebate).",
    portalLink: "https://manakonline.in"
  },
  {
    id: "compulsory-registration",
    title: "Compulsory Registration Scheme (CRS)",
    subtitle: "Fast-track self-declaration scheme for Electronics & IT goods",
    badge: "CRS Scheme-II",
    category: "Electronics & Tech",
    icon: "Cpu",
    summary: "Fast-track registration for 76+ IT and electronic items based on self-declaration and testing in BIS-recognized laboratories.",
    targetAudience: "Smartphone OEMs, IT Hardware makers, Battery & EV firms, Solar manufacturers",
    benefits: [
      "No mandatory pre-registration factory audit required",
      "Accelerated timeline (15 to 20 working days)",
      "Essential for clearance through Indian Customs (ICEGATE)",
      "Supports series/family model bundling"
    ],
    requiredDocuments: [
      "Test Report from BIS-recognized testing laboratory (issued within 90 days)",
      "Brand Owner Authorization (Form 1/2) & Trademark Registration",
      "Undertaking for Compliance & Technical Construction File (TCF)",
      "Authorized Indian Representative (AIR) appointment for foreign OEMs"
    ],
    processSteps: [
      { step: 1, title: "Sample Testing", desc: "Send product models to BIS-recognized lab for IS safety standard testing." },
      { step: 2, title: "Portal Profile", desc: "Create OEM and AIR profile on BIS CRS Manakonline portal." },
      { step: 3, title: "Upload Test Report", desc: "Submit test report along with brand declaration and Undertaking." },
      { step: 4, title: "Grant of R-Number", desc: "BIS issues 8-digit Registration Number (e.g., R-XXXXXXXX) for labelling." }
    ],
    fees: "Application fee ₹53,000 for primary model + ₹20,000 per series addition (valid 2 years).",
    portalLink: "https://www.crsbis.in"
  },
  {
    id: "hallmarking-services",
    title: "Hallmarking of Gold & Silver Jewellery",
    subtitle: "Third-party purity certification with 6-digit HUID",
    badge: "Gold Purity",
    category: "Jewellers & Consumers",
    icon: "Sparkles",
    summary: "Mandatory hallmarking scheme ensuring consumers receive exact karat purity with laser-engraved 6-digit Hallmark Unique Identification (HUID).",
    targetAudience: "Jewellery Retailers, Goldsmiths, Bullion Refiners, Consumers",
    benefits: [
      "Zero registration fee for jewellers (Lifetime registration)",
      "100% traceability and anti-counterfeit protection via 6-digit HUID verification",
      "Fair valuation and easy collateral loans for consumers",
      "Level playing field for traditional and corporate jewellers"
    ],
    requiredDocuments: [
      "GST Registration Certificate of Jewellery Retailer",
      "PAN Card & Aadhaar of Proprietor/Directors",
      "Proof of Shop/Showroom premises",
      "Turnover declaration for MSME category"
    ],
    processSteps: [
      { step: 1, title: "Instant Registration", desc: "Register on Manakonline with GST; automatic certificate generation." },
      { step: 2, title: "Send to AHC", desc: "Submit jewellery batch to BIS Recognized Assaying & Hallmarking Centre." },
      { step: 3, title: "Assaying & Fire Testing", desc: "AHC samples gold purity via Fire Assay / XRF testing." },
      { step: 4, title: "Laser Marking HUID", desc: "AHC laser-engraves BIS mark, purity grade (e.g., 22K916) and 6-digit HUID." }
    ],
    fees: "Jeweller registration: FREE. Hallmarking charge: ₹45 per gold article, ₹35 per silver article.",
    portalLink: "https://manakonline.in"
  },
  {
    id: "foreign-manufacturers",
    title: "Foreign Manufacturers Certification Scheme (FMCS)",
    subtitle: "Enabling global manufacturers to use ISI Mark for India exports",
    badge: "FMCS Global",
    category: "Overseas Exporters",
    icon: "Globe",
    summary: "Dedicated scheme allowing foreign manufacturers located outside India to obtain BIS licences and apply the ISI Mark on goods shipped to India.",
    targetAudience: "Global manufacturers, multinational exporters, trading houses",
    benefits: [
      "Seamless clearance at Indian sea ports and air cargo terminals",
      "Full compliance with mandatory Indian Quality Control Orders",
      "Long-term market access to 1.4 billion consumer base in India"
    ],
    requiredDocuments: [
      "Manufacturing License / Business Registration from country of origin",
      "Appointment of Authorized Indian Representative (AIR) based in India",
      "Plant machinery list, quality plan, testing apparatus calibration",
      "Performance Bank Guarantee (PBG) as per BIS guidelines"
    ],
    processSteps: [
      { step: 1, title: "Application & AIR", desc: "Submit Form-VI on FMCS portal with nomination of resident Indian representative." },
      { step: 2, title: "Factory Audit Overseas", desc: "BIS technical officer travels to overseas plant for physical quality inspection." },
      { step: 3, title: "Sample Testing in India", desc: "Samples drawn during audit are shipped and tested in India at BIS labs." },
      { step: 4, title: "Grant of FMCS Licence", desc: "Licence granted upon verification and submission of Performance Bank Guarantee." }
    ],
    fees: "Application fee $1,000 + Inspection travel expenses + Annual marking fees + Performance Bank Guarantee ($10,000).",
    portalLink: "https://manakonline.in/fmcs"
  },
  {
    id: "laboratory-recognition",
    title: "Laboratory Recognition Scheme (LRS)",
    subtitle: "Accreditation and recognition of testing facilities",
    badge: "LRS Lab Network",
    category: "Laboratories & R&D",
    icon: "FlaskConical",
    summary: "Recognizes competent public and private testing laboratories compliant with IS/ISO/IEC 17025 to test product samples for conformity assessment.",
    targetAudience: "NABL accredited testing laboratories, University R&D centers, PSU labs",
    benefits: [
      "Official mandate to test samples for national BIS certification",
      "Substantial testing sample volume routed directly from BIS regional branches",
      "National prestige as an authorized quality infrastructure partner"
    ],
    requiredDocuments: [
      "Valid NABL Accreditation Certificate as per ISO/IEC 17025",
      "Test scope mapping matching specific Indian Standards (IS codes)",
      "Proficiency Testing (PT) and Inter-Laboratory Comparison (ILC) reports",
      "List of qualified testing personnel and equipment calibration logs"
    ],
    processSteps: [
      { step: 1, title: "Online Application", desc: "Submit LRS application specifying Indian Standards test capabilities." },
      { step: 2, title: "Technical Assessment", desc: "Joint technical assessment by BIS auditors and domain technical experts." },
      { step: 3, title: "Proficiency Check", desc: "Successful testing of blind reference samples supplied by BIS." },
      { step: 4, title: "Grant of Recognition", desc: "Recognition letter issued and listed on national BIS lab directory (3 years)." }
    ],
    fees: "Application fee ₹10,000 + Assessment fee ₹12,000/auditor-day + Annual renewal.",
    portalLink: "https://manakonline.in/lrs"
  },
  {
    id: "standards-clubs",
    title: "Standards Clubs in Schools & Colleges",
    subtitle: "Cultivating quality consciousness among the youth of India",
    badge: "Youth & Education",
    category: "Students & Academia",
    icon: "GraduationCap",
    summary: "Grassroots initiative establishing standards clubs in educational institutions to sensitize students to quality, standardisation, and consumer rights.",
    targetAudience: "Schools, Engineering Colleges, Science Institutes, University Students",
    benefits: [
      "Annual financial grant of ₹10,000 to ₹1,00,000 for organizing quality activities",
      "Hands-on exposure through industrial visits to testing laboratories and factories",
      "Competitions, educational workshops, quizzes with exciting awards",
      "Youth empowerment to become ambassadors of quality in their communities"
    ],
    requiredDocuments: [
      "Institutional recognition certificate / Affiliation letter",
      "Nomination of Faculty Mentor / Standards Club Coordinator",
      "List of minimum 15 enrolled student club members"
    ],
    processSteps: [
      { step: 1, title: "College Proposal", desc: "Principal / Dean registers the institution on the BIS Standards Club portal." },
      { step: 2, title: "BIS Branch Approval", desc: "Local BIS Branch Office approves the club and assigns an engagement officer." },
      { step: 3, title: "Grant Disbursement", desc: "Financial grant released directly to institution bank account." },
      { step: 4, title: "Conduct Quality Events", desc: "Conduct standard writing competitions, lab visits, and consumer awareness drives." }
    ],
    fees: "100% Free - BIS provides full sponsorship and financial assistance.",
    portalLink: "https://bis.gov.in/standards-clubs"
  },
  {
    id: "consumer-affairs",
    title: "Consumer Affairs & Grievance Portal",
    subtitle: "Empowering Indian consumers to verify marks and register complaints",
    badge: "Consumer Rights",
    category: "General Public",
    icon: "HeartHandshake",
    summary: "Dedicated ecosystem for consumers to verify ISI licenses, check 6-digit HUID authenticity, and register grievances regarding substandard products.",
    targetAudience: "Citizens, Consumer welfare organizations, Retail buyers",
    benefits: [
      "Instant real-time verification of ISI mark CM/L numbers via portal",
      "Verify exact gold karat, jeweller name and AHC centre for any HUID piece",
      "Direct mechanism to file complaints against misleading quality claims",
      "Time-bound grievance redressal monitored by BIS vigilance cell"
    ],
    requiredDocuments: [
      "Purchase invoice / Bill showing BIS ISI mark or HUID number",
      "Photographs of product labelling and defective feature",
      "Brief description of consumer grievance"
    ],
    processSteps: [
      { step: 1, title: "Access BIS Portal", desc: "Visit official BIS Consumer Grievance Portal on manakonline.in." },
      { step: 2, title: "Verify License / HUID", desc: "Use 'Verify Licence Details' or 'Verify HUID' with zero paperwork." },
      { step: 3, title: "File Grievance", desc: "If substandard, submit complaint under 'Complaints' tab with bill photo." },
      { step: 4, title: "Resolution & Action", desc: "BIS team investigates, collects sample, and initiates legal action against violators." }
    ],
    fees: "100% Free public service under Consumer Protection Act.",
    portalLink: "https://bis.gov.in/consumer-affairs"
  }
];
