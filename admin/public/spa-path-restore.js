/**
 * GitHub Pages + React Router (BrowserRouter) : au F5, le serveur ne renvoie pas
 * index.html pour /ZoliBlog/admin/articles. La 404 racine redirige d'abord vers :
 *   /ZoliBlog/admin/?/articles
 *
 * Ce script tourne AVANT React : il lit ?/articles, remet la vraie URL dans
 * la barre d'adresse (/ZoliBlog/admin/articles), puis l'app charge la bonne route.
 *
 * ~and~ remplace & dans le chemin pour ne pas casser la query string.
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
