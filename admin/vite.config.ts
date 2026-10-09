import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";

/** Prod : Spring sert l’admin sous /admin/ (Railway). Dev : /. */
const PROD_BASE = "/admin/";

export default defineConfig(({ mode }) => ({
  // VITE_API_URL : ../.env.production (envDir racine)
  envDir: "..",
  base: mode === "production" ? PROD_BASE : "/",
  plugins: [react()],
  test: {
    environment: "jsdom",
    coverage: {
      provider: "v8",
      include: ["src/api/**", "src/store/**", "src/utils/**"],
      thresholds: {
        lines: 80,
        functions: 80,
        statements: 80,
        branches: 50,
      },
    },
  },
}));
