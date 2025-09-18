import React from 'react';
import { Button } from './ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Badge } from './ui/badge';
import { Alert, AlertDescription } from './ui/alert';
import { 
  FileText, 
  Map, 
  BarChart3, 
  List, 
  AlertTriangle, 
  CheckCircle, 
  Clock, 
  TrendingUp,
  Users,
  Lightbulb,
  Trash2,
  Construction
} from 'lucide-react';

type Page = 'dashboard' | 'report' | 'my-issues' | 'map' | 'community' | 'transparency' | 'profile';

interface DashboardProps {
  onNavigate: (page: Page) => void;
  onEmergencyReport: () => void;
}

export function Dashboard({ onNavigate, onEmergencyReport }: DashboardProps) {
  const recentIssues = [
    {
      id: 'ISS-001',
      title: 'Pothole on Main Street',
      category: 'Roads',
      status: 'In Progress',
      date: '2 days ago',
      icon: Construction,
      urgency: 'medium'
    },
    {
      id: 'ISS-002',
      title: 'Broken Streetlight',
      category: 'Lighting',
      status: 'Resolved',
      date: '1 week ago',
      icon: Lightbulb,
      urgency: 'low'
    },
    {
      id: 'ISS-003',
      title: 'Overflowing Trash Bin',
      category: 'Sanitation',
      status: 'Pending',
      date: '3 days ago',
      icon: Trash2,
      urgency: 'high'
    }
  ];

  const cityUpdates = [
    "New bike lane construction starting on Oak Avenue next week",
    "Community meeting about park renovations scheduled for Friday",
    "Water service maintenance planned for downtown area this weekend"
  ];

  const quickStats = [
    { label: 'Total Reports', value: '2,847', change: '+12%', trend: 'up' },
    { label: 'Resolved This Month', value: '156', change: '+8%', trend: 'up' },
    { label: 'Avg. Response Time', value: '3.2 days', change: '-15%', trend: 'down' },
    { label: 'Community Score', value: '4.2/5', change: '+0.3', trend: 'up' },
  ];

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold">Welcome back!</h1>
          <p className="text-muted-foreground mt-1">
            Here's what's happening in your community today.
          </p>
        </div>
        <Button 
          onClick={onEmergencyReport}
          variant="destructive"
          className="flex items-center gap-2"
        >
          <AlertTriangle className="w-4 h-4" />
          Emergency Report
        </Button>
      </div>

      {/* City Updates Banner */}
      <Alert className="border-blue-200 bg-blue-50">
        <TrendingUp className="h-4 w-4 text-blue-600" />
        <AlertDescription className="text-blue-800">
          <strong>City Updates:</strong> {cityUpdates[0]}
          <Button variant="link" className="ml-2 p-0 h-auto text-blue-600">
            View all updates
          </Button>
        </AlertDescription>
      </Alert>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card className="cursor-pointer hover:shadow-md transition-shadow" onClick={() => onNavigate('report')}>
          <CardHeader className="pb-3">
            <div className="flex items-center gap-2">
              <FileText className="w-5 h-5 text-blue-600" />
              <CardTitle className="text-lg">Report Issue</CardTitle>
            </div>
          </CardHeader>
          <CardContent>
            <CardDescription>
              Report a new civic issue in your area
            </CardDescription>
          </CardContent>
        </Card>

        <Card className="cursor-pointer hover:shadow-md transition-shadow" onClick={() => onNavigate('my-issues')}>
          <CardHeader className="pb-3">
            <div className="flex items-center gap-2">
              <List className="w-5 h-5 text-green-600" />
              <CardTitle className="text-lg">My Issues</CardTitle>
            </div>
          </CardHeader>
          <CardContent>
            <CardDescription>
              Track the status of your reported issues
            </CardDescription>
          </CardContent>
        </Card>

        <Card className="cursor-pointer hover:shadow-md transition-shadow" onClick={() => onNavigate('map')}>
          <CardHeader className="pb-3">
            <div className="flex items-center gap-2">
              <Map className="w-5 h-5 text-purple-600" />
              <CardTitle className="text-lg">Map View</CardTitle>
            </div>
          </CardHeader>
          <CardContent>
            <CardDescription>
              Explore issues on an interactive map
            </CardDescription>
          </CardContent>
        </Card>

        <Card className="cursor-pointer hover:shadow-md transition-shadow" onClick={() => onNavigate('transparency')}>
          <CardHeader className="pb-3">
            <div className="flex items-center gap-2">
              <BarChart3 className="w-5 h-5 text-orange-600" />
              <CardTitle className="text-lg">Transparency</CardTitle>
            </div>
          </CardHeader>
          <CardContent>
            <CardDescription>
              View public statistics and progress
            </CardDescription>
          </CardContent>
        </Card>
      </div>

      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {quickStats.map((stat, index) => (
          <Card key={index}>
            <CardContent className="p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-muted-foreground">{stat.label}</p>
                  <p className="text-2xl font-bold">{stat.value}</p>
                </div>
                <div className={`flex items-center gap-1 text-sm ${
                  stat.trend === 'up' ? 'text-green-600' : 'text-red-600'
                }`}>
                  <TrendingUp className={`w-4 h-4 ${stat.trend === 'down' ? 'rotate-180' : ''}`} />
                  {stat.change}
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle>Recent Issues</CardTitle>
            <CardDescription>
              Your latest reported issues and their status
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            {recentIssues.map((issue) => (
              <div key={issue.id} className="flex items-start gap-3 p-3 rounded-lg border">
                <div className={`p-2 rounded-full ${
                  issue.urgency === 'high' ? 'bg-red-100 text-red-600' :
                  issue.urgency === 'medium' ? 'bg-yellow-100 text-yellow-600' :
                  'bg-green-100 text-green-600'
                }`}>
                  <issue.icon className="w-4 h-4" />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="font-medium">{issue.title}</p>
                  <p className="text-sm text-muted-foreground">{issue.category} • {issue.date}</p>
                  <div className="flex items-center gap-2 mt-2">
                    <Badge variant={
                      issue.status === 'Resolved' ? 'default' :
                      issue.status === 'In Progress' ? 'secondary' :
                      'outline'
                    }>
                      {issue.status === 'Resolved' && <CheckCircle className="w-3 h-3 mr-1" />}
                      {issue.status === 'In Progress' && <Clock className="w-3 h-3 mr-1" />}
                      {issue.status}
                    </Badge>
                    <span className="text-xs text-muted-foreground">#{issue.id}</span>
                  </div>
                </div>
              </div>
            ))}
            <Button variant="outline" className="w-full" onClick={() => onNavigate('my-issues')}>
              View All My Issues
            </Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Community Activity</CardTitle>
            <CardDescription>
              What's happening in your neighborhood
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-3">
              <div className="flex items-center gap-3 p-3 rounded-lg bg-blue-50">
                <Users className="w-5 h-5 text-blue-600" />
                <div>
                  <p className="font-medium">12 neighbors validated your pothole report</p>
                  <p className="text-sm text-muted-foreground">2 hours ago</p>
                </div>
              </div>
              <div className="flex items-center gap-3 p-3 rounded-lg bg-green-50">
                <CheckCircle className="w-5 h-5 text-green-600" />
                <div>
                  <p className="font-medium">Streetlight on Elm Street was fixed</p>
                  <p className="text-sm text-muted-foreground">Yesterday</p>
                </div>
              </div>
              <div className="flex items-center gap-3 p-3 rounded-lg bg-orange-50">
                <AlertTriangle className="w-5 h-5 text-orange-600" />
                <div>
                  <p className="font-medium">New urgent issue reported nearby</p>
                  <p className="text-sm text-muted-foreground">3 days ago</p>
                </div>
              </div>
            </div>
            <Button variant="outline" className="w-full" onClick={() => onNavigate('community')}>
              View Community Activity
            </Button>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}