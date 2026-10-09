/// <reference types="vitest/config" />
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

/** Prod : Spring sert le site à la racine (Railway). Dev : /. */
const PROD_BASE = "/";

// https://vite.dev/config/
export default defineConfig(({ mode }) => ({
  // VITE_API_URL : ../.env.production (envDir racine)
  envDir: "..",
  base: mode === "production" ? PROD_BASE : "/",
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
