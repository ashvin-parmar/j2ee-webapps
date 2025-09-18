import React from 'react';
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarFooter,
} from './ui/sidebar';
import { Button } from './ui/button';
import { 
  Home, 
  FileText, 
  List, 
  Map, 
  Users, 
  BarChart3, 
  User, 
  AlertTriangle,
  LogOut,
  Lightbulb,
  Mic,
  Calendar,
  Users2
} from 'lucide-react';

type Page = 'dashboard' | 'report' | 'my-issues' | 'map' | 'community' | 'transparency' | 'profile' | 'voice-reporting' | 'campaigns' | 'collaboration';

interface AppSidebarProps {
  currentPage: Page;
  onNavigate: (page: Page) => void;
  onEmergencyReport: () => void;
  onLogout: () => void;
}

export function AppSidebar({ currentPage, onNavigate, onEmergencyReport, onLogout }: AppSidebarProps) {
  const menuItems = [
    {
      title: 'Dashboard',
      icon: Home,
      page: 'dashboard' as Page,
    },
    {
      title: 'Report Issue',
      icon: FileText,
      page: 'report' as Page,
    },
    {
      title: 'My Issues',
      icon: List,
      page: 'my-issues' as Page,
    },
    {
      title: 'Map View',
      icon: Map,
      page: 'map' as Page,
    },
    {
      title: 'Community',
      icon: Users,
      page: 'community' as Page,
    },
    {
      title: 'Transparency',
      icon: BarChart3,
      page: 'transparency' as Page,
    },
    {
      title: 'Voice Reporting',
      icon: Mic,
      page: 'voice-reporting' as Page,
    },
    {
      title: 'Collaboration',
      icon: Users2,
      page: 'collaboration' as Page,
    },
    {
      title: 'Campaigns',
      icon: Calendar,
      page: 'campaigns' as Page,
    },
    {
      title: 'Profile',
      icon: User,
      page: 'profile' as Page,
    },
  ];

  return (
    <Sidebar>
      <SidebarHeader className="p-4">
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center">
            <Lightbulb className="w-4 h-4 text-white" />
          </div>
          <div>
            <h2 className="text-lg font-medium">CitizenConnect</h2>
            <p className="text-sm text-muted-foreground">Civic Reporting</p>
          </div>
        </div>
      </SidebarHeader>
      
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupContent>
            <SidebarMenu>
              {menuItems.map((item) => (
                <SidebarMenuItem key={item.page}>
                  <SidebarMenuButton
                    onClick={() => onNavigate(item.page)}
                    isActive={currentPage === item.page}
                    className="w-full justify-start"
                  >
                    <item.icon className="w-4 h-4" />
                    <span>{item.title}</span>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter className="p-4 space-y-2">
        <Button
          onClick={onEmergencyReport}
          variant="destructive"
          className="w-full justify-start"
          size="sm"
        >
          <AlertTriangle className="w-4 h-4 mr-2" />
          Emergency Report
        </Button>
        
        <Button
          onClick={onLogout}
          variant="ghost"
          className="w-full justify-start text-muted-foreground"
          size="sm"
        >
          <LogOut className="w-4 h-4 mr-2" />
          Sign Out
        </Button>
      </SidebarFooter>
    </Sidebar>
  );
}