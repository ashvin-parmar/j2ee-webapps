import React, { useState } from 'react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Badge } from './ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from './ui/avatar';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './ui/tabs';
import { Calendar } from './ui/calendar';
import { 
  Calendar as CalendarIcon,
  MapPin,
  Users,
  Clock,
  ArrowLeft,
  Search,
  Filter,
  ExternalLink,
  Heart,
  Share2,
  Bell,
  Trash2,
  TreePine,
  Lightbulb,
  HandHeart,
  Recycle,
  School,
  Building2,
  Star,
  CheckCircle2,
  UserPlus
} from 'lucide-react';

type Page = 'dashboard' | 'report' | 'my-issues' | 'map' | 'community' | 'transparency' | 'profile' | 'voice-reporting' | 'campaigns' | 'collaboration';

interface CampaignEvent {
  id: string;
  title: string;
  description: string;
  type: 'cleanup' | 'awareness' | 'plantation' | 'community' | 'education' | 'infrastructure';
  status: 'upcoming' | 'ongoing' | 'completed' | 'cancelled';
  startDate: Date;
  endDate?: Date;
  location: string;
  organizer: string;
  participants: number;
  maxParticipants?: number;
  isRegistered: boolean;
  tags: string[];
  imageUrl?: string;
  requirements?: string[];
  benefits: string[];
}

interface CampaignsProps {
  onNavigate: (page: Page) => void;
}

export function Campaigns({ onNavigate }: CampaignsProps) {
  const [activeTab, setActiveTab] = useState('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedDate, setSelectedDate] = useState<Date | undefined>(new Date());

  const mockCampaigns: CampaignEvent[] = [
    {
      id: '1',
      title: 'Downtown Clean-Up Drive',
      description: 'Join us for a community-wide effort to clean and beautify our downtown area. We\'ll provide all supplies including gloves, trash bags, and cleaning equipment.',
      type: 'cleanup',
      status: 'upcoming',
      startDate: new Date('2024-02-15T09:00:00'),
      endDate: new Date('2024-02-15T16:00:00'),
      location: 'Downtown Central Park',
      organizer: 'City Environmental Department',
      participants: 47,
      maxParticipants: 100,
      isRegistered: false,
      tags: ['Environment', 'Community', 'Volunteering'],
      requirements: ['Comfortable walking shoes', 'Water bottle', 'Sun protection'],
      benefits: ['Community service hours', 'Free lunch', 'Certificate of participation']
    },
    {
      id: '2',
      title: 'Tree Plantation Campaign 2024',
      description: 'Help us plant 500 trees across the city to improve air quality and create a greener environment for future generations.',
      type: 'plantation',
      status: 'upcoming',
      startDate: new Date('2024-02-20T08:00:00'),
      endDate: new Date('2024-02-20T17:00:00'),
      location: 'Multiple locations citywide',
      organizer: 'Green City Initiative',
      participants: 123,
      maxParticipants: 200,
      isRegistered: true,
      tags: ['Environment', 'Green Initiative', 'Climate Action'],
      requirements: ['Work gloves recommended', 'Comfortable outdoor clothing'],
      benefits: ['Free breakfast and lunch', 'Planting certificate', 'Follow-up tree care training']
    },
    {
      id: '3',
      title: 'Water Conservation Awareness Program',
      description: 'Educational program about water conservation techniques, rainwater harvesting, and sustainable water usage in homes and businesses.',
      type: 'awareness',
      status: 'ongoing',
      startDate: new Date('2024-01-10T10:00:00'),
      endDate: new Date('2024-03-10T18:00:00'),
      location: 'Community Center & Online',
      organizer: 'Water Management Authority',
      participants: 89,
      isRegistered: true,
      tags: ['Education', 'Sustainability', 'Water Conservation'],
      benefits: ['Free water conservation kit', 'Home audit voucher', 'Educational materials']
    },
    {
      id: '4',
      title: 'Senior Citizen Digital Literacy Workshop',
      description: 'Teaching our elderly citizens how to use smartphones, tablets, and digital government services to stay connected and access services.',
      type: 'education',
      status: 'upcoming',
      startDate: new Date('2024-02-25T14:00:00'),
      endDate: new Date('2024-02-25T17:00:00'),
      location: 'Public Library - Main Branch',
      organizer: 'Digital Inclusion Committee',
      participants: 23,
      maxParticipants: 50,
      isRegistered: false,
      tags: ['Education', 'Digital Literacy', 'Senior Citizens'],
      requirements: ['Bring your own device if available', 'Notebook and pen'],
      benefits: ['Free device training', 'Ongoing support group access', 'Printed guides']
    },
    {
      id: '5',
      title: 'Community Garden Project',
      description: 'Transform unused city lots into productive community gardens where residents can grow fresh vegetables and build neighborhood connections.',
      type: 'community',
      status: 'completed',
      startDate: new Date('2023-11-15T09:00:00'),
      endDate: new Date('2023-12-15T17:00:00'),
      location: '5th Street Community Lot',
      organizer: 'Neighborhood Association',
      participants: 67,
      isRegistered: false,
      tags: ['Community Building', 'Food Security', 'Gardening'],
      benefits: ['Fresh produce sharing', 'Gardening skills', 'Community connections']
    },
    {
      id: '6',
      title: 'Road Safety Awareness Week',
      description: 'Educational campaign about pedestrian safety, bicycle safety, and responsible driving to reduce accidents in our community.',
      type: 'awareness',
      status: 'upcoming',
      startDate: new Date('2024-03-01T08:00:00'),
      endDate: new Date('2024-03-07T20:00:00'),
      location: 'Various schools and community centers',
      organizer: 'Traffic Safety Department',
      participants: 156,
      isRegistered: false,
      tags: ['Safety', 'Education', 'Transportation'],
      benefits: ['Safety materials', 'Free reflective gear', 'Bicycle safety check']
    }
  ];

  const [campaigns] = useState<CampaignEvent[]>(mockCampaigns);

  const getTypeIcon = (type: string) => {
    switch (type) {
      case 'cleanup': return <Trash2 className="w-4 h-4" />;
      case 'plantation': return <TreePine className="w-4 h-4" />;
      case 'awareness': return <Lightbulb className="w-4 h-4" />;
      case 'community': return <HandHeart className="w-4 h-4" />;
      case 'education': return <School className="w-4 h-4" />;
      case 'infrastructure': return <Building2 className="w-4 h-4" />;
      default: return <Calendar className="w-4 h-4" />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'upcoming': return 'bg-blue-100 text-blue-800';
      case 'ongoing': return 'bg-green-100 text-green-800';
      case 'completed': return 'bg-gray-100 text-gray-800';
      case 'cancelled': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const filteredCampaigns = campaigns.filter(campaign => {
    const matchesSearch = campaign.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         campaign.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         campaign.organizer.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         campaign.tags.some(tag => tag.toLowerCase().includes(searchQuery.toLowerCase()));
    
    if (activeTab === 'upcoming') {
      return matchesSearch && campaign.status === 'upcoming';
    } else if (activeTab === 'ongoing') {
      return matchesSearch && campaign.status === 'ongoing';
    } else if (activeTab === 'completed') {
      return matchesSearch && campaign.status === 'completed';
    } else if (activeTab === 'registered') {
      return matchesSearch && campaign.isRegistered;
    }
    
    return matchesSearch;
  });

  const formatDateTime = (date: Date) => {
    return date.toLocaleString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatDate = (date: Date) => {
    return date.toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  };

  const toggleRegistration = (campaignId: string) => {
    // Mock toggle registration - in real app, this would make an API call
    console.log(`Toggling registration for campaign ${campaignId}`);
  };

  return (
    <div className="p-6">
      <div className="max-w-7xl mx-auto">
        <div className="mb-6 flex items-center justify-between">
          <div className="flex items-center gap-4">
            <Button
              variant="ghost"
              onClick={() => onNavigate('dashboard')}
              className="p-2"
            >
              <ArrowLeft className="w-4 h-4" />
            </Button>
            <div>
              <h1 className="text-3xl font-bold flex items-center gap-2">
                <CalendarIcon className="w-8 h-8 text-green-600" />
                Campaigns & Events
              </h1>
              <p className="text-muted-foreground mt-1">
                Join municipal social drives and community initiatives
              </p>
            </div>
          </div>
        </div>

        {/* Search and Filters */}
        <div className="mb-6 flex flex-col lg:flex-row gap-4">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Search campaigns, events, or organizers..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10"
            />
          </div>
          <Button variant="outline" className="flex items-center gap-2">
            <Filter className="w-4 h-4" />
            Filters
          </Button>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
          {/* Calendar Sidebar */}
          <div className="lg:col-span-1">
            <Card>
              <CardHeader className="pb-3">
                <CardTitle className="text-lg">Event Calendar</CardTitle>
              </CardHeader>
              <CardContent>
                <Calendar
                  mode="single"
                  selected={selectedDate}
                  onSelect={setSelectedDate}
                  className="rounded-md border-0 p-0"
                />
              </CardContent>
            </Card>

            <Card className="mt-4">
              <CardHeader className="pb-3">
                <CardTitle className="text-lg">Quick Stats</CardTitle>
              </CardHeader>
              <CardContent className="space-y-3">
                <div className="flex justify-between items-center">
                  <span className="text-sm">Active Events</span>
                  <Badge variant="secondary">{campaigns.filter(c => c.status === 'ongoing').length}</Badge>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-sm">Upcoming</span>
                  <Badge variant="secondary">{campaigns.filter(c => c.status === 'upcoming').length}</Badge>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-sm">My Registrations</span>
                  <Badge variant="secondary">{campaigns.filter(c => c.isRegistered).length}</Badge>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-sm">Total Participants</span>
                  <Badge variant="secondary">{campaigns.reduce((sum, c) => sum + c.participants, 0)}</Badge>
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Main Content */}
          <div className="lg:col-span-3">
            <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-6">
              <TabsList className="grid w-full grid-cols-5">
                <TabsTrigger value="all">All ({campaigns.length})</TabsTrigger>
                <TabsTrigger value="upcoming">Upcoming ({campaigns.filter(c => c.status === 'upcoming').length})</TabsTrigger>
                <TabsTrigger value="ongoing">Active ({campaigns.filter(c => c.status === 'ongoing').length})</TabsTrigger>
                <TabsTrigger value="completed">Completed ({campaigns.filter(c => c.status === 'completed').length})</TabsTrigger>
                <TabsTrigger value="registered">Mine ({campaigns.filter(c => c.isRegistered).length})</TabsTrigger>
              </TabsList>

              <TabsContent value={activeTab}>
                {filteredCampaigns.length === 0 ? (
                  <Card className="p-12 text-center">
                    <CalendarIcon className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
                    <h3 className="text-lg font-medium mb-2">No campaigns found</h3>
                    <p className="text-muted-foreground">
                      {searchQuery ? 'Try adjusting your search terms.' : 'Check back later for new campaigns and events.'}
                    </p>
                  </Card>
                ) : (
                  <div className="space-y-6">
                    {filteredCampaigns.map((campaign) => (
                      <Card key={campaign.id} className="hover:shadow-md transition-shadow">
                        <CardHeader className="pb-4">
                          <div className="flex items-start justify-between gap-4">
                            <div className="flex-1">
                              <div className="flex items-center gap-2 mb-2">
                                {getTypeIcon(campaign.type)}
                                <CardTitle className="text-xl">{campaign.title}</CardTitle>
                                {campaign.isRegistered && (
                                  <CheckCircle2 className="w-5 h-5 text-green-600" />
                                )}
                              </div>
                              <div className="flex items-center gap-2">
                                <Badge className={getStatusColor(campaign.status)}>
                                  {campaign.status.charAt(0).toUpperCase() + campaign.status.slice(1)}
                                </Badge>
                                <Badge variant="outline" className="capitalize">
                                  {campaign.type.replace('-', ' ')}
                                </Badge>
                                {campaign.maxParticipants && campaign.participants >= campaign.maxParticipants && (
                                  <Badge variant="secondary">Full</Badge>
                                )}
                              </div>
                            </div>
                            <div className="flex items-center gap-1">
                              <Button variant="ghost" size="sm" className="p-1">
                                <Share2 className="w-4 h-4" />
                              </Button>
                              <Button variant="ghost" size="sm" className="p-1">
                                <Bell className="w-4 h-4" />
                              </Button>
                            </div>
                          </div>
                        </CardHeader>
                        
                        <CardContent className="space-y-4">
                          <p className="text-muted-foreground">
                            {campaign.description}
                          </p>

                          {/* Event Details */}
                          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 py-4 border-t border-b">
                            <div className="flex items-center gap-2">
                              <CalendarIcon className="w-4 h-4 text-muted-foreground" />
                              <div>
                                <p className="text-sm font-medium">
                                  {formatDateTime(campaign.startDate)}
                                </p>
                                {campaign.endDate && (
                                  <p className="text-xs text-muted-foreground">
                                    Until {formatDate(campaign.endDate)}
                                  </p>
                                )}
                              </div>
                            </div>
                            
                            <div className="flex items-center gap-2">
                              <MapPin className="w-4 h-4 text-muted-foreground" />
                              <div>
                                <p className="text-sm font-medium">{campaign.location}</p>
                                <p className="text-xs text-muted-foreground">{campaign.organizer}</p>
                              </div>
                            </div>
                            
                            <div className="flex items-center gap-2">
                              <Users className="w-4 h-4 text-muted-foreground" />
                              <div>
                                <p className="text-sm font-medium">
                                  {campaign.participants} registered
                                  {campaign.maxParticipants && ` / ${campaign.maxParticipants}`}
                                </p>
                                <p className="text-xs text-muted-foreground">participants</p>
                              </div>
                            </div>
                          </div>

                          {/* Tags */}
                          <div className="flex flex-wrap gap-2">
                            {campaign.tags.map((tag, index) => (
                              <Badge key={index} variant="outline" className="text-xs">
                                {tag}
                              </Badge>
                            ))}
                          </div>

                          {/* Requirements & Benefits */}
                          {(campaign.requirements || campaign.benefits) && (
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                              {campaign.requirements && (
                                <div>
                                  <h4 className="font-medium mb-2">Requirements:</h4>
                                  <ul className="text-muted-foreground space-y-1">
                                    {campaign.requirements.map((req, index) => (
                                      <li key={index} className="flex items-center gap-2">
                                        <div className="w-1.5 h-1.5 bg-muted-foreground rounded-full" />
                                        {req}
                                      </li>
                                    ))}
                                  </ul>
                                </div>
                              )}
                              
                              {campaign.benefits && (
                                <div>
                                  <h4 className="font-medium mb-2">Benefits:</h4>
                                  <ul className="text-muted-foreground space-y-1">
                                    {campaign.benefits.map((benefit, index) => (
                                      <li key={index} className="flex items-center gap-2">
                                        <Star className="w-3 h-3 text-yellow-500" />
                                        {benefit}
                                      </li>
                                    ))}
                                  </ul>
                                </div>
                              )}
                            </div>
                          )}

                          {/* Action Buttons */}
                          <div className="flex gap-2 pt-4">
                            {campaign.status === 'upcoming' && (
                              <Button
                                className="flex-1"
                                onClick={() => toggleRegistration(campaign.id)}
                                disabled={campaign.maxParticipants && campaign.participants >= campaign.maxParticipants}
                              >
                                {campaign.isRegistered ? (
                                  <>
                                    <CheckCircle2 className="w-4 h-4 mr-2" />
                                    Registered
                                  </>
                                ) : (
                                  <>
                                    <UserPlus className="w-4 h-4 mr-2" />
                                    {campaign.maxParticipants && campaign.participants >= campaign.maxParticipants 
                                      ? 'Full' 
                                      : 'Register'
                                    }
                                  </>
                                )}
                              </Button>
                            )}
                            <Button variant="outline" className="flex-1">
                              <ExternalLink className="w-4 h-4 mr-2" />
                              Learn More
                            </Button>
                          </div>
                        </CardContent>
                      </Card>
                    ))}
                  </div>
                )}
              </TabsContent>
            </Tabs>
          </div>
        </div>
      </div>
    </div>
  );
}