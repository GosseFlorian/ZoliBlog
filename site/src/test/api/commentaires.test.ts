import { beforeEach, describe, expect, it, vi } from 'vitest';
import { API_URL } from '../../api/articles';
import { createComment, deleteComment, fetchComments, updateComment } from '../../api/commentaires';

function mockFetch(response: Partial<Response>) {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue(response));
}

describe('commentaires.ts', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
  });

  it('fetchComments retourne la liste', async () => {
    const comments = [{ id: 1, contenu: 'Hi', userId: 1, pseudo: 'alice', date: '2024-01-01' }];
    mockFetch({ ok: true, json: () => Promise.resolve(comments) });

    const result = await fetchComments(1);

    expect(fetch).toHaveBeenCalledWith(`${API_URL}/articles/1/commentaires`);
    expect(result).toEqual(comments);
  });

  it('createComment exige un token', async () => {
    await expect(createComment(1, { contenu: 'Hi', userId: 1 })).rejects.toThrow(
      'Connecte-toi pour laisser un commentaire.'
    );
  });

  it('createComment envoie un POST avec Authorization', async () => {
    localStorage.setItem('site_token', 'jwt');
    const created = { id: 2, contenu: 'Hi', userId: 1, pseudo: 'alice', date: '2024-01-01' };
    mockFetch({ ok: true, json: () => Promise.resolve(created) });

    const result = await createComment(1, { contenu: 'Hi', userId: 1 });

    expect(fetch).toHaveBeenCalledWith(
      `${API_URL}/articles/1/commentaires`,
      expect.objectContaining({
        method: 'POST',
        headers: expect.objectContaining({ Authorization: 'Bearer jwt' }),
      })
    );
    expect(result).toEqual(created);
  });

  it('updateComment lance une erreur si 403', async () => {
    localStorage.setItem('site_token', 'jwt');
    mockFetch({ ok: false, status: 403 });

    await expect(updateComment(1, 'modifié')).rejects.toThrow(
      'Tu ne peux modifier que tes propres commentaires.'
    );
  });

  it('createComment lance une erreur si 401', async () => {
    localStorage.setItem('site_token', 'jwt');
    mockFetch({ ok: false, status: 401 });

    await expect(createComment(1, { contenu: 'Hi', userId: 1 })).rejects.toThrow(
      'Session expirée — reconnecte-toi.'
    );
  });

  it('updateComment exige un token', async () => {
    await expect(updateComment(1, 'texte')).rejects.toThrow(
      'Connecte-toi pour modifier ton commentaire.'
    );
  });

  it('deleteComment envoie un DELETE', async () => {
    localStorage.setItem('site_token', 'jwt');
    mockFetch({ ok: true });

    await deleteComment(3);

    expect(fetch).toHaveBeenCalledWith(
      `${API_URL}/commentaires/3`,
      expect.objectContaining({ method: 'DELETE' })
    );
  });
});
