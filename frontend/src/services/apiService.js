// BIS Intelligent Assistant - Full Stack API Service
// Connects React Frontend to Java Spring Boot Backend with PostgreSQL Database
// Handles JWT Authorization headers and 401 token expiration handling

import { getStoredToken, clearStoredAuth } from './authService';

const RAW_BACKEND_URL = import.meta.env.VITE_API_BASE_URL || import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080';
const BACKEND_BASE_URL = RAW_BACKEND_URL.replace(/\/+$/, '');

/**
 * Helper to build headers with Authorization Bearer token for protected endpoints.
 */
function getAuthHeaders(additionalHeaders = {}) {
  const headers = { ...additionalHeaders };
  const token = getStoredToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

/**
 * Handle HTTP 401 Unauthorized responses by clearing auth storage and broadcasting an event.
 */
function handleUnauthorized() {
  clearStoredAuth();
  if (typeof window !== 'undefined') {
    window.dispatchEvent(new CustomEvent('bis-auth-unauthorized'));
  }
}

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
      headers: getAuthHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }),
      body: JSON.stringify({
        message: message.trim(),
        language: language || 'en',
        history: history || [],
        sessionId: sessionId || undefined
      })
    });

    if (response.status === 401) {
      handleUnauthorized();
      return {
        success: false,
        error: "Session expired or authentication required. Please log in again.",
        isAuthError: true,
        status: 401
      };
    }

    const data = await response.json().catch(() => ({}));

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
      error: "Unable to connect to the BIS backend service. Please verify your internet connection or check server availability.",
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
// Database-Backed Catalog APIs (Public)
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

// ==========================================
// Protected Conversation APIs (Requires JWT)
// ==========================================

export async function fetchConversations() {
  try {
    const token = getStoredToken();
    if (!token) return [];

    const res = await fetch(`${BACKEND_BASE_URL}/api/conversations`, {
      headers: getAuthHeaders({ 'Accept': 'application/json' })
    });
    if (res.status === 401) {
      handleUnauthorized();
      return [];
    }
    if (res.ok) return await res.json();
    return [];
  } catch (e) {
    console.warn("Failed to fetch conversations from database:", e);
    return [];
  }
}

export async function fetchConversationById(sessionId) {
  try {
    const token = getStoredToken();
    if (!token) return null;

    const res = await fetch(`${BACKEND_BASE_URL}/api/conversations/${encodeURIComponent(sessionId)}`, {
      headers: getAuthHeaders({ 'Accept': 'application/json' })
    });
    if (res.status === 401) {
      handleUnauthorized();
      return null;
    }
    if (res.ok) return await res.json();
    return null;
  } catch (e) {
    console.warn("Failed to fetch conversation by id from database:", e);
    return null;
  }
}

export async function deleteConversation(sessionId) {
  try {
    const token = getStoredToken();
    if (!token) return { deleted: false };

    const res = await fetch(`${BACKEND_BASE_URL}/api/conversations/${encodeURIComponent(sessionId)}`, {
      method: 'DELETE',
      headers: getAuthHeaders({ 'Accept': 'application/json' })
    });
    if (res.status === 401) {
      handleUnauthorized();
      return { deleted: false };
    }
    if (res.ok) return await res.json();
    return { deleted: false };
  } catch (e) {
    console.warn("Failed to delete conversation in database:", e);
    return { deleted: false };
  }
}
