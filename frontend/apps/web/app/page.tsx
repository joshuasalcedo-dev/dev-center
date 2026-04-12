import { Button } from "@workspace/ui/components/button"
import { CodeIcon } from "lucide-react"

export default function Page() {
  return (
    <div className="flex min-h-svh flex-col items-center justify-center gap-8 p-6">
      <div className="flex flex-col items-center gap-4">
        <div className="flex size-14 items-center justify-center rounded-xl bg-primary text-primary-foreground">
          <CodeIcon className="size-7" />
        </div>
        <h1 className="text-3xl font-bold">Dev Center</h1>
        <p className="max-w-md text-center text-muted-foreground">
          Manage your OpenAPI docs, artifacts, and API keys from one place.
        </p>
      </div>
      <div className="flex gap-3">
        <Button asChild>
          <a href="/login">Sign in</a>
        </Button>
        <Button variant="outline" asChild>
          <a href="/dashboard">Dashboard</a>
        </Button>
      </div>
    </div>
  )
}
