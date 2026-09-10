import { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useBlogStore } from '../store/blogStore';
import { useAuthStore } from '../store/authStore';
import CommentForm from '../components/CommentForm';
import CommentList from '../components/CommentList';
import CategoryTags from '../components/CategoryTags';
import { formatArticleDate } from '../utils/formatUtils';

function ArticlePage() {
  const { id } = useParams<{ id: string }>();
  const articleId = Number(id);

  const article = useBlogStore((state) => state.currentArticle);
  const articleLoading = useBlogStore((state) => state.currentArticleLoading);
  const articleError = useBlogStore((state) => state.currentArticleError);
  const loadArticle = useBlogStore((state) => state.loadArticle);
  const resetCurrentArticle = useBlogStore((state) => state.resetCurrentArticle);

  const comments = useBlogStore((state) => state.comments);
  const commentsLoading = useBlogStore((state) => state.commentsLoading);
  const commentsError = useBlogStore((state) => state.commentsError);
  const loadComments = useBlogStore((state) => state.loadComments);

  const commentSubmitting = useBlogStore((state) => state.commentSubmitting);
  const commentSubmitError = useBlogStore((state) => state.commentSubmitError);
  const submitComment = useBlogStore((state) => state.submitComment);

  const commentUpdating = useBlogStore((state) => state.commentUpdating);
  const commentDeleting = useBlogStore((state) => state.commentDeleting);
  const commentActionError = useBlogStore((state) => state.commentActionError);
  const updateComment = useBlogStore((state) => state.updateComment);
  const deleteComment = useBlogStore((state) => state.deleteComment);

  const userId = useAuthStore((state) => state.userId);

  useEffect(() => {
    if (!Number.isNaN(articleId)) {
      loadArticle(articleId);
      loadComments(articleId);
    }
    return () => resetCurrentArticle();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [articleId]);

  if (articleLoading) {
    return (
      <main className="article-page">
        <p className="loading-message">Chargement de l'article…</p>
      </main>
    );
  }

  if (articleError || !article) {
    return (
      <main className="article-page">
        <p className="error-message">{articleError ?? 'Article introuvable.'}</p>
        <Link to="/" className="back-link">
          ← Retour aux articles
        </Link>
      </main>
    );
  }

  return (
    <main className="article-page">
      <Link to="/" className="back-link">
        ← Retour aux articles
      </Link>

      <article>
        <CategoryTags categories={article.categories} />
        <h1>{article.titre}</h1>
        <time className="article-date" dateTime={article.date}>
          {formatArticleDate(article.date)}
        </time>
        <div className="article-contenu">
          {article.contenu.split('\n').map((paragraphe, index) => (
            <p key={index}>{paragraphe}</p>
          ))}
        </div>
      </article>

      <section className="comments-section">
        <h2>
          Commentaires{!commentsLoading && !commentsError ? ` (${comments.length})` : ''}
        </h2>

        {commentsLoading && <p className="loading-message">Chargement des commentaires…</p>}
        {commentsError && <p className="error-message">{commentsError}</p>}
        {!commentsLoading && !commentsError && (
          <CommentList
            comments={comments}
            currentUserId={userId}
            onUpdate={updateComment}
            onDelete={deleteComment}
            isUpdating={commentUpdating}
            isDeleting={commentDeleting}
            actionError={commentActionError}
          />
        )}

        <CommentForm
          onSubmit={(contenu) => {
            if (userId === null) {
              return Promise.resolve(false);
            }
            return submitComment(articleId, { contenu, userId });
          }}
          isSubmitting={commentSubmitting}
          error={commentSubmitError}
        />
      </section>
    </main>
  );
}

export default ArticlePage;
