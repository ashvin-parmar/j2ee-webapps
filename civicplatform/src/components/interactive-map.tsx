import React, { useState } from 'react';
import { Button } from './ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Badge } from './ui/badge';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Checkbox } from './ui/checkbox';
import { ImageWithFallback } from './figma/ImageWithFallback';
import { 
  Map, 
  Layers, 
  Filter,
  MapPin,
  Search,
  Maximize,
  Eye,
  ThumbsUp,
  MessageSquare,
  Construction,
  Lightbulb,
  Trash2,
  TreePine,
  Car,
  AlertTriangle,
  CheckCircle,
  Clock
} from 'lucide-react';

type Page = 'dashboard' | 'report' | 'my-issues' | 'map' | 'community' | 'transparency' | 'profile';

interface InteractiveMapProps {
  onNavigate: (page: Page) => void;
}

interface MapIssue {
  id: string;
  title: string;
  category: string;
  status: 'Pending' | 'In Progress' | 'Resolved';
  urgency: 'low' | 'medium' | 'high' | 'emergency';
  coordinates: { lat: number; lng: number };
  views: number;
  votes: number;
  comments: number;
  image?: string;
  reportDate: string;
  icon: any;
}

export function InteractiveMap({ onNavigate }: InteractiveMapProps) {
  const [selectedIssue, setSelectedIssue] = useState<MapIssue | null>(null);
  const [categoryFilters, setCategoryFilters] = useState({
    roads: true,
    lighting: true,
    sanitation: true,
    parks: true,
    traffic: true
  });
  const [statusFilter, setStatusFilter] = useState('all');
  const [urgencyFilter, setUrgencyFilter] = useState('all');

  const mapIssues: MapIssue[] = [
    {
      id: 'ISS-001',
      title: 'Large pothole causing vehicle damage',
      category: 'Roads & Infrastructure',
      status: 'In Progress',
      urgency: 'high',
      coordinates: { lat: 40.7589, lng: -73.9851 },
      views: 47,
      votes: 23,
      comments: 8,
      image: 'https://images.unsplash.com/photo-1469510090920-fd33379d1f7c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwb3Rob2xlJTIwc3RyZWV0JTIwZGFtYWdlJTIwdXJiYW58ZW58MXx8fHwxNzU3NzAzNTQ3fDA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral',
      reportDate: '2024-12-08',
      icon: Construction
    },
    {
      id: 'ISS-002',
      title: 'Broken streetlight near school',
      category: 'Street Lighting',
      status: 'Resolved',
      urgency: 'medium',
      coordinates: { lat: 40.7614, lng: -73.9776 },
      views: 32,
      votes: 15,
      comments: 4,
      image: 'https://images.unsplash.com/photo-1717394311716-582291b1eaf6?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxicm9rZW4lMjBzdHJlZXRsaWdodCUyMHVyYmFuJTIwbWFpbnRlbmFuY2V8ZW58MXx8fHwxNzU3Njg3MDU3fDA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral',
      reportDate: '2024-12-05',
      icon: Lightbulb
    },
    {
      id: 'ISS-003',
      title: 'Overflowing trash bin',
      category: 'Sanitation',
      status: 'Pending',
      urgency: 'low',
      coordinates: { lat: 40.7505, lng: -73.9934 },
      views: 12,
      votes: 8,
      comments: 2,
      reportDate: '2024-12-09',
      icon: Trash2
    },
    {
      id: 'ISS-004',
      title: 'Damaged playground equipment',
      category: 'Parks & Recreation',
      status: 'In Progress',
      urgency: 'medium',
      coordinates: { lat: 40.7829, lng: -73.9654 },
      views: 89,
      votes: 34,
      comments: 12,
      reportDate: '2024-12-06',
      icon: TreePine
    },
    {
      id: 'ISS-005',
      title: 'Emergency water main break',
      category: 'Infrastructure',
      status: 'In Progress',
      urgency: 'emergency',
      coordinates: { lat: 40.7480, lng: -73.9857 },
      views: 156,
      votes: 67,
      comments: 23,
      reportDate: '2024-12-11',
      icon: AlertTriangle
    }
  ];

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Resolved':
        return 'bg-green-100 text-green-800';
      case 'In Progress':
        return 'bg-blue-100 text-blue-800';
      case 'Pending':
        return 'bg-yellow-100 text-yellow-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getUrgencyColor = (urgency: string) => {
    switch (urgency) {
      case 'emergency':
        return 'bg-red-500 text-white';
      case 'high':
        return 'bg-red-100 text-red-800';
      case 'medium':
        return 'bg-yellow-100 text-yellow-800';
      case 'low':
        return 'bg-green-100 text-green-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getMarkerColor = (urgency: string, status: string) => {
    if (status === 'Resolved') return 'bg-green-600';
    if (urgency === 'emergency') return 'bg-red-600';
    if (urgency === 'high') return 'bg-orange-500';
    if (urgency === 'medium') return 'bg-yellow-500';
    return 'bg-blue-500';
  };

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold">Interactive Map</h1>
          <p className="text-muted-foreground mt-1">
            Explore civic issues across the city on an interactive map
          </p>
        </div>
        <Button onClick={() => onNavigate('report')}>
          Report New Issue
        </Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Filters Panel */}
        <div className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Filter className="w-4 h-4" />
                Filters
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div>
                <label className="text-sm font-medium mb-2 block">Status</label>
                <Select value={statusFilter} onValueChange={setStatusFilter}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">All Status</SelectItem>
                    <SelectItem value="pending">Pending</SelectItem>
                    <SelectItem value="in-progress">In Progress</SelectItem>
                    <SelectItem value="resolved">Resolved</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div>
                <label className="text-sm font-medium mb-2 block">Priority</label>
                <Select value={urgencyFilter} onValueChange={setUrgencyFilter}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="all">All Priorities</SelectItem>
                    <SelectItem value="emergency">Emergency</SelectItem>
                    <SelectItem value="high">High</SelectItem>
                    <SelectItem value="medium">Medium</SelectItem>
                    <SelectItem value="low">Low</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div>
                <label className="text-sm font-medium mb-3 block">Categories</label>
                <div className="space-y-3">
                  <div className="flex items-center space-x-2">
                    <Checkbox 
                      id="roads" 
                      checked={categoryFilters.roads}
                      onCheckedChange={(checked) => 
                        setCategoryFilters(prev => ({ ...prev, roads: !!checked }))
                      }
                    />
                    <label htmlFor="roads" className="text-sm flex items-center gap-2">
                      <Construction className="w-4 h-4" />
                      Roads & Infrastructure
                    </label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Checkbox 
                      id="lighting"
                      checked={categoryFilters.lighting}
                      onCheckedChange={(checked) => 
                        setCategoryFilters(prev => ({ ...prev, lighting: !!checked }))
                      }
                    />
                    <label htmlFor="lighting" className="text-sm flex items-center gap-2">
                      <Lightbulb className="w-4 h-4" />
                      Street Lighting
                    </label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Checkbox 
                      id="sanitation"
                      checked={categoryFilters.sanitation}
                      onCheckedChange={(checked) => 
                        setCategoryFilters(prev => ({ ...prev, sanitation: !!checked }))
                      }
                    />
                    <label htmlFor="sanitation" className="text-sm flex items-center gap-2">
                      <Trash2 className="w-4 h-4" />
                      Sanitation
                    </label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Checkbox 
                      id="parks"
                      checked={categoryFilters.parks}
                      onCheckedChange={(checked) => 
                        setCategoryFilters(prev => ({ ...prev, parks: !!checked }))
                      }
                    />
                    <label htmlFor="parks" className="text-sm flex items-center gap-2">
                      <TreePine className="w-4 h-4" />
                      Parks & Recreation
                    </label>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Checkbox 
                      id="traffic"
                      checked={categoryFilters.traffic}
                      onCheckedChange={(checked) => 
                        setCategoryFilters(prev => ({ ...prev, traffic: !!checked }))
                      }
                    />
                    <label htmlFor="traffic" className="text-sm flex items-center gap-2">
                      <Car className="w-4 h-4" />
                      Traffic & Parking
                    </label>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Legend</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="flex items-center gap-2 text-sm">
                <div className="w-3 h-3 rounded-full bg-red-600"></div>
                <span>Emergency</span>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <div className="w-3 h-3 rounded-full bg-orange-500"></div>
                <span>High Priority</span>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <div className="w-3 h-3 rounded-full bg-yellow-500"></div>
                <span>Medium Priority</span>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <div className="w-3 h-3 rounded-full bg-blue-500"></div>
                <span>Low Priority</span>
              </div>
              <div className="flex items-center gap-2 text-sm">
                <div className="w-3 h-3 rounded-full bg-green-600"></div>
                <span>Resolved</span>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Map Area */}
        <div className="lg:col-span-3">
          <Card className="h-[600px]">
            <CardHeader className="pb-2">
              <div className="flex items-center justify-between">
                <CardTitle className="flex items-center gap-2">
                  <Map className="w-5 h-5" />
                  City Issues Map
                </CardTitle>
                <div className="flex items-center gap-2">
                  <Button variant="outline" size="sm">
                    <Layers className="w-4 h-4 mr-2" />
                    Layers
                  </Button>
                  <Button variant="outline" size="sm">
                    <Maximize className="w-4 h-4 mr-2" />
                    Fullscreen
                  </Button>
                </div>
              </div>
            </CardHeader>
            <CardContent className="p-0 relative">
              {/* Mock Map Background */}
              <div className="w-full h-[500px] bg-gray-100 relative overflow-hidden rounded-b-lg">
                <div className="absolute inset-0 bg-gradient-to-br from-green-50 to-blue-50">
                  {/* Mock street grid */}
                  <svg className="absolute inset-0 w-full h-full opacity-20">
                    <defs>
                      <pattern id="grid" width="50" height="50" patternUnits="userSpaceOnUse">
                        <path d="M 50 0 L 0 0 0 50" fill="none" stroke="#333" strokeWidth="1"/>
                      </pattern>
                    </defs>
                    <rect width="100%" height="100%" fill="url(#grid)" />
                  </svg>
                  
                  {/* Issue Markers */}
                  {mapIssues.map((issue, index) => (
                    <div
                      key={issue.id}
                      className={`absolute transform -translate-x-1/2 -translate-y-1/2 cursor-pointer hover:scale-110 transition-transform ${
                        selectedIssue?.id === issue.id ? 'z-20 scale-125' : 'z-10'
                      }`}
                      style={{
                        left: `${20 + (index * 15)}%`,
                        top: `${30 + (index * 10)}%`
                      }}
                      onClick={() => setSelectedIssue(issue)}
                    >
                      <div className={`w-6 h-6 rounded-full ${getMarkerColor(issue.urgency, issue.status)} 
                        flex items-center justify-center shadow-lg border-2 border-white`}>
                        <issue.icon className="w-3 h-3 text-white" />
                      </div>
                      {issue.urgency === 'emergency' && (
                        <div className="absolute -top-1 -right-1 w-3 h-3 bg-red-500 rounded-full animate-pulse"></div>
                      )}
                    </div>
                  ))}
                  
                  {/* Clustering indicator */}
                  <div className="absolute top-[20%] left-[60%] transform -translate-x-1/2 -translate-y-1/2">
                    <div className="w-8 h-8 rounded-full bg-blue-600 text-white flex items-center justify-center shadow-lg border-2 border-white cursor-pointer hover:scale-110 transition-transform">
                      <span className="text-xs font-bold">3</span>
                    </div>
                  </div>
                </div>
                
                {/* Map Controls */}
                <div className="absolute top-4 right-4 space-y-2">
                  <Button variant="outline" size="sm" className="w-8 h-8 p-0">+</Button>
                  <Button variant="outline" size="sm" className="w-8 h-8 p-0">-</Button>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>

      {/* Selected Issue Details */}
      {selectedIssue && (
        <Card>
          <CardHeader>
            <div className="flex items-start justify-between">
              <div className="flex items-start gap-3">
                <div className={`p-2 rounded-full ${getMarkerColor(selectedIssue.urgency, selectedIssue.status)}`}>
                  <selectedIssue.icon className="w-5 h-5 text-white" />
                </div>
                <div>
                  <CardTitle>{selectedIssue.title}</CardTitle>
                  <CardDescription>
                    #{selectedIssue.id} • {selectedIssue.category}
                  </CardDescription>
                </div>
              </div>
              <Button variant="ghost" size="sm" onClick={() => setSelectedIssue(null)}>
                ×
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div className="md:col-span-2 space-y-4">
                <div className="flex items-center gap-2">
                  <Badge className={getStatusColor(selectedIssue.status)}>
                    {selectedIssue.status === 'Resolved' && <CheckCircle className="w-3 h-3 mr-1" />}
                    {selectedIssue.status === 'In Progress' && <Clock className="w-3 h-3 mr-1" />}
                    {selectedIssue.status}
                  </Badge>
                  <Badge className={getUrgencyColor(selectedIssue.urgency)}>
                    {selectedIssue.urgency} priority
                  </Badge>
                </div>
                
                <div className="flex items-center gap-6 text-sm text-muted-foreground">
                  <div className="flex items-center gap-1">
                    <Eye className="w-4 h-4" />
                    {selectedIssue.views} views
                  </div>
                  <div className="flex items-center gap-1">
                    <ThumbsUp className="w-4 h-4" />
                    {selectedIssue.votes} votes
                  </div>
                  <div className="flex items-center gap-1">
                    <MessageSquare className="w-4 h-4" />
                    {selectedIssue.comments} comments
                  </div>
                  <div className="flex items-center gap-1">
                    <MapPin className="w-4 h-4" />
                    {selectedIssue.coordinates.lat.toFixed(4)}, {selectedIssue.coordinates.lng.toFixed(4)}
                  </div>
                </div>

                <p className="text-sm text-muted-foreground">
                  Reported on {new Date(selectedIssue.reportDate).toLocaleDateString()}
                </p>

                <div className="flex gap-2">
                  <Button size="sm" onClick={() => onNavigate('community')}>
                    <ThumbsUp className="w-4 h-4 mr-2" />
                    Validate Issue
                  </Button>
                  <Button variant="outline" size="sm">
                    <MessageSquare className="w-4 h-4 mr-2" />
                    Add Comment
                  </Button>
                </div>
              </div>
              
              {selectedIssue.image && (
                <div>
                  <ImageWithFallback
                    src={selectedIssue.image}
                    alt={selectedIssue.title}
                    className="w-full h-32 object-cover rounded-lg"
                  />
                </div>
              )}
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}