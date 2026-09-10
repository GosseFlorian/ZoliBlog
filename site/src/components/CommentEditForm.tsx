import { useState, type SubmitEvent } from 'react';

interface CommentEditFormProps {
  initialContenu: string;
  onSubmit: (contenu: string) => Promise<boolean>;
  onCancel: () => void;
  isSubmitting: boolean;
  error: string | null;
}

function CommentEditForm({
  initialContenu,
  onSubmit,
  onCancel,
  isSubmitting,
  error,
}: CommentEditFormProps) {
  const [contenu, setContenu] = useState(initialContenu);

  async function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();
    await onSubmit(contenu);
  }

  return (
    <form className="comment-form comment-edit-form" onSubmit={handleSubmit}>
      {error && (
        <p className="error-message" role="alert">
          {error}
        </p>
      )}

      <label>
        Modifier votre message
        <textarea value={contenu} onChange={(e) => setContenu(e.target.value)} rows={4} required />
      </label>

      <div className="comment-form-actions">
        <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
          {isSubmitting ? 'Enregistrement…' : 'Enregistrer'}
        </button>
        <button type="button" className="btn btn-ghost" onClick={onCancel} disabled={isSubmitting}>
          Annuler
        </button>
      </div>
    </form>
  );
}

export default CommentEditForm;
