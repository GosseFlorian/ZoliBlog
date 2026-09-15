import { Navigate, Route, Routes, useLocation } from 'react-router-dom';
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

function App() {
  const location = useLocation();
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const pseudo = useAuthStore((s) => s.pseudo);
  const logout = useAuthStore((s) => s.logout);
  const feedback = useAdminStore((s) => s.feedback);
  const clearFeedback = useAdminStore((s) => s.clearFeedback);
  const reset = useAdminStore((s) => s.reset);

  function handleLogout() {
    logout();
    reset();
  }

  if (!isAuthenticated) {
    return (
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="*" element={<Navigate to="/login" state={{ from: location }} replace />} />
      </Routes>
    );
  }

  return (
    <div className="admin-shell">
      <PageHeader pseudo={pseudo} onLogout={handleLogout} />
      <AdminNav />

      <main className="admin-main">
        {feedback && (
          <FeedbackMessage
            type={feedback.type}
            message={feedback.message}
            onClose={clearFeedback}
          />
        )}

        <Routes>
          <Route path="/login" element={<Navigate to="/articles" replace />} />
          <Route path="/" element={<Navigate to="/articles" replace />} />
          <Route path="/articles/*" element={<ArticlesPage />} />
          <Route path="/categories/*" element={<CategoriesPage />} />
          <Route path="/users" element={<UsersPage />} />
          <Route path="*" element={<Navigate to="/articles" replace />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;
