import { Titlebar } from "@/components/title-bar"
import { HomeScreen } from "@/components/home-screen"
import { Dashboard } from "@/components/dashboard"
import { useAuth } from "@/hooks/use-auth"
import { Loader2Icon } from "lucide-react"

function App() {
  const auth = useAuth()

  return (
    <div className="flex min-h-svh flex-col overflow-hidden rounded-lg border border-border bg-background">
      <Titlebar />
      {auth.status === "loading" && (
        <div className="flex flex-1 items-center justify-center">
          <Loader2Icon className="size-5 animate-spin text-muted-foreground" />
        </div>
      )}
      {(auth.status === "unauthenticated" || auth.status === "polling") && (
        <HomeScreen
          onLogin={auth.login}
          polling={auth.status === "polling"}
        />
      )}
      {auth.status === "authenticated" && (
        <Dashboard
          credentials={auth.credentials}
          onLogout={auth.logout}
        />
      )}
    </div>
  )
}

export default App
