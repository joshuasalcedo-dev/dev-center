import { defineConfig } from "@hey-api/openapi-ts";

export default defineConfig({
  input: "http://localhost:5050/v3/api-docs",
  output: {
    path: "./src/lib/api",
    format: "prettier",
    importFileExtension: ".ts",
  },
  plugins: [
    "@hey-api/typescript",
    "@hey-api/sdk",
    {
      name: "@tanstack/react-query",
      queryOptions: true,
      queryKeys: true,
      infiniteQueryOptions: true,
      mutationOptions: true,
    },
  ],
});
