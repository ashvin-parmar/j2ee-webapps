import React, { useState } from 'react';
import { Button } from './ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Badge } from './ui/badge';
import { Textarea } from './ui/textarea';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from './ui/dialog';
import { ImageWithFallback } from './figma/ImageWithFallback';
import { 
  ThumbsUp, 
  ThumbsDown, 
  MessageSquare, 
  MapPin, 
  Calendar,
  Eye,
  Users,
  CheckCircle,
  AlertTriangle,
  Flag,
  Construction,
  Lightbulb,
  Trash2,
  TreePine,
  Car,
  Heart,
  Share
} from 'lucide-react';

type Page = 'dashboard' | 'report' | 'my-issues' | 'map' | 'community' | 'transparency' | 'profile';

interface CommunityValidationProps {
  onNavigate: (page: Page) => void;
}

interface CommunityIssue {
  id: string;
  title: string;
  description: string;
  category: string;
  location: string;
  distance: string;
  status: 'Pending' | 'In Progress' | 'Resolved';
  urgency: 'low' | 'medium' | 'high' | 'emergency';
  reportedBy: string;
  reportDate: string;
  views: number;
  upvotes: number;
  downvotes: number;
  comments: number;
  validated: boolean;
  userVoted: 'up' | 'down' | null;
  image?: string;
  icon: any;
}

export function CommunityValidation({ onNavigate }: CommunityValidationProps) {
  const [selectedIssue, setSelectedIssue] = useState<CommunityIssue | null>(null);
  const [newComment, setNewComment] = useState('');
  const [issues, setIssues] = useState<CommunityIssue[]>([
    {
      id: 'ISS-006',
      title: 'Broken fire hydrant leaking water',
      description: 'Fire hydrant on the corner has been leaking for several days, creating a puddle that freezes at night.',
      category: 'Infrastructure',
      location: '145 Oak Street',
      distance: '0.2 miles away',
      status: 'Pending',
      urgency: 'high',
      reportedBy: 'Sarah M.',
      reportDate: '2024-12-11',
      views: 23,
      upvotes: 8,
      downvotes: 1,
      comments: 3,
      validated: false,
      userVoted: null,
      image: 'https://images.unsplash.com/photo-1469510090920-fd33379d1f7c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxwb3Rob2xlJTIwc3RyZWV0JTIwZGFtYWdlJTIwdXJiYW58ZW58MXx8fHwxNzU3NzAzNTQ3fDA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral',
      icon: Construction
    },
    {
      id: 'ISS-007',
      title: 'Dead tree blocking sidewalk',
      description: 'Large tree fell during last storm and is completely blocking the sidewalk. Pedestrians have to walk in the street.',
      category: 'Parks & Recreation',
      location: '67 Pine Avenue',
      distance: '0.5 miles away',
      status: 'Pending',
      urgency: 'medium',
      reportedBy: 'Mike R.',
      reportDate: '2024-12-10',
      views: 45,
      upvotes: 15,
      downvotes: 0,
      comments: 7,
      validated: true,
      userVoted: 'up',
      icon: TreePine
    },
    {
      id: 'ISS-008',
      title: 'Graffiti on public building',
      description: 'Extensive graffiti appeared overnight on the side of the community center building.',
      category: 'Public Property',
      location: '234 Main Street',
      distance: '0.8 miles away',
      status: 'Pending',
      urgency: 'low',
      reportedBy: 'Anonymous',
      reportDate: '2024-12-09',
      views: 18,
      upvotes: 3,
      downvotes: 2,
      comments: 1,
      validated: false,
      userVoted: null,
      icon: Construction
    },
    {
      id: 'ISS-009',
      title: 'Dangerous intersection needs stop sign',
      description: 'This intersection has had 3 near-accidents this week. Cars don\'t slow down and it\'s dangerous for pedestrians.',
      category: 'Traffic & Parking',
      location: 'Elm St & 5th Ave',
      distance: '1.2 miles away',
      status: 'In Progress',
      urgency: 'high',
      reportedBy: 'Jennifer L.',
      reportDate: '2024-12-07',
      views: 89,
      upvotes: 34,
      downvotes: 3,
      comments: 16,
      validated: true,
      userVoted: null,
      icon: Car
    }
  ]);

  const handleVote = (issueId: string, voteType: 'up' | 'down') => {
    setIssues(prev => prev.map(issue => {
      if (issue.id === issueId) {
        const newIssue = { ...issue };
        
        // Remove previous vote if exists
        if (issue.userVoted === 'up') newIssue.upvotes--;
        if (issue.userVoted === 'down') newIssue.downvotes--;
        
        // Add new vote or remove if same vote
        if (issue.userVoted === voteType) {
          newIssue.userVoted = null;
        } else {
          newIssue.userVoted = voteType;
          if (voteType === 'up') newIssue.upvotes++;
          if (voteType === 'down') newIssue.downvotes++;
        }
        
        return newIssue;
      }
      return issue;
    }));
  };

  const handleValidate = (issueId: string) => {
    setIssues(prev => prev.map(issue => 
      issue.id === issueId ? { ...issue, validated: true, upvotes: issue.upvotes + 1 } : issue
    ));
  };

  const handleComment = (issueId: string) => {
    if (!newComment.trim()) return;
    
    setIssues(prev => prev.map(issue => 
      issue.id === issueId ? { ...issue, comments: issue.comments + 1 } : issue
    ));
    setNewComment('');
    setSelectedIssue(null);
  };

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

  const nearbyStats = {
    totalIssues: issues.length,
    validatedIssues: issues.filter(i => i.validated).length,
    myValidations: issues.filter(i => i.userVoted === 'up').length,
    communitieImpact: 156
  };

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold">Community Validation</h1>
          <p className="text-muted-foreground mt-1">
            Help verify and support issues reported by your neighbors
          </p>
        </div>
        <Button onClick={() => onNavigate('report')}>
          Report New Issue
        </Button>
      </div>

      {/* Community Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card>
          <CardContent className="p-4 text-center">
            <div className="text-2xl font-bold text-blue-600">{nearbyStats.totalIssues}</div>
            <div className="text-sm text-muted-foreground">Nearby Issues</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-center">
            <div className="text-2xl font-bold text-green-600">{nearbyStats.validatedIssues}</div>
            <div className="text-sm text-muted-foreground">Community Validated</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-center">
            <div className="text-2xl font-bold text-purple-600">{nearbyStats.myValidations}</div>
            <div className="text-sm text-muted-foreground">My Validations</div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4 text-center">
            <div className="text-2xl font-bold text-orange-600">{nearbyStats.communitieImpact}</div>
            <div className="text-sm text-muted-foreground">Community Impact</div>
          </CardContent>
        </Card>
      </div>

      {/* Issues List */}
      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <h2 className="text-xl font-semibold">Issues Near You</h2>
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            <MapPin className="w-4 h-4" />
            Within 2 miles of your location
          </div>
        </div>

        {issues.map((issue) => (
          <Card key={issue.id} className={`${issue.validated ? 'border-green-200 bg-green-50/30' : ''}`}>
            <CardContent className="p-6">
              <div className="flex gap-6">
                {/* Issue Image */}
                {issue.image && (
                  <div className="flex-shrink-0">
                    <ImageWithFallback
                      src={issue.image}
                      alt={issue.title}
                      className="w-32 h-24 object-cover rounded-lg"
                    />
                  </div>
                )}

                {/* Issue Content */}
                <div className="flex-1 space-y-3">
                  <div className="flex items-start justify-between">
                    <div className="flex items-start gap-3">
                      <div className="p-2 rounded-full bg-gray-100">
                        <issue.icon className="w-5 h-5" />
                      </div>
                      <div>
                        <h3 className="font-semibold text-lg">{issue.title}</h3>
                        <p className="text-muted-foreground">{issue.description}</p>
                        <div className="flex items-center gap-4 mt-2 text-sm text-muted-foreground">
                          <span className="flex items-center gap-1">
                            <MapPin className="w-3 h-3" />
                            {issue.location} • {issue.distance}
                          </span>
                          <span className="flex items-center gap-1">
                            <Calendar className="w-3 h-3" />
                            {new Date(issue.reportDate).toLocaleDateString()}
                          </span>
                          <span>by {issue.reportedBy}</span>
                        </div>
                      </div>
                    </div>
                    
                    {issue.validated && (
                      <Badge className="bg-green-100 text-green-800">
                        <CheckCircle className="w-3 h-3 mr-1" />
                        Community Validated
                      </Badge>
                    )}
                  </div>

                  <div className="flex items-center gap-2">
                    <Badge className={getStatusColor(issue.status)}>
                      {issue.status}
                    </Badge>
                    <Badge className={getUrgencyColor(issue.urgency)}>
                      {issue.urgency} priority
                    </Badge>
                    <span className="text-sm text-muted-foreground">#{issue.id}</span>
                  </div>

                  {/* Engagement Stats */}
                  <div className="flex items-center gap-6 text-sm text-muted-foreground">
                    <div className="flex items-center gap-1">
                      <Eye className="w-4 h-4" />
                      {issue.views} views
                    </div>
                    <div className="flex items-center gap-1">
                      <Users className="w-4 h-4" />
                      {issue.upvotes + issue.downvotes} votes
                    </div>
                    <div className="flex items-center gap-1">
                      <MessageSquare className="w-4 h-4" />
                      {issue.comments} comments
                    </div>
                  </div>

                  {/* Action Buttons */}
                  <div className="flex items-center gap-3 pt-2">
                    <div className="flex items-center gap-1">
                      <Button
                        variant={issue.userVoted === 'up' ? 'default' : 'outline'}
                        size="sm"
                        onClick={() => handleVote(issue.id, 'up')}
                        className="flex items-center gap-1"
                      >
                        <ThumbsUp className="w-4 h-4" />
                        {issue.upvotes}
                      </Button>
                      <Button
                        variant={issue.userVoted === 'down' ? 'destructive' : 'outline'}
                        size="sm"
                        onClick={() => handleVote(issue.id, 'down')}
                        className="flex items-center gap-1"
                      >
                        <ThumbsDown className="w-4 h-4" />
                        {issue.downvotes}
                      </Button>
                    </div>

                    <Dialog>
                      <DialogTrigger asChild>
                        <Button variant="outline" size="sm" onClick={() => setSelectedIssue(issue)}>
                          <MessageSquare className="w-4 h-4 mr-2" />
                          Comment
                        </Button>
                      </DialogTrigger>
                      <DialogContent>
                        <DialogHeader>
                          <DialogTitle>Add Comment</DialogTitle>
                          <DialogDescription>
                            Share additional information or ask questions about this issue
                          </DialogDescription>
                        </DialogHeader>
                        <div className="space-y-4">
                          <Textarea
                            placeholder="Write your comment..."
                            value={newComment}
                            onChange={(e) => setNewComment(e.target.value)}
                            rows={3}
                          />
                          <div className="flex justify-end gap-2">
                            <Button variant="outline" onClick={() => setSelectedIssue(null)}>
                              Cancel
                            </Button>
                            <Button onClick={() => handleComment(issue.id)}>
                              Post Comment
                            </Button>
                          </div>
                        </div>
                      </DialogContent>
                    </Dialog>

                    {!issue.validated && (
                      <Button 
                        variant="default"
                        size="sm"
                        onClick={() => handleValidate(issue.id)}
                        className="bg-green-600 hover:bg-green-700"
                      >
                        <CheckCircle className="w-4 h-4 mr-2" />
                        Validate Issue
                      </Button>
                    )}

                    <Button variant="ghost" size="sm">
                      <Share className="w-4 h-4 mr-2" />
                      Share
                    </Button>

                    <Button variant="ghost" size="sm" className="text-red-600 hover:text-red-700">
                      <Flag className="w-4 h-4 mr-2" />
                      Report
                    </Button>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Community Guidelines */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Users className="w-5 h-5" />
            Community Guidelines
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
            <div className="space-y-2">
              <div className="flex items-center gap-2">
                <CheckCircle className="w-4 h-4 text-green-600" />
                <span>Only validate issues you can personally confirm</span>
              </div>
              <div className="flex items-center gap-2">
                <CheckCircle className="w-4 h-4 text-green-600" />
                <span>Provide helpful, constructive comments</span>
              </div>
              <div className="flex items-center gap-2">
                <CheckCircle className="w-4 h-4 text-green-600" />
                <span>Report issues that violate community standards</span>
              </div>
            </div>
            <div className="space-y-2">
              <div className="flex items-center gap-2">
                <AlertTriangle className="w-4 h-4 text-red-600" />
                <span>Don't validate issues based on photos alone</span>
              </div>
              <div className="flex items-center gap-2">
                <AlertTriangle className="w-4 h-4 text-red-600" />
                <span>Avoid duplicate reports of the same issue</span>
              </div>
              <div className="flex items-center gap-2">
                <AlertTriangle className="w-4 h-4 text-red-600" />
                <span>Don't use the platform for personal disputes</span>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}