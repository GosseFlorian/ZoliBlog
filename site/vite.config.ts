/// <reference types="vitest/config" />
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: { port: 5174 },
  test: {
    environment: "jsdom",
    coverage: {
      provider: "v8",
      include: ["src/api/**", "src/store/**", "src/utils/**"],
      thresholds: {
        lines: 70,
        functions: 70,
        statements: 70,
        branches: 50,
      },
    },
  },
});
