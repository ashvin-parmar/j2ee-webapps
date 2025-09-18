import React, { useState } from 'react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from './ui/table';
import { Badge } from './ui/badge';
import { Progress } from './ui/progress';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { 
  Search,
  Filter,
  Eye,
  MessageSquare,
  ThumbsUp,
  MapPin,
  Calendar,
  CheckCircle,
  Clock,
  AlertCircle,
  Construction,
  Lightbulb,
  Trash2,
  TreePine,
  Car,
  Shield,
  Link,
  Hash
} from 'lucide-react';

type Page = 'dashboard' | 'report' | 'my-issues' | 'map' | 'community' | 'transparency' | 'profile' | 'voice-reporting' | 'campaigns' | 'collaboration';

interface MyIssuesProps {
  onNavigate: (page: Page) => void;
}

interface Issue {
  id: string;
  title: string;
  category: string;
  location: string;
  status: 'Pending' | 'In Progress' | 'Resolved' | 'Rejected';
  urgency: 'low' | 'medium' | 'high' | 'emergency';
  reportDate: string;
  lastUpdate: string;
  progress: number;
  views: number;
  votes: number;
  comments: number;
  icon: any;
  hasBlockchainBadge?: boolean;
  blockchainHash?: string;
}

export function MyIssues({ onNavigate }: MyIssuesProps) {
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [selectedIssue, setSelectedIssue] = useState<Issue | null>(null);

  const issues: Issue[] = [
    {
      id: 'ISS-001',
      title: 'Large pothole causing vehicle damage',
      category: 'Roads & Infrastructure',
      location: '123 Main Street',
      status: 'In Progress',
      urgency: 'high',
      reportDate: '2024-12-08',
      lastUpdate: '2024-12-10',
      progress: 65,
      views: 47,
      votes: 23,
      comments: 8,
      icon: Construction
    },
    {
      id: 'ISS-002',
      title: 'Broken streetlight near school',
      category: 'Street Lighting',
      location: '456 Oak Avenue',
      status: 'Resolved',
      urgency: 'medium',
      reportDate: '2024-12-05',
      lastUpdate: '2024-12-09',
      progress: 100,
      views: 32,
      votes: 15,
      comments: 4,
      icon: Lightbulb,
      hasBlockchainBadge: true,
      blockchainHash: '0xa1b2c3d4e5f6789abcdef0123456789abcdef0123456789'
    },
    {
      id: 'ISS-003',
      title: 'Overflowing trash bin on sidewalk',
      category: 'Sanitation',
      location: '789 Pine Street',
      status: 'Pending',
      urgency: 'low',
      reportDate: '2024-12-09',
      lastUpdate: '2024-12-09',
      progress: 10,
      views: 12,
      votes: 8,
      comments: 2,
      icon: Trash2
    },
    {
      id: 'ISS-004',
      title: 'Damaged playground equipment',
      category: 'Parks & Recreation',
      location: 'Central Park',
      status: 'In Progress',
      urgency: 'medium',
      reportDate: '2024-12-06',
      lastUpdate: '2024-12-11',
      progress: 35,
      views: 89,
      votes: 34,
      comments: 12,
      icon: TreePine
    },
    {
      id: 'ISS-005',
      title: 'Faded crosswalk markings',
      category: 'Traffic & Parking',
      location: '321 Elm Street',
      status: 'Rejected',
      urgency: 'low',
      reportDate: '2024-12-04',
      lastUpdate: '2024-12-07',
      progress: 0,
      views: 18,
      votes: 5,
      comments: 3,
      icon: Car
    },
    {
      id: 'ISS-006',
      title: 'Water main leak fixed',
      category: 'Infrastructure',
      location: '789 Water Street',
      status: 'Resolved',
      urgency: 'high',
      reportDate: '2024-11-28',
      lastUpdate: '2024-12-02',
      progress: 100,
      views: 156,
      votes: 78,
      comments: 23,
      icon: Construction,
      hasBlockchainBadge: true,
      blockchainHash: '0x9f8e7d6c5b4a3928374829384729384827394827384'
    }
  ];

  const filteredIssues = issues.filter(issue => {
    const matchesSearch = issue.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         issue.category.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         issue.location.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === 'all' || issue.status.toLowerCase() === statusFilter;
    return matchesSearch && matchesStatus;
  });

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Resolved':
        return 'bg-green-100 text-green-800';
      case 'In Progress':
        return 'bg-blue-100 text-blue-800';
      case 'Pending':
        return 'bg-yellow-100 text-yellow-800';
      case 'Rejected':
        return 'bg-red-100 text-red-800';
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

  const getProgressSteps = (issue: Issue) => {
    const steps = [
      { label: 'Reported', completed: true, date: issue.reportDate },
      { label: 'Under Review', completed: issue.progress >= 25, date: issue.status !== 'Pending' ? issue.reportDate : '' },
      { label: 'Assigned', completed: issue.progress >= 50, date: issue.status === 'In Progress' || issue.status === 'Resolved' ? issue.lastUpdate : '' },
      { label: 'In Progress', completed: issue.progress >= 75, date: issue.status === 'In Progress' || issue.status === 'Resolved' ? issue.lastUpdate : '' },
      { label: 'Resolved', completed: issue.status === 'Resolved', date: issue.status === 'Resolved' ? issue.lastUpdate : '' }
    ];
    return steps;
  };

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold">My Issues</h1>
          <p className="text-muted-foreground mt-1">
            Track the status and progress of your reported issues
          </p>
        </div>
        <Button onClick={() => onNavigate('report')}>
          Report New Issue
        </Button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardContent className="p-4 text-center">
            <div className="text-2xl font-bold text-blue-600">{issues.length}</div>
            <div className="text-sm text-muted-foreground">Total Reports</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-center">
            <div className="text-2xl font-bold text-green-600">
              {issues.filter(i => i.status === 'Resolved').length}
            </div>
            <div className="text-sm text-muted-foreground">Resolved</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-center">
            <div className="text-2xl font-bold text-orange-600">
              {issues.filter(i => i.status === 'In Progress').length}
            </div>
            <div className="text-sm text-muted-foreground">In Progress</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-center">
            <div className="text-2xl font-bold text-yellow-600">
              {issues.filter(i => i.status === 'Pending').length}
            </div>
            <div className="text-sm text-muted-foreground">Pending</div>
          </CardContent>
        </Card>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="p-4">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                <Input
                  placeholder="Search issues..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-10"
                />
              </div>
            </div>
            <div className="w-full md:w-48">
              <Select value={statusFilter} onValueChange={setStatusFilter}>
                <SelectTrigger>
                  <Filter className="w-4 h-4 mr-2" />
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="all">All Status</SelectItem>
                  <SelectItem value="pending">Pending</SelectItem>
                  <SelectItem value="in progress">In Progress</SelectItem>
                  <SelectItem value="resolved">Resolved</SelectItem>
                  <SelectItem value="rejected">Rejected</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Issues Table */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <Card>
            <CardHeader>
              <CardTitle>Issues List</CardTitle>
              <CardDescription>
                {filteredIssues.length} of {issues.length} issues
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Issue</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Priority</TableHead>
                    <TableHead>Date</TableHead>
                    <TableHead>Engagement</TableHead>
                    <TableHead></TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredIssues.map((issue) => (
                    <TableRow 
                      key={issue.id} 
                      className="cursor-pointer hover:bg-muted/50"
                      onClick={() => setSelectedIssue(issue)}
                    >
                      <TableCell>
                        <div className="flex items-start gap-3">
                          <div className="p-2 rounded-full bg-gray-100">
                            <issue.icon className="w-4 h-4" />
                          </div>
                          <div>
                            <p className="font-medium">{issue.title}</p>
                            <p className="text-sm text-muted-foreground">{issue.category}</p>
                            <div className="flex items-center gap-1 text-xs text-muted-foreground mt-1">
                              <MapPin className="w-3 h-3" />
                              {issue.location}
                            </div>
                          </div>
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center gap-2">
                          <Badge className={getStatusColor(issue.status)}>
                            {issue.status}
                          </Badge>
                          {issue.hasBlockchainBadge && (
                            <div className="flex items-center gap-1 text-blue-600 bg-blue-50 px-2 py-1 rounded-full">
                              <Shield className="w-3 h-3" />
                              <span className="text-xs font-medium">Verified</span>
                            </div>
                          )}
                        </div>
                      </TableCell>
                      <TableCell>
                        <Badge className={getUrgencyColor(issue.urgency)}>
                          {issue.urgency}
                        </Badge>
                      </TableCell>
                      <TableCell>
                        <div className="text-sm">
                          <div>{new Date(issue.reportDate).toLocaleDateString()}</div>
                          <div className="text-muted-foreground">
                            Updated {new Date(issue.lastUpdate).toLocaleDateString()}
                          </div>
                        </div>
                      </TableCell>
                      <TableCell>
                        <div className="flex items-center gap-3 text-sm text-muted-foreground">
                          <div className="flex items-center gap-1">
                            <Eye className="w-3 h-3" />
                            {issue.views}
                          </div>
                          <div className="flex items-center gap-1">
                            <ThumbsUp className="w-3 h-3" />
                            {issue.votes}
                          </div>
                          <div className="flex items-center gap-1">
                            <MessageSquare className="w-3 h-3" />
                            {issue.comments}
                          </div>
                        </div>
                      </TableCell>
                      <TableCell>
                        <Button variant="ghost" size="sm">
                          <Eye className="w-4 h-4" />
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </div>

        {/* Issue Detail Panel */}
        <div>
          {selectedIssue ? (
            <Card>
              <CardHeader>
                <div className="flex items-start gap-3">
                  <div className="p-2 rounded-full bg-gray-100">
                    <selectedIssue.icon className="w-5 h-5" />
                  </div>
                  <div className="flex-1">
                    <CardTitle className="text-lg">{selectedIssue.title}</CardTitle>
                    <CardDescription>
                      #{selectedIssue.id} • {selectedIssue.category}
                    </CardDescription>
                  </div>
                </div>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex items-center gap-2 flex-wrap">
                  <Badge className={getStatusColor(selectedIssue.status)}>
                    {selectedIssue.status}
                  </Badge>
                  <Badge className={getUrgencyColor(selectedIssue.urgency)}>
                    {selectedIssue.urgency} priority
                  </Badge>
                  {selectedIssue.hasBlockchainBadge && (
                    <Badge className="bg-blue-100 text-blue-800 flex items-center gap-1">
                      <Shield className="w-3 h-3" />
                      Blockchain Verified
                    </Badge>
                  )}
                </div>

                {selectedIssue.hasBlockchainBadge && selectedIssue.blockchainHash && (
                  <div className="p-3 bg-blue-50 rounded-lg border border-blue-200">
                    <div className="flex items-center gap-2 mb-2">
                      <Link className="w-4 h-4 text-blue-600" />
                      <span className="text-sm font-medium text-blue-900">Blockchain Verification</span>
                    </div>
                    <p className="text-xs text-blue-700 mb-2">
                      This issue resolution has been permanently recorded on the blockchain for transparent tracking.
                    </p>
                    <div className="flex items-center gap-2">
                      <Hash className="w-3 h-3 text-blue-600" />
                      <code className="text-xs font-mono bg-white px-2 py-1 rounded border text-blue-800 break-all">
                        {selectedIssue.blockchainHash}
                      </code>
                    </div>
                  </div>
                )}

                <div className="space-y-2">
                  <div className="flex items-center justify-between text-sm">
                    <span>Progress</span>
                    <span>{selectedIssue.progress}%</span>
                  </div>
                  <Progress value={selectedIssue.progress} />
                </div>

                <div className="space-y-3">
                  <h4 className="font-medium">Timeline</h4>
                  {getProgressSteps(selectedIssue).map((step, index) => (
                    <div key={index} className="flex items-center gap-3">
                      <div className={`w-4 h-4 rounded-full flex items-center justify-center ${
                        step.completed ? 'bg-green-600' : 'bg-gray-300'
                      }`}>
                        {step.completed && <CheckCircle className="w-3 h-3 text-white" />}
                      </div>
                      <div className="flex-1">
                        <p className={`text-sm ${step.completed ? 'text-foreground' : 'text-muted-foreground'}`}>
                          {step.label}
                        </p>
                        {step.date && (
                          <p className="text-xs text-muted-foreground">
                            {new Date(step.date).toLocaleDateString()}
                          </p>
                        )}
                      </div>
                    </div>
                  ))}
                </div>

                <div className="flex items-center justify-between pt-4 border-t">
                  <div className="flex items-center gap-4 text-sm text-muted-foreground">
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
                  </div>
                </div>

                <Button 
                  variant="outline" 
                  className="w-full"
                  onClick={() => onNavigate('map')}
                >
                  <MapPin className="w-4 h-4 mr-2" />
                  View on Map
                </Button>
              </CardContent>
            </Card>
          ) : (
            <Card>
              <CardContent className="p-8 text-center text-muted-foreground">
                <Eye className="w-8 h-8 mx-auto mb-2" />
                <p>Select an issue to view details</p>
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  );
}