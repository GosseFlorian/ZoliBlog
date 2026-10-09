/**
 * Étape de build du service Railway « web ».
 *
 * Ce script ne tourne PAS en production : il s’exécute une fois au deploy (npm run build),
 * avant npm start. Il produit le dossier web/deploy/ que server.js sert ensuite.
 *
 * Pourquoi un script séparé ?
 * - Les apps Vite vivent dans ../site et ../admin (hors du dossier web/).
 * - Railway ne build que le service dont la racine est web/ : on enchaîne les deux npm run build ici.
 *
 * VITE_API_URL (variable Railway sur le service web) :
 * - Passée à Vite au build → injectée dans le JS (import.meta.env.VITE_API_URL).
 * - Doit être l’URL HTTPS du service API (ex. https://xxx.up.railway.app), sans / final.
 */
const { execSync } = require("node:child_process");
const { cpSync, mkdirSync, rmSync } = require("node:fs");
const { join } = require("node:path");

// __dirname = web/scripts → web = .. → racine du repo = ../..
const webDir = join(__dirname, "..");
const repoRoot = join(webDir, "..");
const deployDir = join(webDir, "deploy");

// Même valeur que sur Railway ; en local : $env:VITE_API_URL="http://localhost:8080"
const viteApi = process.env.VITE_API_URL ?? "";
const npmEnv = { ...process.env, VITE_API_URL: viteApi };

function runViteBuild(appDir, label) {
  console.log(`\n=== Build ${label} (${appDir}) ===`);
  // npm ci = install exacte depuis package-lock (comme en CI)
  execSync("npm ci", { cwd: appDir, stdio: "inherit", env: npmEnv });
  // mode production → base / ou /admin/ dans vite.config.ts
  execSync("npm run build", { cwd: appDir, stdio: "inherit", env: npmEnv });
}

// Repartir d’un deploy/ propre à chaque build
rmSync(deployDir, { recursive: true, force: true });
mkdirSync(join(deployDir, "admin"), { recursive: true });

// 1) Site public → deploy/index.html, deploy/assets/…
runViteBuild(join(repoRoot, "site"), "site");
cpSync(join(repoRoot, "site", "dist"), deployDir, { recursive: true });

// 2) Admin → deploy/admin/index.html, deploy/admin/assets/…
runViteBuild(join(repoRoot, "admin"), "admin");
cpSync(join(repoRoot, "admin", "dist"), join(deployDir, "admin"), {
  recursive: true,
});

console.log(`\nDeploy prêt : ${deployDir}`);
