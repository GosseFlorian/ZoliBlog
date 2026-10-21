import CategorieCard from './CategorieCard.tsx';
import type { Categorie } from '../api/categories.ts';

interface CategorieListProps {
  categories: Categorie[];
  onEdit: (id: number) => void;
  onDelete: (id: number) => void;
}

function CategorieList({ categories, onEdit, onDelete }: CategorieListProps) {
  if (categories.length === 0) {
    return <p className="empty-list">Aucune catégorie à afficher.</p>;
  }

  return (
    <section className="article-list" aria-label="Liste des catégories">
      {categories.map((categorie) => (
        <CategorieCard
          key={categorie.id}
          categorie={categorie}
          onEdit={onEdit}
          onDelete={onDelete}
        />
      ))}
    </section>
  );
}

export default CategorieList;
