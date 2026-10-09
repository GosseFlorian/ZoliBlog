/**
 * Serveur HTTP en production (npm start) — sert le dossier deploy/ créé par build-static.js.
 *
 * serve-handler :
 * - sert d’abord les vrais fichiers (.js, .css, images) s’ils existent ;
 * - sinon « rewrites » : routes SPA → index.html (F5 sur /articles ou /admin/articles).
 *
 * PORT : obligatoire sur Railway (sinon 3000 en local).
 */
const http = require("http");
const { existsSync } = require("node:fs");
const { join } = require("node:path");
const handler = require("serve-handler");

const webDir = __dirname;
const deployDir = join(webDir, "deploy");

if (!existsSync(join(deployDir, "index.html"))) {
  console.error(
    `deploy/ introuvable (${deployDir}). Le build Railway a-t-il exécuté « npm run build » dans web/ ?`
  );
  process.exit(1);
}

const server = http.createServer((request, response) =>
  handler(request, response, {
    public: deployDir,
    rewrites: [
      { source: "/admin/**", destination: "/admin/index.html" },
      { source: "**", destination: "/index.html" },
    ],
  })
);

const port = Number(process.env.PORT) || 3000;
const host = "0.0.0.0";
server.listen(port, host, () => {
  console.log(`zoliblog-web listening on http://${host}:${port} (cwd=${process.cwd()})`);
});
