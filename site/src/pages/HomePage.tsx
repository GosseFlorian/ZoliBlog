import { useEffect } from 'react';
import { useBlogStore } from '../store/blogStore';
import ArticleList from '../components/ArticleList';

function HomePage() {
  const articles = useBlogStore((state) => state.articles);
  const loading = useBlogStore((state) => state.articlesLoading);
  const error = useBlogStore((state) => state.articlesError);
  const categories = useBlogStore((state) => state.categories);
  const categoriesLoading = useBlogStore((state) => state.categoriesLoading);
  const categoriesError = useBlogStore((state) => state.categoriesError);
  const selectedCategoryId = useBlogStore((state) => state.selectedCategoryId);
  const loadArticles = useBlogStore((state) => state.loadArticles);
  const loadCategories = useBlogStore((state) => state.loadCategories);
  const setSelectedCategoryId = useBlogStore((state) => state.setSelectedCategoryId);

  useEffect(() => {
    loadCategories();
    loadArticles();
  }, [loadCategories, loadArticles]);

  return (
    <>
      <div className="category-nav-bar">
        <nav className="category-nav" aria-label="Filtrer par catégorie">
          <button
            type="button"
            className={
              selectedCategoryId == null ? 'category-nav-link active' : 'category-nav-link'
            }
            aria-current={selectedCategoryId == null ? 'true' : undefined}
            onClick={() => setSelectedCategoryId(null)}
          >
            Toutes
          </button>
          {categoriesLoading && <span className="category-nav-loading">Chargement…</span>}
          {categoriesError && <span className="error-message">{categoriesError}</span>}
          {!categoriesLoading &&
            categories.map((cat) => (
              <button
                key={cat.id}
                type="button"
                className={
                  selectedCategoryId === cat.id ? 'category-nav-link active' : 'category-nav-link'
                }
                aria-current={selectedCategoryId === cat.id ? 'true' : undefined}
                onClick={() => setSelectedCategoryId(cat.id)}
              >
                {cat.nom}
              </button>
            ))}
        </nav>
      </div>

      <main className="home-page">
        {loading && <p className="loading-message">Chargement des articles…</p>}
        {error && <p className="error-message">{error}</p>}
        {!loading && !error && <ArticleList articles={articles} />}
      </main>
    </>
  );
}

export default HomePage;
