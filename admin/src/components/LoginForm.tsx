import { useState, type SubmitEvent } from 'react';
import type { LoginCredentials } from '../api/auth.ts';

interface LoginFormProps {
  onSubmit: (credentials: LoginCredentials) => void;
  errorMessage: string | null;
  isSubmitting: boolean;
}

function LoginForm({ onSubmit, errorMessage, isSubmitting }: LoginFormProps) {
  const [mail, setMail] = useState('');
  const [mdp, setMdp] = useState('');

  function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();
    onSubmit({ mail, mdp });
  }

  return (
    <form className="login-form" onSubmit={handleSubmit}>
      <h2>Connexion admin</h2>
      <p className="login-hint">
        Compte démo : <strong>alice@example.com</strong> / <strong>demo1234</strong>
      </p>

      {errorMessage && (
        <p className="error-message" role="alert">
          {errorMessage}
        </p>
      )}

      <label>
        Adresse mail
        <input
          type="email"
          value={mail}
          onChange={(e) => setMail(e.target.value)}
          placeholder="alice@example.com"
          required
          autoComplete="username"
        />
      </label>

      <label>
        Mot de passe
        <input
          type="password"
          value={mdp}
          onChange={(e) => setMdp(e.target.value)}
          placeholder="••••••••"
          required
          autoComplete="current-password"
        />
      </label>

      <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
        {isSubmitting ? 'Connexion…' : 'Se connecter'}
      </button>
    </form>
  );
}

export default LoginForm;
