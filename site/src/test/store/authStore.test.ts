import { beforeEach, describe, expect, it, vi } from 'vitest';
import { useAuthStore } from '../../store/authStore';
import { useConfirmStore } from '../../store/confirmStore';
import * as authApi from '../../api/auth';

vi.mock('../../api/auth');

describe('authStore', () => {
  beforeEach(() => {
    localStorage.clear();
    useAuthStore.setState({
      token: null,
      userId: null,
      pseudo: null,
      authSubmitting: false,
      authError: null,
    });
    vi.clearAllMocks();
  });

  it('login réussit et met à jour le state', async () => {
    vi.mocked(authApi.login).mockResolvedValue({ token: 'jwt', pseudo: 'alice', userId: 1 });

    const ok = await useAuthStore.getState().login({ mail: 'a@example.com', mdp: 'demo1234' });

    expect(ok).toBe(true);
    expect(useAuthStore.getState().token).toBe('jwt');
    expect(useAuthStore.getState().pseudo).toBe('alice');
    expect(authApi.persistAuth).toHaveBeenCalled();
  });

  it('login échoue et stocke authError', async () => {
    vi.mocked(authApi.login).mockRejectedValue(new Error('Email ou mot de passe incorrect.'));

    const ok = await useAuthStore.getState().login({ mail: 'x@example.com', mdp: 'wrong' });

    expect(ok).toBe(false);
    expect(useAuthStore.getState().authError).toBe('Email ou mot de passe incorrect.');
  });

  it('register réussit', async () => {
    vi.mocked(authApi.register).mockResolvedValue({ token: 'jwt', pseudo: 'new', userId: 3 });

    const ok = await useAuthStore.getState().register({
      pseudo: 'new',
      mail: 'n@example.com',
      mdp: 'demo1234',
    });

    expect(ok).toBe(true);
    expect(useAuthStore.getState().userId).toBe(3);
  });

  it('logout efface la session', () => {
    useAuthStore.setState({ token: 'jwt', userId: 1, pseudo: 'alice' });
    useAuthStore.getState().logout();

    expect(useAuthStore.getState().token).toBeNull();
    expect(authApi.clearAuth).toHaveBeenCalled();
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
