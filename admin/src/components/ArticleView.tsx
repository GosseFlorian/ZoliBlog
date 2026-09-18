import { useEffect, useState } from 'react';
import { fetchArticleById, fetchArticleCategories } from '../api/articles.ts';
import type { Article } from '../types/article.ts';
import { deleteComment, fetchComments } from '../api/commentaires.ts';
import type { Commentaire } from '../api/commentaires.ts';
import { formatArticleDate } from '../utils/formatUtils.ts';
import CategoryTags from './CategoryTags.tsx';
import LoadingMessage from './LoadingMessage.tsx';
import { requestConfirm } from '../store/confirmStore.ts';

interface ArticleViewProps {
  articleId: number;
  onBack: () => void;
  onSessionExpired: (message: string) => void;
}

function ArticleView({ articleId, onBack, onSessionExpired }: ArticleViewProps) {
  const [article, setArticle] = useState<Article | null>(null);
  const [comments, setComments] = useState<Commentaire[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  async function loadData() {
    try {
      setLoading(true);
      setError(null);
      const [articleData, categories, commentsData] = await Promise.all([
        fetchArticleById(articleId),
        fetchArticleCategories(articleId),
        fetchComments(articleId),
      ]);
      setArticle({ ...articleData, categories });
      setComments(commentsData);
    } catch (err) {
      const message = err instanceof Error ? err.message : "Impossible de charger l'article.";
      onSessionExpired(message);
      setError(message);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    // Pattern fetch au changement d'articleId — setLoading(true) est voulu ici
    // eslint-disable-next-line react-hooks/set-state-in-effect
    loadData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [articleId]);

  async function handleDeleteComment(commentId: number) {
    if (!(await requestConfirm('Supprimer ce commentaire ?\n\nCette action est définitive.'))) {
      return;
    }

    try {
      await deleteComment(commentId);
      setComments((prev) => prev.filter((c) => c.id !== commentId));
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erreur à la suppression.';
      onSessionExpired(message);
      setError(message);
    }
  }

  if (loading) {
    return <LoadingMessage />;
  }

  if (error || !article) {
    return (
      <>
        <button type="button" className="back-button" onClick={onBack}>
          ← Retour à la liste
        </button>
        <p className="error-message">{error ?? 'Article introuvable.'}</p>
      </>
    );
  }

  const statutLabel = article.publie ? 'Publié' : 'Brouillon';

  return (
    <div className="article-view">
      <button type="button" className="back-button" onClick={onBack}>
        ← Retour à la liste
      </button>

      <article>
        <h1>{article.titre}</h1>
        <CategoryTags categories={article.categories} />
        <p className="article-date">
          Posté le {formatArticleDate(article.date)}{' '}
          <span className={`badge ${article.publie ? 'badge-success' : 'badge-draft'}`}>
            {statutLabel}
          </span>
        </p>
        <div className="article-contenu">
          {article.contenu.split('\n').map((paragraphe, index) => (
            <p key={index}>{paragraphe}</p>
          ))}
        </div>
      </article>

      <section className="comments-section">
        <h2>Commentaires ({comments.length})</h2>

        {comments.length === 0 ? (
          <p className="empty-list">Aucun commentaire pour cet article.</p>
        ) : (
          <ul className="comment-list">
            {comments.map((comment) => (
              <li key={comment.id} className="comment-item">
                <p className="comment-meta">
                  {comment.pseudo} — {formatArticleDate(comment.date)}
                </p>
                <p className="comment-contenu">{comment.contenu}</p>
                <button
                  type="button"
                  className="comment-delete"
                  onClick={() => handleDeleteComment(comment.id)}
                >
                  Supprimer
                </button>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
}

export default ArticleView;
