// BIS Intelligent Assistant - API Adapter
// Delegates all AI interactions to the Java Spring Boot Backend (POST /api/chat)
import { sendChatMessage, testBackendGemini } from './apiService';

/**
 * Call the Java Spring Boot backend which invokes Google Gemini securely.
 *
 * @param {string} userPrompt - User query
 * @param {string} language - Language code ('en', 'hi', 'te')
 * @param {Array} chatHistory - Conversational history
 * @returns {Promise<Object>} Formatted response
 */
export async function callGeminiApi(userPrompt, language = 'en', chatHistory = []) {
  const result = await sendChatMessage(userPrompt, language, chatHistory);
  if (result.success) {
    return {
      success: true,
      modelUsed: result.model || 'Google Gemini (Spring Boot)',
      text: result.answer
    };
  } else {
    return {
      success: false,
      error: result.error || 'Backend communication failed'
    };
  }
}

/**
 * Test backend and Gemini connection.
 */
export async function testGeminiConnection() {
  const result = await testBackendGemini();
  return {
    ok: result.ok,
    message: result.message || (result.ok ? "Connected to Spring Boot & Gemini" : "Connection failed"),
    error: !result.ok ? (result.message || "Connection failed") : undefined
  };
}

export function getActiveApiKey() {
  return "Managed securely on Java Spring Boot Backend";
}

export function setActiveApiKey() {
  // No-op: Keys are secured server-side in Spring Boot
}
