import { useState, type SubmitEvent } from 'react';
import type { Article } from '../types/article.ts';
import type { Categorie } from '../api/categories.ts';
import type { CreateArticlePayload, UpdateArticlePayload } from '../api/articles.ts';

interface ArticleFormProps {
  initialValues: Article | null;
  allCategories: Categorie[];
  initialCategoryIds: number[];
  connectedUserId: number;
  onSubmit: (
    payload: CreateArticlePayload | (UpdateArticlePayload & { id: number }),
    categorieIds?: number[]
  ) => void;
  onCancel: () => void;
  submitLabel: string;
  showBackButton?: boolean;
}

function ArticleForm({
  initialValues,
  allCategories,
  initialCategoryIds,
  connectedUserId,
  onSubmit,
  onCancel,
  submitLabel,
  showBackButton = false,
}: ArticleFormProps) {
  const isEdit = initialValues != null;

  const [titre, setTitre] = useState(initialValues?.titre ?? '');
  const [contenu, setContenu] = useState(initialValues?.contenu ?? '');
  const [publie, setPublie] = useState(initialValues?.publie ?? false);
  const [selectedCategoryIds, setSelectedCategoryIds] = useState<number[]>(initialCategoryIds);

  function toggleCategory(id: number) {
    setSelectedCategoryIds((prev) =>
      prev.includes(id) ? prev.filter((cId) => cId !== id) : [...prev, id]
    );
  }

  function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();

    if (initialValues) {
      onSubmit(
        {
          id: initialValues.id,
          titre,
          contenu,
          publie,
        },
        selectedCategoryIds
      );
    } else {
      onSubmit(
        {
          titre,
          contenu,
          userId: connectedUserId,
        },
        selectedCategoryIds
      );
    }
  }

  return (
    <>
      {showBackButton && (
        <button type="button" className="back-button" onClick={onCancel}>
          ← Retour à la liste
        </button>
      )}

      <form className="article-form admin-form" onSubmit={handleSubmit}>
        <h2>{isEdit ? "Modifier l'article" : 'Nouvel article'}</h2>

        <label>
          Titre
          <input
            type="text"
            value={titre}
            onChange={(e) => setTitre(e.target.value)}
            placeholder="Titre de l'article"
            required
          />
        </label>

        <label>
          Contenu
          <textarea
            value={contenu}
            onChange={(e) => setContenu(e.target.value)}
            rows={8}
            required
          />
        </label>

        {isEdit && (
          <div className="publish-toggle-row">
            <button
              type="button"
              role="switch"
              aria-checked={publie}
              className="toggle-switch"
              onClick={() => setPublie((prev) => !prev)}
            >
              <span className="toggle-switch-thumb" />
            </button>
            <span className={`badge ${publie ? 'badge-success' : 'badge-draft'}`}>
              {publie ? 'Publié' : 'Brouillon'}
            </span>
          </div>
        )}

        {allCategories.length > 0 && (
          <fieldset className="category-fieldset">
            <legend>Catégories associées</legend>
            <div className="category-checkboxes">
              {allCategories.map((cat) => (
                <label key={cat.id} className="checkbox-label">
                  <input
                    type="checkbox"
                    checked={selectedCategoryIds.includes(cat.id)}
                    onChange={() => toggleCategory(cat.id)}
                  />
                  {cat.nom}
                </label>
              ))}
            </div>
          </fieldset>
        )}

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

export default ArticleForm;
