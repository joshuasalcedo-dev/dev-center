"use client"

import * as React from "react"

import { NavMain } from "@/components/nav-main"
import { NavUser } from "@/components/nav-user"
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarRail,
} from "@workspace/ui/components/sidebar"
import {
  FileTextIcon,
  PackageIcon,
  KeyRoundIcon,
  LayoutDashboardIcon,
  Settings2Icon,
  CodeIcon,
} from "lucide-react"

const data = {
  user: {
    name: "Joshua Salcedo",
    email: "joshua@devscentral.com",
    avatar: "",
  },
  navMain: [
    {
      title: "Dashboard",
      url: "/dashboard",
      icon: <LayoutDashboardIcon />,
      isActive: true,
    },
    {
      title: "API Docs",
      url: "/dashboard/api-docs",
      icon: <FileTextIcon />,
      items: [
        { title: "All Services", url: "/dashboard/api-docs" },
        { title: "Upload", url: "/dashboard/api-docs/upload" },
        { title: "CI/CD Guide", url: "/dashboard/api-docs/cicd" },
      ],
    },
    {
      title: "Artifacts",
      url: "/dashboard/artifacts",
      icon: <PackageIcon />,
      items: [
        { title: "All Artifacts", url: "/dashboard/artifacts" },
        { title: "Releases", url: "/dashboard/artifacts/releases" },
      ],
    },
    {
      title: "API Keys",
      url: "/dashboard/api-keys",
      icon: <KeyRoundIcon />,
      items: [
        { title: "My Keys", url: "/dashboard/api-keys" },
        { title: "Create", url: "/dashboard/api-keys/create" },
      ],
    },
    {
      title: "Settings",
      url: "/dashboard/settings",
      icon: <Settings2Icon />,
      items: [
        { title: "Profile", url: "/dashboard/settings/profile" },
      ],
    },
  ],
}

export function AppSidebar({ ...props }: React.ComponentProps<typeof Sidebar>) {
  return (
    <Sidebar collapsible="icon" {...props}>
      <SidebarHeader>
        <SidebarMenu>
          <SidebarMenuItem>
            <SidebarMenuButton size="lg" asChild>
              <a href="/dashboard">
                <div className="flex aspect-square size-8 items-center justify-center rounded-lg bg-sidebar-primary text-sidebar-primary-foreground">
                  <CodeIcon className="size-4" />
                </div>
                <div className="grid flex-1 text-left text-sm leading-tight">
                  <span className="truncate font-medium">Dev Center</span>
                  <span className="truncate text-xs text-muted-foreground">devscentral.com</span>
                </div>
              </a>
            </SidebarMenuButton>
          </SidebarMenuItem>
        </SidebarMenu>
      </SidebarHeader>
      <SidebarContent>
        <NavMain items={data.navMain} />
      </SidebarContent>
      <SidebarFooter>
        <NavUser user={data.user} />
      </SidebarFooter>
      <SidebarRail />
    </Sidebar>
  )
}
