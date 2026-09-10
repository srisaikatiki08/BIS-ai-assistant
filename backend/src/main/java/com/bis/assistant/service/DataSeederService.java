package com.bis.assistant.service;

import com.bis.assistant.config.DatabaseMigrationService;
import com.bis.assistant.model.*;
import com.bis.assistant.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeederService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeederService.class);

    private final StandardRepository standardRepository;
    private final CertificationSchemeRepository schemeRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final BISServiceRepository serviceRepository;
    private final HallmarkingInfoRepository hallmarkingRepository;
    private final BISUpdateRepository updateRepository;
    private final ProductRepository productRepository;
    private final KnowledgeChunkRepository knowledgeChunkRepository;
    private final DatabaseMigrationService databaseMigrationService;

    public DataSeederService(StandardRepository standardRepository,
                             CertificationSchemeRepository schemeRepository,
                             LaboratoryRepository laboratoryRepository,
                             BISServiceRepository serviceRepository,
                             HallmarkingInfoRepository hallmarkingRepository,
                             BISUpdateRepository updateRepository,
                             ProductRepository productRepository,
                             KnowledgeChunkRepository knowledgeChunkRepository,
                             DatabaseMigrationService databaseMigrationService) {
        this.standardRepository = standardRepository;
        this.schemeRepository = schemeRepository;
        this.laboratoryRepository = laboratoryRepository;
        this.serviceRepository = serviceRepository;
        this.hallmarkingRepository = hallmarkingRepository;
        this.updateRepository = updateRepository;
        this.productRepository = productRepository;
        this.knowledgeChunkRepository = knowledgeChunkRepository;
        this.databaseMigrationService = databaseMigrationService;
    }

    @Override
    public void run(String... args) {
        try {
            databaseMigrationService.migrate();
            if (standardRepository.count() == 0) {
                logger.info("Seeding authentic Bureau of Indian Standards (BIS) database...");
                seedCertificationSchemes();
                seedStandardsAndProducts();
                seedLaboratories();
                seedBISServices();
                seedHallmarkingInfo();
                seedBISUpdates();
                seedKnowledgeChunks();
                logger.info("Database seeding completed successfully.");
            } else {
                logger.info("Database already contains standard records.");
                if (laboratoryRepository.count() < 20) {
                    logger.info("Upgrading laboratory repository to full verified BIS recognized laboratory dataset...");
                    laboratoryRepository.deleteAll();
                    seedLaboratories();
                    logger.info("Laboratories upgraded successfully.");
                }
            }
        } catch (Exception e) {
            logger.error("Database seeding encountered error: ", e);
        }
    }

    private void seedCertificationSchemes() {
        schemeRepository.saveAll(List.of(
                new CertificationScheme("SCHEME-I", "Product Certification Scheme (ISI Mark)", "ISI Mark",
                        "Domestic and Foreign Manufacturers",
                        "The flagship product certification scheme in India. Requires third-party audit of factory premises and pre-license product testing.",
                        "Factory Inspection + Lab Testing", "https://manakonline.in"),
                new CertificationScheme("SCHEME-II", "Compulsory Registration Scheme (CRS)", "Standard Mark (CRS)",
                        "Electronics, IT & Solar Photovoltaic Manufacturers",
                        "Self-declaration conformity scheme notified under MeitY and MNRE orders for IT/Electronics goods.",
                        "BIS Recognized Lab Testing + Registration", "https://www.crsbis.in"),
                new CertificationScheme("SCHEME-IV", "Foreign Manufacturers Certification Scheme (FMCS)", "ISI Mark",
                        "Overseas Manufacturing Units",
                        "Grants standard ISI mark licence to overseas manufacturing plants. Requires physical on-site audit in foreign country.",
                        "International Factory Audit + Indian Lab Testing", "https://manakonline.in"),
                new CertificationScheme("HALLMARKING", "Hallmarking Scheme for Gold and Silver Jewellery", "HUID Mark",
                        "Jewellers and Assaying & Hallmarking Centres",
                        "Mandatory 6-digit alphanumeric Hallmark Unique Identification (HUID) for precious metal jewellery.",
                        "Assaying + Laser Inscription", "https://manakonline.in")
        ));
    }

    private void seedStandardsAndProducts() {
        // Standard 1: IS/IEC 62368-1:2023
        Standard s1 = new Standard(
                "IS-IEC-62368-1-2023",
                "IS/IEC 62368-1 : 2023",
                "Audio/Video, Information and Communication Technology Equipment — Safety Requirements",
                "Electronics & IT (LITD 07 / LITD 16)",
                "Electronics & IT Goods",
                "2023",
                "Active (Mandatory CRS)",
                true,
                "Scheme-II (Compulsory Registration Scheme - CRS)",
                "Electronics and Information Technology Goods (Requirement for Compulsory Registration) Order, 2021",
                "Major modern safety standard for audio/video, information and communication technology equipment. Covers computers, laptops, notebooks, tablets, visual display units, TVs, set-top boxes, CCTV cameras/recorders, electronic games, smart watches, smart speakers, Bluetooth speakers, and TWS earphones.",
                "Sample testing at BIS-recognized lab, submission on Manakonline CRS portal, grant of R-Number for CRS self-declaration label.",
                "Tiered CRS model family registration fees."
        );
        s1.addDocument(new StandardDocument("Clause 4", "General Requirements & Energy Sources", "Classification of energy sources (Class 1, 2, 3) for electrical, thermal, mechanical, and radiation hazards.", "https://manakonline.in"));
        s1.addDocument(new StandardDocument("Clause 5", "Electrically-Caused Injury & Insulation", "Protection against electric shock, contact voltage limits, creepage and clearance distances.", "https://manakonline.in"));
        s1.addDocument(new StandardDocument("Clause 6", "Electrically-Caused Fire", "Potential ignition sources, fire containment enclosures, and flammability ratings (V-0, V-1).", "https://manakonline.in"));
        s1.addTestingRequirement(new TestingRequirement("Electric Strength High-Voltage Breakdown", "Clause 5.4.9", "Dielectric strength 1500V - 3000V AC without dielectric breakdown"));
        s1.addTestingRequirement(new TestingRequirement("Touch Current & Earth Continuity", "Clause 5.7", "Touch current <= 0.25 mA for Class II equipment"));
        s1.addTestingRequirement(new TestingRequirement("Acoustic Sound Pressure Limit (SPL)", "Annex ZB", "Sound pressure level <= 100 dBA to prevent permanent hearing damage"));
        s1.setApplicableProducts(List.of("Laptops, Notebooks & Tablets", "Television Receivers & Video Monitors", "CCTV Cameras & CCTV Recorders", "Smart Watches & Fitness Wearables", "Smart Speakers & Bluetooth Speakers", "TWS Wireless Earbuds"));

        // Standard 2: IS 4151:2015
        Standard s2 = new Standard(
                "IS-4151-2015",
                "IS 4151 : 2015",
                "Protective Helmets for Motorcycle Riders — Specification",
                "Transport Engineering (TED 22)",
                "Automotive & Safety",
                "2015",
                "Active (Mandatory QCO)",
                true,
                "Scheme-I (ISI Mark)",
                "Two-Wheeler Helmet (Quality Control) Order, 2020",
                "Covers requirements regarding materials, construction, finish, mass and performance for protective helmets for everyday use by motorcycle and two-wheeler riders.",
                "Factory audit by BIS inspecting officer, in-factory test facility verification, independent lab testing, grant of CM/L license.",
                "Standard Scheme-I Annual Marking Fee"
        );
        s2.addDocument(new StandardDocument("Clause 4.1", "Shell Material & Construction", "Must be impact resistant, non-degrading under sunlight and moisture.", "https://manakonline.in"));
        s2.addDocument(new StandardDocument("Clause 7.1", "Impact Attenuation Test", "Peak acceleration shall not exceed 300g under ambient, hot, cold and wet conditioning.", "https://manakonline.in"));
        s2.addTestingRequirement(new TestingRequirement("Impact Attenuation Test", "Drop test onto flat and hemispherical steel anvils", "Peak headform acceleration <= 300g"));
        s2.addTestingRequirement(new TestingRequirement("Penetration Resistance Test", "Conical striker 3kg dropped from 1m", "No electrical contact with headform"));
        s2.addTestingRequirement(new TestingRequirement("Dynamic Retention System Test", "Drop mass 15kg on chin strap", "Dynamic elongation <= 35mm, residual elongation <= 25mm"));
        s2.setApplicableProducts(List.of("Motorcycle Full-Face Helmets", "Open-Face Two-Wheeler Helmets", "Modular Rider Helmets"));

        // Standard 3: IS 10500:2012
        Standard s3 = new Standard(
                "IS-10500-2012",
                "IS 10500 : 2012",
                "Drinking Water — Specification (Second Revision)",
                "Food & Agriculture (FAD 14)",
                "Water Quality",
                "2012",
                "Active (National Standard)",
                false,
                "Scheme-I (ISI Mark)",
                "National Benchmark Quality Standard",
                "Prescribes the requirements and the methods of sampling and test for potable drinking water supplied through municipal pipelines or community water supplies.",
                "Lab analysis at NABL/BIS testing facility for physicochemical, toxic metal, pesticide and microbiological parameters.",
                "Standard Certification Testing Fees"
        );
        s3.addTestingRequirement(new TestingRequirement("Total Dissolved Solids (TDS)", "Gravimetric Test", "Acceptable Limit: 500 mg/L, Permissible Limit: 2000 mg/L"));
        s3.addTestingRequirement(new TestingRequirement("Turbidity", "Nephelometric Method", "Acceptable Limit: 1 NTU, Permissible Limit: 5 NTU"));
        s3.addTestingRequirement(new TestingRequirement("E. coli & Total Coliform", "Multiple Tube Fermentation / Membrane Filter", "Must be undetectable in any 100 mL sample"));
        s3.setApplicableProducts(List.of("Piped Potable Water Supply", "Community Tap Water Systems"));

        // Standard 4: IS 16046 (Part 2):2018
        Standard s4 = new Standard(
                "IS-16046-2018",
                "IS 16046 (Part 2) : 2018",
                "Secondary Cells and Batteries Containing Alkaline or Other Non-Acid Electrolytes — Lithium Systems",
                "Electrotechnical (ETD 11)",
                "Batteries & Energy Storage",
                "2018",
                "Active (Mandatory CRS)",
                true,
                "Scheme-II (Compulsory Registration Scheme - CRS)",
                "Electronics & IT Goods (Compulsory Registration Order)",
                "Prescribes safety requirements and test procedures for secondary sealed lithium cells and portable batteries used in smartphones, laptops, power banks, and portable electronics.",
                "Testing in BIS recognized laboratory in India, online registration on Manakonline.",
                "Tiered battery family registration fee"
        );
        s4.addTestingRequirement(new TestingRequirement("Thermal Abuse Test", "Chamber heated to 130°C for 10 minutes", "No explosion, no fire"));
        s4.addTestingRequirement(new TestingRequirement("External Short Circuit Test", "Short circuit <= 100 mOhm at 55°C", "No explosion, no fire, case temperature < 150°C"));
        s4.addTestingRequirement(new TestingRequirement("Mechanical Crush Test", "13 kN force between flat surfaces", "No explosion, no fire"));
        s4.setApplicableProducts(List.of("Lithium-Ion Power Banks", "Smartphone Lithium Battery Packs", "Laptop Rechargeable Batteries"));

        // Standard 5: IS 1417:2016
        Standard s5 = new Standard(
                "IS-1417-2016",
                "IS 1417 : 2016",
                "Gold and Gold Alloys, Jewellery/Artefacts — Fineness and Marking",
                "Metallurgical Engineering (MTD 10)",
                "Precious Metals & Hallmarking",
                "2016",
                "Active (Mandatory Hallmarking)",
                true,
                "Hallmarking Scheme (HUID)",
                "Gold Jewellery Hallmarking Order, 2020",
                "Prescribes fineness grades (14K, 18K, 20K, 22K, 23K, 24K) and marking specifications for gold jewellery and artefacts sold in India.",
                "Jeweller registration with BIS, testing & assaying at recognized A&H Centre, laser inscription of 6-digit HUID.",
                "Per-article hallmarking fee"
        );
        s5.addTestingRequirement(new TestingRequirement("Fire Assaying (Cupellation)", "Quantitative assaying of precious metal", "Fineness conformance to declared karatage within permissible tolerance"));
        s5.setApplicableProducts(List.of("22K Gold Necklaces & Chains", "18K Gold Diamond Studded Rings", "24K Gold Coins & Bullion"));

        standardRepository.saveAll(List.of(s1, s2, s3, s4, s5));

        // Seed Product Catalog
        productRepository.saveAll(List.of(
                new Product("Smartphones & Tablets", "Electronics", "Handheld cellular computing devices", "Personal communication", "Aluminium/Glass/Silicon"),
                new Product("Two-Wheeler Helmets", "Automotive Safety", "Protective headgear for motorcycle riders", "Rider head protection", "Fiberglass/ABS/EPS"),
                new Product("TWS Wireless Earbuds & Headphones", "Audio Equipment", "Bluetooth stereo audio earphones and charging cases", "Personal listening", "Polycarbonate/Lithium Cell"),
                new Product("Gold Jewellery & Bangles", "Precious Metals", "22 Karat and 18 Karat gold ornaments", "Adornment and investment", "Gold alloy (91.6% / 75.0%)")
        ));
    }

    private void seedLaboratories() {
        laboratoryRepository.saveAll(List.of(
                // --- ANDHRA PRADESH ---
                new Laboratory("CIPET, Vijayawada", "Central Autonomous LRS Lab", "Vijayawada", "Andhra Pradesh",
                        "SY NO. 377, Surampalli (V), Gannavaram (M), Krishna Dist., VIJAYAWADA, Krishna, Andhra Pradesh, India - 521212",
                        "https://www.google.com/maps/dir/?api=1&destination=SY%20NO.%20377%2C%20Surampalli%20(V)%2C%20Gannavaram%20(M)%2C%20Krishna%20Dist.%2C%20VIJAYAWADA%2C%20Krishna%2C%20Andhra%20Pradesh%2C%20India%20-%20521212",
                        "NABL TC-6141", "Dr. CH Shekar (QM)", "+91 7077573905", "vijayawada@cipet.gov.in",
                        "IS 4984 (HDPE Pipes), IS 4985 (PVC Pipes), IS 15298 (Footwear), IS 12786 (Irrigation Pipes)"),
                new Laboratory("TUV Rheinland (India) Pvt. Ltd., Visakhapatnam", "BIS Recognized Lab", "Visakhapatnam", "Andhra Pradesh",
                        "Survey No. 480/2, AMTZ Campus, Nadupuru Village, Pedagantyada Mandal, Pragadi Maidan,, visakhapatnam, Visakhapatanam, Andhra Pradesh, India - 530031",
                        "https://www.google.com/maps/dir/?api=1&destination=Survey%20No.%20480%2F2%2C%20AMTZ%20Campus%2C%20Nadupuru%20Village%2C%20Pedagantyada%20Mandal%2C%20Pragadi%20Maidan%2C%2C%20visakhapatnam%2C%20Visakhapatanam%2C%20Andhra%20Pradesh%2C%20India%20-%20530031",
                        "NABL TC-6167", "Mr. Nagendra Hebbar", "+91 6364894533", "Nagendra.Hebbar@ind.tuv.com",
                        "IS/IEC 62368-1 (ICT), IS 13252 (IT Safety), IS 16046 (Batteries), IS 13450 (Medical Equipment)"),
                new Laboratory("Accurate Labs, Vijayawada", "BIS Recognized Lab", "Vijayawada", "Andhra Pradesh",
                        "K.V.S.R. Siddhardha College of Pharmaceutical Sciences, SPIIC Block, IIIrd Floor, Pinnamaneni Polyclinic Road, Siddhardha Nagar, Vijayawada-520010, Andhra Pradesh., Vijayawada, Krishna, Andhra Pradesh, India - 520010",
                        "https://www.google.com/maps/dir/?api=1&destination=K.V.S.R.%20Siddhardha%20College%20of%20Pharmaceutical%20Sciences%2C%20SPIIC%20Block%2C%20IIIrd%20Floor%2C%20Pinnamaneni%20Polyclinic%20Road%2C%20Siddhardha%20Nagar%2C%20Vijayawada-520010%2C%20Andhra%20Pradesh.%2C%20Vijayawada%2C%20Krishna%2C%20Andhra%20Pradesh%2C%20India%20-%20520010",
                        "NABL TC-6168", "Swarna Kumari V (QM)", "+91 6301959631", "aefal.labs@gmail.com",
                        "IS 10500 (Drinking Water), IS 14543 (Packaged Water), IS 13428 (Mineral Water)"),

                // --- ASSAM ---
                new Laboratory("BIS, Guwahati Branch Laboratory", "BIS Branch Laboratory", "Guwahati", "Assam",
                        "2nd Floor, West End Block, Housefed Building Complex, Last Gate, Dispur, Guwahati, Assam 781006",
                        "https://www.google.com/maps/dir/?api=1&destination=2nd%20Floor%2C%20West%20End%20Block%2C%20Housefed%20Building%20Complex%2C%20Last%20Gate%2C%20Dispur%2C%20Guwahati%2C%20Assam%20781006",
                        "NABL TC-5010", "Thechano C Ovung (OIC)", "0361-2224670", "gbol@bis.gov.in",
                        "IS 10500 (Drinking Water), IS 14543 (Packaged Water), IS 269 (Cement), IS 1786 (Steel Rebars)"),
                new Laboratory("National Test House (NER) - NTH, Guwahati", "Govt Recognized Lab", "Guwahati", "Assam",
                        "C.I.T.I Complex, Post: Gopinath Nagar, Kalapahar, Guwahati-781016, Guwahati, Kamrup Metro, Assam, India - 781016",
                        "https://www.google.com/maps/dir/?api=1&destination=C.I.T.I%20Complex%2C%20Post%3A%20Gopinath%20Nagar%2C%20Kalapahar%2C%20Guwahati-781016%2C%20Guwahati%2C%20Kamrup%20Metro%2C%20Assam%2C%20India%20-%20781016",
                        "NABL TC-5169", "Shri Animesh Das", "+91 9412223452", "director-guw@nth.gov.in",
                        "IS 10500 (Drinking Water), IS 1786 (TMT Steel), IS 456 (Concrete), IS 694 (Wires)"),
                new Laboratory("Central Institute of Petrochemical Engineering and Technology (CIPET), Guwahati", "Autonomous Central Institute", "Guwahati", "Assam",
                        "NH 31, Near Assam Oil Petrol Pump, P.O Changsari, Guwahati, Assam, Guwahati, Kamrup, Assam, India - 781101",
                        "https://www.google.com/maps/dir/?api=1&destination=NH%2031%2C%20Near%20Assam%20Oil%20Petrol%20Pump%2C%20P.O%20Changsari%2C%20Guwahati%2C%20Assam%2C%20Guwahati%2C%20Kamrup%2C%20Assam%2C%20India%20-%20781101",
                        "NABL TC-5117", "Sagolsem Itomba (QM)", "+91 044 22254780", "cipetcstsguwahati@gmail.com",
                        "IS 4984 (HDPE Pipes), IS 4985 (PVC Pipes), IS 12786 (Polyethylene Pipes)"),

                // --- BIHAR ---
                new Laboratory("BIS, Patna Branch Laboratory", "BIS Branch Laboratory", "Patna", "Bihar",
                        "Bureau of Indian Standards, Patliputra Industrial Estate, Patna, Bihar, India - 800013",
                        "https://www.google.com/maps/dir/?api=1&destination=Bureau%20of%20Indian%20Standards%2C%20Patliputra%20Industrial%20Estate%2C%20Patna%2C%20Bihar%2C%20India%20-%20800013",
                        "NABL TC-5009", "Aabid Hussain (OIC)", "+91 9471192544", "pbol@bis.gov.in",
                        "IS 10500 (Water), IS 14543 (Packaged Water), IS 1786 (TMT Bars), IS 269 (Cement)"),
                new Laboratory("CIPET, Hajipur", "Autonomous Central Institute", "Hajipur", "Bihar",
                        "INDUSTRIAL AREA, Hajipur, Vaishali, Bihar, India - 844102",
                        "https://www.google.com/maps/dir/?api=1&destination=INDUSTRIAL%20AREA%2C%20Hajipur%2C%20Vaishali%2C%20Bihar%2C%20India%20-%20844102",
                        "NABL TC-5118", "Avinash Kumar", "+91 8294497482", "testing-hajipur@cipet.gov.in",
                        "IS 4984 (HDPE Pipes), IS 4985 (PVC Pipes), IS 15351 (Woven Sacks)"),

                // --- CHHATTISGARH ---
                new Laboratory("Central institute of Petrochemicals Engineering & Technology (CIPET), Raipur", "Autonomous Central Institute", "Raipur", "Chhattisgarh",
                        "PLOT NO 48, INDUSTRIAL ARE BHANPURI, BHANPURI, Raipur, Raipur, Chhattisgarh, India - 493221",
                        "https://www.google.com/maps/dir/?api=1&destination=PLOT%20NO%2048%2C%20INDUSTRIAL%20ARE%20BHANPURI%2C%20BHANPURI%2C%20Raipur%2C%20Raipur%2C%20Chhattisgarh%2C%20India%20-%20493221",
                        "NABL TC-5163", "Dr Alok Sahu (QM)", "+91 44 22254780", "cipetraipur@gmail.com",
                        "IS 4984 (HDPE Pipes), IS 4985 (PVC Pipes), IS 14333 (High Density Polyethylene)"),
                new Laboratory("NBML Building Materials Testing Lab LLP, Raipur", "BIS Recognized Lab", "Raipur", "Chhattisgarh",
                        "Raipur Bilaspur Road, Near Akaswani Radio Station, Urkura Nagar Raipur Chhattisgarh 493221, Raipur, Raipur, Chhattisgarh, India - 493221",
                        "https://www.google.com/maps/dir/?api=1&destination=Raipur%20Bilaspur%20Road%2C%20Near%20Akaswani%20Radio%20Station%2C%20Urkura%20Nagar%20Raipur%20Chhattisgarh%20493221%2C%20Raipur%2C%20Raipur%2C%20Chhattisgarh%2C%20India%20-%20493221",
                        "NABL TC-5171", "Nikhil Bajpayee (TM)", "+91 9881110389", "nbmtl2017@gmail.com",
                        "IS 1786 (Steel Rebars), IS 269 (Cement), IS 383 (Aggregates), IS 456 (Concrete)"),

                // --- DELHI ---
                new Laboratory("SIIR, Delhi Shriram Institute For Industrial Research", "Autonomous Research Institute", "Delhi", "Delhi",
                        "19-University Road, Delhi 110007, Delhi, North, Delhi, India - 110007",
                        "https://www.google.com/maps/dir/?api=1&destination=19-University%20Road%2C%20Delhi%20110007%2C%20Delhi%2C%20India",
                        "NABL TC-8102", "Dr. Laxmi Rawat (QM)", "+91 011 35200445", "laxmirawat@shriraminstitute.org",
                        "IS/IEC 62368-1 (ICT), IS 10500 (Water), IS 9873 (Toys), IS 16046 (Batteries), IS 14543 (Packaged Water)"),
                new Laboratory("Electronic Regional Test Laboratory (North) - ERTL (STQC), Delhi", "STQC / Govt Recognized Lab", "New Delhi", "Delhi",
                        "S-Block, Okhla Industrial Area, Phase-II,, New Delhi, South East, Delhi, India - 110020",
                        "https://www.google.com/maps/dir/?api=1&destination=S-Block%2C%20Okhla%20Industrial%20Area%2C%20Phase-II%2C%2C%20New%20Delhi%2C%20South%20East%2C%20Delhi%2C%20India%20-%20110020",
                        "NABL TC-8101", "Manjula Bhati (QM)", "+91 1126386219", "ertlnorth@stqc.nic.in",
                        "IS 13252 (IT Safety), IS/IEC 62368-1 (ICT), IS 16102 (LED), IS 16046 (Lithium Batteries)"),
                new Laboratory("MSME Testing Centre, New Delhi", "Govt MSME Testing Centre", "New Delhi", "Delhi",
                        "Shaheed Capt. Gaur Marg, Okhla Phase -III,, New Delhi, South East, Delhi, India - 110020",
                        "https://www.google.com/maps/dir/?api=1&destination=Shaheed%20Capt.%20Gaur%20Marg%2C%20Okhla%20Phase%20-III%2C%2C%20New%20Delhi%2C%20South%20East%2C%20Delhi%2C%20India%20-%20110020",
                        "NABL TC-8101", "Dr. D.K. Pandey", "+91 9910201527", "dctc-nr@dcmsme.gov.in",
                        "IS 1293 (Plugs/Sockets), IS 694 (Cables), IS 1786 (Steel Rebars), IS 302 (Electrical Safety)"),
                new Laboratory("Conformity Testing Labs Pvt. Ltd. (CTL), New Delhi", "BIS Recognized Lab", "New Delhi", "Delhi",
                        "WH-52, Mayapuri Industrial Area, Phase-1, New Delhi, South West, Delhi, India - 110064",
                        "https://www.google.com/maps/dir/?api=1&destination=WH-52%2C%20Mayapuri%20Industrial%20Area%2C%20Phase-1%2C%20New%20Delhi%2C%20South%20West%2C%20Delhi%2C%20India%20-%20110064",
                        "NABL TC-8134", "Mr. J.K Dhawan (QM)", "+91 9811127453", "cto@labctl.in",
                        "IS/IEC 62368-1, IS 13252 (IT Safety), IS 16046 (Batteries), IS 16102 (LED)"),

                // --- GUJARAT ---
                new Laboratory("NDDB CALF LIMITED, Anand", "Autonomous LRS Lab", "Anand", "Gujarat",
                        "Anand, Gujarat, Anand, Anand, Gujarat, India - 388001",
                        "https://www.google.com/maps/dir/?api=1&destination=Anand%2C%20Gujarat%2C%20Anand%2C%20Anand%2C%20Gujarat%2C%20India%20-%20388001",
                        "NABL TC-7136", "P Rohith Kumar", "+91 9726425080", "prohith@nddbcalf.com",
                        "IS 10500 (Water), IS 1165 (Milk Powders), IS 14543 (Packaged Water)"),
                new Laboratory("HITECHLAB HEALTHCARE & RESEARCH CENTRE LLP, Ahmedabad", "BIS Recognized Lab", "Ahmedabad", "Gujarat",
                        "201-202, Sahaj Arcade, Opp. Lincoln Healthcare, Near Sola gam, Science city road, Ahmedabd, Ahmedabad, Ahmadabad, Gujarat, India - 380060",
                        "https://www.google.com/maps/dir/?api=1&destination=201-202%2C%20Sahaj%20Arcade%2C%20Opp.%20Lincoln%20Healthcare%2C%20Near%20Sola%20gam%2C%20Science%20city%20road%2C%20Ahmedabd%2C%20Ahmedabad%2C%20Ahmadabad%2C%20Gujarat%2C%20India%20-%20380060",
                        "NABL TC-7131", "Rajesh Patel (QM)", "+91 9099971265", "hitechlabindia@gmail.com",
                        "IS 10500 (Drinking Water), IS 14543 (Packaged Drinking Water), IS 13428 (Mineral Water)"),
                new Laboratory("CIPET, Ahmedabad", "Autonomous Central Institute", "Ahmedabad", "Gujarat",
                        "Plot No. 630, Phase-IV, GIDC, Vatva,, Ahmedabad, Ahmadabad, Gujarat, India - 382445",
                        "https://www.google.com/maps/dir/?api=1&destination=Plot%20No.%20630%2C%20Phase-IV%2C%20GIDC%2C%20Vatva%2C%2C%20Ahmedabad%2C%20Ahmadabad%2C%20Gujarat%2C%20India%20-%20382445",
                        "NABL TC-7116", "Rajesh Panda (TM)", "+91 7229000205", "cipetahmd@gmail.com",
                        "IS 4984 (HDPE), IS 4985 (PVC), IS 12786 (Pipes), IS 15298 (Footwear)"),
                new Laboratory("Electrical Research and Development Association (ERDA), Vadodara", "Autonomous Electrical Research Institute", "Vadodara", "Gujarat",
                        "ERDA Road, GIDC, Makarpura, Vadodara, VADODARA, Vadodara, Gujarat, India - 390010",
                        "https://www.google.com/maps/dir/?api=1&destination=ERDA%20Road%2C%20GIDC%2C%20Makarpura%2C%20Vadodara%2C%20VADODARA%2C%20Vadodara%2C%20Gujarat%2C%20India%20-%20390010",
                        "NABL TC-7101", "Mr. Nirav Taunk", "+91 9978940719", "nirav.taunk@erda.org",
                        "IS 2026 (Transformers), IS 17017 (EV Charging), IS 16046 (Batteries), IS 694 (Cables)"),

                // --- HARYANA ---
                new Laboratory("Nemko India (Test Lab) Pvt. Ltd., Faridabad", "BIS Recognized Lab", "Faridabad", "Haryana",
                        "Plot No 193, Sector 68, IMT Faridabad, Faridabad, Faridabad, Haryana, India - 121004",
                        "https://www.google.com/maps/dir/?api=1&destination=Plot%20No%20193%2C%20Sector%2068%2C%20IMT%20Faridabad%2C%20Faridabad%2C%20Haryana%2C%20India%20-%20121004",
                        "NABL TC-8138", "Kavita Dagar (QM)", "+91 9810629447", "satish.sankhyan@nemko.com",
                        "IS/IEC 62368-1, IS 13252, IS 16046 (Lithium Batteries), IS 16102 (LED)"),
                new Laboratory("Star Wire (India) Laboratories Private Ltd., Ballabhgarh", "BIS Recognized Lab", "Ballabhgarh", "Haryana",
                        "21/4, Mathura Road, Ballabhgarh, Faridabad, Haryana, India - 121004",
                        "https://www.google.com/maps/dir/?api=1&destination=21%2F4%2C%20Mathura%20Road%2C%20Ballabhgarh%2C%20Faridabad%2C%20Haryana%2C%20India%20-%20121004",
                        "NABL TC-8140", "Quality Head", "0129-4094200", "starwirelab@starwire.in",
                        "IS 1786 (Steel Rebars), IS 2062 (Structural Steel), IS 2830 (Carbon Steel)"),
                new Laboratory("FARE Labs Pvt. Ltd., Gurugram", "BIS Recognized Lab", "Gurugram", "Haryana",
                        "L-17/3, DLF PHASE-II, IFFCO CHOWK, M.G.ROAD, GURUGRAM, Gurugram, Haryana, India - 122002",
                        "https://www.google.com/maps/dir/?api=1&destination=L-17%2F3%2C%20DLF%20PHASE-II%2C%20IFFCO%20CHOWK%2C%20M.G.ROAD%2C%20GURUGRAM%2C%20Gurugram%2C%20Haryana%2C%20India%20-%20122002",
                        "NABL TC-8135", "Director FARE", "+91 9289351688", "farelabs@farelabs.com",
                        "IS 10500 (Water), IS 14543 (Packaged Water), IS 5454 (Food Oils), IS 1165 (Dairy)"),

                // --- HIMACHAL PRADESH ---
                new Laboratory("National Research and Technology Consortium (NRTC), Parwanoo", "Govt Autonomous Technology Centre", "Parwanoo", "Himachal Pradesh",
                        "HPCED Building, Department of industries complex, Sector-1, Parwanoo, Solan, Himachal Pradesh, India - 173220",
                        "https://www.google.com/maps/dir/?api=1&destination=HPCED%20Building%2C%20Department%20of%20industries%20complex%2C%20Sector-1%2C%20Parwanoo%2C%20Solan%2C%20Himachal%20Pradesh%2C%20India%20-%20173220",
                        "NABL TC-9136", "Dr Kiran Gupta (QM)", "+91 1792 234107", "nrtcpwn@gmail.com",
                        "IS 13252 (IT Safety), IS 16102 (LED Lamps), IS 302 (Appliances)"),
                new Laboratory("Central Institute of Petrochemicals Engineering and Technology (CIPET), BADDI", "Autonomous Central Institute", "Baddi", "Himachal Pradesh",
                        "Plot No. 198/201, Inside VRLA Building, Near Biogenetic Pvt. Ltd.,  Jharmajri, Baddi, Baddi, Solan, Himachal Pradesh, India - 173205",
                        "https://www.google.com/maps/dir/?api=1&destination=Plot%20No.%20198%2F201%2C%20Inside%20VRLA%20Building%2C%20Near%20Biogenetic%20Pvt.%20Ltd.%2C%20%20Jharmajri%2C%20Baddi%2C%20Baddi%2C%20Solan%2C%20Himachal%20Pradesh%2C%20India%20-%20173205",
                        "NABL TC-9163", "Dr. U.P. Singh (QM)", "+91 8829039100", "baddicipet@gmail.com",
                        "IS 4984 (HDPE), IS 4985 (PVC), IS 15298 (Safety Footwear)"),

                // --- JHARKHAND ---
                new Laboratory("SUNTECH, Ranchi", "BIS Recognized Lab", "Ranchi", "Jharkhand",
                        "40-P, TUPUDANA INDUSTRIAL AREA, RANCHI, Ranchi, Jharkhand, India - 834003",
                        "https://www.google.com/maps/dir/?api=1&destination=40-P%2C%20TUPUDANA%20INDUSTRIAL%20AREA%2C%20RANCHI%2C%20Jharkhand%2C%20India%20-%20834003",
                        "NABL TC-5126", "Anil D. Hans", "+91 9304172295", "sun.tech.lab@gmail.com",
                        "IS 1786 (Steel Rebars), IS 10500 (Water), IS 269 (Cement), IS 383 (Aggregates)"),
                new Laboratory("CENTRAL INSTITUTE OF PETROCHEMICALS ENGINEERING & TECHNOLOGY (CIPET), RANCHI", "Autonomous Central Institute", "Ranchi", "Jharkhand",
                        "HEHAL, RANCHI, JHARKHAND, INDIA, Ranchi, Ranchi, Jharkhand, India - 834005",
                        "https://www.google.com/maps/dir/?api=1&destination=HEHAL%2C%20RANCHI%2C%20JHARKHAND%2C%20INDIA%2C%20Ranchi%2C%20Ranchi%2C%20Jharkhand%2C%20India%20-%20834005",
                        "NABL TC-5173", "Mr. Avneet Kumar Joshi (QM)", "+91 651 2999713", "cipetranchi@gmail.com",
                        "IS 4984 (HDPE), IS 4985 (PVC), IS 12786 (Pipes)"),
                new Laboratory("Adityapur Auto Cluster, Adityapur", "Auto Cluster Testing Lab", "Adityapur", "Jharkhand",
                        "Phase VII, Tata kandra main road, near toll bridge junction, Adityapur, Dist- Saraikela kharsawan, Jharkhand, Aditypur, Saraikela Kharsawan, Jharkhand, India - 832109",
                        "https://www.google.com/maps/dir/?api=1&destination=Phase%20VII%2C%20Tata%20kandra%20main%20road%2C%20near%20toll%20bridge%20junction%2C%20Adityapur%2C%20Dist-%20Saraikela%20kharsawan%2C%20Jharkhand%2C%20Aditypur%2C%20Saraikela%20Kharsawan%2C%20Jharkhand%2C%20India%20-%20832109",
                        "NABL TC-5178", "BS Mandal (QM)", "+91 8709051989", "adityapurcluster@gmail.com",
                        "IS 1786 (Steel), IS 2062 (Steel Plates), IS 4151 (Helmets)"),

                // --- JAMMU & KASHMIR ---
                new Laboratory("BIS, Jammu Kashmir Branch Laboratory", "BIS Branch Laboratory", "Jammu", "Jammu & Kashmir",
                        "Lane No.4, SIDCO Industrial Complex, Bari Brahmana, Jammu, Jammu & Kashmir, India - 181133",
                        "https://www.google.com/maps/dir/?api=1&destination=Lane%20No.4%2C%20SIDCO%20Industrial%20Complex%2C%20Bari%20Brahmana%2C%20Jammu%2C%20Jammu%20%26%20Kashmir%2C%20India%20-%20181133",
                        "NABL TC-5011", "Saaqib Raahi", "+91 7006607673", "jkbl@bis.gov.in",
                        "IS 10500 (Water), IS 14543 (Packaged Water), IS 269 (Cement), IS 1786 (Steel)"),
                new Laboratory("Agro Tech Aromatics Pvt Ltd, Samba", "BIS Recognized Lab", "Bari Brahmana", "Jammu & Kashmir",
                        "SIDCO INDUSTRIAL AREA EPIP KARTHOLI, BARI BRAHMANA, Samba, Jammu & Kashmir, India - 181133",
                        "https://www.google.com/maps/dir/?api=1&destination=SIDCO%20INDUSTRIAL%20AREA%20EPIP%20KARTHOLI%2C%20BARI%20BRAHMANA%2C%20Samba%2C%20Jammu%20%26%20Kashmir%2C%20India%20-%20181133",
                        "NABL TC-9198", "Lab Manager", "+91 9872923100", "agrotecharomatics@gmail.com",
                        "IS 10500 (Drinking Water), IS 14543 (Packaged Drinking Water)"),

                // --- KARNATAKA ---
                new Laboratory("BIS, Bengaluru Branch Laboratory", "BIS Branch Laboratory", "Bengaluru", "Karnataka",
                        "Peenya Industrial Area, 1st Stage, Tumkur Road, Bengaluru - 560058, Bengaluru, Bengaluru Urban, Karnataka, India - 560058",
                        "https://www.google.com/maps/dir/?api=1&destination=Peenya%20Industrial%20Area%2C%201st%20Stage%2C%20Tumkur%20Road%2C%20Bengaluru%20-%20560058%2C%20Karnataka%2C%20India",
                        "NABL TC-5008", "Pyla Deshick (OIC)", "080-29908860", "bnbol@bis.gov.in",
                        "IS 10500 (Water), IS 14543 (Packaged Water), IS 694 (Cables), IS 1293 (Plugs)"),
                new Laboratory("TUV Rheinland (India) Pvt. Ltd., Bengaluru", "BIS Recognized International Lab", "Bengaluru", "Karnataka",
                        "27/B, 2nd Cross Road, Electronic City Phase-1, Bangalore, Bengaluru Rural, Karnataka, India - 560100",
                        "https://www.google.com/maps/dir/?api=1&destination=27%2FB%2C%202nd%20Cross%20Road%2C%20Electronic%20City%20Phase-1%2C%20Bangalore%2C%20Karnataka%2C%20India%20-%20560100",
                        "NABL TC-5210", "Director TUV", "080-46498000", "info-ind@tuv.com",
                        "IS/IEC 62368-1, IS 16046 (Lithium Batteries), IS 14286 (Solar Modules), IS 17017 (EV Charging)"),
                new Laboratory("UL INDIA PRIVATE LIMITED, Bengaluru", "BIS Recognized International Safety Lab", "Bengaluru", "Karnataka",
                        "Loc 1&2-Kalyani Platina Campus, Sy. no 129/4, EPIP Zone, Phase II, Whitefield, Bangalore-560066, Loc3-30/A, I Stage Vishveshwarya Industrial Estate, Doddanekkundi Industrial Area, Bangalore-560048",
                        "https://www.google.com/maps/dir/?api=1&destination=Kalyani%20Platina%20Campus%2C%20EPIP%20Zone%2C%20Phase%20II%2C%20Whitefield%2C%20Bangalore%2C%20Karnataka%20-%20560066",
                        "NABL TC-5312", "Operations Head", "080-41384400", "sales.in@ul.com",
                        "IS/IEC 62368-1, IS 13252, IS 16046, IS 302 (Appliances)"),
                new Laboratory("CIPET, Mysuru", "Autonomous Central Institute", "Mysuru", "Karnataka",
                        "437/A, Hebbal Industrial Area, Mysuru, Karnataka, India - 570016",
                        "https://www.google.com/maps/dir/?api=1&destination=437%2FA%2C%20Hebbal%20Industrial%20Area%2C%20Mysuru%2C%20Karnataka%2C%20India%20-%20570016",
                        "NABL TC-5401", "Director CIPET", "0821-2510618", "mysore@cipet.gov.in",
                        "IS 4984 (HDPE), IS 4985 (PVC), IS 15298 (Safety Shoes)"),

                // --- KERALA ---
                new Laboratory("CIPET, KOCHI", "Autonomous Central Institute", "Kochi", "Kerala",
                        "196 A, HIL Colony, Pathalam, Edayar Road,Eloor, Udyogamandal.P.O., Kochi, KOCHI, Ernakulam, Kerala, India - 683501",
                        "https://www.google.com/maps/dir/?api=1&destination=196%20A%2C%20HIL%20Colony%2C%20Pathalam%2C%20Edayar%20Road%2CEloor%2C%20Udyogamandal.P.O.%2C%20Kochi%2C%20KOCHI%2C%20Ernakulam%2C%20Kerala%2C%20India%20-%20683501",
                        "NABL TC-6137", "Lab Manager", "+91 8129497182", "cipetkochi@gmail.com",
                        "IS 4984 (HDPE), IS 4985 (PVC), IS 15298 (Footwear), IS 12786 (Pipes)"),
                new Laboratory("Interfield Laboratories, Kochi", "BIS Recognized Lab", "Kochi", "Kerala",
                        "XIII/1208, Interprint House, R K Pillai Road, Karuvelipady, Kochi, Kochi, Ernakulam, Kerala, India - 682005",
                        "https://www.google.com/maps/dir/?api=1&destination=XIII%2F1208%2C%20Interprint%20House%2C%20R%20K%20Pillai%20Road%2C%20Karuvelipady%2C%20Kochi%2C%20Kochi%2C%20Ernakulam%2C%20Kerala%2C%20India%20-%20682005",
                        "NABL TC-6124", "Quality Manager", "+91 484 2210915", "qm@ifl.in",
                        "IS 10500 (Drinking Water), IS 14543 (Packaged Water), IS 13428 (Mineral Water)"),
                new Laboratory("Fluid Control Research Institute (FCRI), Palakkad", "Autonomous Flow Measurement Lab", "Palakkad", "Kerala",
                        "Kanjikode West, Palakkad, Palakkad, Kerala, India - 678623",
                        "https://www.google.com/maps/dir/?api=1&destination=Kanjikode%20West%2C%20Palakkad%2C%20Palakkad%2C%20Kerala%2C%20India%20-%20678623",
                        "NABL TC-6114", "Gopan C K", "+91 9746526100", "diroffice@fcriindia.com",
                        "IS 779 (Water Meters), IS 2373 (Water Flow Meters), IS 14846 (Sluice Valves)"),

                // --- MADHYA PRADESH ---
                new Laboratory("Kailtech Test and Research Centre Pvt. Ltd., Indore", "BIS Recognized Lab", "Indore", "Madhya Pradesh",
                        "141C, Electronic Complex Industrial Area, Indore, Indore, Madhya Pradesh, India - 452010",
                        "https://www.google.com/maps/dir/?api=1&destination=141C%2C%20Electronic%20Complex%20Industrial%20Area%2C%20Indore%2C%20Madhya%20Pradesh%2C%20India%20-%20452010",
                        "NABL TC-8125", "Quality Head", "0731-4040000", "contact@kailtech.net",
                        "IS/IEC 62368-1, IS 16046 (Batteries), IS 16102 (LED), IS 302 (Appliances)"),
                new Laboratory("Choksi Laboratories Limited, Indore", "BIS Recognized Public Lab", "Indore", "Madhya Pradesh",
                        "Survey No. 9/1, Balaji Tusiyana Industrial Estate, Kumedi, Indore, Madhya Pradesh, India - 452010",
                        "https://www.google.com/maps/dir/?api=1&destination=Survey%20No.%209%2F1%2C%20Balaji%20Tusiyana%20Industrial%20Estate%2C%20Kumedi%2C%20Indore%2C%20Madhya%20Pradesh%2C%20India%20-%20452010",
                        "NABL TC-5142", "Director Choksi", "0731-4243888", "info@choksilab.com",
                        "IS 10500 (Water), IS 14543 (Packaged Water), IS 269 (Cement), IS 1786 (Steel)"),

                // --- MAHARASHTRA ---
                new Laboratory("BIS, Western Regional Laboratory", "BIS Regional Laboratory", "Mumbai", "Maharashtra",
                        "Bureau of Indian Standards, Plot No. E9, Road No. 8, M.I.D.C, Andheri East, Mumbai, Maharashtra - 400093",
                        "https://www.google.com/maps/dir/?api=1&destination=Bureau%20of%20Indian%20Standards%2C%20Plot%20No.%20E9%2C%20Road%20No.%208%2C%20M.I.D.C%2C%20Andheri%20East%2C%20Mumbai%2C%20Maharashtra%20-%20400093",
                        "NABL TC-5002", "Shri Chandan Gupta (OIC)", "022-28329295", "wrol@bis.gov.in",
                        "IS/IEC 62368-1, IS 16046 (Batteries), IS 10322 (Luminaires), IS 10500 (Water)"),
                new Laboratory("Umwelt Research Lab Pvt Ltd, Pune", "BIS Recognized Environmental Lab", "Pune", "Maharashtra",
                        "Plot No 20 (Part) D-III Block MIDC Chinchwad, Pune, Maharashtra - 411019",
                        "https://www.google.com/maps/dir/?api=1&destination=Plot%20No%2020%20(Part)%20D-III%20Block%20MIDC%20Chinchwad%2C%20Pune%2C%20Maharashtra%20-%20411019",
                        "NABL TC-7201", "Dr. S. Kulkarni", "020-27471234", "contact@umweltlab.com",
                        "IS 10500 (Drinking Water), IS 14543 (Packaged Water), IS 13428 (Mineral Water)"),
                new Laboratory("Indian Institute of Packaging, Mumbai", "Autonomous Packaging Institute", "Mumbai", "Maharashtra",
                        "Plot E2, MIDC Area, Andheri East, Road No.8, Post Box No. 9432, Mumbai 400093, Maharashtra",
                        "https://www.google.com/maps/dir/?api=1&destination=Plot%20E2%2C%20MIDC%20Area%2C%20Andheri%20East%2C%20Road%20No.8%2C%20Mumbai%20400093",
                        "NABL TC-5088", "Director IIP", "022-28219803", "iip@iip-in.com",
                        "IS 15410 (Packaging Safety), IS 15351 (Woven Sacks), IS 14543 (Bottles)"),
                new Laboratory("SUNREN TELECOM LABORATORY, NAVI MUMBAI", "BIS Recognized Lab", "Navi Mumbai", "Maharashtra",
                        "C-475, MIDC Pavane, Navi Mumbai, Navi Mumbai, Thane, Maharashtra, India - 400705",
                        "https://www.google.com/maps/dir/?api=1&destination=C-475%2C%20MIDC%20Pavane%2C%20Navi%20Mumbai%2C%20Navi%20Mumbai%2C%20Thane%2C%20Maharashtra%2C%20India%20-%20400705",
                        "NABL TC-7139", "Rekha Patel (QM)", "+91 22 24055281", "sunil@sunren.net",
                        "IS 13252 (IT Safety), IS/IEC 62368-1 (ICT), IS 16046 (Batteries)"),

                // --- ODISHA ---
                new Laboratory("Quality Control Division, S. M. Consultants Private Limited, Bhubaneswar", "BIS Recognized Lab", "Bhubaneswar", "Odisha",
                        "S. M. Tower, Plot No.- 130, Mancheswar Industrial Estate, PO.- Rasulgarh, Bhubaneswar, Khordha, Odisha, India - 751010",
                        "https://www.google.com/maps/dir/?api=1&destination=S.%20M.%20Tower%2C%20Plot%20No.%20130%2C%20Mancheswar%20Industrial%20Estate%2C%20PO.%20Rasulgarh%2C%20Bhubaneswar%2C%20Khordha%2C%20Odisha%2C%20India%20-%20751010",
                        "NABL TC-5137", "Mr. Suvendu Mohanty (QM)", "+91 8908215859", "md@smcindia.com",
                        "IS 1786 (Steel Rebars), IS 269 (Cement), IS 383 (Aggregates), IS 456 (Concrete)"),
                new Laboratory("Modern Test Center, Berhampur", "BIS Recognized Lab", "Berhampur", "Odisha",
                        "3rd lane Neelanchal Nagar, Berhampur, Ganjam, Odisha, India - 760010",
                        "https://www.google.com/maps/dir/?api=1&destination=3rd%20lane%20Neelanchal%20Nagar%2C%20Berhampur%2C%20Ganjam%2C%20Odisha%2C%20India%20-%20760010",
                        "NABL TC-5123", "Lab Incharge", "+91 9437358552", "moderntestcenter@gmail.com",
                        "IS 10500 (Drinking Water), IS 14543 (Packaged Water)"),
                new Laboratory("CENTRAL INSTITUTE OF PETROCHEMICALS ENGINEERING & TECHNOLOGY (CIPET) : IPT-BHUBANESWAR", "Autonomous Central Institute", "Bhubaneswar", "Odisha",
                        "B/25, C.N.I complex, Patia, Bhubaneswar, BHUBANESWAR, Khordha, Odisha, India - 751024",
                        "https://www.google.com/maps/dir/?api=1&destination=B%2F25%2C%20C.N.I%20complex%2C%20Patia%2C%20Bhubaneswar%2C%20BHUBANESWAR%2C%20Khordha%2C%20Odisha%2C%20India%20-%20751024",
                        "NABL TC-5102", "Dr. Bishnu Prasasd Panda (TM)", "+91 7566177001", "cipetbbsr@gmail.com",
                        "IS 4984 (HDPE), IS 4985 (PVC), IS 15298 (Footwear)"),

                // --- PUNJAB ---
                new Laboratory("BIS, Northern Regional Laboratory", "BIS Regional Laboratory", "Mohali", "Punjab",
                        "B-69, Industrial Focal Point, Phase VII, Mohali, S.A.S Nagar, Punjab, India - 160059",
                        "https://www.google.com/maps/dir/?api=1&destination=B-69%2C%20Industrial%20Focal%20Point%2C%20Phase%20VII%2C%20Mohali%2C%20Punjab%2C%20India%20-%20160059",
                        "NABL TC-5004", "Ms. Sangeeta Choudhary (OIC)", "0172-4802676", "nrolsample@bis.gov.in",
                        "IS 9873 (Safety of Toys), IS 1786 (Steel Rebars), IS 10500 (Drinking Water), IS 269 (Cement)"),
                new Laboratory("JBS Testing Solutions Pvt Ltd, Jalandhar", "BIS Recognized Lab", "Jalandhar", "Punjab",
                        "Plot No. 27, Street No. 2, adjoining Focal Point Road, Transport Nagar, Jalandhar, Punjab, India - 144004",
                        "https://www.google.com/maps/dir/?api=1&destination=Plot%20No.%2027%2C%20Street%20No.%202%2C%20adjoining%20Focal%20Point%20Road%2C%20Transport%20Nagar%2C%20Jalandhar%2C%20Punjab%2C%20India%20-%20144004",
                        "NABL TC-9201", "Director JBS", "0181-5002030", "info@jbstesting.com",
                        "IS 778 (Gunmetal Valves), IS 14846 (Sluice Valves), IS 1786 (Steel)"),
                new Laboratory("NSIC Technical Services Centre, Rajpura", "Govt Technical Services Centre", "Rajpura", "Punjab",
                        "D/82-83, Focal Point, Rajpura, Patiala, Punjab, India - 140401",
                        "https://www.google.com/maps/dir/?api=1&destination=D%2F82-83%2C%20Focal%20Point%2C%20Rajpura%2C%20Patiala%2C%20Punjab%2C%20India%20-%20140401",
                        "NABL TC-6045", "General Manager", "01762-232644", "ntscrajpura@nsic.co.in",
                        "IS 9079 (Electric Monoset Pumps), IS 996 (Single Phase Motors), IS 12615 (Motors)"),

                // --- RAJASTHAN ---
                new Laboratory("National Test House (NWR), Jaipur", "Govt Recognized Lab", "Jaipur", "Rajasthan",
                        "E 763, Road No. 9F1, VKI Area, Jaipur, Rajasthan, India - 302013",
                        "https://www.google.com/maps/dir/?api=1&destination=E%20763%2C%20Road%20No.%209F1%2C%20VKI%20Area%2C%20Jaipur%2C%20Rajasthan%2C%20India%20-%20302013",
                        "NABL TC-5015", "Director NTH", "0141-2331200", "nthjaipur@nic.in",
                        "IS 10500 (Water), IS 1786 (Steel), IS 269 (Cement), IS 694 (Cables)"),
                new Laboratory("CEG Test House & Research Centre, Jaipur", "BIS Recognized Material Lab", "Jaipur", "Rajasthan",
                        "B-11(G), Basement Floor, Ground Floor & Fourth Floor, Malviya Industrial Area, Malviya Nagar, Jaipur, Rajasthan, India - 302017",
                        "https://www.google.com/maps/dir/?api=1&destination=B-11(G)%2C%20Malviya%20Industrial%20Area%2C%20Malviya%20Nagar%2C%20Jaipur%2C%20Rajasthan%2C%20India%20-%20302017",
                        "NABL TC-6188", "Chief Quality Officer", "0141-4044590", "info@cegtesthouse.com",
                        "IS 1786 (TMT Bars), IS 269 (OPC Cement), IS 456 (Plain and Reinforced Concrete)"),

                // --- TAMIL NADU ---
                new Laboratory("BIS, Southern Regional Laboratory", "BIS Regional Laboratory", "Chennai", "Tamil Nadu",
                        "IV Cross Road, CIT Campus, Taramani, Chennai-600113, Tamil Nadu",
                        "https://www.google.com/maps/dir/?api=1&destination=IV%20Cross%20Road%2C%20CIT%20Campus%2C%20Taramani%2C%20Chennai-600113%2C%20Tamil%20Nadu",
                        "NABL TC-5003", "Dr. Surya Kalyani Sreekanthan", "044-22541208", "srol@bis.gov.in",
                        "IS 17017 (EV Charging), IS 16242 (UPS Systems), IS 14543 (Packaged Water), IS 10500 (Water)"),
                new Laboratory("CSIR-Central Leather Research Institute, Chennai", "CSIR National Laboratory", "Chennai", "Tamil Nadu",
                        "Sardar Patel Road, Adyar, Opposite to IIT, Chennai, Tamil Nadu 600020",
                        "https://www.google.com/maps/dir/?api=1&destination=Sardar%20Patel%20Road%2C%20Adyar%2C%20Opposite%20to%20IIT%2C%20Chennai%2C%20Tamil%20Nadu%20600020",
                        "NABL TC-5034", "Head Testing Services", "044-24911386", "director@clri.res.in",
                        "IS 15298 (Part 2: Safety Footwear), IS 15298 (Part 3: Protective Footwear), IS 15298 (Part 4: Occupational Footwear)"),
                new Laboratory("NSIC Technical Services Centre, Chennai", "Govt Technical Services Centre", "Chennai", "Tamil Nadu",
                        "Sector B-24, Guindy Industrial Estate, Ekkaduthangal, Chennai-600032",
                        "https://www.google.com/maps/dir/?api=1&destination=Sector%20B-24%2C%20Guindy%20Industrial%20Estate%2C%20Ekkaduthangal%2C%20Chennai-600032",
                        "NABL TC-5155", "Centre Head", "044-22254500", "ntscchennai@nsic.co.in",
                        "IS 9079 (Monoset Pumps), IS 12615 (Motors), IS 694 (Wires)"),
                new Laboratory("CVR LABS PRIVATE LIMITED, CHENNAI", "BIS Recognized Lab", "Chennai", "Tamil Nadu",
                        "Dignity Centre, II Floor No 21 Abdulrazack Street, Saidapet, Chennai, Chennai, Tamil Nadu, India - 600015",
                        "https://www.google.com/maps/dir/?api=1&destination=Dignity%20Centre%2C%20II%20Floor%20No%2021%20Abdulrazack%20Street%2C%20Saidapet%2C%20Chennai%2C%20Chennai%2C%20Tamil%20Nadu%2C%20India%20-%20600015",
                        "NABL TC-6133", "S ParveenBanu (TM)", "+91 9500121387", "bala@cvrlabs.com",
                        "IS 10500 (Water), IS 14543 (Packaged Drinking Water)"),

                // --- TELANGANA ---
                new Laboratory("BIS, Hyderabad Branch Laboratory", "BIS Branch Laboratory", "Hyderabad", "Telangana",
                        "Bureau of Indian Standards, Hyderabad Branch Laboratory, Plot No. 1, Sy. No. 367/1, Moula Ali, Hyderabad - 500040",
                        "https://www.google.com/maps/dir/?api=1&destination=Bureau%20of%20Indian%20Standards%2C%20Hyderabad%20Branch%20Laboratory%2C%20Plot%20No.%201%2C%20Sy.%20No.%20367%2F1%2C%20Moula%20Ali%2C%20Hyderabad%20-%20500040",
                        "NABL TC-5007", "MAJ Vinod", "+91 9952993252", "hybl@bis.gov.in",
                        "IS 10500 (Drinking Water), IS 14543 (Packaged Water), IS 1786 (Steel Rebars), IS 1293 (Plugs)"),
                new Laboratory("Intertek India Private Limited (Food Services), Hyderabad", "BIS Recognized Testing Lab", "Hyderabad", "Telangana",
                        "Plot No D-53, IDA, Phase-1, Jeedimetla, Qutubullapur Mandal, Hyderabad, Medchal Malkajgiri, Telangana, India - 500055",
                        "https://www.google.com/maps/dir/?api=1&destination=Plot%20No%20D-53%2C%20IDA%2C%20Phase-1%2C%20Jeedimetla%2C%20Qutubullapur%20Mandal%2C%20Hyderabad%2C%20Telangana%2C%20India%20-%20500055",
                        "NABL TC-6126", "Gandla Krishnaiah", "+91 9912463921", "gandla.krishnaiah@intertek.com",
                        "IS 10500 (Water), IS 14543 (Packaged Drinking Water), IS 13428 (Packaged Natural Mineral Water)"),

                // --- UTTAR PRADESH ---
                new Laboratory("BIS, Central Laboratory", "BIS Central Apex Laboratory", "Ghaziabad", "Uttar Pradesh",
                        "20/9, Site 4, Sahibabad Industrial Area, Sahibabad, Ghaziabad, Uttar Pradesh, India - 201010",
                        "https://www.google.com/maps/dir/?api=1&destination=20%2F9%2C%20Site%204%2C%20Sahibabad%20Industrial%20Area%2C%20Sahibabad%2C%20Ghaziabad%2C%20Uttar%20Pradesh%2C%20India%20-%20201010",
                        "NABL TC-5001", "Mukund Madhav Mishra (OIC)", "0120-2811989", "sample@bis.gov.in",
                        "IS 4151 (Helmets), IS 10500 (Water), IS 1293 (Plugs), IS 694 (Wires), IS 1786 (Steel), IS 15298 (Footwear)"),
                new Laboratory("National Test House (NR), Ghaziabad", "Govt Central Test House", "Ghaziabad", "Uttar Pradesh",
                        "Kamla Nehru Nagar, Ghaziabad, Uttar Pradesh, India - 201002",
                        "https://www.google.com/maps/dir/?api=1&destination=Kamla%20Nehru%20Nagar%2C%20Ghaziabad%2C%20Uttar%20Pradesh%2C%20India%20-%20201002",
                        "NABL TC-5012", "Director NTH", "0120-2789851", "nthnr-ca@nic.in",
                        "IS 10500 (Water), IS 1786 (Steel), IS 269 (Cement), IS 4151 (Helmets), IS 9873 (Toys)"),
                new Laboratory("Testtex India Laboratories Private Limited, Noida", "BIS Recognized Private Lab", "Noida", "Uttar Pradesh",
                        "C-57, Sector-65, Noida, Gautam Buddha Nagar, Uttar Pradesh, India - 201301",
                        "https://www.google.com/maps/dir/?api=1&destination=C-57%2C%20Sector-65%2C%20Noida%2C%20Gautam%20Buddha%20Nagar%2C%20Uttar%20Pradesh%2C%20India%20-%20201301",
                        "NABL TC-8138", "Amit Tiwari (QM)", "+91 7303919463", "labsindianoida@testtex.com",
                        "IS 15298 (Footwear), IS 17594 (Surgical & Medical Textiles), IS 9873 (Toys Safety)"),
                new Laboratory("CIPET, Lucknow", "Autonomous Central Institute", "Lucknow", "Uttar Pradesh",
                        "B-27, Amausi Industrial Area, Nadarganj, Lucknow, Uttar Pradesh, India - 226008",
                        "https://www.google.com/maps/dir/?api=1&destination=B-27%2C%20Amausi%20Industrial%20Area%2C%20Nadarganj%2C%20Lucknow%2C%20Uttar%20Pradesh%2C%20India%20-%20226008",
                        "NABL TC-5125", "Principal Director", "0522-2436227", "lucknow@cipet.gov.in",
                        "IS 4984 (HDPE), IS 4985 (PVC Pipes), IS 12786 (Irrigation), IS 15298 (Footwear)"),
                new Laboratory("FOOTWEAR DESIGN AND DEVELOPMENT INSTITUTE (FDDI), NOIDA", "Institute of National Importance Lab", "Noida", "Uttar Pradesh",
                        "A-10/A GAUTAM BUDDTH NAGAR SECTOR 24 NOIDA, NOIDA, Gautam Buddha Nagar, Uttar Pradesh, India - 201301",
                        "https://www.google.com/maps/dir/?api=1&destination=A-10%2FA%20GAUTAM%20BUDDTH%20NAGAR%20SECTOR%2024%20NOIDA%2C%20NOIDA%2C%20Gautam%20Buddha%20Nagar%2C%20Uttar%20Pradesh%2C%20India%20-%20201301",
                        "NABL TC-8112", "Mr. Rajesh Mishra", "+91 9718303177", "rajeshmishra@fddiindia.com",
                        "IS 15298 (Part 2: Safety Shoes), IS 15298 (Part 3: Protective Footwear), IS 15298 (Part 4: Occupational Footwear)"),

                // --- UTTARAKHAND ---
                new Laboratory("Central Institute of Petrochemicals Engineering & Technology (CIPET), CSTS - Dehradun", "Autonomous Central Institute", "Dehradun", "Uttarakhand",
                        "CIPET : Centre for Skilling and Technical Support (CSTS), Haridwar Road, Post-Bhaniyawala, Doiwala, Dehradun, Dehradun, Dehradun, Uttarakhand, India - 248140",
                        "https://www.google.com/maps/dir/?api=1&destination=CIPET%20%3A%20Centre%20for%20Skilling%20and%20Technical%20Support%20(CSTS)%2C%20Haridwar%20Road%2C%20Post-Bhaniyawala%2C%20Doiwala%2C%20Dehradun%2C%20Dehradun%2C%20Dehradun%2C%20Uttarakhand%2C%20India%20-%20248140",
                        "NABL TC-9178", "Dr. Pratap Chandra Padhi", "+91 9437111344", "testing.cipetdehradun@gmail.com",
                        "IS 4984 (HDPE), IS 4985 (PVC Pipes), IS 12786 (Irrigation Pipes)"),
                new Laboratory("Vijai Electricals Ltd., Transformer Testing Laboratory, Haridwar", "BIS Recognized High Voltage Lab", "Haridwar", "Uttarakhand",
                        "Vijai Electricals Ltd., Plot no. 1A, Sector 12, IIE, SIDCUL, Haridwar, Haridwar, Uttarakhand, India - 249403",
                        "https://www.google.com/maps/dir/?api=1&destination=Vijai%20Electricals%20Ltd.%2C%20Plot%20no.%201A%2C%20Sector%2012%2C%20IIE%2C%20SIDCUL%2C%20Haridwar%2C%20Haridwar%2C%20Uttarakhand%2C%20India%20-%20249403",
                        "NABL TC-9171", "Surendra Kumar Sharma (QM)", "+91 40 67617777", "surendra.sharma@vijai.co.in",
                        "IS 2026 (Power Transformers), IS 1180 (Distribution Transformers)"),
                new Laboratory("Standard Testing and Research Lab, Haridwar", "BIS Recognized Lab", "Haridwar", "Uttarakhand",
                        "Khasra No. 07, Sultanpur Majari, Shubham Market, Sidcul Road, Bahadrabad, Haridwar, Uttarakhand, India - 249402",
                        "https://www.google.com/maps/dir/?api=1&destination=Khasra%20No.%2007%2C%20Sultanpur%20Majari%2C%20Shubham%20Market%2C%20Sidcul%20Road%2C%20Bahadrabad%2C%20Haridwar%2C%20Uttarakhand%2C%20India%20-%20249402",
                        "NABL TC-9197", "Ashok Gaur (QM)", "+91 9013008458", "standardtestingandresearchlab@gmail.com",
                        "IS 10500 (Water), IS 14543 (Packaged Drinking Water)"),

                // --- WEST BENGAL ---
                new Laboratory("BIS, Eastern Regional Laboratory", "BIS Regional Laboratory", "Kolkata", "West Bengal",
                        "Annex Building & Sample-Cell: Bureau of Indian Standards, P-230, CIT Scheme VII-M, Block-W, Kankurgachi, Kolkata-700054; Main Building: 1/14, CIT Scheme VII M, VIP Road, Kankurgachi, Kolkata-700054",
                        "https://www.google.com/maps/dir/?api=1&destination=Bureau%20of%20Indian%20Standards%2C%20P-230%2C%20CIT%20Scheme%20VII-M%2C%20Block-W%2C%20Kankurgachi%2C%20Kolkata%2C%20West%20Bengal%20700054",
                        "NABL TC-5005", "Tarique Sajjad (OIC)", "033-23208561", "sample.erol@bis.gov.in",
                        "IS 694 (Cables), IS 1293 (Plugs), IS 1786 (TMT Bars), IS 269 (Cement), IS 10500 (Water)"),
                new Laboratory("National Test House, Alipore, Kolkata", "Govt Central Testing House", "Kolkata", "West Bengal",
                        "11/1 Judges Court Road, Kolkata, West Bengal, India - 700027",
                        "https://www.google.com/maps/dir/?api=1&destination=11%2F1%20Judges%20Court%20Road%2C%20Kolkata%2C%20West%20Bengal%2C%20India%20-%20700027",
                        "NABL TC-5020", "Director NTH (ER)", "033-24791221", "nthcal-wb@nic.in",
                        "IS 10500 (Water), IS 1786 (Steel), IS 269 (Cement), IS 15298 (Footwear), IS 9873 (Toys)")
        ));
    }

    private void seedBISServices() {
        serviceRepository.saveAll(List.of(
                new BISService("STANDARDS-FORMULATION", "Indian Standards Formulation & Public Review", "Standards",
                        "Participate in Sectional Committees (LITD, TED, FAD, CED) to draft and revise Indian Standards.",
                        "All Stakeholders, Industry Bodies, Academicians",
                        "Access wide circulation draft standards, submit technical comments via Know Your Standards portal.",
                        "https://standardsbis.bsbedge.com"),
                new BISService("CRS-MANAKONLINE", "Compulsory Registration Scheme (CRS) e-Portal", "Certification",
                        "End-to-end paperless registration for 60+ notified Electronics, IT, and Solar product categories.",
                        "Electronics OEMs, Importers, Authorized Indian Representatives (AIR)",
                        "Online application, test report verification, grant of R-Number standard mark.",
                        "https://www.crsbis.in"),
                new BISService("MSME-CONCESSION", "MSME & Startup Certification Fee Rebate", "Financial Subsidy",
                        "Special financial concessions on testing and marking fees for registered Micro, Small, and Medium Enterprises.",
                        "Udyam-registered MSMEs and DPIIT-recognized Startups",
                        "20% to 80% fee concessions on product certification and laboratory testing charges.",
                        "https://manakonline.in"),
                new BISService("BIS-CARE", "BIS CARE Mobile App & Public Verification Portal", "Consumer Affairs",
                        "Verify authenticity of ISI mark, CRS registration R-Number, and 6-digit HUID gold hallmarking.",
                        "General Public, Consumers, Quality Auditors",
                        "Verify CML license validity, scan HUID codes, report counterfeit products or misuse of ISI marks.",
                        "https://bis.gov.in")
        ));
    }

    private void seedHallmarkingInfo() {
        hallmarkingRepository.save(new HallmarkingInformation(
                "Mandatory 6-Digit HUID Gold Hallmarking Rules",
                "IS 1417 : 2016",
                6,
                "Hallmarking is the accurate determination and official recording of the proportionate content of precious metal in gold and silver articles. In India, each hallmarked piece of jewellery is inscribed with a unique 6-digit alphanumeric code known as Hallmark Unique Identification (HUID).",
                "Gold (14K, 18K, 20K, 22K, 23K, 24K) and Silver (900, 925, 990)",
                "Automatic jeweller registration on Manakonline with zero government fee for micro-enterprises.",
                "Open BIS CARE mobile application -> Select 'Verify HUID' -> Enter 6-digit alphanumeric code -> View jeweller name, AHC centre, hallmarking date, and purity grade."
        ));
    }

    private void seedBISUpdates() {
        updateRepository.saveAll(List.of(
                new BISUpdate("Electronics & IT Goods Mandatory CRS Order Update 2026", "10 March 2026",
                        "Mandatory CRS", "Gazette S.O. 1246(E)", "01 September 2026",
                        "Ministry of Electronics and Information Technology (MeitY) notified unified safety requirements under IS/IEC 62368-1:2023.",
                        "IS/IEC 62368-1:2023, IS 13252 (Part 1)", "https://www.crsbis.in"),
                new BISUpdate("Quality Control Order for Footwear & Leather Products", "15 January 2026",
                        "Mandatory QCO", "DPIIT QCO-2026/04", "01 July 2026",
                        "Mandatory ISI Mark certification enforced for protective and industrial safety footwear under IS 15298.",
                        "IS 15298 (Parts 2-4)", "https://manakonline.in"),
                new BISUpdate("Revision of Mandatory Helmet Quality Standards Enforcement", "02 February 2026",
                        "Enforcement Advisory", "MoRTH S.O. 450(E)", "Immediate",
                        "Police and transport authorities instructed to penalize non-ISI marked two-wheeler helmets under Section 16 of the BIS Act, 2016.",
                        "IS 4151:2015", "https://bis.gov.in")
        ));
    }

    private void seedKnowledgeChunks() {
        knowledgeChunkRepository.saveAll(List.of(
                new KnowledgeChunk("IS/IEC 62368-1:2023",
                        "Audio/Video, ICT equipment safety principles classify energy sources into Class 1 (safe), Class 2 (injury possible), and Class 3 (serious injury/fire hazard). All accessible parts must prevent contact with Class 2/3 sources.",
                        "Energy Source Classification", "Clause 4", "4.1", 12, "https://manakonline.in"),
                new KnowledgeChunk("IS 4151:2015",
                        "Motorcycle protective helmet impact attenuation requires peak acceleration to remain below 300g when dropped at 7.5 m/s onto flat and hemispherical steel anvils.",
                        "Impact Attenuation Requirements", "Section 7", "7.1", 8, "https://manakonline.in"),
                new KnowledgeChunk("IS 16046:2018",
                        "Lithium secondary cells must withstand thermal abuse at 130°C for 10 minutes without fire or explosion, and external short circuit at 55°C.",
                        "Lithium Cell Thermal Safety", "Clause 7.3", "7.3.2", 15, "https://www.crsbis.in")
        ));
    }
}
