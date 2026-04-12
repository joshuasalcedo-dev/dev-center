import { Button } from "@workspace/ui/components/button"
import { CodeIcon, Loader2Icon } from "lucide-react"

export function HomeScreen({
  onLogin,
  polling,
}: {
  onLogin: () => void
  polling: boolean
}) {
  return (
    <div className="flex flex-1 flex-col items-center justify-center gap-6">
      <div className="flex flex-col items-center gap-3">
        <div className="flex size-12 items-center justify-center rounded-lg bg-primary text-primary-foreground">
          <CodeIcon className="size-6" />
        </div>
        <h1 className="text-xl font-semibold">Dev Center</h1>
        <p className="max-w-xs text-center text-sm text-muted-foreground">
          Manage your API docs, artifacts, and API keys from one place.
        </p>
      </div>
      {polling ? (
        <div className="flex flex-col items-center gap-2">
          <Loader2Icon className="size-5 animate-spin text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            Completing sign-in in your browser...
          </p>
        </div>
      ) : (
        <Button onClick={onLogin}>Sign in with Google</Button>
      )}
    </div>
  )
}
