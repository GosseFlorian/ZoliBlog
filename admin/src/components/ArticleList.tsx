import ArticleCard from './ArticleCard.tsx';
import type { Article } from '../types/article.ts';

interface ArticleListProps {
  articles: Article[];
  onEdit: (id: number) => void;
  onDelete: (id: number) => void;
  onView: (id: number) => void;
}

function ArticleList({ articles, onEdit, onDelete, onView }: ArticleListProps) {
  if (articles.length === 0) {
    return <p className="empty-list">Aucun article à afficher.</p>;
  }

  return (
    <section className="article-list" aria-label="Liste des articles">
      {articles.map((article) => (
        <ArticleCard
          key={article.id}
          article={article}
          onEdit={onEdit}
          onDelete={onDelete}
          onView={onView}
        />
      ))}
    </section>
  );
}

export default ArticleList;
