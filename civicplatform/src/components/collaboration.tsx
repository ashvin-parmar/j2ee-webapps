import React, { useState } from 'react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Textarea } from './ui/textarea';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Badge } from './ui/badge';
import { Avatar, AvatarFallback, AvatarImage } from './ui/avatar';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './ui/tabs';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from './ui/dialog';
import { 
  Users, 
  Plus, 
  Search, 
  MessageCircle, 
  UserPlus, 
  MapPin, 
  Calendar,
  Star,
  ArrowLeft,
  Share2,
  Bell,
  CheckCircle2,
  Clock,
  AlertCircle,
  Eye,
  Trash2,
  Edit,
  Crown,
  Shield
} from 'lucide-react';

type Page = 'dashboard' | 'report' | 'my-issues' | 'map' | 'community' | 'transparency' | 'profile' | 'voice-reporting' | 'campaigns' | 'collaboration';

interface CollaborationProps {
  onNavigate: (page: Page) => void;
}

interface CollaborativeIssue {
  id: string;
  title: string;
  description: string;
  category: string;
  location: string;
  status: 'open' | 'in-progress' | 'resolved' | 'pending';
  priority: 'low' | 'medium' | 'high' | 'urgent';
  createdAt: Date;
  updatedAt: Date;
  collaborators: Array<{
    id: string;
    name: string;
    role: 'creator' | 'collaborator' | 'supporter';
    avatar?: string;
    joinedAt: Date;
  }>;
  updates: Array<{
    id: string;
    author: string;
    message: string;
    timestamp: Date;
    type: 'comment' | 'status_change' | 'file_upload' | 'collaborator_added';
  }>;
  supporters: number;
  isPublic: boolean;
}

export function Collaboration({ onNavigate }: CollaborationProps) {
  const [activeTab, setActiveTab] = useState('active');
  const [searchQuery, setSearchQuery] = useState('');
  const [showCreateGroup, setShowCreateGroup] = useState(false);

  const mockCollaborativeIssues: CollaborativeIssue[] = [
    {
      id: '1',
      title: 'Downtown Park Restoration Initiative',
      description: 'Collaborative effort to restore the downtown park including new playground equipment, landscaping, and lighting improvements.',
      category: 'parks',
      location: 'Downtown Park, Main Street',
      status: 'in-progress',
      priority: 'high',
      createdAt: new Date('2024-01-15'),
      updatedAt: new Date('2024-01-20'),
      collaborators: [
        { id: '1', name: 'Sarah Johnson', role: 'creator', avatar: '', joinedAt: new Date('2024-01-15') },
        { id: '2', name: 'Mike Chen', role: 'collaborator', avatar: '', joinedAt: new Date('2024-01-16') },
        { id: '3', name: 'Emily Davis', role: 'collaborator', avatar: '', joinedAt: new Date('2024-01-17') },
        { id: '4', name: 'Robert Wilson', role: 'supporter', avatar: '', joinedAt: new Date('2024-01-18') }
      ],
      updates: [
        { id: '1', author: 'Sarah Johnson', message: 'Created the collaborative issue and invited initial team members', timestamp: new Date('2024-01-15T10:00:00'), type: 'comment' },
        { id: '2', author: 'Mike Chen', message: 'Added photos of current park conditions', timestamp: new Date('2024-01-16T14:30:00'), type: 'file_upload' },
        { id: '3', author: 'Emily Davis', message: 'Status updated to in-progress', timestamp: new Date('2024-01-17T09:15:00'), type: 'status_change' },
        { id: '4', author: 'Sarah Johnson', message: 'City council has approved our proposal! Next step is budget allocation.', timestamp: new Date('2024-01-20T11:45:00'), type: 'comment' }
      ],
      supporters: 47,
      isPublic: true
    },
    {
      id: '2',
      title: 'Neighborhood Street Lighting Campaign',
      description: 'Group effort to improve street lighting safety on Oak Avenue and surrounding residential streets.',
      category: 'lighting',
      location: 'Oak Avenue Neighborhood',
      status: 'open',
      priority: 'medium',
      createdAt: new Date('2024-01-10'),
      updatedAt: new Date('2024-01-19'),
      collaborators: [
        { id: '5', name: 'David Park', role: 'creator', avatar: '', joinedAt: new Date('2024-01-10') },
        { id: '6', name: 'Lisa Thompson', role: 'collaborator', avatar: '', joinedAt: new Date('2024-01-12') },
        { id: '7', name: 'James Rodriguez', role: 'supporter', avatar: '', joinedAt: new Date('2024-01-14') }
      ],
      updates: [
        { id: '5', author: 'David Park', message: 'Started this group to address safety concerns on Oak Avenue', timestamp: new Date('2024-01-10T16:00:00'), type: 'comment' },
        { id: '6', author: 'Lisa Thompson', message: 'Added safety incident reports from the past 6 months', timestamp: new Date('2024-01-12T10:20:00'), type: 'file_upload' },
        { id: '7', author: 'James Rodriguez', message: 'James Rodriguez joined as a supporter', timestamp: new Date('2024-01-14T18:45:00'), type: 'collaborator_added' }
      ],
      supporters: 23,
      isPublic: true
    }
  ];

  const [issues] = useState<CollaborativeIssue[]>(mockCollaborativeIssues);

  const filteredIssues = issues.filter(issue => {
    const matchesSearch = issue.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         issue.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         issue.location.toLowerCase().includes(searchQuery.toLowerCase());
    
    if (activeTab === 'active') {
      return matchesSearch && (issue.status === 'open' || issue.status === 'in-progress');
    } else if (activeTab === 'resolved') {
      return matchesSearch && issue.status === 'resolved';
    } else if (activeTab === 'mine') {
      return matchesSearch && issue.collaborators.some(c => c.role === 'creator' || c.role === 'collaborator');
    }
    
    return matchesSearch;
  });

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'open': return 'bg-blue-100 text-blue-800';
      case 'in-progress': return 'bg-yellow-100 text-yellow-800';
      case 'resolved': return 'bg-green-100 text-green-800';
      case 'pending': return 'bg-gray-100 text-gray-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'low': return 'bg-green-100 text-green-800';
      case 'medium': return 'bg-yellow-100 text-yellow-800';
      case 'high': return 'bg-red-100 text-red-800';
      case 'urgent': return 'bg-red-500 text-white';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getRoleIcon = (role: string) => {
    switch (role) {
      case 'creator': return <Crown className="w-3 h-3 text-yellow-500" />;
      case 'collaborator': return <Shield className="w-3 h-3 text-blue-500" />;
      case 'supporter': return <Star className="w-3 h-3 text-purple-500" />;
      default: return null;
    }
  };

  const formatDate = (date: Date) => {
    return date.toLocaleDateString('en-US', { 
      month: 'short', 
      day: 'numeric',
      year: date.getFullYear() !== new Date().getFullYear() ? 'numeric' : undefined
    });
  };

  const formatTime = (date: Date) => {
    return date.toLocaleString('en-US', { 
      month: 'short', 
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
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
                <Users className="w-8 h-8 text-blue-600" />
                Collaborative Issues
              </h1>
              <p className="text-muted-foreground mt-1">
                Work together with your community to solve civic issues
              </p>
            </div>
          </div>
          
          <Dialog open={showCreateGroup} onOpenChange={setShowCreateGroup}>
            <DialogTrigger asChild>
              <Button className="flex items-center gap-2">
                <Plus className="w-4 h-4" />
                Create Group
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-2xl">
              <DialogHeader>
                <DialogTitle>Create Collaborative Issue</DialogTitle>
              </DialogHeader>
              <div className="space-y-4">
                <Input placeholder="Issue title..." />
                <Textarea placeholder="Describe the issue and why collaboration is needed..." rows={4} />
                <div className="grid grid-cols-2 gap-4">
                  <Input placeholder="Location" />
                  <select className="w-full p-2 border rounded-md">
                    <option>Select Category</option>
                    <option>Roads & Infrastructure</option>
                    <option>Parks & Recreation</option>
                    <option>Street Lighting</option>
                    <option>Sanitation</option>
                  </select>
                </div>
                <div className="flex items-center gap-4">
                  <input type="checkbox" id="public" />
                  <label htmlFor="public" className="text-sm">Make this collaboration public for others to join</label>
                </div>
                <div className="flex justify-end gap-2">
                  <Button variant="outline" onClick={() => setShowCreateGroup(false)}>Cancel</Button>
                  <Button onClick={() => setShowCreateGroup(false)}>Create Collaboration</Button>
                </div>
              </div>
            </DialogContent>
          </Dialog>
        </div>

        {/* Search and Filters */}
        <div className="mb-6 flex flex-col sm:flex-row gap-4">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Search collaborative issues..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-10"
            />
          </div>
        </div>

        <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-6">
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="active">Active ({issues.filter(i => i.status === 'open' || i.status === 'in-progress').length})</TabsTrigger>
            <TabsTrigger value="resolved">Resolved ({issues.filter(i => i.status === 'resolved').length})</TabsTrigger>
            <TabsTrigger value="mine">My Groups ({issues.filter(i => i.collaborators.some(c => c.role === 'creator' || c.role === 'collaborator')).length})</TabsTrigger>
            <TabsTrigger value="all">All Issues ({issues.length})</TabsTrigger>
          </TabsList>

          <TabsContent value={activeTab} className="space-y-6">
            {filteredIssues.length === 0 ? (
              <Card className="p-12 text-center">
                <Users className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
                <h3 className="text-lg font-medium mb-2">No collaborative issues found</h3>
                <p className="text-muted-foreground mb-4">
                  {searchQuery ? 'Try adjusting your search terms.' : 'Start by creating a collaborative issue or joining existing ones.'}
                </p>
                <Button onClick={() => setShowCreateGroup(true)}>
                  <Plus className="w-4 h-4 mr-2" />
                  Create First Collaboration
                </Button>
              </Card>
            ) : (
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {filteredIssues.map((issue) => (
                  <Card key={issue.id} className="hover:shadow-md transition-shadow">
                    <CardHeader className="pb-3">
                      <div className="flex items-start justify-between gap-4">
                        <div className="flex-1">
                          <CardTitle className="text-lg leading-tight">{issue.title}</CardTitle>
                          <div className="flex items-center gap-2 mt-2">
                            <Badge className={getStatusColor(issue.status)}>{issue.status.replace('-', ' ')}</Badge>
                            <Badge className={getPriorityColor(issue.priority)}>{issue.priority}</Badge>
                            {!issue.isPublic && <Badge variant="outline">Private</Badge>}
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
                      <p className="text-sm text-muted-foreground line-clamp-2">
                        {issue.description}
                      </p>
                      
                      <div className="flex items-center gap-2 text-sm text-muted-foreground">
                        <MapPin className="w-3 h-3" />
                        <span>{issue.location}</span>
                      </div>

                      {/* Collaborators */}
                      <div>
                        <div className="flex items-center justify-between mb-2">
                          <span className="text-sm font-medium">Team ({issue.collaborators.length})</span>
                          <Button variant="ghost" size="sm" className="h-auto p-1 text-xs">
                            <UserPlus className="w-3 h-3 mr-1" />
                            Invite
                          </Button>
                        </div>
                        <div className="flex items-center gap-2">
                          <div className="flex -space-x-2">
                            {issue.collaborators.slice(0, 4).map((collaborator) => (
                              <div key={collaborator.id} className="relative">
                                <Avatar className="w-6 h-6 border-2 border-white">
                                  <AvatarImage src={collaborator.avatar} />
                                  <AvatarFallback className="text-xs">
                                    {collaborator.name.split(' ').map(n => n[0]).join('')}
                                  </AvatarFallback>
                                </Avatar>
                                <div className="absolute -bottom-1 -right-1">
                                  {getRoleIcon(collaborator.role)}
                                </div>
                              </div>
                            ))}
                          </div>
                          {issue.collaborators.length > 4 && (
                            <span className="text-xs text-muted-foreground">
                              +{issue.collaborators.length - 4} more
                            </span>
                          )}
                          <div className="flex items-center gap-1 ml-auto">
                            <Star className="w-3 h-3 text-yellow-500" />
                            <span className="text-xs">{issue.supporters}</span>
                          </div>
                        </div>
                      </div>

                      {/* Recent Activity */}
                      {issue.updates.length > 0 && (
                        <div className="border-t pt-3">
                          <div className="flex items-center justify-between mb-2">
                            <span className="text-sm font-medium">Latest Update</span>
                            <span className="text-xs text-muted-foreground">
                              {formatDate(issue.updatedAt)}
                            </span>
                          </div>
                          <div className="text-sm text-muted-foreground">
                            <span className="font-medium">{issue.updates[issue.updates.length - 1].author}</span>:{' '}
                            <span className="line-clamp-1">{issue.updates[issue.updates.length - 1].message}</span>
                          </div>
                        </div>
                      )}

                      <div className="flex gap-2 pt-2">
                        <Button variant="outline" size="sm" className="flex-1">
                          <Eye className="w-3 h-3 mr-1" />
                          View Details
                        </Button>
                        <Button variant="outline" size="sm" className="flex-1">
                          <MessageCircle className="w-3 h-3 mr-1" />
                          Join Discussion
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
  );
}