import React, { useState, useRef } from 'react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Textarea } from './ui/textarea';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Progress } from './ui/progress';
import { Badge } from './ui/badge';
import { DialogHeader, DialogTitle, DialogDescription } from './ui/dialog';
import { 
  AlertTriangle, 
  Camera, 
  MapPin, 
  Clock, 
  Check, 
  Upload,
  Phone,
  Shield,
  Zap,
  Flame,
  Droplets,
  Car,
  Building
} from 'lucide-react';

interface EmergencyReportingProps {
  onClose: () => void;
}

export function EmergencyReporting({ onClose }: EmergencyReportingProps) {
  const [formData, setFormData] = useState({
    type: '',
    title: '',
    description: '',
    location: 'Current Location (GPS)',
    coordinates: { lat: 40.7128, lng: -74.0060 }
  });
  const [photo, setPhoto] = useState<File | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitProgress, setSubmitProgress] = useState(0);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const emergencyTypes = [
    { 
      value: 'fire', 
      label: 'Fire Hazard', 
      icon: Flame, 
      color: 'text-red-600',
      description: 'Fire, smoke, or fire hazards'
    },
    { 
      value: 'water', 
      label: 'Water Emergency', 
      icon: Droplets, 
      color: 'text-blue-600',
      description: 'Water main break, flooding'
    },
    { 
      value: 'electrical', 
      label: 'Electrical Hazard', 
      icon: Zap, 
      color: 'text-yellow-600',
      description: 'Downed power lines, electrical issues'
    },
    { 
      value: 'structural', 
      label: 'Structural Damage', 
      icon: Building, 
      color: 'text-orange-600',
      description: 'Building damage, fallen trees'
    },
    { 
      value: 'traffic', 
      label: 'Traffic Emergency', 
      icon: Car, 
      color: 'text-purple-600',
      description: 'Road blockage, traffic hazards'
    },
    { 
      value: 'other', 
      label: 'Other Emergency', 
      icon: AlertTriangle, 
      color: 'text-gray-600',
      description: 'Other urgent public safety issues'
    }
  ];

  const handlePhotoCapture = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setPhoto(file);
    }
  };

  const handleQuickSubmit = () => {
    if (!formData.type || !formData.title) return;
    
    setIsSubmitting(true);
    setSubmitProgress(0);

    // Mock submission with progress
    const interval = setInterval(() => {
      setSubmitProgress(prev => {
        if (prev >= 100) {
          clearInterval(interval);
          setTimeout(() => {
            setIsSubmitting(false);
            onClose();
            // Show success notification or redirect
          }, 500);
          return 100;
        }
        return prev + 20;
      });
    }, 300);
  };

  const selectedType = emergencyTypes.find(type => type.value === formData.type);

  return (
    <div className="space-y-6">
      <DialogHeader>
        <DialogTitle className="flex items-center gap-2 text-red-600">
          <AlertTriangle className="w-5 h-5" />
          Emergency Report
        </DialogTitle>
        <DialogDescription>
          Report urgent issues that require immediate attention from city services or emergency responders.
        </DialogDescription>
      </DialogHeader>

      {/* Emergency Warning */}
      <div className="bg-red-50 border border-red-200 p-4 rounded-lg">
        <div className="flex items-start gap-3">
          <Shield className="w-5 h-5 text-red-600 mt-0.5" />
          <div>
            <h3 className="font-medium text-red-900">For Life-Threatening Emergencies</h3>
            <p className="text-sm text-red-800 mt-1">
              If this is a life-threatening emergency, please call 911 immediately. 
              This system is for non-life-threatening urgent civic issues.
            </p>
            <div className="flex items-center gap-2 mt-2">
              <Button 
                size="sm" 
                className="bg-red-600 hover:bg-red-700 text-white"
                onClick={() => window.open('tel:911')}
              >
                <Phone className="w-4 h-4 mr-2" />
                Call 911
              </Button>
            </div>
          </div>
        </div>
      </div>

      <div className="space-y-4">
        {/* Emergency Type Selection */}
        <div>
          <label className="block text-sm font-medium mb-3">Emergency Type</label>
          <div className="grid grid-cols-2 gap-3">
            {emergencyTypes.map((type) => (
              <button
                key={type.value}
                type="button"
                onClick={() => setFormData(prev => ({ ...prev, type: type.value }))}
                className={`p-3 border rounded-lg text-left transition-all hover:border-gray-300 ${
                  formData.type === type.value 
                    ? 'border-red-500 bg-red-50' 
                    : 'border-gray-200'
                }`}
              >
                <div className="flex items-center gap-2 mb-1">
                  <type.icon className={`w-4 h-4 ${type.color}`} />
                  <span className="font-medium text-sm">{type.label}</span>
                </div>
                <p className="text-xs text-muted-foreground">{type.description}</p>
              </button>
            ))}
          </div>
        </div>

        {/* Title */}
        <div>
          <label className="block text-sm font-medium mb-2">Emergency Title</label>
          <Input
            placeholder="Brief description of the emergency"
            value={formData.title}
            onChange={(e) => setFormData(prev => ({ ...prev, title: e.target.value }))}
            required
          />
        </div>

        {/* Description */}
        <div>
          <label className="block text-sm font-medium mb-2">Details</label>
          <Textarea
            placeholder="Provide specific details about the emergency..."
            rows={3}
            value={formData.description}
            onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
          />
        </div>

        {/* Photo Capture */}
        <div>
          <label className="block text-sm font-medium mb-2">Photo Evidence (Optional)</label>
          <div className="flex items-center gap-3">
            <Button
              type="button"
              variant="outline"
              onClick={() => fileInputRef.current?.click()}
              className="flex-1"
            >
              <Camera className="w-4 h-4 mr-2" />
              {photo ? 'Photo Captured' : 'Take Photo'}
            </Button>
            {photo && (
              <Badge variant="outline" className="text-green-600">
                <Check className="w-3 h-3 mr-1" />
                Ready
              </Badge>
            )}
          </div>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            capture="environment"
            onChange={handlePhotoCapture}
            className="hidden"
          />
        </div>

        {/* Location */}
        <div>
          <div className="flex items-center gap-2 p-3 bg-green-50 rounded-lg border border-green-200">
            <MapPin className="w-4 h-4 text-green-600" />
            <div className="flex-1">
              <p className="text-sm font-medium text-green-900">{formData.location}</p>
              <p className="text-xs text-green-700">
                {formData.coordinates.lat.toFixed(4)}, {formData.coordinates.lng.toFixed(4)}
              </p>
            </div>
            <Badge className="bg-green-100 text-green-800">
              <Clock className="w-3 h-3 mr-1" />
              Live GPS
            </Badge>
          </div>
        </div>

        {/* Submission Progress */}
        {isSubmitting && (
          <div className="space-y-2">
            <div className="flex items-center justify-between text-sm">
              <span>Submitting emergency report...</span>
              <span>{submitProgress}%</span>
            </div>
            <Progress value={submitProgress} />
            <p className="text-xs text-muted-foreground">
              Emergency reports are prioritized and sent directly to relevant city departments.
            </p>
          </div>
        )}

        {/* Priority Information */}
        {selectedType && (
          <div className="bg-yellow-50 border border-yellow-200 p-3 rounded-lg">
            <div className="flex items-start gap-2">
              <selectedType.icon className={`w-4 h-4 mt-0.5 ${selectedType.color}`} />
              <div>
                <h4 className="text-sm font-medium text-yellow-900">
                  {selectedType.label} - Priority Response
                </h4>
                <p className="text-xs text-yellow-800 mt-1">
                  This type of emergency typically receives a response within 1-2 hours. 
                  You'll receive immediate confirmation and regular updates.
                </p>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Action Buttons */}
      <div className="flex gap-3 pt-4 border-t">
        <Button
          variant="outline"
          onClick={onClose}
          disabled={isSubmitting}
          className="flex-1"
        >
          Cancel
        </Button>
        <Button
          onClick={handleQuickSubmit}
          disabled={!formData.type || !formData.title || isSubmitting}
          className="flex-1 bg-red-600 hover:bg-red-700 text-white"
        >
          {isSubmitting ? (
            <>
              <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin mr-2" />
              Submitting...
            </>
          ) : (
            <>
              <AlertTriangle className="w-4 h-4 mr-2" />
              Submit Emergency Report
            </>
          )}
        </Button>
      </div>

      {/* Contact Information */}
      <div className="text-center text-xs text-muted-foreground pt-2 border-t">
        <p>Emergency reports are sent directly to city dispatch.</p>
        <p>For immediate life-threatening emergencies, always call 911.</p>
      </div>
    </div>
  );
}