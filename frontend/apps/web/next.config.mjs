import { resolve, dirname } from "path"
import { fileURLToPath } from "url"

const __dirname = dirname(fileURLToPath(import.meta.url))

/** @type {import('next').NextConfig} */
const nextConfig = {
  transpilePackages: ["@workspace/ui"],
  allowedDevOrigins: ["http://172.23.128.1:3000"],
  turbopack: {
    root: resolve(__dirname, "../../"),
  },
}

export default nextConfig
