import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";

/** Prod : admin sous /admin/ (Express). Dev : /. */
const PROD_BASE = "/admin/";

export default defineConfig(({ mode }) => ({
  // VITE_API_URL : variable d’env au build (Railway) ; envDir racine pour .env* locaux optionnels
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
