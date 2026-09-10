import { useEffect } from 'react';
import PageHeader from './components/PageHeader.tsx';
import AdminNav from './components/AdminNav.tsx';
import FeedbackMessage from './components/FeedbackMessage.tsx';
import LoginPage from './pages/LoginPage.tsx';
import ArticlesPage from './pages/ArticlesPage.tsx';
import CategoriesPage from './pages/CategoriesPage.tsx';
import UsersPage from './pages/UsersPage.tsx';
import { useAuthStore } from './store/authStore.ts';
import { useAdminStore } from './store/adminStore.ts';
import './App.css';

const SECTION_TITLES = {
  articles: 'Articles',
  categories: 'Catégories',
  users: 'Utilisateurs',
} as const;

function App() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const pseudo = useAuthStore((s) => s.pseudo);
  const logout = useAuthStore((s) => s.logout);
  const section = useAdminStore((s) => s.section);
  const mode = useAdminStore((s) => s.mode);
  const feedback = useAdminStore((s) => s.feedback);
  const setSection = useAdminStore((s) => s.setSection);
  const loadCurrentSection = useAdminStore((s) => s.loadCurrentSection);
  const clearFeedback = useAdminStore((s) => s.clearFeedback);
  const reset = useAdminStore((s) => s.reset);
  const showCreate = useAdminStore((s) => s.showCreate);

  useEffect(() => {
    if (isAuthenticated) {
      loadCurrentSection();
    }
  }, [isAuthenticated, section, loadCurrentSection]);

  function handleLogout() {
    logout();
    reset();
  }

  if (!isAuthenticated) {
    return <LoginPage />;
  }

  return (
    <div className="admin-shell">
      <PageHeader pseudo={pseudo} onLogout={handleLogout} />
      <AdminNav active={section} onChange={setSection} />

      <main className="admin-main">
        {feedback && (
          <FeedbackMessage
            type={feedback.type}
            message={feedback.message}
            onClose={clearFeedback}
          />
        )}

        {mode === 'list' && (
          <div className="page-header-row">
            <h1>{SECTION_TITLES[section]}</h1>
            {section === 'articles' && (
              <button type="button" className="btn btn-primary btn-small" onClick={showCreate}>
                + Nouvel article
              </button>
            )}
            {section === 'categories' && (
              <button type="button" className="btn btn-primary btn-small" onClick={showCreate}>
                + Nouvelle catégorie
              </button>
            )}
          </div>
        )}

        {section === 'articles' && <ArticlesPage />}
        {section === 'categories' && <CategoriesPage />}
        {section === 'users' && <UsersPage />}
      </main>
    </div>
  );
}

export default App;
