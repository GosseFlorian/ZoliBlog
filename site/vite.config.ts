/// <reference types="vitest/config" />
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

/** GitHub Pages — repo ZoliBlog (project site). */
const PAGES_BASE = "/ZoliBlog/";

// https://vite.dev/config/
export default defineConfig(({ mode }) => ({
  // VITE_API_URL : ../.env.production (envDir racine)
  envDir: "..",
  base: mode === "production" ? PAGES_BASE : "/",
  plugins: [react()],
  server: { port: 5174 },
  test: {
    environment: "jsdom",
    coverage: {
      provider: "v8",
      include: ["src/api/**", "src/store/**", "src/utils/**"],
      thresholds: {
        lines: 80,
        functions: 80,
        statements: 80,
        branches: 55,
      },
    },
  },
}));
