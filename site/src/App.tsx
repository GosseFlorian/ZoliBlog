import { Link, Route, Routes } from 'react-router-dom';
import HomePage from './pages/HomePage';
import ArticlePage from './pages/ArticlePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import { useAuthStore } from './store/authStore';
import ConfirmDialog from './components/ConfirmDialog';
import './App.css';

function App() {
  const pseudo = useAuthStore((state) => state.pseudo);
  const logout = useAuthStore((state) => state.logout);

  return (
    <div className="site">
      <ConfirmDialog />
      <header className="site-header">
        <Link to="/" className="site-title">
          ZoliBlog
        </Link>

        <nav className="site-nav">
          {pseudo ? (
            <>
              <span className="nav-pseudo">
                Bonjour, <strong>{pseudo}</strong>
              </span>
              <button type="button" className="nav-link-button" onClick={logout}>
                Déconnexion
              </button>
            </>
          ) : (
            <>
              <Link to="/connexion">Connexion</Link>
              <Link to="/inscription" className="btn btn-primary">
                Créer un compte
              </Link>
            </>
          )}
        </nav>
      </header>

      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/articles/:id" element={<ArticlePage />} />
        <Route path="/connexion" element={<LoginPage />} />
        <Route path="/inscription" element={<RegisterPage />} />
      </Routes>
    </div>
  );
}

export default App;
