import { beforeEach, describe, expect, it, vi } from 'vitest';
import { API_URL } from '../../api/client';
import {
  createArticle,
  deleteArticle,
  enrichArticlesWithCategories,
  fetchAllArticles,
  fetchArticleById,
  fetchRecentArticles,
  updateArticle,
} from '../../api/articles';

function mockFetch(response: Partial<Response>) {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue(response));
}

describe('articles.ts', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
  });

  it('fetchRecentArticles appelle la bonne URL', async () => {
    const articles = [{ id: 1, titre: 'Test', contenu: 'C', publie: true, date: '2024-01-01' }];
    mockFetch({ ok: true, json: () => Promise.resolve(articles) });

    const result = await fetchRecentArticles();

    expect(fetch).toHaveBeenCalledWith(`${API_URL}/articles/recents`);
    expect(result).toEqual(articles);
  });

  it('fetchAllArticles lance une erreur si 401', async () => {
    mockFetch({ ok: false, status: 401 });

    await expect(fetchAllArticles()).rejects.toThrow('Session expirée — reconnecte-toi.');
  });

  it('fetchAllArticles lance une erreur HTTP générique', async () => {
    localStorage.setItem('java_blog_token', 'token');
    mockFetch({ ok: false, status: 500 });

    await expect(fetchAllArticles()).rejects.toThrow('Erreur HTTP 500');
  });

  it('fetchArticleById retourne un article', async () => {
    const article = { id: 2, titre: 'A', contenu: 'B', publie: true, date: '2024-01-01' };
    mockFetch({ ok: true, json: () => Promise.resolve(article) });

    const result = await fetchArticleById(2);

    expect(fetch).toHaveBeenCalledWith(`${API_URL}/admin/articles/2`, expect.any(Object));
    expect(result).toEqual(article);
  });

  it('createArticle envoie un POST avec le payload', async () => {
    localStorage.setItem('java_blog_token', 'token');
    const created = { id: 3, titre: 'N', contenu: 'C', publie: false, date: '2024-01-01' };
    mockFetch({ ok: true, json: () => Promise.resolve(created) });

    const result = await createArticle({ titre: 'N', contenu: 'C', userId: 1 });

    expect(fetch).toHaveBeenCalledWith(
      `${API_URL}/admin/articles`,
      expect.objectContaining({ method: 'POST' }),
    );
    expect(result).toEqual(created);
  });

  it('updateArticle envoie un PUT', async () => {
    localStorage.setItem('java_blog_token', 'token');
    const updated = { id: 1, titre: 'Mod', contenu: 'C', publie: true, date: '2024-01-01' };
    mockFetch({ ok: true, json: () => Promise.resolve(updated) });

    await updateArticle(1, { titre: 'Mod', contenu: 'C', publie: true });

    expect(fetch).toHaveBeenCalledWith(
      `${API_URL}/admin/articles/1`,
      expect.objectContaining({ method: 'PUT' }),
    );
  });

  it('deleteArticle envoie un DELETE', async () => {
    localStorage.setItem('java_blog_token', 'token');
    mockFetch({ ok: true });

    await deleteArticle(1);

    expect(fetch).toHaveBeenCalledWith(
      `${API_URL}/admin/articles/1`,
      expect.objectContaining({ method: 'DELETE' }),
    );
  });

  it('enrichArticlesWithCategories ajoute les catégories', async () => {
    localStorage.setItem('java_blog_token', 'token');
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: true,
        json: () => Promise.resolve([{ id: 1, nom: 'Java', description: 'D' }]),
      }),
    );

    const result = await enrichArticlesWithCategories([
      { id: 1, titre: 'T', contenu: 'C', publie: true, date: '2024-01-01' },
    ]);

    expect(result[0].categories).toEqual([{ id: 1, nom: 'Java', description: 'D' }]);
  });

  it('enrichArticlesWithCategories retourne un tableau vide en cas derreur', async () => {
    localStorage.setItem('java_blog_token', 'token');
    mockFetch({ ok: false, status: 500 });

    const result = await enrichArticlesWithCategories([
      { id: 1, titre: 'T', contenu: 'C', publie: true, date: '2024-01-01' },
    ]);

    expect(result[0].categories).toEqual([]);
  });
});
