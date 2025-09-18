import React, { useState, useRef, useEffect } from 'react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Textarea } from './ui/textarea';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Badge } from './ui/badge';
import { Checkbox } from './ui/checkbox';
import { 
  Mic, 
  MicOff, 
  Volume2, 
  VolumeX, 
  Languages, 
  ArrowLeft, 
  Play, 
  Pause,
  RotateCcw,
  FileAudio,
  Globe,
  CheckCircle2,
  AlertCircle,
  Loader2
} from 'lucide-react';

type Page = 'dashboard' | 'report' | 'my-issues' | 'map' | 'community' | 'transparency' | 'profile' | 'voice-reporting' | 'campaigns' | 'collaboration';

interface VoiceReportingProps {
  onNavigate: (page: Page) => void;
}

export function VoiceReporting({ onNavigate }: VoiceReportingProps) {
  const [isRecording, setIsRecording] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [selectedLanguage, setSelectedLanguage] = useState('en-US');
  const [transcribedText, setTranscribedText] = useState('');
  const [audioUrl, setAudioUrl] = useState<string | null>(null);
  const [isPlaying, setIsPlaying] = useState(false);
  const [recordingDuration, setRecordingDuration] = useState(0);
  const [confidence, setConfidence] = useState(0);
  const audioRef = useRef<HTMLAudioElement>(null);
  const intervalRef = useRef<NodeJS.Timeout>();

  const languages = [
    { code: 'en-US', name: 'English (US)', flag: '🇺🇸' },
    { code: 'es-ES', name: 'Spanish (Spain)', flag: '🇪🇸' },
    { code: 'fr-FR', name: 'French (France)', flag: '🇫🇷' },
    { code: 'de-DE', name: 'German (Germany)', flag: '🇩🇪' },
    { code: 'it-IT', name: 'Italian (Italy)', flag: '🇮🇹' },
    { code: 'pt-BR', name: 'Portuguese (Brazil)', flag: '🇧🇷' },
    { code: 'zh-CN', name: 'Chinese (Simplified)', flag: '🇨🇳' },
    { code: 'ja-JP', name: 'Japanese', flag: '🇯🇵' },
    { code: 'ko-KR', name: 'Korean', flag: '🇰🇷' },
    { code: 'ar-SA', name: 'Arabic (Saudi Arabia)', flag: '🇸🇦' },
    { code: 'hi-IN', name: 'Hindi (India)', flag: '🇮🇳' },
    { code: 'ru-RU', name: 'Russian', flag: '🇷🇺' },
  ];

  const mockTranscriptions = {
    'en-US': 'There is a large pothole on Main Street near the intersection with Oak Avenue. The hole is approximately two feet wide and poses a danger to vehicles, especially motorcycles and bicycles. This needs urgent repair as it could cause accidents.',
    'es-ES': 'Hay un bache grande en la Calle Principal cerca de la intersección con Oak Avenue. El hoyo tiene aproximadamente dos pies de ancho y representa un peligro para los vehículos.',
    'fr-FR': 'Il y a un grand nid-de-poule sur Main Street près de l\'intersection avec Oak Avenue. Le trou fait environ deux pieds de large et représente un danger pour les véhicules.',
    'de-DE': 'Es gibt ein großes Schlagloch in der Main Street in der Nähe der Kreuzung mit der Oak Avenue. Das Loch ist etwa zwei Fuß breit und stellt eine Gefahr für Fahrzeuge dar.',
    'zh-CN': '主街靠近橡树大道交叉口有一个大坑洞。这个洞大约两英尺宽，对车辆特别是摩托车和自行车构成危险。',
    'ja-JP': 'オーク・アベニューとの交差点近くのメイン・ストリートに大きな穴があります。穴は約2フィート幅で、車両、特にオートバイや自転車にとって危険です。',
    'hi-IN': 'ओक एवेन्यू के साथ चौराहे के पास मुख्य सड़क पर एक बड़ा गड्ढा है। यह गड्ढा लगभग दो फुट चौड़ा है और वाहनों के लिए खतरनाक है।'
  };

  useEffect(() => {
    if (isRecording) {
      intervalRef.current = setInterval(() => {
        setRecordingDuration(prev => prev + 1);
      }, 1000);
    } else {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    }

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [isRecording]);

  const startRecording = async () => {
    setIsRecording(true);
    setRecordingDuration(0);
    setTranscribedText('');
    setConfidence(0);
    
    // Mock audio recording - in a real app, you'd use MediaRecorder API
    setTimeout(() => {
      setAudioUrl('/mock-audio.mp3'); // Mock audio URL
    }, 1000);
  };

  const stopRecording = async () => {
    setIsRecording(false);
    setIsProcessing(true);
    
    // Mock transcription processing
    setTimeout(() => {
      const mockText = mockTranscriptions[selectedLanguage as keyof typeof mockTranscriptions] || 
                       mockTranscriptions['en-US'];
      setTranscribedText(mockText);
      setConfidence(Math.floor(Math.random() * 20) + 80); // Random confidence between 80-100
      setIsProcessing(false);
    }, 2000);
  };

  const playAudio = () => {
    if (audioRef.current) {
      if (isPlaying) {
        audioRef.current.pause();
        setIsPlaying(false);
      } else {
        audioRef.current.play();
        setIsPlaying(true);
      }
    }
  };

  const formatDuration = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const retryTranscription = () => {
    if (audioUrl) {
      setIsProcessing(true);
      setTimeout(() => {
        const mockText = mockTranscriptions[selectedLanguage as keyof typeof mockTranscriptions] || 
                         mockTranscriptions['en-US'];
        setTranscribedText(mockText);
        setConfidence(Math.floor(Math.random() * 20) + 80);
        setIsProcessing(false);
      }, 2000);
    }
  };

  const submitVoiceReport = () => {
    // Mock submission - in real app, this would submit the transcribed text and audio
    onNavigate('my-issues');
  };

  return (
    <div className="p-6">
      <div className="max-w-4xl mx-auto">
        <div className="mb-6 flex items-center gap-4">
          <Button
            variant="ghost"
            onClick={() => onNavigate('dashboard')}
            className="p-2"
          >
            <ArrowLeft className="w-4 h-4" />
          </Button>
          <div>
            <h1 className="text-3xl font-bold">Voice Reporting</h1>
            <p className="text-muted-foreground mt-1">
              Report civic issues using voice recording with multilingual support
            </p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Voice Recording Section */}
          <div className="lg:col-span-2 space-y-6">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Languages className="w-5 h-5" />
                  Language Selection
                </CardTitle>
                <CardDescription>
                  Choose your preferred language for voice recognition
                </CardDescription>
              </CardHeader>
              <CardContent>
                <Select value={selectedLanguage} onValueChange={setSelectedLanguage}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {languages.map((lang) => (
                      <SelectItem key={lang.code} value={lang.code}>
                        <div className="flex items-center gap-2">
                          <span>{lang.flag}</span>
                          <span>{lang.name}</span>
                        </div>
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Mic className="w-5 h-5" />
                  Voice Recording
                </CardTitle>
                <CardDescription>
                  Describe the civic issue you want to report
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-6">
                {/* Recording Controls */}
                <div className="text-center space-y-4">
                  <div className={`w-32 h-32 rounded-full border-4 mx-auto flex items-center justify-center transition-all duration-300 ${
                    isRecording 
                      ? 'border-red-500 bg-red-50 animate-pulse' 
                      : 'border-blue-500 bg-blue-50 hover:bg-blue-100'
                  }`}>
                    <Button
                      onClick={isRecording ? stopRecording : startRecording}
                      variant="ghost"
                      size="lg"
                      className={`w-20 h-20 rounded-full ${
                        isRecording 
                          ? 'text-red-600 hover:text-red-700' 
                          : 'text-blue-600 hover:text-blue-700'
                      }`}
                      disabled={isProcessing}
                    >
                      {isRecording ? (
                        <MicOff className="w-8 h-8" />
                      ) : (
                        <Mic className="w-8 h-8" />
                      )}
                    </Button>
                  </div>

                  <div className="space-y-2">
                    <p className="text-lg font-medium">
                      {isRecording ? 'Recording...' : 'Press to Start Recording'}
                    </p>
                    {isRecording && (
                      <p className="text-sm text-muted-foreground">
                        Duration: {formatDuration(recordingDuration)}
                      </p>
                    )}
                    {isProcessing && (
                      <div className="flex items-center justify-center gap-2 text-blue-600">
                        <Loader2 className="w-4 h-4 animate-spin" />
                        <span>Processing audio...</span>
                      </div>
                    )}
                  </div>
                </div>

                {/* Audio Playback */}
                {audioUrl && !isRecording && (
                  <div className="p-4 bg-gray-50 rounded-lg">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-3">
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={playAudio}
                        >
                          {isPlaying ? (
                            <Pause className="w-4 h-4" />
                          ) : (
                            <Play className="w-4 h-4" />
                          )}
                        </Button>
                        <div className="flex items-center gap-2">
                          <FileAudio className="w-4 h-4 text-gray-500" />
                          <span className="text-sm">Recording ({formatDuration(recordingDuration)})</span>
                        </div>
                      </div>
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={retryTranscription}
                        disabled={isProcessing}
                      >
                        <RotateCcw className="w-4 h-4 mr-1" />
                        Retry
                      </Button>
                    </div>
                    <audio ref={audioRef} src={audioUrl} onEnded={() => setIsPlaying(false)} />
                  </div>
                )}
              </CardContent>
            </Card>

            {/* Transcription Results */}
            {(transcribedText || isProcessing) && (
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Globe className="w-5 h-5" />
                    Transcription Result
                  </CardTitle>
                  <CardDescription>
                    Review and edit the transcribed text
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  {isProcessing ? (
                    <div className="flex items-center justify-center py-8">
                      <div className="text-center space-y-2">
                        <Loader2 className="w-8 h-8 animate-spin mx-auto text-blue-600" />
                        <p className="text-sm text-muted-foreground">
                          Transcribing audio in {languages.find(l => l.code === selectedLanguage)?.name}...
                        </p>
                      </div>
                    </div>
                  ) : (
                    <>
                      <div className="flex items-center justify-between">
                        <Badge variant="secondary" className="flex items-center gap-1">
                          {confidence >= 90 ? (
                            <CheckCircle2 className="w-3 h-3 text-green-600" />
                          ) : (
                            <AlertCircle className="w-3 h-3 text-yellow-600" />
                          )}
                          Confidence: {confidence}%
                        </Badge>
                        <Badge variant="outline">
                          {languages.find(l => l.code === selectedLanguage)?.flag} {' '}
                          {languages.find(l => l.code === selectedLanguage)?.name}
                        </Badge>
                      </div>
                      
                      <Textarea
                        value={transcribedText}
                        onChange={(e) => setTranscribedText(e.target.value)}
                        placeholder="Transcribed text will appear here..."
                        rows={6}
                        className="resize-none"
                      />
                      
                      <p className="text-xs text-muted-foreground">
                        You can edit the transcribed text above to make corrections if needed.
                      </p>
                    </>
                  )}
                </CardContent>
              </Card>
            )}
          </div>

          {/* Side Panel */}
          <div className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Voice Recording Tips</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3 text-sm">
                <div className="flex items-start gap-2">
                  <Volume2 className="w-4 h-4 mt-0.5 text-blue-600" />
                  <span>Speak clearly and at a moderate pace</span>
                </div>
                <div className="flex items-start gap-2">
                  <Mic className="w-4 h-4 mt-0.5 text-blue-600" />
                  <span>Record in a quiet environment</span>
                </div>
                <div className="flex items-start gap-2">
                  <Languages className="w-4 h-4 mt-0.5 text-blue-600" />
                  <span>Select your language before recording</span>
                </div>
                <div className="flex items-start gap-2">
                  <CheckCircle2 className="w-4 h-4 mt-0.5 text-blue-600" />
                  <span>Review transcription for accuracy</span>
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Supported Languages</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-2 gap-2 text-sm">
                  {languages.slice(0, 8).map((lang) => (
                    <div key={lang.code} className="flex items-center gap-1">
                      <span>{lang.flag}</span>
                      <span className="truncate">{lang.name.split('(')[0]}</span>
                    </div>
                  ))}
                </div>
                <p className="text-xs text-muted-foreground mt-2">
                  And {languages.length - 8} more languages available
                </p>
              </CardContent>
            </Card>

            {transcribedText && (
              <Card>
                <CardHeader>
                  <CardTitle>Submit Report</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <Button
                    onClick={submitVoiceReport}
                    className="w-full"
                    disabled={!transcribedText.trim()}
                  >
                    Submit Voice Report
                  </Button>
                  <Button
                    variant="outline"
                    className="w-full"
                    onClick={() => onNavigate('report')}
                  >
                    Switch to Text Form
                  </Button>
                </CardContent>
              </Card>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}