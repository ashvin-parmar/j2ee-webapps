import React, { useState } from 'react';
import { Button } from './ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from './ui/select';
import { Badge } from './ui/badge';
import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  LineChart,
  Line,
  Area,
  AreaChart
} from 'recharts';
import { 
  BarChart3, 
  TrendingUp, 
  TrendingDown, 
  Clock, 
  CheckCircle, 
  AlertTriangle,
  Users,
  Calendar,
  Download,
  RefreshCw,
  Construction,
  Lightbulb,
  Trash2,
  TreePine,
  Car
} from 'lucide-react';

type Page = 'dashboard' | 'report' | 'my-issues' | 'map' | 'community' | 'transparency' | 'profile';

interface TransparencyDashboardProps {
  onNavigate: (page: Page) => void;
}

export function TransparencyDashboard({ onNavigate }: TransparencyDashboardProps) {
  const [timeRange, setTimeRange] = useState('6months');

  // Mock data for charts
  const categoryData = [
    { name: 'Roads & Infrastructure', value: 145, percentage: 35, color: '#3B82F6' },
    { name: 'Street Lighting', value: 89, percentage: 22, color: '#F59E0B' },
    { name: 'Sanitation', value: 67, percentage: 16, color: '#10B981' },
    { name: 'Parks & Recreation', value: 54, percentage: 13, color: '#8B5CF6' },
    { name: 'Traffic & Parking', value: 45, percentage: 11, color: '#EF4444' },
    { name: 'Other', value: 15, percentage: 3, color: '#6B7280' }
  ];

  const monthlyTrends = [
    { month: 'Jun', reported: 45, resolved: 38, inProgress: 7 },
    { month: 'Jul', reported: 52, resolved: 41, inProgress: 11 },
    { month: 'Aug', reported: 48, resolved: 45, inProgress: 3 },
    { month: 'Sep', reported: 61, resolved: 48, inProgress: 13 },
    { month: 'Oct', reported: 58, resolved: 52, inProgress: 6 },
    { month: 'Nov', reported: 43, resolved: 39, inProgress: 4 },
    { month: 'Dec', reported: 38, resolved: 28, inProgress: 10 }
  ];

  const responseTimeData = [
    { month: 'Jun', avgDays: 4.2 },
    { month: 'Jul', avgDays: 3.8 },
    { month: 'Aug', avgDays: 3.5 },
    { month: 'Sep', avgDays: 4.1 },
    { month: 'Oct', avgDays: 3.2 },
    { month: 'Nov', avgDays: 2.9 },
    { month: 'Dec', avgDays: 3.1 }
  ];

  const departmentPerformance = [
    { department: 'Public Works', resolved: 85, avgTime: 2.8, satisfaction: 4.2 },
    { department: 'Parks & Recreation', resolved: 78, avgTime: 3.5, satisfaction: 4.0 },
    { department: 'Transportation', resolved: 72, avgTime: 4.1, satisfaction: 3.8 },
    { department: 'Sanitation', resolved: 91, avgTime: 1.9, satisfaction: 4.5 },
    { department: 'Utilities', resolved: 68, avgTime: 5.2, satisfaction: 3.6 }
  ];

  const overallStats = {
    totalReports: 2847,
    totalReportsChange: 12,
    resolvedThisMonth: 156,
    resolvedChange: 8,
    avgResponseTime: 3.1,
    responseTimeChange: -15,
    satisfactionScore: 4.2,
    satisfactionChange: 0.3,
    activeUsers: 1243,
    activeUsersChange: 18
  };

  const getChangeIcon = (change: number) => {
    return change > 0 ? 
      <TrendingUp className="w-4 h-4 text-green-600" /> : 
      <TrendingDown className="w-4 h-4 text-red-600" />;
  };

  const getChangeColor = (change: number) => {
    return change > 0 ? 'text-green-600' : 'text-red-600';
  };

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold">Transparency Dashboard</h1>
          <p className="text-muted-foreground mt-1">
            Public statistics and insights into civic issue resolution
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Select value={timeRange} onValueChange={setTimeRange}>
            <SelectTrigger className="w-40">
              <Calendar className="w-4 h-4 mr-2" />
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="1month">Last Month</SelectItem>
              <SelectItem value="3months">Last 3 Months</SelectItem>
              <SelectItem value="6months">Last 6 Months</SelectItem>
              <SelectItem value="1year">Last Year</SelectItem>
            </SelectContent>
          </Select>
          <Button variant="outline" size="sm">
            <RefreshCw className="w-4 h-4 mr-2" />
            Refresh
          </Button>
          <Button variant="outline" size="sm">
            <Download className="w-4 h-4 mr-2" />
            Export
          </Button>
        </div>
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
        <Card>
          <CardContent className="p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-muted-foreground">Total Reports</p>
                <p className="text-2xl font-bold">{overallStats.totalReports.toLocaleString()}</p>
              </div>
              <div className={`flex items-center gap-1 text-sm ${getChangeColor(overallStats.totalReportsChange)}`}>
                {getChangeIcon(overallStats.totalReportsChange)}
                +{overallStats.totalReportsChange}%
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-muted-foreground">Resolved This Month</p>
                <p className="text-2xl font-bold text-green-600">{overallStats.resolvedThisMonth}</p>
              </div>
              <div className={`flex items-center gap-1 text-sm ${getChangeColor(overallStats.resolvedChange)}`}>
                {getChangeIcon(overallStats.resolvedChange)}
                +{overallStats.resolvedChange}%
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-muted-foreground">Avg. Response Time</p>
                <p className="text-2xl font-bold text-blue-600">{overallStats.avgResponseTime} days</p>
              </div>
              <div className={`flex items-center gap-1 text-sm ${getChangeColor(overallStats.responseTimeChange)}`}>
                {getChangeIcon(overallStats.responseTimeChange)}
                {overallStats.responseTimeChange}%
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-muted-foreground">Satisfaction Score</p>
                <p className="text-2xl font-bold text-yellow-600">{overallStats.satisfactionScore}/5</p>
              </div>
              <div className={`flex items-center gap-1 text-sm ${getChangeColor(overallStats.satisfactionChange)}`}>
                {getChangeIcon(overallStats.satisfactionChange)}
                +{overallStats.satisfactionChange}
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardContent className="p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-muted-foreground">Active Users</p>
                <p className="text-2xl font-bold text-purple-600">{overallStats.activeUsers.toLocaleString()}</p>
              </div>
              <div className={`flex items-center gap-1 text-sm ${getChangeColor(overallStats.activeUsersChange)}`}>
                {getChangeIcon(overallStats.activeUsersChange)}
                +{overallStats.activeUsersChange}%
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Issues by Category - Pie Chart */}
        <Card>
          <CardHeader>
            <CardTitle>Issues by Category</CardTitle>
            <CardDescription>
              Distribution of reported issues across different categories
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={categoryData}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    label={({ name, percentage }) => `${name}: ${percentage}%`}
                    outerRadius={80}
                    fill="#8884d8"
                    dataKey="value"
                  >
                    {categoryData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={entry.color} />
                    ))}
                  </Pie>
                  <Tooltip />
                </PieChart>
              </ResponsiveContainer>
            </div>
            <div className="grid grid-cols-2 gap-2 mt-4 text-sm">
              {categoryData.map((category) => (
                <div key={category.name} className="flex items-center gap-2">
                  <div 
                    className="w-3 h-3 rounded-full" 
                    style={{ backgroundColor: category.color }}
                  ></div>
                  <span className="truncate">{category.name}: {category.value}</span>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Monthly Trends - Bar Chart */}
        <Card>
          <CardHeader>
            <CardTitle>Monthly Trends</CardTitle>
            <CardDescription>
              Reported vs resolved issues over time
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={monthlyTrends}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="reported" name="Reported" fill="#3B82F6" />
                  <Bar dataKey="resolved" name="Resolved" fill="#10B981" />
                  <Bar dataKey="inProgress" name="In Progress" fill="#F59E0B" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>

        {/* Response Time Trend - Line Chart */}
        <Card>
          <CardHeader>
            <CardTitle>Average Response Time</CardTitle>
            <CardDescription>
              How quickly issues are being addressed over time
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={responseTimeData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis />
                  <Tooltip formatter={(value) => [`${value} days`, 'Avg Response Time']} />
                  <Line 
                    type="monotone" 
                    dataKey="avgDays" 
                    stroke="#8B5CF6" 
                    strokeWidth={3}
                    dot={{ r: 4 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>

        {/* Department Performance */}
        <Card>
          <CardHeader>
            <CardTitle>Department Performance</CardTitle>
            <CardDescription>
              Resolution rates and response times by department
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {departmentPerformance.map((dept, index) => (
                <div key={index} className="space-y-2">
                  <div className="flex items-center justify-between">
                    <span className="font-medium">{dept.department}</span>
                    <div className="flex items-center gap-4 text-sm">
                      <Badge variant="outline">
                        {dept.resolved}% resolved
                      </Badge>
                      <span className="text-muted-foreground">
                        {dept.avgTime} days avg
                      </span>
                      <div className="flex items-center gap-1">
                        <span>{dept.satisfaction}/5</span>
                        <div className="flex">
                          {[1, 2, 3, 4, 5].map((star) => (
                            <span
                              key={star}
                              className={`text-xs ${
                                star <= Math.floor(dept.satisfaction)
                                  ? 'text-yellow-400'
                                  : 'text-gray-300'
                              }`}
                            >
                              ★
                            </span>
                          ))}
                        </div>
                      </div>
                    </div>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2">
                    <div
                      className="bg-green-600 h-2 rounded-full transition-all"
                      style={{ width: `${dept.resolved}%` }}
                    ></div>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Recent Resolutions */}
      <Card>
        <CardHeader>
          <CardTitle>Recently Resolved Issues</CardTitle>
          <CardDescription>
            Issues that have been successfully resolved in the past week
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {[
              {
                id: 'ISS-045',
                title: 'Streetlight repair on Oak Avenue',
                category: 'Lighting',
                resolvedDate: '2 days ago',
                department: 'Public Works',
                icon: Lightbulb
              },
              {
                id: 'ISS-042',
                title: 'Pothole filled on Main Street',
                category: 'Roads',
                resolvedDate: '3 days ago',
                department: 'Transportation',
                icon: Construction
              },
              {
                id: 'ISS-038',
                title: 'Trash pickup schedule updated',
                category: 'Sanitation',
                resolvedDate: '5 days ago',
                department: 'Sanitation',
                icon: Trash2
              },
              {
                id: 'ISS-034',
                title: 'Park bench repaired',
                category: 'Parks',
                resolvedDate: '1 week ago',
                department: 'Parks & Recreation',
                icon: TreePine
              }
            ].map((resolution) => (
              <div key={resolution.id} className="flex items-center gap-3 p-3 border rounded-lg">
                <div className="p-2 rounded-full bg-green-100">
                  <resolution.icon className="w-4 h-4 text-green-600" />
                </div>
                <div className="flex-1">
                  <p className="font-medium">{resolution.title}</p>
                  <p className="text-sm text-muted-foreground">
                    {resolution.category} • Resolved {resolution.resolvedDate} by {resolution.department}
                  </p>
                </div>
                <Badge className="bg-green-100 text-green-800">
                  <CheckCircle className="w-3 h-3 mr-1" />
                  Resolved
                </Badge>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* Data Transparency Note */}
      <Card className="border-blue-200 bg-blue-50">
        <CardContent className="p-4">
          <div className="flex items-start gap-3">
            <BarChart3 className="w-5 h-5 text-blue-600 mt-0.5" />
            <div>
              <h3 className="font-medium text-blue-900">Data Transparency</h3>
              <p className="text-sm text-blue-800 mt-1">
                All data shown is updated in real-time and represents actual civic issue reports and resolutions. 
                This dashboard promotes accountability and helps citizens track their local government's responsiveness 
                to community needs. Data is aggregated to protect individual privacy while maintaining transparency.
              </p>
              <Button variant="link" className="p-0 h-auto mt-2 text-blue-700">
                Learn more about our data practices
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}