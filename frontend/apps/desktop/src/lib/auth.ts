import { load } from "@tauri-apps/plugin-store"
import { openUrl } from "@tauri-apps/plugin-opener"
import { invoke } from "@tauri-apps/api/core"

const STORE_NAME = "credentials.json"
const API_KEY_KEY = "api_key"
const USER_NAME_KEY = "user_name"
const USER_EMAIL_KEY = "user_email"

const REMOTE_URL = import.meta.env.PROD
  ? "https://api.devscentral.com"
  : "http://localhost:8080"

export interface Credentials {
  apiKey: string
  userName: string
  userEmail: string
}

async function getLocalServerPort(): Promise<number> {
  return await invoke<number>("get_local_server_port")
}

export async function getCredentials(): Promise<Credentials | null> {
  const store = await load(STORE_NAME)
  const apiKey = await store.get<string>(API_KEY_KEY)
  const userName = await store.get<string>(USER_NAME_KEY)
  const userEmail = await store.get<string>(USER_EMAIL_KEY)

  if (apiKey) {
    return { apiKey, userName: userName ?? "", userEmail: userEmail ?? "" }
  }

  return null
}

export async function saveCredentials(creds: Credentials): Promise<void> {
  const store = await load(STORE_NAME)
  await store.set(API_KEY_KEY, creds.apiKey)
  await store.set(USER_NAME_KEY, creds.userName)
  await store.set(USER_EMAIL_KEY, creds.userEmail)
  await store.save()
}

export async function clearCredentials(): Promise<void> {
  const store = await load(STORE_NAME)
  await store.delete(API_KEY_KEY)
  await store.delete(USER_NAME_KEY)
  await store.delete(USER_EMAIL_KEY)
  await store.save()
}

export async function startLogin(): Promise<void> {
  const port = await getLocalServerPort()
  const loginUrl = `${REMOTE_URL}/api/public/auth/desktop?port=${port}`
  await openUrl(loginUrl)
}

/**
 * Listen for credentials via SSE from local-server.
 * Returns a promise that resolves with credentials when received,
 * or null on timeout/error. Also returns an abort function.
 */
export function listenForCredentials(
  timeoutMs = 120_000,
): { promise: Promise<Credentials | null>; abort: () => void } {
  let eventSource: EventSource | null = null
  let timeoutId: ReturnType<typeof setTimeout> | null = null

  const promise = (async () => {
    const port = await getLocalServerPort()
    const url = `http://localhost:${port}/auth/events`

    return new Promise<Credentials | null>((resolve) => {
      eventSource = new EventSource(url)

      timeoutId = setTimeout(() => {
        console.warn("SSE auth listener timed out")
        eventSource?.close()
        resolve(null)
      }, timeoutMs)

      eventSource.addEventListener("credentials", (event) => {
        try {
          const data = JSON.parse(event.data) as {
            api_key: string
            user_name: string
            user_email: string
          }
          const creds: Credentials = {
            apiKey: data.api_key,
            userName: data.user_name,
            userEmail: data.user_email,
          }
          if (timeoutId) clearTimeout(timeoutId)
          eventSource?.close()
          saveCredentials(creds).then(() => resolve(creds))
        } catch (err) {
          console.error("Failed to parse SSE credentials event:", err)
        }
      })

      eventSource.onerror = (err) => {
        console.error("SSE connection error:", err)
        // Don't resolve yet — EventSource auto-reconnects.
        // Only timeout will end it.
      }
    })
  })()

  return {
    promise,
    abort: () => {
      if (timeoutId) clearTimeout(timeoutId)
      eventSource?.close()
    },
  }
}
