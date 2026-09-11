import { beforeEach, describe, expect, it, vi } from 'vitest';
import { API_URL } from '../../api/articles';
import {
  clearAuth,
  getStoredAuth,
  getToken,
  login,
  persistAuth,
  register,
} from '../../api/auth';

function mockFetch(response: Partial<Response>) {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue(response));
}

describe('auth.ts', () => {
  beforeEach(() => {
    vi.restoreAllMocks();
    localStorage.clear();
  });

  it('getStoredAuth retourne null si incomplet', () => {
    localStorage.setItem('site_token', 't');
    expect(getStoredAuth()).toBeNull();
  });

  it('persistAuth puis getStoredAuth retourne la session', () => {
    persistAuth({ token: 't', pseudo: 'alice', userId: 1 });
    expect(getStoredAuth()).toEqual({ token: 't', pseudo: 'alice', userId: 1 });
    expect(getToken()).toBe('t');
  });

  it('clearAuth efface la session', () => {
    persistAuth({ token: 't', pseudo: 'alice', userId: 1 });
    clearAuth();
    expect(getToken()).toBeNull();
    expect(getStoredAuth()).toBeNull();
  });

  it('login retourne les données en cas de succès', async () => {
    const auth = { token: 'jwt', pseudo: 'bob', userId: 2 };
    mockFetch({ ok: true, json: () => Promise.resolve(auth) });

    const result = await login({ mail: 'bob@example.com', mdp: 'demo1234' });

    expect(fetch).toHaveBeenCalledWith(`${API_URL}/auth/login`, expect.any(Object));
    expect(result).toEqual(auth);
  });

  it('login lance une erreur si 401', async () => {
    mockFetch({ ok: false, status: 401 });

    await expect(login({ mail: 'x@example.com', mdp: 'wrong' })).rejects.toThrow(
      'Email ou mot de passe incorrect.',
    );
  });

  it('register lance une erreur si 409', async () => {
    mockFetch({ ok: false, status: 409 });

    await expect(
      register({ pseudo: 'new', mail: 'a@example.com', mdp: 'demo1234' }),
    ).rejects.toThrow('Un compte existe déjà avec cette adresse mail.');
  });
});
