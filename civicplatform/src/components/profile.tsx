        import React, { useState, useEffect } from 'react';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Badge } from './ui/badge';
import { Progress } from './ui/progress';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './ui/tabs';
import { Switch } from './ui/switch';
import { Avatar, AvatarFallback, AvatarImage } from './ui/avatar';
import { 
  User, 
  Edit, 
  Bell, 
  Shield, 
  Award, 
  Trophy, 
  Star, 
  Target,
  TrendingUp,
  Calendar,
  MapPin,
  Mail,
  Phone,
  Camera,
  CheckCircle,
  Users,
  FileText,
  MessageSquare,
  ThumbsUp,
  Eye
} from 'lucide-react';

type Page = 'dashboard' | 'report' | 'my-issues' | 'map' | 'community' | 'transparency' | 'profile';

interface ProfileProps {
  onNavigate: (page: Page) => void;
}

interface UserDTO {
  id: number;
  name: string;
  email: string;
  phone: string;
  location: string;
  joinDate: string;
  issuesReported: number;
  issuesResolved: number;
  communityValidations: number;
  commentsPosted: number;
  totalViews: number;
  upvotesReceived: number;
  accountLevel: string;
  experiencePoints: number;
  nextLevelPoints: number;
}

export function Profile({ onNavigate }: ProfileProps) {
  const [activeTab, setActiveTab] = useState('overview');
  const [isEditing, setIsEditing] = useState(false);
  const [userInfo, setUserInfo] = useState<UserDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch("http://localhost:8080/civicplatform/api/users/1")
      .then((res) => {
        if (!res.ok) {
          throw new Error(`Backend error: ${res.status}`);
        }
        return res.json();
      })
      .then((data: UserDTO) => {
        setUserInfo(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Error fetching user:", err);
        setError(err.message);
        setLoading(false);
      });
  }, []);

  const [notifications, setNotifications] = useState({
    emailUpdates: true,
    pushNotifications: true,
    weeklyDigest: true,
    nearbyIssues: false,
    resolutionUpdates: true
  });

  const badges = [
    {
      id: 'first-report',
      name: 'First Reporter',
      description: 'Submitted your first issue report',
      icon: FileText,
      earned: true,
      earnedDate: '2024-03-16',
      color: 'bg-blue-100 text-blue-800'
    },
    {
      id: 'community-validator',
      name: 'Community Validator',
      description: 'Validated 25+ community issues',
      icon: CheckCircle,
      earned: true,
      earnedDate: '2024-05-22',
      color: 'bg-green-100 text-green-800'
    },
    {
      id: 'frequent-contributor',
      name: 'Frequent Contributor',
      description: 'Reported 10+ issues',
      icon: Trophy,
      earned: true,
      earnedDate: '2024-08-10',
      color: 'bg-yellow-100 text-yellow-800'
    },
    {
      id: 'neighborhood-champion',
      name: 'Neighborhood Champion',
      description: 'Top contributor in your area',
      icon: Award,
      earned: true,
      earnedDate: '2024-10-15',
      color: 'bg-purple-100 text-purple-800'
    },
    {
      id: 'social-connector',
      name: 'Social Connector',
      description: 'Posted 50+ helpful comments',
      icon: MessageSquare,
      earned: true,
      earnedDate: '2024-11-20',
      color: 'bg-orange-100 text-orange-800'
    },
    {
      id: 'resolution-tracker',
      name: 'Resolution Tracker',
      description: 'Track issues until resolution',
      icon: Target,
      earned: false,
      earnedDate: null,
      color: 'bg-gray-100 text-gray-500'
    }
  ];

  const recentActivity = [
    {
      type: 'validated',
      title: 'Validated pothole report on Main Street',
      date: '2 days ago',
      icon: CheckCircle,
      color: 'text-green-600'
    },
    {
      type: 'commented',
      title: 'Commented on streetlight issue',
      date: '4 days ago',
      icon: MessageSquare,
      color: 'text-blue-600'
    },
    {
      type: 'reported',
      title: 'Reported broken sidewalk',
      date: '1 week ago',
      icon: FileText,
      color: 'text-purple-600'
    },
    {
      type: 'badge',
      title: 'Earned "Social Connector" badge',
      date: '2 weeks ago',
      icon: Award,
      color: 'text-yellow-600'
    }
  ];

  const monthlyProgress = {
    reportsGoal: 5,
    reportsActual: 3,
    validationsGoal: 10,
    validationsActual: 7,
    commentsGoal: 15,
    commentsActual: 12
  };

  const handleSaveProfile = () => {
    if (!userInfo) return;
    
    fetch(`http://localhost:8080/civicplatform/api/users/${userInfo.id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userInfo),
    })
    .then((res) => {
      if (!res.ok) {
        throw new Error(`Update failed: ${res.status}`);
      }
      return res.json();
    })
    .then((updatedUser: UserDTO) => {
      setUserInfo(updatedUser);
      setIsEditing(false);
    })
    .catch((err) => {
      console.error("Error updating user:", err);
      setError(err.message);
    });
  };

  if (loading) return <div className="p-6">Loading user profile...</div>;
  if (error) return <div className="p-6 text-red-500">Error: {error}</div>;
  if (!userInfo) return <div className="p-6">No user data found</div>;

  const progressToNextLevel = (userInfo.experiencePoints / userInfo.nextLevelPoints) * 100;

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold">Profile</h1>
          <p className="text-muted-foreground mt-1">
            Manage your account and track your community impact
          </p>
        </div>
        <Button onClick={() => setIsEditing(!isEditing)}>
          <Edit className="w-4 h-4 mr-2" />
          {isEditing ? 'Cancel' : 'Edit Profile'}
        </Button>
      </div>

      <Tabs value={activeTab} onValueChange={setActiveTab}>
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="achievements">Achievements</TabsTrigger>
          <TabsTrigger value="activity">Activity</TabsTrigger>
          <TabsTrigger value="settings">Settings</TabsTrigger>
        </TabsList>

        <TabsContent value="overview" className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Profile Info */}
            <Card className="lg:col-span-1">
              <CardHeader className="text-center">
                <div className="relative mx-auto">
                  <Avatar className="w-24 h-24 mx-auto">
                    <AvatarImage src="/placeholder-avatar.jpg" />
                    <AvatarFallback className="text-2xl">
                      {userInfo.name.split(' ').map(n => n[0]).join('')}
                    </AvatarFallback>
                  </Avatar>
                  {isEditing && (
                    <Button size="sm" className="absolute -bottom-2 -right-2 rounded-full w-8 h-8 p-0">
                      <Camera className="w-4 h-4" />
                    </Button>
                  )}
                </div>
                <CardTitle>{userInfo.name}</CardTitle>
                <CardDescription>{userInfo.accountLevel}</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                {isEditing ? (
                  <div className="space-y-3">
                    <Input
                      value={userInfo.name}
                      onChange={(e) => setUserInfo(prev => prev ? {...prev, name: e.target.value} : null)}
                      placeholder="Full Name"
                    />
                    <Input
                      value={userInfo.email}
                      onChange={(e) => setUserInfo(prev => prev ? {...prev, email: e.target.value} : null)}
                      placeholder="Email"
                      type="email"
                    />
                    <Input
                      value={userInfo.phone}
                      onChange={(e) => setUserInfo(prev => prev ? {...prev, phone: e.target.value} : null)}
                      placeholder="Phone"
                    />
                    <Input
                      value={userInfo.location}
                      onChange={(e) => setUserInfo(prev => prev ? {...prev, location: e.target.value} : null)}
                      placeholder="Location"
                    />
                    <Button onClick={handleSaveProfile} className="w-full">
                      Save Changes
                    </Button>
                  </div>
                ) : (
                  <div className="space-y-3 text-sm">
                    <div className="flex items-center gap-2">
                      <Mail className="w-4 h-4 text-muted-foreground" />
                      {userInfo.email}
                    </div>
                    <div className="flex items-center gap-2">
                      <Phone className="w-4 h-4 text-muted-foreground" />
                      {userInfo.phone}
                    </div>
                    <div className="flex items-center gap-2">
                      <MapPin className="w-4 h-4 text-muted-foreground" />
                      {userInfo.location}
                    </div>
                    <div className="flex items-center gap-2">
                      <Calendar className="w-4 h-4 text-muted-foreground" />
                      Joined {new Date(userInfo.joinDate).toLocaleDateString()}
                    </div>
                  </div>
                )}

                {/* Level Progress */}
                <div className="space-y-2 pt-4 border-t">
                  <div className="flex items-center justify-between text-sm">
                    <span>Level Progress</span>
                    <span>{userInfo.experiencePoints} / {userInfo.nextLevelPoints} XP</span>
                  </div>
                  <Progress value={progressToNextLevel} />
                  <p className="text-xs text-muted-foreground">
                    {userInfo.nextLevelPoints - userInfo.experiencePoints} XP to next level
                  </p>
                </div>
              </CardContent>
            </Card>

            {/* Stats Overview */}
            <div className="lg:col-span-2 space-y-6">
              <Card>
                <CardHeader>
                  <CardTitle>Community Impact</CardTitle>
                  <CardDescription>Your contributions to the community</CardDescription>
                </CardHeader>
                <CardContent>
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                    <div className="text-center p-3 border rounded-lg">
                      <div className="text-2xl font-bold text-blue-600">{userInfo.issuesReported}</div>
                      <div className="text-sm text-muted-foreground">Issues Reported</div>
                    </div>
                    <div className="text-center p-3 border rounded-lg">
                      <div className="text-2xl font-bold text-green-600">{userInfo.issuesResolved}</div>
                      <div className="text-sm text-muted-foreground">Issues Resolved</div>
                    </div>
                    <div className="text-center p-3 border rounded-lg">
                      <div className="text-2xl font-bold text-purple-600">{userInfo.communityValidations}</div>
                      <div className="text-sm text-muted-foreground">Validations</div>
                    </div>
                    <div className="text-center p-3 border rounded-lg">
                      <div className="text-2xl font-bold text-orange-600">{userInfo.upvotesReceived}</div>
                      <div className="text-sm text-muted-foreground">Upvotes Received</div>
                    </div>
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Monthly Goals</CardTitle>
                  <CardDescription>Track your progress this month</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div>
                    <div className="flex items-center justify-between text-sm mb-2">
                      <span>Issue Reports</span>
                      <span>{monthlyProgress.reportsActual} / {monthlyProgress.reportsGoal}</span>
                    </div>
                    <Progress value={(monthlyProgress.reportsActual / monthlyProgress.reportsGoal) * 100} />
                  </div>
                  <div>
                    <div className="flex items-center justify-between text-sm mb-2">
                      <span>Community Validations</span>
                      <span>{monthlyProgress.validationsActual} / {monthlyProgress.validationsGoal}</span>
                    </div>
                    <Progress value={(monthlyProgress.validationsActual / monthlyProgress.validationsGoal) * 100} />
                  </div>
                  <div>
                    <div className="flex items-center justify-between text-sm mb-2">
                      <span>Helpful Comments</span>
                      <span>{monthlyProgress.commentsActual} / {monthlyProgress.commentsGoal}</span>
                    </div>
                    <Progress value={(monthlyProgress.commentsActual / monthlyProgress.commentsGoal) * 100} />
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>
        </TabsContent>

<TabsContent value="achievements" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Trophy className="w-5 h-5" />
                Badges & Achievements
              </CardTitle>
              <CardDescription>
                Earn badges by contributing to your community
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {badges.map((badge) => (
                  <div 
                    key={badge.id} 
                    className={`p-4 border rounded-lg ${badge.earned ? 'border-green-200 bg-green-50/30' : 'border-gray-200 bg-gray-50/30'}`}
                  >
                    <div className="flex items-start gap-3">
                      <div className={`p-2 rounded-full ${badge.color}`}>
                        <badge.icon className="w-5 h-5" />
                      </div>
                      <div className="flex-1">
                        <h3 className="font-medium">{badge.name}</h3>
                        <p className="text-sm text-muted-foreground mb-2">{badge.description}</p>
                        {badge.earned ? (
                          <Badge variant="outline" className="text-green-600 border-green-600">
                            <CheckCircle className="w-3 h-3 mr-1" />
                            Earned {new Date(badge.earnedDate!).toLocaleDateString()}
                          </Badge>
                        ) : (
                          <Badge variant="outline" className="text-gray-500">
                            Not Earned
                          </Badge>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Leaderboard Position</CardTitle>
              <CardDescription>
                See how you rank among community contributors
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div className="flex items-center justify-between p-3 border rounded-lg bg-yellow-50">
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 bg-yellow-500 text-white rounded-full flex items-center justify-center font-bold">
                      3
                    </div>
                    <div>
                      <p className="font-medium">Your Rank: #3</p>
                      <p className="text-sm text-muted-foreground">In Downtown District</p>
                    </div>
                  </div>
                  <Badge className="bg-yellow-100 text-yellow-800">
                    <Star className="w-3 h-3 mr-1" />
                    Top 5%
                  </Badge>
                </div>
                
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-center">
                  <div className="p-3 border rounded-lg">
                    <div className="text-lg font-bold text-blue-600">#2</div>
                    <div className="text-sm text-muted-foreground">Reports Rank</div>
                  </div>
                  <div className="p-3 border rounded-lg">
                    <div className="text-lg font-bold text-green-600">#5</div>
                    <div className="text-sm text-muted-foreground">Validations Rank</div>
                  </div>
                  <div className="p-3 border rounded-lg">
                    <div className="text-lg font-bold text-purple-600">#1</div>
                    <div className="text-sm text-muted-foreground">Engagement Rank</div>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="activity" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle>Recent Activity</CardTitle>
              <CardDescription>
                Your recent contributions to the community
              </CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {recentActivity.map((activity, index) => (
                  <div key={index} className="flex items-start gap-3 p-3 border rounded-lg">
                    <div className={`p-2 rounded-full bg-gray-100 ${activity.color}`}>
                      <activity.icon className="w-4 h-4" />
                    </div>
                    <div className="flex-1">
                      <p className="font-medium">{activity.title}</p>
                      <p className="text-sm text-muted-foreground">{activity.date}</p>
                    </div>
                  </div>
                ))}
              </div>
              <Button variant="outline" className="w-full mt-4" onClick={() => onNavigate('my-issues')}>
                View All Activity
              </Button>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="settings" className="space-y-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Bell className="w-5 h-5" />
                Notification Preferences
              </CardTitle>
              <CardDescription>
                Choose how you'd like to be notified about updates
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-medium">Email Updates</p>
                    <p className="text-sm text-muted-foreground">Receive updates about your reported issues</p>
                  </div>
                  <Switch 
                    checked={notifications.emailUpdates}
                    onCheckedChange={(checked) => 
                      setNotifications(prev => ({ ...prev, emailUpdates: checked }))
                    }
                  />
                </div>
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-medium">Push Notifications</p>
                    <p className="text-sm text-muted-foreground">Get notified on your device</p>
                  </div>
                  <Switch 
                    checked={notifications.pushNotifications}
                    onCheckedChange={(checked) => 
                      setNotifications(prev => ({ ...prev, pushNotifications: checked }))
                    }
                  />
                </div>
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-medium">Weekly Digest</p>
                    <p className="text-sm text-muted-foreground">Weekly summary of community activity</p>
                  </div>
                  <Switch 
                    checked={notifications.weeklyDigest}
                    onCheckedChange={(checked) => 
                      setNotifications(prev => ({ ...prev, weeklyDigest: checked }))
                    }
                  />
                </div>
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-medium">Nearby Issues</p>
                    <p className="text-sm text-muted-foreground">Alert when issues are reported near you</p>
                  </div>
                  <Switch 
                    checked={notifications.nearbyIssues}
                    onCheckedChange={(checked) => 
                      setNotifications(prev => ({ ...prev, nearbyIssues: checked }))
                    }
                  />
                </div>
                <div className="flex items-center justify-between">
                  <div>
                    <p className="font-medium">Resolution Updates</p>
                    <p className="text-sm text-muted-foreground">Know when issues you care about are resolved</p>
                  </div>
                  <Switch 
                    checked={notifications.resolutionUpdates}
                    onCheckedChange={(checked) => 
                      setNotifications(prev => ({ ...prev, resolutionUpdates: checked }))
                    }
                  />
                </div>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Shield className="w-5 h-5" />
                Privacy & Security
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <Button variant="outline" className="w-full justify-start">
                Change Password
              </Button>
              <Button variant="outline" className="w-full justify-start">
                Download My Data
              </Button>
              <Button variant="outline" className="w-full justify-start text-red-600 hover:text-red-700">
                Delete Account
              </Button>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
