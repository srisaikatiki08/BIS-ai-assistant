import { STANDARDS_DATABASE } from '../data/standardsData';
import { sendChatMessage } from './apiService';

// Match standard based on query keywords for quick metadata lookup
export function findMatchingStandard(query) {
  if (!query) return null;
  const q = query.toLowerCase();
  
  if (q.includes("earphone") || q.includes("earphones") || q.includes("headphone") || q.includes("headphones") || q.includes("tws") || q.includes("earbud") || q.includes("earbuds") || q.includes("neckband")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-IEC-62368-1-2023") || STANDARDS_DATABASE[0];
  }
  if (q.includes("62368") || q.includes("laptop") || q.includes("cctv") || q.includes("smart watch") || q.includes("smart speaker") || q.includes("bluetooth speaker")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-IEC-62368-1-2023") || STANDARDS_DATABASE[0];
  }
  if (q.includes("ups") || q.includes("inverter") || q.includes("16242")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-16242-2014") || null;
  }
  if (q.includes("dimmer") || q.includes("switch") || q.includes("60669")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-60669-2-1") || null;
  }
  if (q.includes("tv") || q.includes("television") || q.includes("satellite") || q.includes("18112")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-18112-2022") || null;
  }
  if (q.includes("helmet") || q.includes("4151") || q.includes("two wheeler") || q.includes("हेलमेट") || q.includes("హెల్మెట్")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-4151-2015");
  }
  if (q.includes("water") || q.includes("drinking") || q.includes("10500") || q.includes("14543") || q.includes("पानी") || q.includes("నీరు")) {
    return q.includes("packaged") || q.includes("bottle") || q.includes("बोतल") || q.includes("ప్యాకేజ్డ్")
      ? STANDARDS_DATABASE.find(s => s.id === "IS-14543-2016")
      : STANDARDS_DATABASE.find(s => s.id === "IS-10500-2012");
  }
  if (q.includes("battery") || q.includes("lithium") || q.includes("16046") || q.includes("cell") || q.includes("बैटरी") || q.includes("బ్యాటరీ")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-16046-2018");
  }
  if (q.includes("plug") || q.includes("socket") || q.includes("1293") || q.includes("plugs") || q.includes("प्लग") || q.includes("సాకెట్")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-1293-2019");
  }
  if (q.includes("shoe") || q.includes("footwear") || q.includes("15298") || q.includes("boot") || q.includes("जूते") || q.includes("పాదరక్షలు")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-15298-2016");
  }
  if (q.includes("toy") || q.includes("toys") || q.includes("9873") || q.includes("खिलौने") || q.includes("బొమ్మలు")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-9873-2019");
  }
  if (q.includes("gold") || q.includes("hallmark") || q.includes("huid") || q.includes("jewel") || q.includes("1417") || q.includes("सोना") || q.includes("హాల్‌మార్క్")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-1417-2016");
  }
  if (q.includes("type-c") || q.includes("usb") || q.includes("charger") || q.includes("चार्जर") || q.includes("ఛార్జర్")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-18112-2022");
  }
  if (q.includes("steel") || q.includes("tmt") || q.includes("rebar") || q.includes("1786") || q.includes("स्टील") || q.includes("స్టీల్")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-1786-2008");
  }
  if (q.includes("led") || q.includes("bulb") || q.includes("light") || q.includes("16102") || q.includes("10322") || q.includes("एलईडी") || q.includes("లైట్లు")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-10322-2013") || STANDARDS_DATABASE.find(s => s.id === "IS-16102-2012");
  }
  if (q.includes("wire") || q.includes("cable") || q.includes("694") || q.includes("तार") || q.includes("కేబుల్")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-694-2010");
  }
  if (q.includes("mask") || q.includes("surgical") || q.includes("17594") || q.includes("मास्क")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-17594-2021");
  }
  if (q.includes("ev") || q.includes("17017")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-17017-2018");
  }
  if (q.includes("security") || q.includes("27001") || q.includes("isms")) {
    return STANDARDS_DATABASE.find(s => s.id === "IS-ISO-IEC-27001");
  }
  
  return (STANDARDS_DATABASE || []).find(s => s?.category && q.includes(s.category.toLowerCase())) || null;
}

/**
 * Intelligent AI query response generator.
 * Sends queries exclusively to the Spring Boot REST backend which invokes Google Gemini.
 * Never generates fake hardcoded chatbot answers.
 */
export async function queryAiAssistantAsync(userQuery, language = 'en', history = [], sessionId = null) {
  try {
    const matchedStandard = findMatchingStandard(userQuery);

    // Call Java Spring Boot Backend (POST /api/chat -> Google Gemini API)
    const backendResult = await sendChatMessage(userQuery, language, history, sessionId);

    if (backendResult && backendResult.success && backendResult.answer) {
      return {
        id: "msg-" + Date.now(),
        role: "assistant",
        content: backendResult.answer,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        category: matchedStandard ? matchedStandard.category : "BIS Standards Advisory",
        modelBadge: backendResult.model || "Google Gemini (Spring Boot)",
        matchedStandard: matchedStandard,
        sourceReference: backendResult.sourceReference || (matchedStandard ? {
          document: `${matchedStandard.isNumber} & Official BIS Gazette`,
          clause: matchedStandard.keyClauses?.[0]?.clause ? `Clause ${matchedStandard.keyClauses[0].clause}` : "Quality Control Order",
          disclaimer: "AI-generated output backed by Bureau of Indian Standards regulatory repository. Verify for official certification submissions."
        } : null),
        suggestedFollowUps: backendResult.suggestedFollowUps || [
          "What are the laboratory testing fees and timeline?",
          "Which documents are required for MSME fee concession?",
          "Find accredited testing laboratories nearby",
          "Is this standard under a mandatory QCO in 2026?"
        ]
      };
    } else {
      // Clear, honest error message when Gemini or backend is unavailable
      const errorMsg = backendResult?.error || "AI service is not configured. Please ensure the Spring Boot backend is running and the GEMINI_API_KEY environment variable is configured on the server.";
      return {
        id: "msg-err-" + Date.now(),
        role: "assistant",
        content: errorMsg,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        category: "System Status",
        modelBadge: "AI Service Offline",
        sourceReference: {
          document: "System Configuration",
          clause: "Environment Variable Check",
          disclaimer: "AI service requires a valid GEMINI_API_KEY on the Spring Boot server."
        },
        suggestedFollowUps: [
          "Check Spring Boot backend status and health endpoint",
          "Configure GEMINI_API_KEY on the server"
        ]
      };
    }
  } catch (err) {
    console.error("Critical error in queryAiAssistantAsync:", err);
    return {
      id: "msg-err-" + Date.now(),
      role: "assistant",
      content: "AI service is currently unavailable. Please verify that the BIS backend service is running and accessible.",
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
      category: "Connection Error",
      modelBadge: "Connection Error",
      sourceReference: {
        document: "Backend Connection",
        clause: "Service Gateway",
        disclaimer: "Unable to reach server."
      }
    };
  }
}

// Recommend standard based on product form inputs (for product recommender view)
export function recommendStandardForProduct({ name, description, intendedUse, material, category }) {
  const combined = `${name || ''} ${description || ''} ${intendedUse || ''} ${material || ''} ${category || ''}`.toLowerCase();

  let matched = null;
  let matchScore = 0;
  let reasons = [];

  for (const std of STANDARDS_DATABASE) {
    let score = 0;
    const currentReasons = [];

    if (category && std.category && std.category.toLowerCase().includes(category.toLowerCase())) {
      score += 40;
      currentReasons.push(`Matches product category: ${std.category}`);
    }

    if (name && (std.title.toLowerCase().includes(name.toLowerCase()) || std.scope.toLowerCase().includes(name.toLowerCase()))) {
      score += 35;
      currentReasons.push(`Product name matches standard scope`);
    }

    if (std.applicableProducts) {
      for (const prod of std.applicableProducts) {
        if (combined.includes(prod.toLowerCase()) || prod.toLowerCase().includes(name.toLowerCase())) {
          score += 30;
          currentReasons.push(`Directly covers product item: ${prod}`);
          break;
        }
      }
    }

    if (score > matchScore) {
      matchScore = score;
      matched = std;
      reasons = currentReasons;
    }
  }

  if (!matched || matchScore < 30) {
    matched = STANDARDS_DATABASE[0];
    reasons = ["General electronic/electrical equipment benchmark standard"];
  }

  return {
    standard: matched,
    confidence: matchScore >= 70 ? "High" : matchScore >= 40 ? "Medium" : "General Match",
    reasons: reasons.length > 0 ? reasons : ["Standard safety scope alignment"],
    isMandatory: matched.isMandatory,
    qcoOrder: matched.qcoOrder,
    scheme: matched.scheme,
    nextSteps: [
      "Review testing requirements for product sub-assemblies",
      "Check mandatory QCO compliance deadlines",
      "Identify BIS-recognized testing laboratory",
      "Submit online application via Manakonline"
    ]
  };
}
