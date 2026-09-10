import React, { useState } from 'react';
import { Send, Mic, MicOff } from 'lucide-react';
import { useApp } from '../context/AppContext';

export default function ChatInput() {
  const { sendMessage, isAiThinking, showToast, t } = useApp();
  const [inputVal, setInputVal] = useState('');
  const [isListening, setIsListening] = useState(false);

  const handleSubmit = (e) => {
    e?.preventDefault();
    if (!inputVal.trim() || isAiThinking) return;

    sendMessage(inputVal.trim());
    setInputVal('');
  };

  const handleVoiceInput = () => {
    if (!('webkitSpeechRecognition' in window || 'SpeechRecognition' in window)) {
      setIsListening(true);
      showToast("Voice microphone active: Simulated speech recognition...", "info");
      setTimeout(() => {
        setInputVal("Which standard applies to helmets?");
        setIsListening(false);
        showToast("Voice converted: 'Which standard applies to helmets?'", "success");
      }, 1500);
      return;
    }

    try {
      const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
      const recognition = new SpeechRecognition();
      recognition.continuous = false;
      recognition.interimResults = false;
      recognition.lang = 'en-IN';

      recognition.onstart = () => {
        setIsListening(true);
        showToast("Listening... speak your query clearly", "info");
      };

      recognition.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        setInputVal(transcript);
        setIsListening(false);
        showToast(`Captured: "${transcript}"`, "success");
      };

      recognition.onerror = (event) => {
        console.error(event.error);
        setIsListening(false);
        showToast("Voice input ended or permission denied", "info");
      };

      recognition.onend = () => {
        setIsListening(false);
      };

      recognition.start();
    } catch (e) {
      console.error(e);
      setIsListening(false);
    }
  };

  return (
    <div className="chat-input-wrapper">
      <form
        onSubmit={handleSubmit}
        className="chat-input-bar"
      >
        {/* Input Text */}
        <input
          type="text"
          className="chat-text-input"
          value={inputVal}
          onChange={(e) => setInputVal(e.target.value)}
          placeholder={t.chat?.placeholder || "Ask a question about Indian Standards, certification, or testing..."}
          disabled={isAiThinking}
        />

        {/* Action Buttons */}
        <div className="chat-input-actions">
          {/* Mic Button */}
          <button
            type="button"
            className={`input-mic-btn ${isListening ? 'listening' : ''}`}
            onClick={handleVoiceInput}
            title="Speech to Text"
            aria-label="Speech to Text Voice Input"
          >
            {isListening ? <MicOff size={18} /> : <Mic size={18} />}
          </button>

          {/* Send Button */}
          <button
            type="submit"
            className="send-msg-btn"
            disabled={!inputVal.trim() || isAiThinking}
            title="Send Message"
            aria-label="Send Message"
          >
            <Send size={16} />
          </button>
        </div>
      </form>
    </div>
  );
}
