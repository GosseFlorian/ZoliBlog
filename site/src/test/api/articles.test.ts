import { beforeEach, describe, expect, it, vi } from 'vitest';
import {
  API_URL,
  enrichArticlesWithCategories,
  fetchArticle,
  fetchArticles,
  fetchArticlesByCategory,
  fetchCategories,
  fetchRecentArticles,
} from '../../api/articles';

function mockFetch(response: Partial<Response>) {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue(response));
}

describe('articles.ts', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
  });

  it('fetchRecentArticles appelle la bonne URL', async () => {
    const mockArticles = [
      { id: 1, titre: 'Test', contenu: 'Contenu', publie: true, date: '2024-01-01' },
    ];
    mockFetch({ ok: true, json: () => Promise.resolve(mockArticles) });

    const result = await fetchRecentArticles();

    expect(fetch).toHaveBeenCalledWith(`${API_URL}/articles/recents`);
    expect(result).toEqual(mockArticles);
  });

  it('fetchArticles retourne la liste', async () => {
    mockFetch({ ok: true, json: () => Promise.resolve([]) });
    await fetchArticles();
    expect(fetch).toHaveBeenCalledWith(`${API_URL}/articles`);
  });

  it('fetchArticle retourne null si 404', async () => {
    mockFetch({ ok: false, status: 404 });
    expect(await fetchArticle(999)).toBeNull();
  });

  it('fetchCategories lance une erreur si echec', async () => {
    mockFetch({ ok: false, status: 500 });
    await expect(fetchCategories()).rejects.toThrow('Erreur chargement catégories');
  });

  it('fetchArticlesByCategory appelle la bonne URL', async () => {
    mockFetch({ ok: true, json: () => Promise.resolve([]) });
    await fetchArticlesByCategory(2);
    expect(fetch).toHaveBeenCalledWith(`${API_URL}/categories/2/articles`);
  });

  it('enrichArticlesWithCategories retourne categories vides en cas derreur', async () => {
    mockFetch({ ok: false, status: 500 });
    const result = await enrichArticlesWithCategories([
      { id: 1, titre: 'T', contenu: 'C', publie: true, date: '2024-01-01' },
    ]);
    expect(result[0].categories).toEqual([]);
  });
});
