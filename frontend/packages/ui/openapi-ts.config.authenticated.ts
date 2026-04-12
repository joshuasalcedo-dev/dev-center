import { defineConfig } from "@hey-api/openapi-ts";

export default defineConfig({
  input: "./src/docs/openapi-authenticated.json",
  output: {
    path: "./src/lib/api/authenticated",
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
