import React, { useState, useRef, useEffect } from 'react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Badge } from './ui/badge';
import { 
  MessageCircle, 
  X, 
  Send, 
  Bot, 
  User, 
  HelpCircle,
  Map,
  FileText,
  Phone,
  Clock,
  ChevronDown,
  Minimize2
} from 'lucide-react';

interface ChatMessage {
  id: string;
  type: 'user' | 'bot';
  message: string;
  timestamp: Date;
  quickReplies?: string[];
}

interface ChatbotProps {
  isOpen: boolean;
  onToggle: () => void;
}

export function Chatbot({ isOpen, onToggle }: ChatbotProps) {
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      id: '1',
      type: 'bot',
      message: 'Hello! I\'m here to help you with civic issues and reporting. How can I assist you today?',
      timestamp: new Date(),
      quickReplies: ['Report an Issue', 'Check Issue Status', 'Emergency Contact', 'How to Use App']
    }
  ]);
  const [inputValue, setInputValue] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const [isMinimized, setIsMinimized] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const predefinedResponses = {
    'report an issue': {
      message: 'I can help you report a civic issue! You can:\n\n• Use the "Report Issue" page for detailed reporting\n• Try voice reporting for hands-free submission\n• Report anonymously if needed\n• Add photos and location data\n\nWould you like me to guide you through the process?',
      quickReplies: ['Guide Me', 'Anonymous Reporting', 'Voice Reporting', 'Upload Photos']
    },
    'check issue status': {
      message: 'To check your issue status:\n\n• Go to "My Issues" to see all your reports\n• Each issue shows current status and timeline\n• You\'ll receive notifications for updates\n• Contact details are provided for follow-up\n\nWould you like to see your recent reports?',
      quickReplies: ['View My Issues', 'Track Specific Issue', 'Set Notifications']
    },
    'emergency contact': {
      message: 'For emergencies, please use:\n\n🚨 Emergency: 911\n🚒 Fire Department: (555) 0123\n👮 Police Non-Emergency: (555) 0456\n🏥 Medical Emergency: 911\n💧 Water Emergency: (555) 0789\n⚡ Power Outage: (555) 0012\n\nFor urgent civic issues, use our Emergency Reporting feature.',
      quickReplies: ['Emergency Reporting', 'More Contacts', 'Back to Main']
    },
    'how to use app': {
      message: 'Here\'s how to use CitizenConnect:\n\n📋 Dashboard: Overview of your reports and community activity\n📝 Report Issue: Submit new civic issues\n🗺️ Map View: See issues in your area\n👥 Community: Validate and discuss issues\n📊 Transparency: Track city performance\n\nWhat would you like to learn more about?',
      quickReplies: ['Reporting Process', 'Community Features', 'Map Navigation', 'Getting Started']
    },
    'guide me': {
      message: 'Let me guide you through reporting an issue:\n\n1. Click "Report Issue" in the sidebar\n2. Choose your issue category\n3. Add a clear title and description\n4. Select priority level\n5. Add photos if available\n6. Confirm your location\n7. Choose anonymous option if needed\n8. Submit your report\n\nReady to start?',
      quickReplies: ['Start Reporting', 'Anonymous Reporting Info', 'Photo Guidelines']
    },
    'anonymous reporting': {
      message: 'Anonymous reporting protects your privacy:\n\n✓ Your identity is not shared with officials\n✓ Reports are still tracked and acted upon\n✓ Updates sent to secure notification system\n✓ Community can still validate the issue\n\nYour privacy is completely protected while helping your community!',
      quickReplies: ['Start Anonymous Report', 'Privacy Policy', 'Back to Reporting']
    },
    'voice reporting': {
      message: 'Voice reporting makes it easy to report on-the-go:\n\n🎤 Speak naturally in your preferred language\n🌍 Supports 12+ languages\n📝 Automatically transcribed to text\n✏️ You can edit before submitting\n🔊 Playback to verify accuracy\n\nPerfect for when you can\'t type!',
      quickReplies: ['Try Voice Reporting', 'Supported Languages', 'Voice Tips']
    }
  };

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const sendMessage = (message: string) => {
    const userMessage: ChatMessage = {
      id: Date.now().toString(),
      type: 'user',
      message,
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setInputValue('');
    setIsTyping(true);

    // Simulate bot thinking time
    setTimeout(() => {
      const botResponse = getBotResponse(message.toLowerCase());
      const botMessage: ChatMessage = {
        id: (Date.now() + 1).toString(),
        type: 'bot',
        message: botResponse.message,
        timestamp: new Date(),
        quickReplies: botResponse.quickReplies
      };

      setMessages(prev => [...prev, botMessage]);
      setIsTyping(false);
    }, 1500);
  };

  const getBotResponse = (message: string) => {
    // Simple keyword matching
    for (const [key, response] of Object.entries(predefinedResponses)) {
      if (message.includes(key)) {
        return response;
      }
    }

    // Default fallback responses
    const fallbackResponses = [
      {
        message: 'I understand you need help with that. Here are some things I can assist you with:',
        quickReplies: ['Report an Issue', 'Check Issue Status', 'Emergency Contact', 'How to Use App']
      },
      {
        message: 'I\'m here to help! You can ask me about reporting issues, checking status, emergency contacts, or how to use the app.',
        quickReplies: ['Report an Issue', 'View My Issues', 'Get Help', 'Contact Support']
      }
    ];

    return fallbackResponses[Math.floor(Math.random() * fallbackResponses.length)];
  };

  const handleQuickReply = (reply: string) => {
    sendMessage(reply);
  };

  const formatTime = (timestamp: Date) => {
    return timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  if (!isOpen) {
    return (
      <Button
        onClick={onToggle}
        className="fixed bottom-6 right-6 w-14 h-14 rounded-full shadow-lg z-50 bg-blue-600 hover:bg-blue-700"
        size="lg"
      >
        <MessageCircle className="w-6 h-6 text-white" />
      </Button>
    );
  }

  return (
    <Card className="fixed bottom-6 right-6 w-96 h-[600px] shadow-xl z-50 flex flex-col">
      <CardHeader className="flex flex-row items-center justify-between p-4 pb-2">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
            <Bot className="w-4 h-4 text-white" />
          </div>
          <div>
            <CardTitle className="text-lg">CitizenConnect Assistant</CardTitle>
            <div className="flex items-center gap-1 text-xs text-green-600">
              <div className="w-2 h-2 bg-green-500 rounded-full"></div>
              Online
            </div>
          </div>
        </div>
        <div className="flex items-center gap-1">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => setIsMinimized(!isMinimized)}
            className="p-1 h-8 w-8"
          >
            {isMinimized ? <ChevronDown className="w-4 h-4" /> : <Minimize2 className="w-4 h-4" />}
          </Button>
          <Button
            variant="ghost"
            size="sm"
            onClick={onToggle}
            className="p-1 h-8 w-8"
          >
            <X className="w-4 h-4" />
          </Button>
        </div>
      </CardHeader>

      {!isMinimized && (
        <>
          <CardContent className="flex-1 overflow-y-auto p-4 pt-2 space-y-4">
            {messages.map((message) => (
              <div
                key={message.id}
                className={`flex gap-3 ${message.type === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                {message.type === 'bot' && (
                  <div className="w-6 h-6 bg-blue-600 rounded-full flex items-center justify-center flex-shrink-0 mt-1">
                    <Bot className="w-3 h-3 text-white" />
                  </div>
                )}
                
                <div className={`max-w-[80%] ${message.type === 'user' ? 'order-1' : 'order-2'}`}>
                  <div
                    className={`rounded-lg p-3 ${
                      message.type === 'user'
                        ? 'bg-blue-600 text-white ml-auto'
                        : 'bg-gray-100 text-gray-900'
                    }`}
                  >
                    <p className="text-sm whitespace-pre-line">{message.message}</p>
                  </div>
                  
                  <div className={`text-xs text-muted-foreground mt-1 ${
                    message.type === 'user' ? 'text-right' : 'text-left'
                  }`}>
                    {formatTime(message.timestamp)}
                  </div>

                  {message.quickReplies && message.type === 'bot' && (
                    <div className="flex flex-wrap gap-2 mt-3">
                      {message.quickReplies.map((reply, index) => (
                        <Button
                          key={index}
                          variant="outline"
                          size="sm"
                          onClick={() => handleQuickReply(reply)}
                          className="text-xs h-7"
                        >
                          {reply}
                        </Button>
                      ))}
                    </div>
                  )}
                </div>

                {message.type === 'user' && (
                  <div className="w-6 h-6 bg-gray-300 rounded-full flex items-center justify-center flex-shrink-0 mt-1">
                    <User className="w-3 h-3 text-gray-600" />
                  </div>
                )}
              </div>
            ))}

            {isTyping && (
              <div className="flex gap-3 justify-start">
                <div className="w-6 h-6 bg-blue-600 rounded-full flex items-center justify-center flex-shrink-0 mt-1">
                  <Bot className="w-3 h-3 text-white" />
                </div>
                <div className="bg-gray-100 rounded-lg p-3">
                  <div className="flex space-x-1">
                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce"></div>
                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0.1s' }}></div>
                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></div>
                  </div>
                </div>
              </div>
            )}
            
            <div ref={messagesEndRef} />
          </CardContent>

          <div className="p-4 pt-2 border-t">
            <div className="flex gap-2">
              <Input
                value={inputValue}
                onChange={(e) => setInputValue(e.target.value)}
                placeholder="Type your message..."
                onKeyPress={(e) => {
                  if (e.key === 'Enter' && inputValue.trim()) {
                    sendMessage(inputValue.trim());
                  }
                }}
                className="flex-1"
              />
              <Button
                onClick={() => {
                  if (inputValue.trim()) {
                    sendMessage(inputValue.trim());
                  }
                }}
                disabled={!inputValue.trim() || isTyping}
                size="sm"
              >
                <Send className="w-4 h-4" />
              </Button>
            </div>
            
            <div className="text-xs text-center text-muted-foreground mt-2">
              Powered by CitizenConnect AI • Available 24/7
            </div>
          </div>
        </>
      )}
    </Card>
  );
}