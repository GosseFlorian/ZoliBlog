import { useState, type SubmitEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

function RegisterPage() {
  const [pseudo, setPseudo] = useState('');
  const [mail, setMail] = useState('');
  const [mdp, setMdp] = useState('');
  const [confirmMdp, setConfirmMdp] = useState('');
  const [localError, setLocalError] = useState<string | null>(null);

  const register = useAuthStore((state) => state.register);
  const submitting = useAuthStore((state) => state.authSubmitting);
  const apiError = useAuthStore((state) => state.authError);
  const navigate = useNavigate();

  async function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();
    setLocalError(null);

    if (mdp !== confirmMdp) {
      setLocalError('Les mots de passe ne correspondent pas.');
      return;
    }
    if (mdp.length < 8) {
      setLocalError('Le mot de passe doit contenir au moins 8 caractères.');
      return;
    }

    const ok = await register({ pseudo, mail, mdp });
    if (ok) {
      navigate('/');
    }
  }

  const displayedError = localError ?? apiError;

  return (
    <main className="auth-page">
      <div className="auth-card">
        <h1>Créer un compte</h1>

        {displayedError && (
          <p className="error-message" role="alert">
            {displayedError}
          </p>
        )}

        <form className="auth-form" onSubmit={handleSubmit}>
          <label>
            Pseudo
            <input
              value={pseudo}
              onChange={(e) => setPseudo(e.target.value)}
              placeholder="alice_dev"
              required
              autoComplete="nickname"
            />
          </label>

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
              minLength={8}
              autoComplete="new-password"
            />
          </label>

          <label>
            Confirmer le mot de passe
            <input
              type="password"
              value={confirmMdp}
              onChange={(e) => setConfirmMdp(e.target.value)}
              placeholder="••••••••"
              required
              minLength={8}
              autoComplete="new-password"
            />
          </label>

          <button type="submit" className="btn btn-primary" disabled={submitting}>
            {submitting ? 'Création…' : 'Créer mon compte'}
          </button>
        </form>

        <p className="auth-switch">
          Déjà un compte ? <Link to="/connexion">Se connecter</Link>
        </p>
      </div>
    </main>
  );
}

export default RegisterPage;
