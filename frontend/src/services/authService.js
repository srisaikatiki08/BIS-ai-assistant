// BIS Intelligent Assistant - Frontend Authentication Service
// Communicates with Spring Boot JWT Endpoints (/api/auth)

const RAW_BACKEND_URL = import.meta.env.VITE_API_BASE_URL || import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080';
const BACKEND_BASE_URL = RAW_BACKEND_URL.replace(/\/+$/, '');

export const AUTH_TOKEN_KEY = 'bis_auth_token';
export const AUTH_USER_KEY = 'bis_auth_user';

/**
 * Retrieve saved JWT token from localStorage.
 */
export function getStoredToken() {
  try {
    return localStorage.getItem(AUTH_TOKEN_KEY) || null;
  } catch {
    return null;
  }
}

/**
 * Retrieve saved UserProfile object from localStorage.
 */
export function getStoredUser() {
  try {
    const raw = localStorage.getItem(AUTH_USER_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

/**
 * Persist token and user in localStorage.
 */
export function setStoredAuth(token, user) {
  try {
    if (token) {
      localStorage.setItem(AUTH_TOKEN_KEY, token);
    }
    if (user) {
      localStorage.setItem(AUTH_USER_KEY, JSON.stringify(user));
    }
  } catch (e) {
    console.warn("Failed to persist auth data to localStorage:", e);
  }
}

/**
 * Remove token and user from localStorage.
 */
export function clearStoredAuth() {
  try {
    localStorage.removeItem(AUTH_TOKEN_KEY);
    localStorage.removeItem(AUTH_USER_KEY);
  } catch (e) {
    console.warn("Failed to clear auth data from localStorage:", e);
  }
}

/**
 * Register a new user account on the Spring Boot backend.
 *
 * @param {string} fullName
 * @param {string} email
 * @param {string} password
 * @returns {Promise<{success: boolean, token?: string, user?: Object, message?: string, error?: string}>}
 */
export async function signup(fullName, email, password) {
  try {
    const response = await fetch(`${BACKEND_BASE_URL}/api/auth/signup`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify({
        fullName: fullName?.trim(),
        email: email?.trim().toLowerCase(),
        password: password
      })
    });

    const data = await response.json().catch(() => ({}));

    if (response.ok && data.success) {
      setStoredAuth(data.token, data.user);
      return {
        success: true,
        token: data.token,
        user: data.user,
        message: data.message || "Registration successful."
      };
    } else {
      let errorMessage = data.error || data.message || "Failed to create account.";
      if (response.status === 400 && (errorMessage.includes("already exists") || errorMessage.includes("duplicate"))) {
        errorMessage = "An account with this email already exists.";
      }
      return {
        success: false,
        error: errorMessage,
        status: response.status
      };
    }
  } catch (err) {
    console.error("Signup network error:", err);
    return {
      success: false,
      error: "Unable to connect to the BIS backend. Please try again.",
      networkError: true
    };
  }
}

/**
 * Authenticate with email and password to receive a JWT token.
 *
 * @param {string} email
 * @param {string} password
 * @returns {Promise<{success: boolean, token?: string, user?: Object, message?: string, error?: string}>}
 */
export async function login(email, password) {
  try {
    const response = await fetch(`${BACKEND_BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      body: JSON.stringify({
        email: email?.trim().toLowerCase(),
        password: password
      })
    });

    const data = await response.json().catch(() => ({}));

    if (response.ok && data.success) {
      setStoredAuth(data.token, data.user);
      return {
        success: true,
        token: data.token,
        user: data.user,
        message: data.message || "Login successful."
      };
    } else {
      let errorMessage = data.error || data.message || "Invalid email or password.";
      if (response.status === 401 || response.status === 400) {
        errorMessage = "Invalid email or password.";
      }
      return {
        success: false,
        error: errorMessage,
        status: response.status
      };
    }
  } catch (err) {
    console.error("Login network error:", err);
    return {
      success: false,
      error: "Unable to connect to the BIS backend. Please try again.",
      networkError: true
    };
  }
}

/**
 * Verify JWT token and fetch current user profile from GET /api/auth/me.
 *
 * @param {string} [token]
 * @returns {Promise<{success: boolean, user?: Object, error?: string}>}
 */
export async function getCurrentUser(token = null) {
  const authToken = token || getStoredToken();
  if (!authToken) {
    return { success: false, error: "No authentication token found." };
  }

  try {
    const response = await fetch(`${BACKEND_BASE_URL}/api/auth/me`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${authToken}`,
        'Accept': 'application/json'
      }
    });

    if (response.ok) {
      const user = await response.json();
      if (user && user.email) {
        setStoredAuth(authToken, user);
        return { success: true, user };
      }
    }

    if (response.status === 401) {
      clearStoredAuth();
      return { success: false, error: "Session expired. Please log in again.", isAuthError: true };
    }

    return { success: false, error: `Failed to fetch profile (HTTP ${response.status})` };
  } catch (err) {
    console.warn("Could not verify user token with backend:", err);
    return {
      success: false,
      error: "Unable to connect to the BIS backend. Please try again.",
      networkError: true
    };
  }
}

/**
 * Log out user by clearing stored auth data.
 */
export function logout() {
  clearStoredAuth();
}
