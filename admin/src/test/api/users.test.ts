import { beforeEach, describe, expect, it, vi } from 'vitest';
import { API_URL } from '../../api/client';
import { deleteUser, fetchUsers } from '../../api/users';

function mockFetch(response: Partial<Response>) {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue(response));
}

describe('users.ts', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
  });

  it('fetchUsers retourne la liste', async () => {
    localStorage.setItem('java_blog_token', 'token');
    const users = [{ id: 1, pseudo: 'alice', mail: 'a@example.com' }];
    mockFetch({ ok: true, json: () => Promise.resolve(users) });

    const result = await fetchUsers();

    expect(fetch).toHaveBeenCalledWith(`${API_URL}/admin/users`, expect.any(Object));
    expect(result).toEqual(users);
  });

  it('fetchUsers lance une erreur si 401', async () => {
    mockFetch({ ok: false, status: 401 });

    await expect(fetchUsers()).rejects.toThrow('Session expirée — reconnecte-toi.');
  });

  it('deleteUser envoie un DELETE', async () => {
    localStorage.setItem('java_blog_token', 'token');
    mockFetch({ ok: true });

    await deleteUser(2);

    expect(fetch).toHaveBeenCalledWith(
      `${API_URL}/admin/users/2`,
      expect.objectContaining({ method: 'DELETE' }),
    );
  });
});
