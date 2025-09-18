import React, { useState, useRef } from 'react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Textarea } from './ui/textarea';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Checkbox } from './ui/checkbox';
import { Progress } from './ui/progress';
import { Badge } from './ui/badge';
import { 
  Upload, 
  Camera, 
  MapPin, 
  Mic, 
  MicOff, 
  Check, 
  X,
  Image as ImageIcon,
  Video,
  Trash2,
  Construction,
  Lightbulb,
  TreePine,
  Car,
  Shield,
  Heart,
  UserCheck,
  Eye,
  EyeOff
} from 'lucide-react';

type Page = 'dashboard' | 'report' | 'my-issues' | 'map' | 'community' | 'transparency' | 'profile';

interface ReportIssueProps {
  onNavigate: (page: Page) => void;
}

export function ReportIssue({ onNavigate }: ReportIssueProps) {
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: '',
    urgency: '',
    location: 'Current Location (GPS)',
    coordinates: { lat: 40.7128, lng: -74.0060 }, // Mock NYC coordinates
    isAnonymous: false,
    isPriorityReporting: false,
    priorityType: ''
  });
  const [files, setFiles] = useState<File[]>([]);
  const [isRecording, setIsRecording] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const categories = [
    { value: 'roads', label: 'Roads & Infrastructure', icon: Construction },
    { value: 'lighting', label: 'Street Lighting', icon: Lightbulb },
    { value: 'sanitation', label: 'Trash & Sanitation', icon: Trash2 },
    { value: 'parks', label: 'Parks & Recreation', icon: TreePine },
    { value: 'traffic', label: 'Traffic & Parking', icon: Car },
    { value: 'other', label: 'Other', icon: null },
  ];

  const urgencyLevels = [
    { value: 'low', label: 'Low Priority', color: 'bg-green-100 text-green-800' },
    { value: 'medium', label: 'Medium Priority', color: 'bg-yellow-100 text-yellow-800' },
    { value: 'high', label: 'High Priority', color: 'bg-red-100 text-red-800' },
    { value: 'emergency', label: 'Emergency', color: 'bg-red-500 text-white' },
  ];

  const priorityTypes = [
    { value: 'elderly', label: 'Elderly Citizen', icon: Heart, color: 'bg-blue-100 text-blue-800' },
    { value: 'disabled', label: 'Person with Disability', icon: Shield, color: 'bg-purple-100 text-purple-800' },
    { value: 'caregiver', label: 'Caregiver', icon: UserCheck, color: 'bg-green-100 text-green-800' },
  ];

  const handleFileUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = Array.from(event.target.files || []);
    setFiles(prev => [...prev, ...selectedFiles]);
    
    // Mock upload progress
    setUploadProgress(0);
    const interval = setInterval(() => {
      setUploadProgress(prev => {
        if (prev >= 100) {
          clearInterval(interval);
          return 100;
        }
        return prev + 10;
      });
    }, 100);
  };

  const removeFile = (index: number) => {
    setFiles(prev => prev.filter((_, i) => i !== index));
  };

  const handleVoiceToText = () => {
    setIsRecording(!isRecording);
    // Mock voice recording functionality
    if (!isRecording) {
      setTimeout(() => {
        setFormData(prev => ({
          ...prev,
          description: prev.description + (prev.description ? ' ' : '') + 
            'There is a large pothole on the left side of the street that could damage vehicles.'
        }));
        setIsRecording(false);
      }, 3000);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    
    // Mock submission
    setTimeout(() => {
      setIsSubmitting(false);
      // Navigate to my-issues page to show the submitted issue
      onNavigate('my-issues');
    }, 2000);
  };

  const getCurrentLocation = () => {
    // Mock getting current location
    setFormData(prev => ({
      ...prev,
      location: '123 Main Street, Downtown',
      coordinates: { lat: 40.7589, lng: -73.9851 }
    }));
  };

  return (
    <div className="p-6">
      <div className="max-w-4xl mx-auto">
        <div className="mb-6">
          <h1 className="text-3xl font-bold">Report an Issue</h1>
          <p className="text-muted-foreground mt-1">
            Help improve your community by reporting civic issues
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Main Form */}
            <div className="lg:col-span-2 space-y-6">
              {/* Basic Information */}
              <Card>
                <CardHeader>
                  <CardTitle>Issue Details</CardTitle>
                  <CardDescription>
                    Provide details about the issue you're reporting
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div>
                    <label className="block text-sm font-medium mb-2">Issue Title</label>
                    <Input
                      placeholder="Brief description of the issue"
                      value={formData.title}
                      onChange={(e) => setFormData(prev => ({ ...prev, title: e.target.value }))}
                      required
                    />
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <label className="block text-sm font-medium mb-2">Category</label>
                      <Select value={formData.category} onValueChange={(value) => 
                        setFormData(prev => ({ ...prev, category: value }))
                      }>
                        <SelectTrigger>
                          <SelectValue placeholder="Select category" />
                        </SelectTrigger>
                        <SelectContent>
                          {categories.map((category) => (
                            <SelectItem key={category.value} value={category.value}>
                              <div className="flex items-center gap-2">
                                {category.icon && <category.icon className="w-4 h-4" />}
                                {category.label}
                              </div>
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>

                    <div>
                      <label className="block text-sm font-medium mb-2">Priority Level</label>
                      <Select value={formData.urgency} onValueChange={(value) => 
                        setFormData(prev => ({ ...prev, urgency: value }))
                      }>
                        <SelectTrigger>
                          <SelectValue placeholder="Select priority" />
                        </SelectTrigger>
                        <SelectContent>
                          {urgencyLevels.map((level) => (
                            <SelectItem key={level.value} value={level.value}>
                              <Badge className={level.color}>{level.label}</Badge>
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                  </div>

                  <div>
                    <div className="flex items-center justify-between mb-2">
                      <label className="block text-sm font-medium">Description</label>
                      <Button
                        type="button"
                        variant="outline"
                        size="sm"
                        onClick={handleVoiceToText}
                        className={isRecording ? 'text-red-600 border-red-600' : ''}
                      >
                        {isRecording ? <MicOff className="w-4 h-4 mr-2" /> : <Mic className="w-4 h-4 mr-2" />}
                        {isRecording ? 'Recording...' : 'Voice to Text'}
                      </Button>
                    </div>
                    <Textarea
                      placeholder="Provide detailed description of the issue..."
                      rows={4}
                      value={formData.description}
                      onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
                      required
                    />
                  </div>
                </CardContent>
              </Card>

              {/* File Upload */}
              <Card>
                <CardHeader>
                  <CardTitle>Attach Photos or Videos</CardTitle>
                  <CardDescription>
                    Visual evidence helps city officials understand and prioritize the issue
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  <div 
                    className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center hover:border-gray-400 transition-colors cursor-pointer"
                    onClick={() => fileInputRef.current?.click()}
                  >
                    <Upload className="w-8 h-8 text-gray-400 mx-auto mb-2" />
                    <p className="text-sm font-medium">Click to upload or drag and drop</p>
                    <p className="text-xs text-muted-foreground">PNG, JPG, MP4 up to 10MB each</p>
                  </div>
                  
                  <input
                    ref={fileInputRef}
                    type="file"
                    multiple
                    accept="image/*,video/*"
                    onChange={handleFileUpload}
                    className="hidden"
                  />

                  {files.length > 0 && (
                    <>
                      {uploadProgress > 0 && uploadProgress < 100 && (
                        <div className="mt-4">
                          <div className="flex items-center justify-between text-sm mb-1">
                            <span>Uploading files...</span>
                            <span>{uploadProgress}%</span>
                          </div>
                          <Progress value={uploadProgress} />
                        </div>
                      )}
                      
                      <div className="mt-4 grid grid-cols-2 md:grid-cols-3 gap-3">
                        {files.map((file, index) => (
                          <div key={index} className="relative border rounded-lg p-3">
                            <div className="flex items-center gap-2">
                              {file.type.startsWith('image/') ? (
                                <ImageIcon className="w-4 h-4 text-blue-600" />
                              ) : (
                                <Video className="w-4 h-4 text-purple-600" />
                              )}
                              <span className="text-sm truncate">{file.name}</span>
                            </div>
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              className="absolute top-1 right-1 h-6 w-6 p-0"
                              onClick={() => removeFile(index)}
                            >
                              <X className="w-3 h-3" />
                            </Button>
                          </div>
                        ))}
                      </div>
                    </>
                  )}
                </CardContent>
              </Card>
            </div>

            {/* Location & Submit */}
            <div className="space-y-6">
              <Card>
                <CardHeader>
                  <CardTitle>Location</CardTitle>
                  <CardDescription>
                    Precise location helps with faster response
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex items-center gap-2 p-3 bg-green-50 rounded-lg">
                    <MapPin className="w-4 h-4 text-green-600" />
                    <div className="flex-1">
                      <p className="text-sm font-medium">{formData.location}</p>
                      <p className="text-xs text-muted-foreground">
                        {formData.coordinates.lat.toFixed(4)}, {formData.coordinates.lng.toFixed(4)}
                      </p>
                    </div>
                  </div>
                  
                  <Button
                    type="button"
                    variant="outline"
                    className="w-full"
                    onClick={getCurrentLocation}
                  >
                    <MapPin className="w-4 h-4 mr-2" />
                    Update Location
                  </Button>

                  {/* Mock Mini Map */}
                  <div className="w-full h-32 bg-gray-100 rounded-lg flex items-center justify-center">
                    <div className="text-center text-sm text-muted-foreground">
                      <MapPin className="w-6 h-6 mx-auto mb-1 text-red-500" />
                      Interactive Map View
                    </div>
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Submit Report</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  {/* Anonymous Reporting Option */}
                  <div className="space-y-4">
                    <div className="flex items-center space-x-2">
                      <Checkbox 
                        id="anonymous" 
                        checked={formData.isAnonymous}
                        onCheckedChange={(checked) => 
                          setFormData(prev => ({ ...prev, isAnonymous: !!checked }))
                        }
                      />
                      <label htmlFor="anonymous" className="text-sm font-medium flex items-center gap-2">
                        {formData.isAnonymous ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                        Submit anonymously
                      </label>
                    </div>
                    
                    {/* Priority Accessibility Reporting */}
                    <div className="flex items-center space-x-2">
                      <Checkbox 
                        id="priority-reporting" 
                        checked={formData.isPriorityReporting}
                        onCheckedChange={(checked) => 
                          setFormData(prev => ({ ...prev, isPriorityReporting: !!checked, priorityType: !checked ? '' : prev.priorityType }))
                        }
                      />
                      <label htmlFor="priority-reporting" className="text-sm font-medium">
                        I need priority assistance (Elderly/Disabled)
                      </label>
                    </div>
                    
                    {formData.isPriorityReporting && (
                      <div className="ml-6">
                        <Select value={formData.priorityType} onValueChange={(value) => 
                          setFormData(prev => ({ ...prev, priorityType: value }))
                        }>
                          <SelectTrigger className="w-full">
                            <SelectValue placeholder="Select priority category" />
                          </SelectTrigger>
                          <SelectContent>
                            {priorityTypes.map((type) => (
                              <SelectItem key={type.value} value={type.value}>
                                <div className="flex items-center gap-2">
                                  <type.icon className="w-4 h-4" />
                                  <span>{type.label}</span>
                                </div>
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </div>
                    )}
                  </div>

                  <div className="text-sm text-muted-foreground space-y-2 pt-4 border-t">
                    <div className="flex items-center gap-2">
                      <Check className="w-4 h-4 text-green-600" />
                      <span>{formData.isAnonymous ? 'Anonymous reporting' : 'Email updates on progress'}</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Check className="w-4 h-4 text-green-600" />
                      <span>Report will be public{formData.isAnonymous ? ' (identity protected)' : ''}</span>
                    </div>
                    {formData.isPriorityReporting && (
                      <div className="flex items-center gap-2">
                        <Shield className="w-4 h-4 text-blue-600" />
                        <span>Priority assistance enabled</span>
                      </div>
                    )}
                  </div>

                  <Button 
                    type="submit" 
                    className="w-full" 
                    disabled={isSubmitting || !formData.title || !formData.description}
                  >
                    {isSubmitting ? (
                      <>
                        <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin mr-2" />
                        Submitting...
                      </>
                    ) : (
                      'Submit Report'
                    )}
                  </Button>

                  <Button
                    type="button"
                    variant="outline"
                    className="w-full"
                    onClick={() => onNavigate('dashboard')}
                  >
                    Cancel
                  </Button>
                </CardContent>
              </Card>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}