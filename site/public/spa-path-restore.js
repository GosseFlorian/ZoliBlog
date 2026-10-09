/**
 * Même rôle que admin/public/spa-path-restore.js, pour le site public.
 *
 * Après la 404 GitHub, l'URL peut être /ZoliBlog/?/articles/3 au lieu de
 * /ZoliBlog/articles/3. Ce script restaure le chemin avant le démarrage de React.
 */
(function (l) {
  if (l.search.length > 1 && l.search[1] === "/") {
    var decoded = l.search
      .slice(1)
      .split("&")
      .map(function (s) {
        return s.replace(/~and~/g, "&");
      })
      .join("?");
    var base = l.pathname.replace(/\/index\.html$/, "").replace(/\/$/, "");
    window.history.replaceState(null, null, base + decoded + l.hash);
  }
})(window.location);
