import { useState, useEffect, useCallback, useRef } from "react"
import {
  getCredentials,
  startLogin,
  clearCredentials,
  listenForCredentials,
  type Credentials,
} from "@/lib/auth"

type AuthState =
  | { status: "loading" }
  | { status: "unauthenticated" }
  | { status: "polling" }
  | { status: "authenticated"; credentials: Credentials }

export function useAuth() {
  const [state, setState] = useState<AuthState>({ status: "loading" })
  const abortRef = useRef<(() => void) | null>(null)

  useEffect(() => {
    getCredentials().then((creds) => {
      if (creds) {
        setState({ status: "authenticated", credentials: creds })
      } else {
        setState({ status: "unauthenticated" })
      }
    })

    return () => {
      abortRef.current?.()
    }
  }, [])

  const login = useCallback(async () => {
    // Start SSE listener BEFORE opening the browser so we don't miss the event
    const { promise, abort } = listenForCredentials()
    abortRef.current = abort

    setState({ status: "polling" })
    await startLogin()

    const creds = await promise
    abortRef.current = null

    if (creds) {
      setState({ status: "authenticated", credentials: creds })
    } else {
      setState({ status: "unauthenticated" })
    }
  }, [])

  const logout = useCallback(async () => {
    abortRef.current?.()
    abortRef.current = null
    await clearCredentials()
    setState({ status: "unauthenticated" })
  }, [])

  return { ...state, login, logout }
}
