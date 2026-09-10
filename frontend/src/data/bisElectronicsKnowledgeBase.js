// Comprehensive BIS Electronics, Electrical & IT Knowledge Base (LITD 01 - LITD 20)
export const BIS_ELECTRONICS_KB = {
  overview: {
    nationalBody: "Bureau of Indian Standards (BIS)",
    department: "Electronics and Information Technology Department (LITD)",
    scope: "Covers electronic components, electrical and electronic equipment, IT, audio/video, telecom, EMC, environmental, reliability, software, security, displays, lighting, power supplies, batteries."
  },
  
  // LITD Technical Committees (LITD 01 to LITD 19)
  committees: [
    {
      code: "LITD 01",
      name: "Environmental Testing Procedures",
      topics: ["Dry heat testing", "Cold testing", "Damp heat", "Cyclic humidity", "Temperature shock", "Vibration", "Shock", "Transportation simulation", "Corrosion exposure", "Dust/Ingress"],
      distinction: "Environmental testing evaluates behavior under physical climatic/mechanical stress, distinct from electrical safety or EMC."
    },
    {
      code: "LITD 02",
      name: "Reliability of Electronic and Electrical Components and Equipment",
      topics: ["Failure rate", "Life testing", "Endurance", "Accelerated life testing (ALT)", "Reliability prediction", "Component qualification", "Failure mechanisms", "Maintainability"],
      distinction: "Evaluates dependable satisfactory operation over specified periods or conditions."
    },
    {
      code: "LITD 03",
      name: "Electromechanical Components and Mechanical Structures",
      topics: ["Connectors", "Switches", "Mechanical structures", "Mounting arrangements", "Equipment enclosures", "Interfaces", "Dimensions", "Mechanical compatibility"]
    },
    {
      code: "LITD 04",
      name: "Electron Tubes and Display Devices",
      topics: ["Display devices", "Electronic displays (LCD, LED, OLED)", "Display characteristics", "Electron tubes", "Display performance & testing"]
    },
    {
      code: "LITD 05",
      name: "Electronic Components and Modules",
      topics: ["Resistors", "Capacitors", "Inductors", "Transformers", "Semiconductor devices", "Diodes", "Transistors", "Integrated circuits (ICs)", "Passive & active components", "Packaging & marking", "Component qualification"]
    },
    {
      code: "LITD 06",
      name: "Wires, Cables, Waveguides and Accessories",
      topics: ["Electronic cables", "Communication cables", "Coaxial cables", "Waveguides", "Cable accessories", "Transmission characteristics", "Insulation & testing"]
    },
    {
      code: "LITD 07",
      name: "Audio, Video and Multimedia Systems and Equipment",
      topics: ["Televisions", "Audio equipment", "Video equipment", "Multimedia systems", "Set-top boxes", "Amplifiers", "Speakers", "Electronic games"],
      safetyStandard: "IS/IEC 62368-1:2023"
    },
    {
      code: "LITD 08",
      name: "Electronic Measuring Instruments, Systems and Accessories",
      topics: ["Measuring instruments", "Measurement systems", "Test equipment", "Accuracy & calibration", "Measurement methods", "Instrument testing"]
    },
    {
      code: "LITD 09",
      name: "Electromagnetic Compatibility (EMC)",
      topics: [
        "Conducted emissions (power/signal)",
        "Radiated emissions",
        "Electrostatic discharge (ESD)",
        "Electrical fast transients (EFT)",
        "Surge immunity",
        "Radiated RF fields",
        "Voltage dips & interruptions",
        "Harmonics & flicker"
      ]
    },
    {
      code: "LITD 10",
      name: "Power System Control and Associated Communications",
      topics: ["Power system control", "Control communication", "Electrical system automation", "Power-system information exchange"]
    },
    {
      code: "LITD 11",
      name: "Fibre Optics, Fibres, Cables and Devices",
      topics: ["Optical fibre", "Fibre cables", "Attenuation", "Optical transmission", "Fibre connectors", "Optical devices & testing"]
    },
    {
      code: "LITD 12",
      name: "Transmitting Equipment for Radio Communication",
      topics: ["Radio transmitters", "Radio communication systems", "RF equipment", "RF measurements", "Transmission characteristics"]
    },
    {
      code: "LITD 13",
      name: "Information and Communication Technologies (ICT)",
      topics: ["ICT equipment", "Digital communication", "Information exchange", "Network technologies", "Interoperability", "Communication protocols"]
    },
    {
      code: "LITD 14",
      name: "Software and System Engineering",
      topics: ["Software lifecycle", "System lifecycle", "Requirements engineering", "Software architecture", "Software testing & QA", "Verification & validation", "Dependability"],
      distinction: "Management/software engineering standards are distinct from product safety standards."
    },
    {
      code: "LITD 15",
      name: "Data Management, Document Processing and Programming Languages",
      topics: ["Data management", "Data structures", "Document processing", "Information exchange", "Programming languages", "Metadata & formats"]
    },
    {
      code: "LITD 16",
      name: "Computer Hardware, Peripherals and Identification Cards",
      topics: ["Computers", "Servers", "Workstations", "Laptops", "Tablets", "Peripherals", "Input/output devices", "Identification cards", "Card readers"]
    },
    {
      code: "LITD 17",
      name: "Information Systems Security and Biometrics",
      topics: ["Information security management (IS/ISO/IEC 27001)", "Authentication", "Access control", "Biometrics (Fingerprint, Iris, Facial)", "Identity management", "Security controls"]
    },
    {
      code: "LITD 18",
      name: "E-Governance",
      topics: ["Electronic government", "Digital government services", "Electronic records", "Government interoperability", "Digital processes"]
    },
    {
      code: "LITD 19",
      name: "E-Learning",
      topics: ["Digital learning systems", "Educational technology", "Learning content interoperability", "Digital educational environments"]
    }
  ],

  // Key Electronics Standards
  keyStandards: [
    {
      isNumber: "IS/IEC 62368-1:2023",
      title: "Audio/video, information and communication technology equipment — Safety requirements",
      scope: "Major modern safety standard replacing older disparate frameworks. Covers computers, laptops, tablets, visual displays, video monitors, TVs, set-top boxes, CCTV cameras/recorders, electronic games, smart watches, smart speakers, Bluetooth speakers.",
      hazardsCovered: ["Electrical energy (shock)", "Fire hazards", "Mechanical energy", "Thermal energy", "Radiation hazards", "Insulation & Enclosures"]
    },
    {
      isNumber: "IS 13252 (Part 1)",
      title: "Information Technology Equipment — Safety",
      context: "Historical foundation of India's electronics CRS framework. Modern products are progressively transitioning to IS/IEC 62368-1:2023."
    },
    {
      isNumber: "IS 302 (Part 1):2008 & IS 302-2-25",
      title: "Safety of Household and Similar Electrical Appliances (General & Microwave Ovens Particular)",
      scope: "Covers kitchen appliances, heating, motor-operated appliances, and specific microwave radiation/heating safety."
    },
    {
      isNumber: "IS 16242 (Part 1):2014",
      title: "General and safety requirements for UPS",
      scope: "Mandatory CRS coverage for UPS / Inverters of rating ≤ 10 kVA."
    },
    {
      isNumber: "IS 10322 Series & IS 16103 (Part 1):2012",
      title: "Luminaires & LED Modules",
      subStandards: [
        "IS 10322 (Part 5/Sec 7):2013 - LED Lighting Chains",
        "IS 10322 (Part 5/Sec 8):2013 - LED Luminaires for Emergency Lighting",
        "IS 16103 (Part 1):2012 - Standalone LED Modules for General Lighting"
      ]
    },
    {
      isNumber: "IS 60669-2-1",
      title: "Particular requirements for electronic switches for household and similar fixed electrical installations",
      scope: "Mandatory CRS coverage for Dimmers for LED products."
    },
    {
      isNumber: "IS 18112:2022",
      title: "Digital Television Receiver for Satellite Broadcast Transmission — Specification",
      scope: "Standard specification for digital television receivers and built-in satellite tuners."
    },
    {
      isNumber: "IS/ISO/IEC 27001",
      title: "Information Security Management Systems (ISMS)",
      scope: "Management-system standard for information security, risk governance, and access control."
    }
  ],

  // Regulatory Frameworks
  regulatoryFrameworks: {
    crs: {
      fullName: "Compulsory Registration Scheme (CRS)",
      basis: "Electronics and Information Technology Goods (Requirement of Compulsory Registration) Order, 2021",
      scheme: "Scheme-II (Self-Declaration of Conformity + BIS Recognized Lab Test Report)",
      fmcsExclusion: "Foreign Manufacturers Certification Scheme (FMCS) does not apply to MeitY-notified CRS goods; CRS has its own distinct foreign OEM registration route via Authorized Indian Representative (AIR).",
      gazetteUpdates2026: {
        amendmentGazette: "S.O. 1246(E) dated 10 March 2026",
        keyImplementationDates: [
          "21 March 2025",
          "25 September 2025",
          "22 January 2026",
          "10 March 2026",
          "26 July 2026"
        ]
      }
    },
    qcoVsCrsVsStandard: {
      standard: "Technical document established by BIS (NOT automatically a mandatory law).",
      qco: "Government order (DPIIT/Ministry) making specified standards legally mandatory under Scheme-I (ISI Mark).",
      crs: "MeitY/MNRE compulsory registration regime under Scheme-II for specified electronics & IT items.",
      testReport: "Evidence of lab testing (NOT a licence or registration by itself).",
      sti: "Scheme of Testing and Inspection for quality control and surveillance."
    }
  }
};
