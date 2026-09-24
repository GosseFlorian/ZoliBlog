import { beforeEach, describe, expect, it, vi } from 'vitest';
import { API_URL } from '../../api/client';
import { deleteComment, fetchComments } from '../../api/commentaires';

function mockFetch(response: Partial<Response>) {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue(response));
}

describe('commentaires.ts', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
  });

  it('fetchComments retourne la liste', async () => {
    localStorage.setItem('java_blog_token', 'token');
    const comments = [{ id: 1, contenu: 'Hi', userId: 1, pseudo: 'alice', date: '2024-01-01' }];
    mockFetch({ ok: true, json: () => Promise.resolve(comments) });

    const result = await fetchComments(1);

    expect(fetch).toHaveBeenCalledWith(
      `${API_URL}/admin/articles/1/commentaires`,
      expect.any(Object)
    );
    expect(result).toEqual(comments);
  });

  it('fetchComments lance une erreur si echec', async () => {
    mockFetch({ ok: false, status: 500 });

    await expect(fetchComments(1)).rejects.toThrow('Erreur lors du chargement des commentaires.');
  });

  it('deleteComment envoie un DELETE', async () => {
    localStorage.setItem('java_blog_token', 'token');
    mockFetch({ ok: true });

    await deleteComment(5);

    expect(fetch).toHaveBeenCalledWith(
      `${API_URL}/admin/commentaires/5`,
      expect.objectContaining({ method: 'DELETE' })
    );
  });
});
