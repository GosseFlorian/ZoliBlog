import { useEffect, useState } from 'react';
import { Link, Navigate, Route, Routes, useNavigate, useParams } from 'react-router-dom';
import ArticleList from '../components/ArticleList.tsx';
import ArticleForm from '../components/ArticleForm.tsx';
import ArticleView from '../components/ArticleView.tsx';
import LoadingMessage from '../components/LoadingMessage.tsx';
import { fetchArticleById, fetchArticleCategories } from '../api/articles.ts';
import type { Article } from '../types/article.ts';
import { useAuthStore } from '../store/authStore.ts';
import { useAdminStore } from '../store/adminStore.ts';

function ArticlesListView() {
  const navigate = useNavigate();
  const articles = useAdminStore((s) => s.articles);
  const isLoading = useAdminStore((s) => s.isLoading);
  const error = useAdminStore((s) => s.error);
  const loadArticles = useAdminStore((s) => s.loadArticles);
  const handleDeleteArticle = useAdminStore((s) => s.handleDeleteArticle);

  useEffect(() => {
    loadArticles();
  }, [loadArticles]);

  return (
    <>
      <div className="page-header-row">
        <h1>Articles</h1>
        <Link to="/articles/new" className="btn btn-primary btn-small">
          + Nouvel article
        </Link>
      </div>

      {isLoading && <LoadingMessage />}
      {error && <p className="error-message">{error}</p>}
      {!isLoading && !error && (
        <ArticleList
          articles={articles}
          onEdit={(id) => navigate(`/articles/${id}/edit`)}
          onDelete={handleDeleteArticle}
          onView={(id) => navigate(`/articles/${id}`)}
        />
      )}
    </>
  );
}

function ArticleCreateView() {
  const navigate = useNavigate();
  const userId = useAuthStore((s) => s.userId);
  const categories = useAdminStore((s) => s.categories);
  const loadArticles = useAdminStore((s) => s.loadArticles);
  const handleCreateArticleSubmit = useAdminStore((s) => s.handleCreateArticleSubmit);

  useEffect(() => {
    loadArticles();
  }, [loadArticles]);

  if (userId == null) {
    return <Navigate to="/articles" replace />;
  }

  return (
    <ArticleForm
      key="create-article"
      initialValues={null}
      allCategories={categories}
      initialCategoryIds={[]}
      connectedUserId={userId}
      submitLabel="Créer"
      onSubmit={async (payload, categorieIds) => {
        const success = await handleCreateArticleSubmit(payload, categorieIds);
        if (success) {
          navigate('/articles');
        }
      }}
      onCancel={() => navigate('/articles')}
      showBackButton
    />
  );
}

function ArticleEditView() {
  const navigate = useNavigate();
  const { id } = useParams();
  const articleId = Number(id);
  const userId = useAuthStore((s) => s.userId);
  const categories = useAdminStore((s) => s.categories);
  const handleEditArticleSubmit = useAdminStore((s) => s.handleEditArticleSubmit);
  const handleSessionExpired = useAdminStore((s) => s.handleSessionExpired);
  const [article, setArticle] = useState<Article | null>(null);
  const [categoryIds, setCategoryIds] = useState<number[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!Number.isFinite(articleId)) {
      return;
    }

    let cancelled = false;

    async function loadEditData() {
      try {
        setLoading(true);
        setError(null);
        const [articleData, articleCategories] = await Promise.all([
          fetchArticleById(articleId),
          fetchArticleCategories(articleId),
        ]);
        if (cancelled) {
          return;
        }
        setArticle(articleData);
        setCategoryIds(articleCategories.map((c) => c.id));
      } catch (err) {
        if (cancelled) {
          return;
        }
        const message = err instanceof Error ? err.message : "Impossible de charger l'article.";
        handleSessionExpired(message);
        setError(message);
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    loadEditData();

    return () => {
      cancelled = true;
    };
  }, [articleId, handleSessionExpired]);

  if (!Number.isFinite(articleId)) {
    return <Navigate to="/articles" replace />;
  }

  if (loading) {
    return <LoadingMessage />;
  }

  if (error || !article || userId == null) {
    return <Navigate to="/articles" replace />;
  }

  return (
    <ArticleForm
      key={article.id}
      initialValues={article}
      allCategories={categories}
      initialCategoryIds={categoryIds}
      connectedUserId={userId}
      submitLabel="Enregistrer"
      onSubmit={async (payload, categorieIds) => {
        const success = await handleEditArticleSubmit(payload, categorieIds);
        if (success) {
          navigate('/articles');
        }
      }}
      onCancel={() => navigate('/articles')}
      showBackButton
    />
  );
}

function ArticleDetailView() {
  const navigate = useNavigate();
  const { id } = useParams();
  const articleId = Number(id);
  const handleSessionExpired = useAdminStore((s) => s.handleSessionExpired);

  if (!Number.isFinite(articleId)) {
    return <Navigate to="/articles" replace />;
  }

  return (
    <ArticleView
      articleId={articleId}
      onBack={() => navigate('/articles')}
      onSessionExpired={handleSessionExpired}
    />
  );
}

function ArticlesPage() {
  return (
    <Routes>
      <Route index element={<ArticlesListView />} />
      <Route path="new" element={<ArticleCreateView />} />
      <Route path=":id/edit" element={<ArticleEditView />} />
      <Route path=":id" element={<ArticleDetailView />} />
    </Routes>
  );
}

export default ArticlesPage;
