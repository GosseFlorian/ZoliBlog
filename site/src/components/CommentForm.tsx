import { useState, type SubmitEvent } from 'react';
import { Link } from 'react-router-dom';
import { useAuthStore } from '../store/authStore';

interface CommentFormProps {
  onSubmit: (contenu: string) => Promise<boolean>;
  isSubmitting: boolean;
  error: string | null;
}

function CommentForm({ onSubmit, isSubmitting, error }: CommentFormProps) {
  const [contenu, setContenu] = useState('');
  const [success, setSuccess] = useState(false);
  const pseudo = useAuthStore((state) => state.pseudo);

  async function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();
    setSuccess(false);

    const ok = await onSubmit(contenu);

    if (ok) {
      setContenu('');
      setSuccess(true);
    }
  }

  if (!pseudo) {
    return (
      <p className="comment-form-locked">
        <Link to="/connexion">Connecte-toi</Link> pour laisser un commentaire (ou{' '}
        <Link to="/inscription">crée un compte</Link>).
      </p>
    );
  }

  return (
    <form className="comment-form" onSubmit={handleSubmit}>
      <h3>Laisser un commentaire</h3>

      {error && (
        <p className="error-message" role="alert">
          {error}
        </p>
      )}
      {success && (
        <p className="success-message" role="status">
          ✓ Commentaire envoyé avec succès.
        </p>
      )}

      <label>
        Votre message
        <textarea value={contenu} onChange={(e) => setContenu(e.target.value)} rows={4} required />
      </label>

      <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
        {isSubmitting ? 'Envoi…' : 'Publier le commentaire'}
      </button>
    </form>
  );
}

export default CommentForm;
