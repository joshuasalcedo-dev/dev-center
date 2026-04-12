import { useEffect, useState } from "react"
import { Button } from "@workspace/ui/components/button"
import type { Credentials } from "@/lib/auth"
import { apiClient } from "@/lib/api-client"
import { listAll, list as listApiKeys } from "@workspace/ui/lib/api/authenticated"
import type { OpenApiDocSummary, ApiKeyDto } from "@workspace/ui/lib/api/authenticated"
import { LogOutIcon, FileTextIcon, KeyRoundIcon, Loader2Icon } from "lucide-react"

export function Dashboard({
  credentials,
  onLogout,
}: {
  credentials: Credentials
  onLogout: () => void
}) {
  const [docs, setDocs] = useState<OpenApiDocSummary[]>([])
  const [keys, setKeys] = useState<ApiKeyDto[]>([])
  const [loading, setLoading] = useState(true)

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
        // API may not be reachable yet
      } finally {
        setLoading(false)
      }
    }
    load()
  }, [])

  return (
    <div className="flex flex-1 flex-col p-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-lg font-semibold">{credentials.userName || "Dashboard"}</h1>
          <p className="text-sm text-muted-foreground">{credentials.userEmail}</p>
        </div>
        <Button variant="ghost" size="sm" onClick={onLogout}>
          <LogOutIcon className="mr-2 size-4" />
          Sign out
        </Button>
      </div>

      {loading ? (
        <div className="mt-6 flex flex-1 items-center justify-center">
          <Loader2Icon className="size-5 animate-spin text-muted-foreground" />
        </div>
      ) : (
        <div className="mt-6 grid gap-4 md:grid-cols-2">
          <div className="rounded-lg border border-border p-4">
            <div className="flex items-center gap-2 text-sm font-medium">
              <FileTextIcon className="size-4 text-muted-foreground" />
              API Docs
            </div>
            <p className="mt-1 text-2xl font-bold">{docs.length}</p>
            <ul className="mt-3 space-y-1">
              {docs.map((doc) => (
                <li key={doc.id} className="text-sm text-muted-foreground">
                  {doc.serviceName} <span className="text-xs">v{doc.version}</span>
                </li>
              ))}
              {docs.length === 0 && (
                <li className="text-sm text-muted-foreground">No services registered</li>
              )}
            </ul>
          </div>

          <div className="rounded-lg border border-border p-4">
            <div className="flex items-center gap-2 text-sm font-medium">
              <KeyRoundIcon className="size-4 text-muted-foreground" />
              API Keys
            </div>
            <p className="mt-1 text-2xl font-bold">{keys.length}</p>
            <ul className="mt-3 space-y-1">
              {keys.map((key) => (
                <li key={key.id} className="flex items-center gap-2 text-sm text-muted-foreground">
                  {key.name}
                  {key.revoked && <span className="rounded bg-destructive/10 px-1 text-xs text-destructive">revoked</span>}
                </li>
              ))}
              {keys.length === 0 && (
                <li className="text-sm text-muted-foreground">No API keys</li>
              )}
            </ul>
          </div>
        </div>
      )}
    </div>
  )
}
