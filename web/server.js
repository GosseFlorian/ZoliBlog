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
const handler = require("serve-handler");

const server = http.createServer((request, response) =>
  handler(request, response, {
    public: "deploy",
    rewrites: [
      // Back-office React (basename /admin/)
      { source: "/admin/**", destination: "/admin/index.html" },
      // Site public React (basename /)
      { source: "**", destination: "/index.html" },
    ],
  })
);

const port = Number(process.env.PORT) || 3000;
server.listen(port, () => {
  console.log(`zoliblog-web listening on ${port}`);
});
