// BIS Intelligent Assistant - Full Stack API Service
// Connects React Frontend to Java Spring Boot Backend (http://localhost:8080) with PostgreSQL Database

const BACKEND_BASE_URL = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080';

/**
 * Send a chat query to the Java Spring Boot backend.
 * Spring Boot securely handles domain grounding and Google Gemini API communication.
 *
 * @param {string} message - User query
 * @param {string} language - Target language ('en', 'hi', 'te')
 * @param {Array} history - Previous message history
 * @param {string} sessionId - Optional conversation session identifier
 * @returns {Promise<Object>} Formatted response object
 */
export async function sendChatMessage(message, language = 'en', history = [], sessionId = null) {
  try {
    const response = await fetch(`${BACKEND_BASE_URL}/api/chat`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        message: message.trim(),
        language: language || 'en',
        history: history || [],
        sessionId: sessionId || undefined
      })
    });

    const data = await response.json();

    if (response.ok && data.success) {
      return {
        success: true,
        answer: data.answer,
        model: data.model || 'Google Gemini (Spring Boot)',
        sources: data.sources || [],
        sourceReference: data.sourceReference,
        suggestedFollowUps: data.suggestedFollowUps
      };
    } else {
      return {
        success: false,
        error: data.error || `Server responded with status ${response.status}`,
        status: response.status
      };
    }
  } catch (err) {
    console.warn("Backend API request failed:", err.message);
    return {
      success: false,
      error: "Unable to connect to Java Spring Boot backend. Please ensure the backend is running on http://localhost:8080.",
      networkError: true
    };
  }
}

/**
 * Check health, PostgreSQL database, and Gemini configuration status of Spring Boot backend.
 * @returns {Promise<Object>}
 */
export async function checkBackendHealth() {
  try {
    const response = await fetch(`${BACKEND_BASE_URL}/api/health`, {
      method: 'GET',
      headers: { 'Accept': 'application/json' }
    });
    if (response.ok) {
      const data = await response.json();
      return { ok: true, ...data };
    }
    return { ok: false, status: response.status };
  } catch (err) {
    return { ok: false, error: err.message };
  }
}

/**
 * Test Gemini connectivity via backend.
 * @returns {Promise<Object>}
 */
export async function testBackendGemini() {
  try {
    const response = await fetch(`${BACKEND_BASE_URL}/api/test-gemini`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    });
    if (response.ok) {
      const data = await response.json();
      return { ok: data.connected, message: data.message, model: data.model };
    }
    return { ok: false, message: `HTTP ${response.status}` };
  } catch (err) {
    return { ok: false, message: err.message };
  }
}

// ==========================================
// Database-Backed Catalog APIs
// ==========================================

export async function fetchStandards() {
  try {
    const res = await fetch(`${BACKEND_BASE_URL}/api/standards`);
    if (res.ok) return await res.json();
    return [];
  } catch (e) {
    console.warn("Failed to fetch standards from database:", e);
    return [];
  }
}

export async function searchStandards(query) {
  try {
    const res = await fetch(`${BACKEND_BASE_URL}/api/standards/search?query=${encodeURIComponent(query || '')}`);
    if (res.ok) return await res.json();
    return [];
  } catch (e) {
    console.warn("Failed to search standards in database:", e);
    return [];
  }
}

export async function fetchLaboratories() {
  try {
    const res = await fetch(`${BACKEND_BASE_URL}/api/laboratories`);
    if (res.ok) return await res.json();
    return [];
  } catch (e) {
    console.warn("Failed to fetch laboratories from database:", e);
    return [];
  }
}

export async function fetchServices() {
  try {
    const res = await fetch(`${BACKEND_BASE_URL}/api/services`);
    if (res.ok) return await res.json();
    return [];
  } catch (e) {
    console.warn("Failed to fetch services from database:", e);
    return [];
  }
}

export async function fetchCertificationSchemes() {
  try {
    const res = await fetch(`${BACKEND_BASE_URL}/api/certification-schemes`);
    if (res.ok) return await res.json();
    return [];
  } catch (e) {
    console.warn("Failed to fetch certification schemes from database:", e);
    return [];
  }
}

export async function fetchUpdates() {
  try {
    const res = await fetch(`${BACKEND_BASE_URL}/api/updates`);
    if (res.ok) return await res.json();
    return [];
  } catch (e) {
    console.warn("Failed to fetch updates from database:", e);
    return [];
  }
}

export async function fetchConversations() {
  try {
    const res = await fetch(`${BACKEND_BASE_URL}/api/conversations`);
    if (res.ok) return await res.json();
    return [];
  } catch (e) {
    console.warn("Failed to fetch conversations from database:", e);
    return [];
  }
}

export async function fetchConversationById(sessionId) {
  try {
    const res = await fetch(`${BACKEND_BASE_URL}/api/conversations/${encodeURIComponent(sessionId)}`);
    if (res.ok) return await res.json();
    return null;
  } catch (e) {
    console.warn("Failed to fetch conversation by id from database:", e);
    return null;
  }
}

export async function deleteConversation(sessionId) {
  try {
    const res = await fetch(`${BACKEND_BASE_URL}/api/conversations/${encodeURIComponent(sessionId)}`, {
      method: 'DELETE'
    });
    if (res.ok) return await res.json();
    return { deleted: false };
  } catch (e) {
    console.warn("Failed to delete conversation in database:", e);
    return { deleted: false };
  }
}
