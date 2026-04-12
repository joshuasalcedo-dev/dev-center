"use client"

import { useEffect, useState } from "react"
import { AppSidebar } from "@/components/app-sidebar"
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbList,
  BreadcrumbPage,
} from "@workspace/ui/components/breadcrumb"
import { Separator } from "@workspace/ui/components/separator"
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@workspace/ui/components/card"
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from "@workspace/ui/components/sidebar"
import { FileTextIcon, PackageIcon, KeyRoundIcon } from "lucide-react"
import { apiClient } from "@/lib/api-client"
import {
  listAll,
  list as listApiKeys,
} from "@workspace/ui/lib/api/authenticated"
import type {
  OpenApiDocSummary,
  ApiKeyDto,
} from "@workspace/ui/lib/api/authenticated"

export default function Page() {
  const [docs, setDocs] = useState<OpenApiDocSummary[]>([])
  const [keys, setKeys] = useState<ApiKeyDto[]>([])

  useEffect(() => {
    async function load() {
      try {
        const [docsRes, keysRes] = await Promise.all([
          listAll({ client: apiClient }),
          listApiKeys({ client: apiClient }),
        ])
        if (docsRes.data) setDocs(docsRes.data)
        if (keysRes.data) setKeys(keysRes.data)
      } catch {
        // not authenticated or API unreachable
      }
    }
    load()
  }, [])

  return (
    <SidebarProvider>
      <AppSidebar />
      <SidebarInset>
        <header className="flex h-16 shrink-0 items-center gap-2 transition-[width,height] ease-linear group-has-data-[collapsible=icon]/sidebar-wrapper:h-12">
          <div className="flex items-center gap-2 px-4">
            <SidebarTrigger className="-ml-1" />
            <Separator
              orientation="vertical"
              className="mr-2 data-vertical:h-4 data-vertical:self-auto"
            />
            <Breadcrumb>
              <BreadcrumbList>
                <BreadcrumbItem>
                  <BreadcrumbPage>Dashboard</BreadcrumbPage>
                </BreadcrumbItem>
              </BreadcrumbList>
            </Breadcrumb>
          </div>
        </header>
        <div className="flex flex-1 flex-col gap-4 p-4 pt-0">
          <div className="grid gap-4 md:grid-cols-3">
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">API Docs</CardTitle>
                <FileTextIcon className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{docs.length}</div>
                <CardDescription>Registered services</CardDescription>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">Artifacts</CardTitle>
                <PackageIcon className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">--</div>
                <CardDescription>Published artifacts (admin only)</CardDescription>
              </CardContent>
            </Card>
            <Card>
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium">API Keys</CardTitle>
                <KeyRoundIcon className="h-4 w-4 text-muted-foreground" />
              </CardHeader>
              <CardContent>
                <div className="text-2xl font-bold">{keys.filter((k) => k.valid).length}</div>
                <CardDescription>Active keys</CardDescription>
              </CardContent>
            </Card>
          </div>
          <div className="grid gap-4 md:grid-cols-2">
            <Card>
              <CardHeader>
                <CardTitle>Services</CardTitle>
                <CardDescription>Your registered OpenAPI services</CardDescription>
              </CardHeader>
              <CardContent>
                {docs.length === 0 ? (
                  <p className="text-sm text-muted-foreground">No services registered yet.</p>
                ) : (
                  <ul className="space-y-2">
                    {docs.map((doc) => (
                      <li key={doc.id} className="flex items-center justify-between text-sm">
                        <span>{doc.serviceName}</span>
                        <span className="text-xs text-muted-foreground">v{doc.version}</span>
                      </li>
                    ))}
                  </ul>
                )}
              </CardContent>
            </Card>
            <Card>
              <CardHeader>
                <CardTitle>API Keys</CardTitle>
                <CardDescription>Your active and revoked keys</CardDescription>
              </CardHeader>
              <CardContent>
                {keys.length === 0 ? (
                  <p className="text-sm text-muted-foreground">No API keys created yet.</p>
                ) : (
                  <ul className="space-y-2">
                    {keys.map((key) => (
                      <li key={key.id} className="flex items-center justify-between text-sm">
                        <span>{key.name}</span>
                        {key.revoked ? (
                          <span className="rounded bg-destructive/10 px-1.5 py-0.5 text-xs text-destructive">revoked</span>
                        ) : (
                          <span className="rounded bg-green-500/10 px-1.5 py-0.5 text-xs text-green-600 dark:text-green-400">active</span>
                        )}
                      </li>
                    ))}
                  </ul>
                )}
              </CardContent>
            </Card>
          </div>
        </div>
      </SidebarInset>
    </SidebarProvider>
  )
}
