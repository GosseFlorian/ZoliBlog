const DEFAULT_API_URL = 'http://localhost:8080';

/** URL de l’API (build prod : variable VITE_API_URL). */
export const API_URL = (import.meta.env.VITE_API_URL ?? DEFAULT_API_URL).replace(/\/$/, '');

export interface Categorie {
  id: number;
  nom: string;
  description: string;
}

export interface Article {
  id: number;
  titre: string;
  contenu: string;
  publie: boolean;
  date: string;
  categories?: Categorie[];
}

export async function fetchArticles(): Promise<Article[]> {
  const res = await fetch(`${API_URL}/articles`);
  if (!res.ok) throw new Error('Erreur chargement articles');
  return res.json();
}

export async function fetchRecentArticles(): Promise<Article[]> {
  const res = await fetch(`${API_URL}/articles/recents`);
  if (!res.ok) throw new Error('Erreur chargement articles récents');
  return res.json();
}

export async function fetchArticle(id: number): Promise<Article | null> {
  const res = await fetch(`${API_URL}/articles/${id}`);
  if (res.status === 404) return null;
  if (!res.ok) throw new Error('Erreur article');
  return res.json();
}

export async function fetchCategories(): Promise<Categorie[]> {
  const res = await fetch(`${API_URL}/categories`);
  if (!res.ok) throw new Error('Erreur chargement catégories');
  return res.json();
}

export async function fetchArticlesByCategory(categoryId: number): Promise<Article[]> {
  const res = await fetch(`${API_URL}/categories/${categoryId}/articles`);
  if (!res.ok) throw new Error('Erreur chargement articles par catégorie');
  return res.json();
}

export async function fetchArticleCategories(articleId: number): Promise<Categorie[]> {
  const res = await fetch(`${API_URL}/articles/${articleId}/categories`);
  if (!res.ok) throw new Error("Erreur chargement catégories de l'article");
  return res.json();
}

export async function enrichArticlesWithCategories(articles: Article[]): Promise<Article[]> {
  return Promise.all(
    articles.map(async (article) => {
      try {
        const categories = await fetchArticleCategories(article.id);
        return { ...article, categories };
      } catch {
        return { ...article, categories: [] };
      }
    })
  );
}
