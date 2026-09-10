import ArticleList from '../components/ArticleList.tsx';
import ArticleForm from '../components/ArticleForm.tsx';
import ArticleView from '../components/ArticleView.tsx';
import LoadingMessage from '../components/LoadingMessage.tsx';
import { useAuthStore } from '../store/authStore.ts';
import { useAdminStore } from '../store/adminStore.ts';

function ArticlesPage() {
  const userId = useAuthStore((s) => s.userId);
  const mode = useAdminStore((s) => s.mode);
  const articles = useAdminStore((s) => s.articles);
  const categories = useAdminStore((s) => s.categories);
  const isLoading = useAdminStore((s) => s.isLoading);
  const error = useAdminStore((s) => s.error);
  const editingArticle = useAdminStore((s) => s.editingArticle);
  const editingArticleCategoryIds = useAdminStore((s) => s.editingArticleCategoryIds);
  const viewingArticleId = useAdminStore((s) => s.viewingArticleId);
  const showList = useAdminStore((s) => s.showList);
  const handleEditArticle = useAdminStore((s) => s.handleEditArticle);
  const handleDeleteArticle = useAdminStore((s) => s.handleDeleteArticle);
  const handleViewArticle = useAdminStore((s) => s.handleViewArticle);
  const handleCreateArticleSubmit = useAdminStore((s) => s.handleCreateArticleSubmit);
  const handleEditArticleSubmit = useAdminStore((s) => s.handleEditArticleSubmit);
  const handleSessionExpired = useAdminStore((s) => s.handleSessionExpired);

  return (
    <>
      {mode === 'create' && userId != null && (
        <ArticleForm
          key="create-article"
          initialValues={null}
          allCategories={categories}
          initialCategoryIds={[]}
          connectedUserId={userId}
          submitLabel="Créer"
          onSubmit={handleCreateArticleSubmit}
          onCancel={showList}
          showBackButton
        />
      )}

      {mode === 'edit' && editingArticle && userId != null && (
        <ArticleForm
          key={editingArticle.id}
          initialValues={editingArticle}
          allCategories={categories}
          initialCategoryIds={editingArticleCategoryIds}
          connectedUserId={userId}
          submitLabel="Enregistrer"
          onSubmit={handleEditArticleSubmit}
          onCancel={showList}
          showBackButton
        />
      )}

      {mode === 'view' && viewingArticleId != null && (
        <ArticleView
          articleId={viewingArticleId}
          onBack={showList}
          onSessionExpired={handleSessionExpired}
        />
      )}

      {mode === 'list' && isLoading && <LoadingMessage />}

      {mode === 'list' && error && <p className="error-message">{error}</p>}

      {mode === 'list' && !isLoading && !error && (
        <ArticleList
          articles={articles}
          onEdit={handleEditArticle}
          onDelete={handleDeleteArticle}
          onView={handleViewArticle}
        />
      )}
    </>
  );
}

export default ArticlesPage;
