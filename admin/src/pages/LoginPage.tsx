import { useEffect } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import LoginForm from '../components/LoginForm.tsx';
import { useAuthStore } from '../store/authStore.ts';

function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const login = useAuthStore((s) => s.login);
  const loginError = useAuthStore((s) => s.loginError);
  const isLoggingIn = useAuthStore((s) => s.isLoggingIn);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);

  const from =
    (location.state as { from?: { pathname: string } } | null)?.from?.pathname ?? '/articles';

  useEffect(() => {
    if (isAuthenticated) {
      navigate(from, { replace: true });
    }
  }, [isAuthenticated, navigate, from]);

  if (isAuthenticated) {
    return <Navigate to={from} replace />;
  }

  return (
    <div className="admin-login-shell">
      <div className="admin-login-brand">
        <h1 className="admin-brand-title">Back-office</h1>
        <span className="admin-brand-subtitle">ZoliBlog</span>
      </div>
      <LoginForm onSubmit={login} errorMessage={loginError} isSubmitting={isLoggingIn} />
    </div>
  );
}

export default LoginPage;
