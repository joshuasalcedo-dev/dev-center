import { createClient, createConfig } from "@workspace/ui/lib/api/authenticated/client"
import { getCredentials } from "@/lib/auth"

const REMOTE_URL = import.meta.env.PROD
  ? "https://api.devscentral.com"
  : "http://localhost:8080"

export const apiClient = createClient(
  createConfig({
    baseUrl: REMOTE_URL,
  })
)

// Configure the client to inject the API key on every request
apiClient.interceptors.request.use(async (request) => {
  const creds = await getCredentials()
  if (creds?.apiKey) {
    request.headers.set("X-API-Key", creds.apiKey)
  }
  return request
})
