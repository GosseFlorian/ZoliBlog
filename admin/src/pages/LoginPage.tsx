import LoginForm from '../components/LoginForm.tsx';
import { useAuthStore } from '../store/authStore.ts';

function LoginPage() {
  const login = useAuthStore((s) => s.login);
  const loginError = useAuthStore((s) => s.loginError);
  const isLoggingIn = useAuthStore((s) => s.isLoggingIn);

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
