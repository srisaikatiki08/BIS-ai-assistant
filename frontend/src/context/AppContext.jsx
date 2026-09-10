import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { TRANSLATIONS } from '../data/i18nData';
import { queryAiAssistantAsync } from '../services/aiAssistantService';
import { 
  fetchConversations, 
  fetchConversationById, 
  deleteConversation as deleteConversationApi 
} from '../services/apiService';

const AppContext = createContext();

export function AppProvider({ children }) {
  const [activeTab, setActiveTab] = useState('home');
  const [language, setLanguage] = useState('en');
  const [theme, setTheme] = useState('light');
  const [searchQuery, setSearchQuery] = useState('');
  const [activeStandardModal, setActiveStandardModal] = useState(null);
  const [toast, setToast] = useState(null);

  // Saved Items
  const [savedStandards, setSavedStandards] = useState(() => {
    try {
      const saved = localStorage.getItem('bis_saved_standards');
      return saved ? JSON.parse(saved) : ["IS-4151-2015", "IS-10500-2012", "IS-16046-2018"];
    } catch {
      return ["IS-4151-2015", "IS-10500-2012"];
    }
  });

  const [savedAnswers, setSavedAnswers] = useState(() => {
    try {
      const saved = localStorage.getItem('bis_saved_answers');
      return saved ? JSON.parse(saved) : [];
    } catch {
      return [];
    }
  });

  // Current Active Conversation Session ID
  const [currentSessionId, setCurrentSessionId] = useState(() => {
    try {
      const saved = localStorage.getItem('bis_active_session_id');
      if (saved) return saved;
    } catch (e) {
      console.error(e);
    }
    const newId = "sess-" + Date.now();
    try { localStorage.setItem('bis_active_session_id', newId); } catch {}
    return newId;
  });

  // Full Conversations History List (ChatGPT-style)
  const [conversations, setConversations] = useState(() => {
    try {
      const saved = localStorage.getItem('bis_stored_conversations');
      if (saved) return JSON.parse(saved);
    } catch (e) {
      console.error(e);
    }
    return [];
  });

  // Welcome message template
  const createWelcomeMessage = useCallback(() => ({
    id: "welcome-" + Date.now(),
    role: "assistant",
    content: TRANSLATIONS[language]?.chat?.welcomeSubtitle || "Hello! I’m BIS Intelligent Assistant. Ask any question about Indian Standards, BIS certification schemes, testing parameters, or compliance requirements.",
    timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    category: "Welcome",
    modelBadge: "Google Gemini (Spring Boot Backend)",
    sourceReference: {
      document: "Bureau of Indian Standards Repository",
      clause: "Official Standards & Gazette Portal",
      portalUrl: "https://manakonline.in",
      disclaimer: "AI-generated output backed by Bureau of Indian Standards regulatory repository."
    }
  }), [language]);

  // Current Active Chat Messages
  const [messages, setMessages] = useState([createWelcomeMessage()]);
  const [isAiThinking, setIsAiThinking] = useState(false);

  // Apply Theme to DOM
  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  // Persist Saved Standards
  useEffect(() => {
    try {
      localStorage.setItem('bis_saved_standards', JSON.stringify(savedStandards));
    } catch (e) {
      console.error(e);
    }
  }, [savedStandards]);

  // Persist Saved Answers
  useEffect(() => {
    try {
      localStorage.setItem('bis_saved_answers', JSON.stringify(savedAnswers));
    } catch (e) {
      console.error(e);
    }
  }, [savedAnswers]);

  // Persist Active Session ID
  useEffect(() => {
    try {
      localStorage.setItem('bis_active_session_id', currentSessionId);
    } catch (e) {
      console.error(e);
    }
  }, [currentSessionId]);

  // Persist Stored Conversations
  useEffect(() => {
    try {
      localStorage.setItem('bis_stored_conversations', JSON.stringify(conversations));
    } catch (e) {
      console.error(e);
    }
  }, [conversations]);

  // Sync conversation history from backend on initial mount
  const refreshConversationsFromBackend = useCallback(async () => {
    try {
      const serverConvs = await fetchConversations();
      if (Array.isArray(serverConvs) && serverConvs.length > 0) {
        const formatted = serverConvs.map(c => ({
          id: c.sessionIdentifier || `sess-${c.id}`,
          sessionIdentifier: c.sessionIdentifier,
          title: c.title || "BIS Inquiry",
          updatedAt: c.updatedAt || new Date().toISOString(),
          messages: c.messages || []
        }));
        setConversations(formatted);
      }
    } catch (e) {
      console.warn("Could not sync conversations from backend:", e);
    }
  }, []);

  useEffect(() => {
    refreshConversationsFromBackend();
  }, [refreshConversationsFromBackend]);

  // Extract a clean meaningful title from user query
  const extractTitleFromQuery = (text) => {
    if (!text || typeof text !== 'string') return "BIS Inquiry";
    const clean = text.trim().replace(/^["']|["']$/g, '');
    if (clean.length > 36) {
      return clean.substring(0, 36).trim() + "...";
    }
    return clean;
  };

  // Helper: Show Toast
  const showToast = (message, type = 'success') => {
    setToast({ message, type, id: Date.now() });
    setTimeout(() => setToast(null), 3500);
  };

  // Start New Chat (ChatGPT-style)
  const startNewChat = () => {
    const newSessionId = "sess-" + Date.now();
    setCurrentSessionId(newSessionId);
    setMessages([createWelcomeMessage()]);
    showToast("Started new chat", "info");
  };

  // Select and Load an Old Conversation (ChatGPT-style)
  const selectConversation = async (sessionId) => {
    if (!sessionId) return;
    setCurrentSessionId(sessionId);
    setIsAiThinking(false);

    try {
      // 1. Try to fetch from backend
      const convData = await fetchConversationById(sessionId);
      if (convData && convData.messages && convData.messages.length > 0) {
        const mapped = convData.messages.map((m, idx) => ({
          id: m.id ? `msg-${m.id}` : `msg-${idx}-${Date.now()}`,
          role: (m.role === 'model' || m.role === 'assistant') ? 'assistant' : 'user',
          content: m.content,
          timestamp: m.timestamp 
            ? new Date(m.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) 
            : new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
          category: "BIS Standards Advisory",
          sourceReference: {
            document: "Bureau of Indian Standards Repository",
            clause: "Official Standards Portal",
            portalUrl: "https://manakonline.in",
            disclaimer: "AI-generated output backed by Bureau of Indian Standards regulatory repository."
          }
        }));
        setMessages(mapped);
        return;
      }
    } catch (e) {
      console.warn("Backend conversation fetch failed, checking local cache:", e);
    }

    // 2. Fallback to locally cached conversation
    const cached = conversations.find(c => (c.sessionIdentifier === sessionId || c.id === sessionId));
    if (cached && cached.messages && cached.messages.length > 0) {
      const mapped = cached.messages.map((m, idx) => ({
        id: m.id ? `msg-${m.id}` : `msg-${idx}-${Date.now()}`,
        role: (m.role === 'model' || m.role === 'assistant') ? 'assistant' : 'user',
        content: m.content,
        timestamp: m.timestamp 
          ? new Date(m.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) 
          : new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      }));
      setMessages(mapped);
    } else {
      setMessages([createWelcomeMessage()]);
    }
  };

  // Delete a Conversation
  const deleteChatSession = async (sessionId, e) => {
    if (e && e.stopPropagation) e.stopPropagation();
    
    // Call backend delete
    try {
      await deleteConversationApi(sessionId);
    } catch (err) {
      console.warn("Error deleting conversation from server:", err);
    }

    // Update local state
    setConversations(prev => prev.filter(c => (c.sessionIdentifier !== sessionId && c.id !== sessionId)));
    
    // If the active conversation was deleted, start a new one
    if (currentSessionId === sessionId) {
      startNewChat();
    }
    showToast("Conversation deleted", "info");
  };

  // Clear all conversations
  const clearAllChatSessions = async () => {
    for (const c of conversations) {
      try {
        await deleteConversationApi(c.sessionIdentifier || c.id);
      } catch {}
    }
    setConversations([]);
    startNewChat();
    showToast("All chat history cleared", "info");
  };

  // Send Chat Message (Persists to CURRENT conversation session)
  const sendMessage = async (userText) => {
    if (!userText || !userText.trim()) return;

    const userMsg = {
      id: "user-" + Date.now(),
      role: "user",
      content: userText.trim(),
      timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    };

    // Format previous messages in this conversation as history for Gemini
    const historyPayload = messages
      .filter(m => m.content && !m.id?.includes('welcome'))
      .slice(-10)
      .map(m => ({
        role: m.role === 'assistant' ? 'model' : 'user',
        content: m.content
      }));

    // Update messages in current conversation view
    setMessages(prev => [...prev, userMsg]);
    setIsAiThinking(true);

    // Update or add this conversation to sidebar immediately
    const cleanTitle = extractTitleFromQuery(userText);
    setConversations(prev => {
      const existingIdx = prev.findIndex(c => (c.sessionIdentifier === currentSessionId || c.id === currentSessionId));
      if (existingIdx >= 0) {
        const updated = [...prev];
        updated[existingIdx] = {
          ...updated[existingIdx],
          updatedAt: new Date().toISOString()
        };
        // Move to top
        const [item] = updated.splice(existingIdx, 1);
        return [item, ...updated];
      } else {
        const newConv = {
          id: currentSessionId,
          sessionIdentifier: currentSessionId,
          title: cleanTitle,
          updatedAt: new Date().toISOString(),
          messages: [{ role: 'user', content: userText }]
        };
        return [newConv, ...prev].slice(0, 50);
      }
    });

    try {
      const aiResponse = await queryAiAssistantAsync(userText, language, historyPayload, currentSessionId);
      setMessages(prev => [...prev, aiResponse]);

      // Sync backend conversation list in background to ensure accurate persistence
      refreshConversationsFromBackend();
    } catch (err) {
      console.error(err);
      setMessages(prev => [...prev, {
        id: "msg-err-" + Date.now(),
        role: "assistant",
        content: "I encountered an issue connecting to the AI service. Please verify your connection.",
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
        category: "System Error"
      }]);
    } finally {
      setIsAiThinking(false);
    }
  };

  // Toggle Save Standard
  const toggleSaveStandard = (standardId) => {
    if (savedStandards.includes(standardId)) {
      setSavedStandards(prev => prev.filter(id => id !== standardId));
      showToast("Standard removed from saved list", "info");
    } else {
      setSavedStandards(prev => [...prev, standardId]);
      showToast("Standard saved to your workspace", "success");
    }
  };

  // Toggle Save Answer
  const toggleSaveAnswer = (message) => {
    const exists = savedAnswers.some(a => a.id === message.id);
    if (exists) {
      setSavedAnswers(prev => prev.filter(a => a.id !== message.id));
      showToast("Answer removed from saved notes", "info");
    } else {
      setSavedAnswers(prev => [...prev, message]);
      showToast("Answer bookmarked to your dashboard", "success");
    }
  };

  // Quick Action: Search & Deep-link
  const launchChatWithQuery = (queryText) => {
    setActiveTab('chat');
    sendMessage(queryText);
  };

  const t = TRANSLATIONS[language] || TRANSLATIONS.en;

  return (
    <AppContext.Provider
      value={{
        activeTab,
        setActiveTab,
        language,
        setLanguage,
        theme,
        setTheme,
        searchQuery,
        setSearchQuery,
        activeStandardModal,
        setActiveStandardModal,
        savedStandards,
        toggleSaveStandard,
        savedAnswers,
        toggleSaveAnswer,
        currentSessionId,
        conversations,
        selectConversation,
        startNewChat,
        deleteChatSession,
        clearAllChatSessions,
        messages,
        setMessages,
        sendMessage,
        isAiThinking,
        launchChatWithQuery,
        toast,
        showToast,
        t
      }}
    >
      {children}
    </AppContext.Provider>
  );
}

export function useApp() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within an AppProvider');
  }
  return context;
}

