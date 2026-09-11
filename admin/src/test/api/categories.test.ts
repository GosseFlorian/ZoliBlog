import { beforeEach, describe, expect, it, vi } from 'vitest';
import { API_URL } from '../../api/client';
import { createCategory, deleteCategory, fetchCategories, updateCategory } from '../../api/categories';

function mockFetch(response: Partial<Response>) {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue(response));
}

describe('categories.ts', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
  });

  it('fetchCategories retourne la liste', async () => {
    const categories = [{ id: 1, nom: 'Java', description: 'D' }];
    mockFetch({ ok: true, json: () => Promise.resolve(categories) });

    const result = await fetchCategories();

    expect(fetch).toHaveBeenCalledWith(`${API_URL}/categories`);
    expect(result).toEqual(categories);
  });

  it('createCategory lance une erreur si 401', async () => {
    mockFetch({ ok: false, status: 401 });

    await expect(createCategory({ nom: 'X', description: 'Y' })).rejects.toThrow(
      'Session expirée — reconnecte-toi.',
    );
  });

  it('updateCategory envoie un PUT', async () => {
    localStorage.setItem('java_blog_token', 'token');
    const updated = { id: 1, nom: 'Mod', description: 'D' };
    mockFetch({ ok: true, json: () => Promise.resolve(updated) });

    const result = await updateCategory(1, { nom: 'Mod', description: 'D' });

    expect(fetch).toHaveBeenCalledWith(
      `${API_URL}/admin/categories/1`,
      expect.objectContaining({ method: 'PUT' }),
    );
    expect(result).toEqual(updated);
  });

  it('deleteCategory envoie un DELETE', async () => {
    localStorage.setItem('java_blog_token', 'token');
    mockFetch({ ok: true });

    await deleteCategory(1);

    expect(fetch).toHaveBeenCalledWith(
      `${API_URL}/admin/categories/1`,
      expect.objectContaining({ method: 'DELETE' }),
    );
  });
});
