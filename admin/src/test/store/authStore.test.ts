import { beforeEach, describe, expect, it, vi } from 'vitest';
import { useAuthStore } from '../../store/authStore';
import { useConfirmStore } from '../../store/confirmStore';
import * as authApi from '../../api/auth';

vi.mock('../../api/auth');

describe('authStore', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
    useAuthStore.setState({
      pseudo: null,
      userId: null,
      loginError: null,
      isLoggingIn: false,
      isAuthenticated: false,
    });
  });

  it('isAuthenticated est false par défaut après reset', () => {
    expect(useAuthStore.getState().isAuthenticated).toBe(false);
  });

  it('clearLoginError efface loginError', () => {
    useAuthStore.setState({ loginError: 'Erreur test' });
    useAuthStore.getState().clearLoginError();
    expect(useAuthStore.getState().loginError).toBeNull();
  });

  it('login réussit et met à jour le state', async () => {
    vi.mocked(authApi.login).mockResolvedValue({
      token: 'jwt',
      pseudo: 'alice',
      userId: 1,
      role: 'ADMIN',
    });

    await useAuthStore.getState().login({ mail: 'a@example.com', mdp: 'demo1234' });

    expect(useAuthStore.getState().isAuthenticated).toBe(true);
    expect(useAuthStore.getState().pseudo).toBe('alice');
  });

  it('logout remet isAuthenticated à false', () => {
    useAuthStore.setState({
      isAuthenticated: true,
      pseudo: 'alice',
      userId: 1,
    });
    useAuthStore.getState().logout();
    expect(useAuthStore.getState().isAuthenticated).toBe(false);
    expect(useAuthStore.getState().pseudo).toBeNull();
  });

  it('logout ferme une confirmation en cours', async () => {
    let resolved: boolean | undefined;
    void useConfirmStore.getState().requestConfirm('Test ?').then((ok) => {
      resolved = ok;
    });

    useAuthStore.getState().logout();

    expect(useConfirmStore.getState().confirmRequest).toBeNull();
    await Promise.resolve();
    expect(resolved).toBe(false);
  });
});
