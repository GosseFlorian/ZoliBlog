import type { Categorie } from '../api/categories.ts';

interface CategorieCardProps {
  categorie: Categorie;
  onEdit: (id: number) => void;
  onDelete: (id: number) => void;
}

function CategorieCard({ categorie, onEdit, onDelete }: CategorieCardProps) {
  const { id, nom, description } = categorie;

  return (
    <article className="categorie-card">
      <h2>{nom}</h2>
      <p>{description}</p>
      <div className="article-actions">
        <button type="button" className="btn btn-secondary btn-small" onClick={() => onEdit(id)}>
          Modifier
        </button>
        <button type="button" className="btn btn-danger btn-small" onClick={() => onDelete(id)}>
          Supprimer
        </button>
      </div>
    </article>
  );
}

export default CategorieCard;
