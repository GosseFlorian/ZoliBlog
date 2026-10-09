/// <reference types="vitest/config" />
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

/** Prod : site à la racine (/). Dev : /. */
const PROD_BASE = "/";

// https://vite.dev/config/
export default defineConfig(({ mode }) => ({
  // VITE_API_URL : variable d’env au build (Railway) ; envDir racine pour .env* locaux optionnels
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
