import { useState, type SubmitEvent } from 'react';
import type { Categorie } from '../api/categories.ts';
import type { CreateCategoriePayload, UpdateCategoriePayload } from '../api/categories.ts';

interface CategorieFormProps {
  initialValues: Categorie | null;
  onSubmit: (payload: CreateCategoriePayload | (UpdateCategoriePayload & { id: number })) => void;
  onCancel: () => void;
  submitLabel: string;
  showBackButton?: boolean;
}

function CategorieForm({
  initialValues,
  onSubmit,
  onCancel,
  submitLabel,
  showBackButton = false,
}: CategorieFormProps) {
  const isEdit = initialValues != null;

  const [nom, setNom] = useState(initialValues?.nom ?? '');
  const [description, setDescription] = useState(initialValues?.description ?? '');

  function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();

    if (initialValues) {
      onSubmit({ id: initialValues.id, nom, description });
    } else {
      onSubmit({ nom, description });
    }
  }

  return (
    <>
      {showBackButton && (
        <button type="button" className="back-button" onClick={onCancel}>
          ← Retour à la liste
        </button>
      )}

      <form className="categorie-form admin-form" onSubmit={handleSubmit}>
        <h2>{isEdit ? 'Modifier la catégorie' : 'Nouvelle catégorie'}</h2>

        <label>
          Nom
          <input type="text" value={nom} onChange={(e) => setNom(e.target.value)} required />
        </label>

        <label>
          Description
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            rows={3}
            required
          />
        </label>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary">
            {submitLabel}
          </button>
          <button type="button" className="btn btn-ghost" onClick={onCancel}>
            Annuler
          </button>
        </div>
      </form>
    </>
  );
}

export default CategorieForm;
