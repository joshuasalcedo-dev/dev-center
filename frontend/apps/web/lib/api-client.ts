import { createClient, createConfig } from "@workspace/ui/lib/api/authenticated/client"

const API_BASE = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080"

export const apiClient = createClient(
  createConfig({
    baseUrl: API_BASE,
    credentials: "include",
  })
)
