import { Link } from 'react-router-dom';
import type { Article } from '../api/articles';
import { excerpt, formatArticleDate } from '../utils/formatUtils';
import CategoryTags from './CategoryTags';

interface ArticleCardProps {
  article: Article;
}

function ArticleCard({ article }: ArticleCardProps) {
  return (
    <article className="article-card">
      <CategoryTags categories={article.categories} />
      <h2>{article.titre}</h2>
      <blockquote className="article-excerpt">
        <p>{excerpt(article.contenu)}</p>
      </blockquote>
      <footer className="article-card-footer">
        <span className="article-date">Posté le {formatArticleDate(article.date)}</span>
        <Link to={`/articles/${article.id}`} className="article-link">
          Lire la suite →
        </Link>
      </footer>
    </article>
  );
}

export default ArticleCard;
