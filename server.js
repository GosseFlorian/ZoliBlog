/**
 * Service Railway « fronts » — site (/) + admin (/admin).
 * Prérequis : npm run build (site/dist + admin/dist).
 */
const express = require("express");
const path = require("path");
const { existsSync } = require("node:fs");

const siteDist = path.join(__dirname, "site", "dist");
const adminDist = path.join(__dirname, "admin", "dist");

if (!existsSync(path.join(siteDist, "index.html"))) {
  console.error(`site/dist manquant — lancer « npm run build » (${siteDist})`);
  process.exit(1);
}

const app = express();
const port = Number(process.env.PORT) || 3000;

app.use("/admin", express.static(adminDist, { index: "index.html" }));
app.get("/admin/*", (_req, res) => {
  res.sendFile(path.join(adminDist, "index.html"));
});

app.use(express.static(siteDist));
app.get("*", (_req, res) => {
  res.sendFile(path.join(siteDist, "index.html"));
});

app.listen(port, "0.0.0.0", () => {
  console.log(`zoliblog-front listening on http://0.0.0.0:${port}`);
});
