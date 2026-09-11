import { describe, it, expect, beforeEach, vi } from 'vitest';
import { getToken, logout, isLoggedIn, getAuthHeaders, login } from '../../api/auth';

describe('auth.ts', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('isLoggedIn retourne false sans token', () => {
    expect(isLoggedIn()).toBe(false);
  });

  it('isLoggedIn retourne false si le token existe mais le role nest pas ADMIN', () => {
    localStorage.setItem('java_blog_token', 'fake-jwt');
    localStorage.setItem('java_blog_role', 'USER');

    expect(isLoggedIn()).toBe(false);
  });

  it('isLoggedIn retourne true pour un admin', () => {
    localStorage.setItem('java_blog_token', 'fake-jwt');
    localStorage.setItem('java_blog_role', 'ADMIN');

    expect(isLoggedIn()).toBe(true);
  });

  it('getAuthHeaders retourne Authorization si token présent', () => {
    localStorage.setItem('java_blog_token', 'fake-jwt');

    expect(getAuthHeaders()).toEqual({
      Authorization: 'Bearer fake-jwt',
    });
  });

  it('logout efface le token', () => {
    localStorage.setItem('java_blog_token', 'x');
    localStorage.setItem('java_blog_role', 'ADMIN');

    logout();

    expect(getToken()).toBeNull();
    expect(localStorage.getItem('java_blog_role')).toBeNull();
  });

  it('login réussit pour un admin', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: true,
        json: () =>
          Promise.resolve({
            token: 'jwt',
            pseudo: 'alice',
            userId: 1,
            role: 'ADMIN',
          }),
      }),
    );

    const result = await login({ mail: 'alice@example.com', mdp: 'demo1234' });

    expect(result.role).toBe('ADMIN');
    expect(isLoggedIn()).toBe(true);

    vi.unstubAllGlobals();
  });

  it('login lance une erreur si identifiants invalides', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: false,
        status: 401,
      }),
    );

    await expect(login({ mail: 'alice@example.com', mdp: 'wrong' })).rejects.toThrow(
      'Identifiants invalides',
    );

    vi.unstubAllGlobals();
  });

  it('login refuse un utilisateur non admin', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: false,
        status: 403,
      }),
    );

    await expect(
      login({ mail: 'bob@example.com', mdp: 'demo1234' }),
    ).rejects.toThrow('Acces reserve aux administrateurs.');

    expect(getToken()).toBeNull();
    expect(isLoggedIn()).toBe(false);

    vi.unstubAllGlobals();
  });
});
