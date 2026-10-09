/**
 * Étape de build du service Railway « web ».
 *
 * Ce script ne tourne PAS en production : il s’exécute une fois au deploy (npm run build),
 * avant npm start. Il produit le dossier web/deploy/ que server.js sert ensuite.
 *
 * Les apps Vite sont dans site/ et admin/ à la racine du repo.
 * Sur Railway : Root Directory du service web = RACINE du repo (pas web/ seul).
 *
 * VITE_API_URL : URL HTTPS du service API (variable Railway), sans / final.
 */
const { execFileSync } = require("node:child_process");
const { cpSync, existsSync, mkdirSync, rmSync } = require("node:fs");
const { join } = require("node:path");

const webDir = join(__dirname, "..");
const repoRoot = join(webDir, "..");
const deployDir = join(webDir, "deploy");
const siteDir = join(repoRoot, "site");
const adminDir = join(repoRoot, "admin");

const viteApi = process.env.VITE_API_URL ?? "";
const npmEnv = { ...process.env, VITE_API_URL: viteApi };

function runNpm(args, cwd) {
  // Sans shell (/bin/sh) — compatible environnements Railpack minimaux
  execFileSync("npm", args, { cwd, stdio: "inherit", env: npmEnv });
}

function runViteBuild(appDir, label) {
  console.log(`\n=== Build ${label} (${appDir}) ===`);
  if (!existsSync(appDir)) {
    throw new Error(
      [
        `Dossier introuvable : ${appDir}`,
        "Sur Railway → service web : Root Directory = racine du repo (vide), pas « web ».",
        "Build : cd web && npm ci && npm run build",
        "Start : cd web && npm start",
      ].join("\n")
    );
  }
  runNpm(["ci"], appDir);
  runNpm(["run", "build"], appDir);
}

rmSync(deployDir, { recursive: true, force: true });
mkdirSync(join(deployDir, "admin"), { recursive: true });

runViteBuild(siteDir, "site");
cpSync(join(siteDir, "dist"), deployDir, { recursive: true });

runViteBuild(adminDir, "admin");
cpSync(join(adminDir, "dist"), join(deployDir, "admin"), { recursive: true });

console.log(`\nDeploy prêt : ${deployDir}`);
