import type { Article } from '../types/article.ts';
import { excerpt, formatArticleDate } from '../utils/formatUtils.ts';
import CategoryTags from './CategoryTags.tsx';

interface ArticleCardProps {
  article: Article;
  onEdit: (id: number) => void;
  onDelete: (id: number) => void;
  onView: (id: number) => void;
}

function ArticleCard({ article, onEdit, onDelete, onView }: ArticleCardProps) {
  const { id, titre, contenu, publie, date, categories } = article;
  const statutLabel = publie ? 'Publié' : 'Brouillon';

  return (
    <article className="article-card">
      <div className="article-card-inner">
        <div className="article-card-content">
          <div className="article-card-head">
            <h2>{titre}</h2>
            <span className={`badge ${publie ? 'badge-success' : 'badge-draft'}`}>
              {statutLabel}
            </span>
          </div>
          <CategoryTags categories={categories} />
          <blockquote className="article-excerpt">
            <p>{excerpt(contenu)}</p>
          </blockquote>
          <p className="article-date">Posté le {formatArticleDate(date)}</p>
        </div>
        <div className="article-actions">
          <button type="button" className="btn btn-secondary btn-small" onClick={() => onView(id)}>
            Voir
          </button>
          <button type="button" className="btn btn-secondary btn-small" onClick={() => onEdit(id)}>
            Modifier
          </button>
          <button type="button" className="btn btn-danger btn-small" onClick={() => onDelete(id)}>
            Supprimer
          </button>
        </div>
      </div>
    </article>
  );
}

export default ArticleCard;
