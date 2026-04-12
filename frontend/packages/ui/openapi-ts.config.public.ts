import { defineConfig } from "@hey-api/openapi-ts";

export default defineConfig({
  input: "./src/docs/openapi-public.json",
  output: {
    path: "./src/lib/api/public",
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
