// Authentic Indian Standards Catalog for BIS Intelligent Assistant
export const STANDARDS_DATABASE = [
  {
    id: "IS-IEC-62368-1-2023",
    isNumber: "IS/IEC 62368-1 : 2023",
    title: "Audio/Video, Information and Communication Technology Equipment — Safety Requirements",
    division: "Electronics & IT (LITD 07 / LITD 16)",
    category: "Electronics & IT Goods",
    year: "2023",
    status: "Active (Mandatory CRS)",
    isMandatory: true,
    scheme: "Scheme-II (Compulsory Registration Scheme - CRS)",
    qcoOrder: "Electronics and Information Technology Goods (Requirement for Compulsory Registration) Order, 2021",
    scope: "Major modern safety standard for audio/video, information and communication technology equipment. Covers computers, laptops, notebooks, tablets, visual display units, video monitors, TVs, set-top boxes, CCTV cameras/recorders, electronic games, smart watches, smart speakers, and Bluetooth speakers.",
    keyClauses: [
      { clause: "Clause 4", title: "General Requirements & Energy Sources", desc: "Classification of energy sources (Class 1, 2, 3) for electrical, thermal, mechanical, and radiation hazards." },
      { clause: "Clause 5", title: "Electrically-Caused Injury & Insulation", desc: "Protection against electric shock, contact voltage limits, creepage and clearance distances." },
      { clause: "Clause 6", title: "Electrically-Caused Fire", desc: "Potential ignition sources, fire containment enclosures, and flammability ratings (V-0, V-1)." },
      { clause: "Clause 9", title: "Thermal Burn & Temperature Limits", desc: "Accessible surface temperature limits under normal and abnormal operating conditions." }
    ],
    testingRequirements: [
      "Electric strength high-voltage breakdown test",
      "Touch current & protective earth continuity test",
      "Energy source classification and fault condition simulation",
      "Thermal heating and enclosure flammability tests",
      "Mechanical drop, impact and stability tests"
    ],
    applicableProducts: [
      "Laptops, Notebooks & Tablets",
      "Television Receivers & Video Monitors",
      "CCTV Cameras & CCTV Recorders",
      "Smart Watches & Fitness Wearables",
      "Smart Speakers & Bluetooth Speakers",
      "Set-top Boxes & Electronic Gaming Consoles"
    ],
    relatedStandards: ["IS 13252 (Historical IT Safety)", "IS 616 (Audio/Video)", "IEC 62368-1:2018 (3rd Ed)"],
    licensingProcess: "Sample testing at BIS-recognized lab, submission on Manakonline CRS portal, grant of R-Number for CRS self-declaration label.",
    feeCategory: "Tiered CRS model family registration fees."
  },
  {
    id: "IS-4151-2015",
    isNumber: "IS 4151 : 2015",
    title: "Protective Helmets for Motorcycle Riders — Specification",
    division: "Transport Engineering (TED 22)",
    category: "Automotive & Safety",
    year: "2015",
    status: "Active (Mandatory QCO)",
    isMandatory: true,
    scheme: "Scheme-I (ISI Mark)",
    qcoOrder: "Two-Wheeler Helmet (Quality Control) Order, 2020",
    scope: "Covers the requirements regarding materials, construction, finish, mass and performance for protective helmets for everyday use by motorcycle riders.",
    keyClauses: [
      { clause: "4.1", title: "Shell Material", desc: "Must be impact resistant, non-degrading under sunlight and moisture." },
      { clause: "4.2", title: "Retention System", desc: "Chin strap with minimum width 20mm and dynamic elongation test limits." },
      { clause: "7.1", title: "Impact Attenuation Test", desc: "Peak acceleration shall not exceed 300g under ambient, hot, cold and wet conditioning." },
      { clause: "7.2", title: "Penetration Resistance", desc: "No electrical contact between conical striker and headform." },
      { clause: "8.1", title: "Marking & Labelling", desc: "Mandatory standard ISI mark with valid CM/L licence number." }
    ],
    testingRequirements: [
      "Impact Attenuation Test at 4 test sites",
      "Dynamic retention test (chin strap elongation & slippage)",
      "Rigidity and shell deflection test",
      "Peripheral vision test (horizontal ≥ 105°, vertical ≥ 30°)",
      "Audibility test and visor optical characteristics"
    ],
    applicableProducts: [
      "Full Face Motorcycle Helmets",
      "Open Face Helmets",
      "Modular / Flip-up Helmets",
      "Off-road Motorcycle Helmets"
    ],
    relatedStandards: ["IS 9873 (Part 1)", "IS 4151:1993", "ECE 22.05 / 22.06"],
    licensingProcess: "Factory inspection, in-house laboratory verification, witness testing of samples, and grant of CM/L licence for ISI Mark.",
    feeCategory: "Large Scale / MSME concession available (20% fee rebate for MSMEs)."
  },
  {
    id: "IS-16046-2018",
    isNumber: "IS 16046 (Part 1 & 2) : 2018",
    title: "Secondary Cells and Batteries Containing Alkaline or Other Non-Acid Electrolytes (Lithium & Nickel Systems)",
    division: "Electronics & IT (LITD 10)",
    category: "Electronics & EV",
    year: "2018",
    status: "Active (Mandatory CRS)",
    isMandatory: true,
    scheme: "Scheme-II (Compulsory Registration Scheme - CRS)",
    qcoOrder: "Electronics and Information Technology Goods (Requirement for Compulsory Registration) Order",
    scope: "Covers safety requirements for portable sealed secondary cells and for batteries made from them, for use in portable and EV applications.",
    keyClauses: [
      { clause: "7.2.1", title: "Continuous Charging Test", desc: "No fire or explosion when charged continuously for 28 days at manufacturer limit." },
      { clause: "7.3.2", title: "External Short-Circuit Test", desc: "Battery tested at 55°C ± 5°C with external resistance < 100mΩ." },
      { clause: "7.3.3", title: "Free Fall Test", desc: "Dropped 3 times from 1.0m height on concrete floor in 3 orientations." },
      { clause: "7.3.6", title: "Crush Test", desc: "Crush force of 13kN ± 0.78kN applied between flat surfaces." },
      { clause: "7.3.8", title: "Thermal Abuse", desc: "Oven raised to 130°C ± 2°C at 5°C/min; cell held for 10 min without explosion/fire." }
    ],
    testingRequirements: [
      "Thermal cycling abuse test (-20°C to +75°C)",
      "Overcharge safety protection evaluation",
      "Forced discharge & polarity reversal test",
      "Vibration and shock endurance",
      "Mechanical crush and impact testing"
    ],
    applicableProducts: [
      "Lithium-ion Cells & Battery Packs",
      "Electric Vehicle (EV) Traction Batteries",
      "Power Banks & Portable Chargers",
      "Laptop and Smartphone Batteries",
      "Energy Storage Systems (ESS)"
    ],
    relatedStandards: ["IEC 62133-2:2017", "AIS 156 (EV Battery Safety)", "AIS 038 Rev 2", "IS 16270"],
    licensingProcess: "Sample testing in BIS-recognized lab, submission on Manakonline portal, grant of R-Number for CRS label.",
    feeCategory: "MeitY CRS scheme registration fee."
  },
  {
    id: "IS-16242-2014",
    isNumber: "IS 16242 (Part 1) : 2014",
    title: "Uninterruptible Power Systems (UPS) — General and Safety Requirements",
    division: "Electronics & IT (LITD 10 / LITD 16)",
    category: "Power Equipment & UPS",
    year: "2014",
    status: "Active (Mandatory CRS)",
    isMandatory: true,
    scheme: "Scheme-II (Compulsory Registration Scheme - CRS)",
    qcoOrder: "Electronics and Information Technology Goods (Requirement for Compulsory Registration) Order",
    scope: "Prescribes safety requirements for electronic Uninterruptible Power Systems (UPS) and Inverters of rating ≤ 10 kVA used in commercial, IT and domestic installations.",
    keyClauses: [
      { clause: "4.3", title: "Electric Shock Protection", desc: "Insulation barrier requirements between mains input, battery circuit and inverter output." },
      { clause: "4.5", title: "Thermal & Fire Protection", desc: "Temperature limits on power transformers, semiconductor heatsinks, and internal battery enclosures." },
      { clause: "5.1", title: "Abnormal Operating Conditions", desc: "Short circuit on output terminals, fan failure, and overload without fire or casing rupture." }
    ],
    testingRequirements: [
      "High voltage insulation withstand test (1.5kV to 3kV)",
      "Output short-circuit and overload protection test",
      "Temperature rise under rated 100% continuous load",
      "Acoustic noise and battery reverse-polarity protection",
      "Touch current and earth continuity resistance"
    ],
    applicableProducts: [
      "Desktop & Server Online UPS Units",
      "Home & Commercial Power Inverters (≤ 10 kVA)",
      "Line-Interactive Backup UPS Systems",
      "Solar Hybrid Inverters with Battery Integration"
    ],
    relatedStandards: ["IEC 62040-1", "IS 13252 (Part 1)", "IS 16046 (Batteries)"],
    licensingProcess: "Testing at BIS/NABL accredited power lab, registration on CRS portal, grant of R-Number.",
    feeCategory: "MeitY CRS standard registration fee."
  },
  {
    id: "IS-18112-2022",
    isNumber: "IS 18112 : 2022",
    title: "Digital Television Receiver for Satellite Broadcast Transmission — Specification",
    division: "Electronics & IT (LITD 07)",
    category: "Audio, Video & Broadcasting",
    year: "2022",
    status: "Active (Mandatory Standards Framework)",
    isMandatory: true,
    scheme: "Scheme-II / Scheme-I Harmonized",
    qcoOrder: "Digital Television Receivers Satellite Tuner Framework",
    scope: "Establishes technical specifications for digital television receivers equipped with built-in satellite broadcast tuners (DVB-S2) for reception of free-to-air satellite television without an external set-top box.",
    keyClauses: [
      { clause: "4.1", title: "RF Tuner & Demodulator", desc: "DVB-S / DVB-S2 satellite demodulation supporting QPSK / 8PSK modulation schemes." },
      { clause: "5.2", title: "Video & Audio Decoding", desc: "HEVC (H.265), MPEG-4 AVC decoding with Dolby/AAC audio compliance." },
      { clause: "6.1", title: "LNB Power & DiSEqC Control", desc: "13V/18V polarization switching and 22kHz tone control for LNB interfacing." }
    ],
    testingRequirements: [
      "RF sensitivity and carrier-to-noise ratio (C/N) threshold test",
      "LNB power supply overload and short-circuit protection",
      "Channel scanning and conditional access evaluation",
      "Safety and EMC compliance per IS/IEC 62368-1 and CISPR 32"
    ],
    applicableProducts: [
      "Smart TVs with Integrated Satellite Tuners",
      "Digital Television Displays (LCD/LED/OLED)",
      "Commercial Hospitality Satellite TVs"
    ],
    relatedStandards: ["IS/IEC 62368-1:2023", "IS 616", "IS 18112 Common Charger"],
    licensingProcess: "Lab test report from recognized RF/AV testing lab and CRS/BIS registration.",
    feeCategory: "Standard electronics CRS registration tier."
  },
  {
    id: "IS-10322-2013",
    isNumber: "IS 10322 (Part 5/Sec 7 & 8) : 2013",
    title: "Luminaires — Particular Requirements: LED Lighting Chains & Emergency Lighting",
    division: "Electrotechnical & Lighting (ETD 23)",
    category: "Lighting & Luminaires",
    year: "2013",
    status: "Active (Mandatory CRS)",
    isMandatory: true,
    scheme: "Scheme-II (Compulsory Registration Scheme - CRS)",
    qcoOrder: "Electronics and IT Goods (Requirement for Compulsory Registration) Order",
    scope: "Covers particular safety requirements for LED lighting chains (decorative/festival chains) and luminaires for emergency lighting used with emergency battery supplies.",
    keyClauses: [
      { clause: "Sec 7 / 4.1", title: "Insulation & Ingress Protection", desc: "Outdoor lighting chains must provide minimum IP44 weatherproofing; creepage and clearance limits." },
      { clause: "Sec 7 / 7.2", title: "Mechanical Strength & Cord Pull", desc: "Chain conductors must withstand 60N axial pull without severance." },
      { clause: "Sec 8 / 5.1", title: "Emergency Battery Duration", desc: "Emergency luminaires must provide rated lumen output for minimum 1 to 3 hours upon mains failure." }
    ],
    testingRequirements: [
      "Cord tensile strength and flexure endurance test",
      "IPX4 / IPX5 ingress water spray test",
      "Dielectric high voltage test (2000V AC)",
      "Emergency lumen output & battery switchover response time (< 0.5s)",
      "Thermal endurance at 45°C ambient"
    ],
    applicableProducts: [
      "LED Festive & Decorative Lighting Chains",
      "LED Emergency Exit Signs & Bulkhead Luminaires",
      "Commercial Emergency Backup Lights",
      "Standalone LED Modules for General Lighting (IS 16103 Part 1)"
    ],
    relatedStandards: ["IS 16103 (Part 1)", "IS 15885-2-13 (LED Controlgear)", "IEC 60598-2-20"],
    licensingProcess: "Testing at BIS-recognized lighting lab and registration on Manakonline CRS portal.",
    feeCategory: "CRS Lighting products fee schedule."
  },
  {
    id: "IS-60669-2-1",
    isNumber: "IS 60669-2-1 : 2008",
    title: "Switches for Household and Similar Fixed-Electrical Installations — Electronic Switches & Dimmers",
    division: "Electrotechnical (ETD 14)",
    category: "Electrical Accessories",
    year: "2008",
    status: "Active (Mandatory CRS)",
    isMandatory: true,
    scheme: "Scheme-II (Compulsory Registration Scheme - CRS)",
    qcoOrder: "Electronics and Information Technology Goods (Requirement for Compulsory Registration) Order",
    scope: "Applies to electronic switches and dimmers for household and similar fixed electrical installations intended for controlling LED lighting, fans, and appliance loads.",
    keyClauses: [
      { clause: "8.1", title: "Protection Against Electric Shock", desc: "Live parts shall not be accessible when dimmer knob/plate is installed." },
      { clause: "17.1", title: "Making and Breaking Capacity", desc: "Must withstand 40,000 switching operations at rated load without contact sticking or arc hazard." },
      { clause: "26.1", title: "EMC Emission & Immunity", desc: "Conducted RF noise suppression on mains wiring per CISPR 14/15." }
    ],
    testingRequirements: [
      "40,000 cycles electrical switching endurance test",
      "Temperature rise test at full rated current (max 45°C rise)",
      "Glow wire fire resistance test at 850°C",
      "Insulation resistance and 2000V dielectric test",
      "Conducted EMC disturbance measurement"
    ],
    applicableProducts: [
      "Rotary & Touch Dimmers for LED Products",
      "Smart WiFi / Zigbee In-Wall Electronic Switches",
      "PIR Motion Sensor Switches",
      "Electronic Fan Speed Regulators"
    ],
    relatedStandards: ["IS 3854", "IS 1293 (Plugs/Sockets)", "IEC 60669-2-1"],
    licensingProcess: "Type testing at BIS-recognized electrical/electronics laboratory and CRS registration.",
    feeCategory: "CRS accessories schedule."
  },
  {
    id: "IS-ISO-IEC-27001",
    isNumber: "IS/ISO/IEC 27001 : 2022",
    title: "Information Security, Cybersecurity and Privacy Protection — Information Security Management Systems (ISMS)",
    division: "Electronics & IT (LITD 17)",
    category: "Management Systems Certification (MSCS)",
    year: "2022",
    status: "Active (National Management Standard)",
    isMandatory: false,
    scheme: "Management Systems Certification Scheme (MSCS)",
    qcoOrder: "Not a product safety QCO (Enterprise Management Standard)",
    scope: "Specifies requirements for establishing, implementing, maintaining and continually improving an Information Security Management System (ISMS) within an organization.",
    keyClauses: [
      { clause: "Clause 4", title: "Context of the Organization", desc: "Determining internal/external issues and scope of the ISMS." },
      { clause: "Clause 6", title: "Planning & Information Security Risk Assessment", desc: "Risk assessment, risk treatment methodology and Statement of Applicability (SoA)." },
      { clause: "Clause 9", title: "Performance Evaluation", desc: "Monitoring, measurement, internal audit and management review." },
      { clause: "Annex A", title: "Information Security Controls", desc: "93 controls grouped into Organizational, People, Physical, and Technological categories." }
    ],
    testingRequirements: [
      "Stage 1 Document Adequacy Audit",
      "Stage 2 On-site Implementation Audit",
      "Vulnerability assessment & risk treatment verification",
      "Access control, data encryption, and incident response audit"
    ],
    applicableProducts: [
      "IT & Cloud Service Providers",
      "Fintech & Banking Data Centers",
      "Government Digital Public Infrastructure (DPI) Portals",
      "Electronics Manufacturing Enterprises"
    ],
    relatedStandards: ["IS/ISO 9001 (Quality)", "IS/ISO 27701 (Privacy)", "IS/ISO/IEC 27002 (Controls Guide)"],
    licensingProcess: "Two-stage management system audit by BIS Management Systems Certification Division (MSCD).",
    feeCategory: "Management System Certification audit fee schedule."
  },
  {
    id: "IS-10500-2012",
    isNumber: "IS 10500 : 2012",
    title: "Drinking Water — Specification (Second Revision)",
    division: "Food & Agriculture (FAD 14)",
    category: "Food & Water Quality",
    year: "2012",
    status: "Active (National Standard)",
    isMandatory: true,
    scheme: "Scheme-I (ISI Mark) / Jal Jeevan Mission Mandate",
    qcoOrder: "Potable Water Quality Mandate & Central Public Health Directive",
    scope: "Prescribes the essential physical, chemical, microbiological and toxicological requirements and methods of sampling and test for drinking water.",
    keyClauses: [
      { clause: "Table 1", title: "Organoleptic and Physical Parameters", desc: "pH (6.5-8.5), Turbidity (max 1 NTU), Total Dissolved Solids (max 500 mg/L)." },
      { clause: "Table 2", title: "General Parameters Concerning Substances Undesirable in Excessive Amounts", desc: "Total Hardness, Calcium, Magnesium, Chlorides, Sulphates, Iron." },
      { clause: "Table 3", title: "Parameters Concerning Toxic Substances", desc: "Lead (max 0.01 mg/L), Arsenic (max 0.01 mg/L), Mercury, Cadmium, Chromium." },
      { clause: "Table 4", title: "Bacteriological Quality of Drinking Water", desc: "E. coli or thermotolerant coliform bacteria must not be detectable in any 100ml sample." }
    ],
    testingRequirements: [
      "Total Coliform and E. coli Membrane Filtration",
      "Heavy Metals testing by ICP-MS / Atomic Absorption",
      "Pesticide residues multi-residue GC-MS/MS testing",
      "Radioactive substances (Alpha and Beta emitters)"
    ],
    applicableProducts: [
      "Municipal Piped Water Supply",
      "Water Purification Plants",
      "Jal Jeevan Mission Rural Water Schemes",
      "Tanker Water Deliveries"
    ],
    relatedStandards: ["IS 14543 (Packaged Water)", "IS 13428 (Natural Mineral Water)", "IS 3025 (Methods of Sampling)"],
    licensingProcess: "Comprehensive water source analysis, pipeline hygiene audit, continuous monitoring protocol.",
    feeCategory: "Public Utilities / MSME concessions."
  },
  {
    id: "IS-1293-2019",
    isNumber: "IS 1293 : 2019",
    title: "Plugs and Socket-Outlets of Rated Voltage up to and including 250V and Rated Current up to 16A",
    division: "Electrotechnical (ETD 14)",
    category: "Electrical Equipment",
    year: "2019",
    status: "Active (Mandatory QCO)",
    isMandatory: true,
    scheme: "Scheme-I (ISI Mark)",
    qcoOrder: "Plugs and Sockets (Quality Control) Order, 2020",
    scope: "Applies to plugs and fixed or portable socket-outlets for a.c. only, with or without earthing contact, rated voltage not exceeding 250 V and rated current not exceeding 16 A.",
    keyClauses: [
      { clause: "9.1", title: "Dimensions & Gauges", desc: "Strict adherence to Type D (6A) and Type M (16A) Indian configuration pin gauges." },
      { clause: "10.1", title: "Protection Against Electric Shock", desc: "Live parts shall not be accessible during insertion or withdrawal." },
      { clause: "13.2", title: "Temperature Rise Test", desc: "Maximum terminal temperature rise shall not exceed 45°C during 1-hour rated current." },
      { clause: "20.1", title: "Resistance to Heat and Fire (Glow Wire Test)", desc: "Insulating parts tested with glow-wire up to 850°C." }
    ],
    testingRequirements: [
      "Gauge verification for pin dimensions and spacing",
      "10,000 cycles mechanical endurance test",
      "Insulation resistance (≥ 5 MΩ) and dielectric strength (2000V)",
      "Cord retention and torsion strain test",
      "Glow wire and flame retardancy test"
    ],
    applicableProducts: [
      "2-pin and 3-pin Wall Plugs (6A & 16A)",
      "Multi-plug adaptors and Extension cords",
      "Smart WiFi Plugs with energy monitoring",
      "Appliance power cords with molded plugs"
    ],
    relatedStandards: ["IS 302-1", "IEC 60884-1", "IS 694 (Cables)"],
    licensingProcess: "Factory audit, in-house endurance & temperature rise setup, BIS lab verification.",
    feeCategory: "Mandatory QCO for all manufacturers and importers."
  },
  {
    id: "IS-15298-2016",
    isNumber: "IS 15298 (Part 2) : 2016",
    title: "Personal Protective Equipment — Safety Footwear",
    division: "Textiles & Leather (TXD 05)",
    category: "Personal Protective Equipment",
    year: "2016",
    status: "Active (Mandatory QCO)",
    isMandatory: true,
    scheme: "Scheme-I (ISI Mark)",
    qcoOrder: "Footwear made from Leather and other materials (Quality Control) Order, 2024",
    scope: "Specifies basic and additional (optional) requirements for safety footwear used for general purposes, including mechanical risks, slip resistance, thermal risks and ergonomic behaviour.",
    keyClauses: [
      { clause: "5.3.2.2", title: "Toe Cap Impact Resistance", desc: "Must withstand 200 Joules impact energy without reducing internal clearance below minimum limits." },
      { clause: "5.3.2.3", title: "Toe Cap Compression Resistance", desc: "Must withstand 15 kN compression load." },
      { clause: "5.4.4", title: "Outsole Slip Resistance", desc: "Friction coefficient tested on ceramic tile with NaLS and steel floor with glycerol." },
      { clause: "5.8.3", title: "Upper Leather Tear Strength", desc: "Minimum tear force of 120 N for full grain leather." }
    ],
    testingRequirements: [
      "200J Drop weight impact test on steel/composite toe cap",
      "15kN hydraulic compression load test",
      "Outsole flexing resistance (Bata/De Mattia 30,000 flexes)",
      "Water penetration and absorption of upper material",
      "Electrical resistance (Conductive / Antistatic / Insulating)"
    ],
    applicableProducts: [
      "Industrial Steel Toe Safety Shoes",
      "Mining & Construction Safety Boots",
      "Chemical Resistant Safety Footwear",
      "Dielectric Electrician Work Boots"
    ],
    relatedStandards: ["ISO 20345:2011", "IS 15298 (Part 1, 3 & 4)"],
    licensingProcess: "Factory inspection, testing of toe caps, bonding and sole adhesion, grant of ISI license.",
    feeCategory: "80% concession on testing fees for micro enterprises."
  },
  {
    id: "IS-9873-2019",
    isNumber: "IS 9873 (Part 1 to 9) : 2019",
    title: "Safety of Toys — Safety Requirements for Children’s Toys",
    division: "Chemical (CHD 34)",
    category: "Consumer Products & Toys",
    year: "2019",
    status: "Active (Mandatory QCO)",
    isMandatory: true,
    scheme: "Scheme-I (ISI Mark)",
    qcoOrder: "Toys (Quality Control) Order, 2020",
    scope: "Applies to all toys intended for use by children under 14 years of age. Covers mechanical, physical, flammability, toxic element migration and electric safety.",
    keyClauses: [
      { clause: "Part 1 / 4.4", title: "Small Parts Hazard", desc: "Toys for children under 36 months must not fit entirely within the small parts test cylinder." },
      { clause: "Part 1 / 4.5", title: "Sharp Edges & Points", desc: "No accessible sharp edges (tested with sharp edge tester) or sharp points." },
      { clause: "Part 3", title: "Migration of Certain Heavy Metals", desc: "Limits for Antimony, Arsenic, Barium, Cadmium, Chromium, Lead, Mercury, Selenium." },
      { clause: "Part 6", title: "Phthalates Content", desc: "DEHP, DBP, BBP concentration shall not exceed 0.1% by mass." },
      { clause: "Part 9 / IS 15644", title: "Electric Toy Safety", desc: "Low voltage power, no overheating, battery leakage protection." }
    ],
    testingRequirements: [
      "Torque & Tension pull test on detachable components",
      "Drop test (850mm onto steel plate)",
      "Heavy element migration testing via ICP-OES",
      "Flammability burn rate evaluation (Part 2)",
      "Phthalate ester extraction via GC-MS"
    ],
    applicableProducts: [
      "Plastic & Wooden Toys",
      "Electronic & Battery Operated Toys",
      "Plush & Stuffed Animals",
      "Ride-on Cars & Tricycles",
      "Board Games & Puzzles"
    ],
    relatedStandards: ["IS 15644 (Electric Toys)", "ISO 8124-1", "EN 71-1"],
    licensingProcess: "Factory audit, Scheme-I certification, separate testing for electric and non-electric categories.",
    feeCategory: "50% rebate on minimum marking fees for Startups & Micro units."
  },
  {
    id: "IS-1417-2016",
    isNumber: "IS 1417 : 2016",
    title: "Gold and Gold Alloys, Jewellery/Artefacts — Fineness and Marking (Fifth Revision)",
    division: "Metallurgical Engineering (MTD 10)",
    category: "Hallmarking & Precious Metals",
    year: "2016",
    status: "Active (Mandatory Hallmarking)",
    isMandatory: true,
    scheme: "Hallmarking Scheme (AHC Recognized Centres)",
    qcoOrder: "Hallmarking of Gold Jewellery and Gold Artefacts Order",
    scope: "Specifies the purity (fineness) grades of gold alloys used in jewellery/artefacts, method of determination and mandatory hallmarking marks.",
    keyClauses: [
      { clause: "4.1", title: "Mandatory Purity Grades", desc: "6 standard grades: 24K (999), 23K (958), 22K (916), 20K (833), 18K (750), 14K (585)." },
      { clause: "5.1", title: "Mandatory 3 Marks on Jewellery", desc: "1) BIS Logo, 2) Purity in Karat & Fineness (e.g., 22K916), 3) 6-digit alphanumeric HUID (Hallmark Unique Identification)." },
      { clause: "6.2", title: "Fire Assay Testing Method", desc: "Cupellation method as per IS 1418 with accuracy of 0.5 parts per thousand." }
    ],
    testingRequirements: [
      "Fire Assay Cupellation method (Standard IS 1418)",
      "X-Ray Fluorescence (XRF) non-destructive screening",
      "Laser engraving and HUID portal verification",
      "Density and touchstone preliminary check"
    ],
    applicableProducts: [
      "Gold Rings, Necklaces, Bangles & Chains",
      "Gold Coins & Medallions (up to 50g)",
      "Bridal Gold Jewellery Sets",
      "Gold Artefacts and Religious Idols"
    ],
    relatedStandards: ["IS 1418 (Fire Assay)", "IS 2112 (Silver Hallmarking)", "IS 15820 (AHC Recognition)"],
    licensingProcess: "Jeweller registration on Manakonline (Instant portal issue), testing at BIS-recognized Assaying and Hallmarking Centre (AHC).",
    feeCategory: "Jeweller registration is ZERO fee for life; per piece hallmarking charge ₹45/- + GST."
  },
  {
    id: "IS-13252-1-2010",
    isNumber: "IS 13252 (Part 1) : 2010 / IEC 60950-1",
    title: "Information Technology Equipment — Safety (Mobile Phones, Smart Phones & Cellular Devices)",
    division: "Electronics & IT (LITD 16)",
    category: "Electronics & IT Goods",
    year: "2010",
    status: "Active (Mandatory CRS)",
    isMandatory: true,
    scheme: "Scheme-II (Compulsory Registration Scheme - CRS)",
    qcoOrder: "Electronics and Information Technology Goods (Requirement for Compulsory Registration) Order",
    scope: "Mandatory safety standard for mobile phones, smartphones, cellular handsets, and handheld IT computing devices. Covers electric shock, heating, fire hazard, battery integration safety, SAR (Specific Absorption Rate) acoustic safety, and mechanical strength.",
    keyClauses: [
      { clause: "1.5 / 1.7", title: "Safety Interlocks & Labeling", desc: "Mandatory BIS CRS registration R-Number marking with statement 'Self-Declaration - Conforming to IS 13252 (Part 1)'." },
      { clause: "2.1", title: "Protection from Electric Shock & Energy Hazards", desc: "Creepage/clearance on USB charging ports and internal power isolation barriers." },
      { clause: "4.2", title: "Mechanical Strength & Drop Test", desc: "Handheld phone unit must withstand multiple drops from 1 meter without hazardous live part exposure." },
      { clause: "4.3.8", title: "Lithium Battery Safety Integration", desc: "Must incorporate BIS-certified secondary cells compliant with IS 16046 (Part 2)." }
    ],
    testingRequirements: [
      "Specific Absorption Rate (SAR) radiofrequency exposure limits (≤ 1.6 W/kg over 1g tissue)",
      "High voltage insulation breakdown test (1500V AC)",
      "Mechanical drop and tumbling barrel impact test",
      "Temperature rise and thermal runaway test under continuous fast charging",
      "Battery overcharge, short circuit and reverse polarity safety"
    ],
    applicableProducts: [
      "Smartphones (5G / 4G LTE)",
      "Feature Phones & Basic Cellular Handsets",
      "Tablet Computers & Phablets",
      "Cellular POS Terminals & Smart Handheld Readers"
    ],
    relatedStandards: ["IS/IEC 62368-1:2023", "IS 16046 (Part 2) (Batteries)", "IS 18112 (USB Type-C Charger)", "TEC / WPC Rules"],
    licensingProcess: "Sample testing at NABL/BIS-recognized electronics laboratory, online filing on Manakonline CRS portal, issuance of unique R-Number.",
    feeCategory: "MeitY CRS mobile device schedule."
  },
  {
    id: "IS-14543-2016",
    isNumber: "IS 14543 : 2016",
    title: "Packaged Drinking Water (Other than Natural Mineral Water) — Specification",
    division: "Food & Agriculture (FAD 14)",
    category: "Food & Water Quality",
    year: "2016",
    status: "Active (Mandatory QCO)",
    isMandatory: true,
    scheme: "Scheme-I (ISI Mark)",
    qcoOrder: "Packaged Drinking Water (Quality Control) Order",
    scope: "Prescribes requirements and methods of sampling and test for packaged drinking water filled in hermetically sealed containers of various sizes, intended for direct human consumption.",
    keyClauses: [
      { clause: "3.2", title: "Treatment Processes", desc: "Permitted processing includes filtration, aeration, RO, demineralization, remineralization, and UV/Ozone disinfection." },
      { clause: "Table 1", title: "Physical & Chemical Requirements", desc: "TDS (max 500 mg/L), pH (6.5 to 8.5), Turbidity (max 2 NTU), Total Hardness (max 200 mg/L)." },
      { clause: "Table 2", title: "Toxic Elements", desc: "Strict limits for Lead, Cadmium, Arsenic, Chromium, Nickel, and Nitrate." },
      { clause: "Table 3", title: "Microbiological Requirements", desc: "Zero count for E. coli, Coliform, Faecal streptococci, Pseudomonas aeruginosa, and Yeast/Mould." }
    ],
    testingRequirements: [
      "Microbiological pathogen isolation (Membrane filter technique)",
      "Heavy metals determination by ICP-MS",
      "Pesticide residue testing by GC-MS/MS (individual residue ≤ 0.0001 mg/L)",
      "Container migration and food-grade safety per IS 15410"
    ],
    applicableProducts: [
      "Packaged Drinking Water (200ml, 500ml, 1L, 2L Bottles)",
      "20-Litre Polycarbonate Water Jars",
      "Packaged Water Pouches & Dispenser Carboys"
    ],
    relatedStandards: ["IS 10500:2012 (Drinking Water)", "IS 13428 (Natural Mineral Water)", "IS 15410 (PET Containers)"],
    licensingProcess: "Mandatory in-house laboratory setup, complete chemical and microbiological equipment, factory inspection and license grant.",
    feeCategory: "Mandatory ISI mark with MSME concession."
  },
  {
    id: "IS-14286-2019",
    isNumber: "IS 14286 : 2019 / IEC 61215",
    title: "Terrestrial Photovoltaic (PV) Modules — Design Qualification and Type Approval",
    division: "Electrotechnical (ETD 28)",
    category: "Renewable Energy & Solar",
    year: "2019",
    status: "Active (Mandatory CRS/MNRE)",
    isMandatory: true,
    scheme: "Scheme-II (Compulsory Registration Scheme - CRS)",
    qcoOrder: "Solar Photovoltaics, Systems, Devices and Components Goods (Requirement for Compulsory Registration) Order",
    scope: "Specifies requirements for design qualification and type approval of terrestrial photovoltaic modules suitable for long-term operation in general open-air climates.",
    keyClauses: [
      { clause: "10.1", title: "Visual Inspection & Insulation", desc: "No bubbles, delamination, or cracked solar cells; wet leakage current test." },
      { clause: "10.11", title: "Thermal Cycling Test", desc: "200 thermal cycles (-40°C to +85°C) with maximum rated current injection." },
      { clause: "10.12", title: "Humidity Freeze Test", desc: "10 cycles of 85°C/85% RH followed by rapid freezing to -40°C." },
      { clause: "10.13", title: "Damp Heat Test", desc: "1000 hours continuous exposure at 85°C and 85% relative humidity." },
      { clause: "10.16", title: "Mechanical Load Test", desc: "Dynamic and static load test up to 2400 Pa / 5400 Pa for wind and snow resistance." }
    ],
    testingRequirements: [
      "Maximum Power Determination (STC Flash Test)",
      "Damp Heat & Humidity Freeze environmental stress testing",
      "UV preconditioning and hot-spot endurance testing",
      "Hail impact test (25mm ice sphere at 23 m/s)",
      "Wet leakage current and insulation breakdown test"
    ],
    applicableProducts: [
      "Crystalline Silicon Terrestrial Solar PV Panels",
      "Thin-Film Solar PV Modules",
      "Bifacial Solar Modules",
      "Rooftop & Utility-Scale Solar Arrays"
    ],
    relatedStandards: ["IS/IEC 61730 (Solar Safety)", "IS 16221 (Inverter Safety)", "MNRE ALMM Approved List"],
    licensingProcess: "Comprehensive test series in BIS/MNRE-accredited solar laboratory and CRS portal registration.",
    feeCategory: "Tiered module capacity fee structure."
  },
  {
    id: "IS-694-2010",
    isNumber: "IS 694 : 2010",
    title: "Polyvinyl Chloride (PVC) Insulated Cables for Working Voltages up to and including 1100 V",
    division: "Electrotechnical (ETD 09)",
    category: "Electrical Equipment",
    year: "2010",
    status: "Active (Mandatory QCO)",
    isMandatory: true,
    scheme: "Scheme-I (ISI Mark)",
    qcoOrder: "Wires and Cables (Quality Control) Order",
    scope: "Covers single-core and multi-core PVC insulated and sheathed/unsheathed cables with copper or aluminum conductors for electric power, lighting, and industrial wiring.",
    keyClauses: [
      { clause: "5.1", title: "Conductor Material", desc: "High-purity annealed copper or EC grade aluminum conforming to IS 8130." },
      { clause: "6.1", title: "Insulation Thickness & Properties", desc: "PVC compound Type A or Type C with minimum tensile strength 12.5 N/mm²." },
      { clause: "14.1", title: "Spark Testing", desc: "100% in-line high-voltage spark testing during cable extrusion." }
    ],
    testingRequirements: [
      "Conductor resistance test at 20°C (Kelvin Double Bridge)",
      "High voltage water immersion test (3kV AC for 5 minutes)",
      "Oxygen index and temperature index flame retardance test (FR/FRLS)",
      "Insulation resistance test at ambient and 70°C",
      "Aging in air oven and hot deformation test"
    ],
    applicableProducts: [
      "Building Wiring Single Core Wires (1.0 to 10 sq mm)",
      "Flexible Multi-Core Copper Appliance Cords",
      "Submersible Pump Flat 3-Core Cables",
      "Industrial Control and Power Cables"
    ],
    relatedStandards: ["IS 8130 (Conductors)", "IS 5831 (PVC Insulation)", "IS 10810 (Test Methods)"],
    licensingProcess: "Factory audit, continuous in-house resistance and spark test verification, ISI license grant.",
    feeCategory: "Scheme-I Cable schedule with MSME concession."
  },
  {
    id: "IS-1786-2008",
    isNumber: "IS 1786 : 2008",
    title: "High Strength Deformed Steel Bars and Wires for Concrete Reinforcement — Specification (Fourth Revision)",
    division: "Metallurgical Engineering (MTD 04)",
    category: "Construction & Infrastructure",
    year: "2008",
    status: "Active (Mandatory QCO)",
    isMandatory: true,
    scheme: "Scheme-I (ISI Mark)",
    qcoOrder: "Steel and Steel Products (Quality Control) Order",
    scope: "Covers requirements for deformed steel bars and wires (TMT Rebars) of grades Fe 415, Fe 415D, Fe 500, Fe 500D, Fe 550, Fe 550D, and Fe 600 used in reinforced concrete structures.",
    keyClauses: [
      { clause: "4.1", title: "Chemical Composition", desc: "Strict maximum limits for Carbon (max 0.25%), Sulphur (max 0.040%), and Phosphorus (max 0.040%)." },
      { clause: "7.1", title: "Mechanical Properties", desc: "Minimum 0.2% proof stress / yield stress, tensile strength ratio (UTS/YS ≥ 1.10 or 1.12 for 'D' grades), and total elongation at max force." },
      { clause: "8.1", title: "Bend and Rebend Test", desc: "No transverse cracks on the tension zone when bent 180° around prescribed mandrel." }
    ],
    testingRequirements: [
      "Tensile yield strength & ultimate tensile strength (UTM machine)",
      "Elongation percentage and uniform elongation (Agt)",
      "180° Bend & 135° Rebend reverse flexure test",
      "Optical Emission Spectrometry (OES) chemical analysis for C, S, P, CE",
      "Nominal mass per meter and rib pattern dimension measurement"
    ],
    applicableProducts: [
      "TMT Rebars (Fe 500D, Fe 550D, Fe 600)",
      "Thermo-Mechanically Treated Construction Steel Bars (8mm to 40mm)",
      "Earthquake Resistant 'D' Grade Reinforcement Rebars",
      "Corrosion Resistant (CRS) Steel Bars"
    ],
    relatedStandards: ["IS 2062 (Structural Steel)", "IS 1608 (Tensile Test)", "IS 1599 (Bend Test)"],
    licensingProcess: "Primary/Secondary mill inspection, heat-wise traceability audit, spectrometer and UTM lab verification.",
    feeCategory: "Steel QCO mandatory scheme."
  }
];

export const STANDARDS_DIVISIONS = [
  { id: "ALL", label: "All Divisions", count: 21450 },
  { id: "LITD", code: "LITD", label: "Electronics & IT (LITD)", count: 2450 },
  { id: "TED", code: "TED", label: "Transport Engineering (TED)", count: 1820 },
  { id: "ETD", code: "ETD", label: "Electrotechnical (ETD)", count: 3100 },
  { id: "FAD", code: "FAD", label: "Food & Agriculture (FAD)", count: 2680 },
  { id: "CED", code: "CED", label: "Civil Engineering (CED)", count: 3890 },
  { id: "TXD", code: "TXD", label: "Textiles & Leather (TXD)", count: 2150 },
  { id: "CHD", code: "CHD", label: "Chemical (CHD)", count: 2940 },
  { id: "MTD", code: "MTD", label: "Metallurgical Engineering (MTD)", count: 1420 },
  { id: "MHD", code: "MHD", label: "Medical Equipment (MHD)", count: 1000 }
];
