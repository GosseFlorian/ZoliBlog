/**
 * Étape de build du service Railway « web ».
 *
 * Ce script ne tourne PAS en production : il s’exécute une fois au deploy (npm run build),
 * avant npm start. Il produit le dossier web/deploy/ que server.js sert ensuite.
 *
 * Monorepo : backend-api/, site/, admin/, web/ à la racine du repo.
 * Railway (service web) : Root Directory vide + build `cd web && npm ci && npm run build`.
 *
 * VITE_API_URL : URL HTTPS du service API (variable Railway), sans / final.
 */
const { spawnSync } = require("node:child_process");
const { cpSync, existsSync, mkdirSync, rmSync } = require("node:fs");
const { join } = require("node:path");

const webDir = join(__dirname, "..");
const repoRoot = join(webDir, "..");
const deployDir = join(webDir, "deploy");
const siteDir = join(repoRoot, "site");
const adminDir = join(repoRoot, "admin");

const viteApi = process.env.VITE_API_URL ?? "";
const npmEnv = { ...process.env, VITE_API_URL: viteApi };

function assertMonorepoLayout() {
  if (existsSync(siteDir) && existsSync(adminDir)) return;
  throw new Error(
    [
      `site/ ou admin/ introuvable (site=${siteDir}, admin=${adminDir}).`,
      "",
      "Railway → service web → Settings → Source :",
      "  • Root Directory = VIDE (racine du repo), pas « web ».",
      "  • Build : cd web && npm ci && npm run build",
      "  • Start : cd web && npm start",
    ].join("\n")
  );
}

function runNpm(args, cwd) {
  // npm sur PATH est souvent un script sh → ENOENT sur Railpack si on spawn « npm ».
  // npm_execpath = chemin vers npm-cli.js quand on est lancé via « npm run build ».
  const npmCli = process.env.npm_execpath;
  if (!npmCli) {
    throw new Error("npm_execpath manquant : lancer via « npm run build » dans web/.");
  }
  const result = spawnSync(process.execPath, [npmCli, ...args], {
    cwd,
    stdio: "inherit",
    env: npmEnv,
  });
  if (result.error) throw result.error;
  if (result.status !== 0) process.exit(result.status ?? 1);
}

function runViteBuild(appDir, label) {
  console.log(`\n=== Build ${label} (${appDir}) ===`);
  runNpm(["ci"], appDir);
  runNpm(["run", "build"], appDir);
}

assertMonorepoLayout();

rmSync(deployDir, { recursive: true, force: true });
mkdirSync(join(deployDir, "admin"), { recursive: true });

runViteBuild(siteDir, "site");
cpSync(join(siteDir, "dist"), deployDir, { recursive: true });

runViteBuild(adminDir, "admin");
cpSync(join(adminDir, "dist"), join(deployDir, "admin"), { recursive: true });

console.log(`\nDeploy prêt : ${deployDir}`);
