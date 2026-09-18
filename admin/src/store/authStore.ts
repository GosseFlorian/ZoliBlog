import { create } from 'zustand';
import {
  login as apiLogin,
  logout as apiLogout,
  isLoggedIn,
  getPseudo,
  getUserId,
} from '../api/auth.ts';
import type { LoginCredentials } from '../api/auth.ts';
import { useConfirmStore } from './confirmStore.ts';

interface AuthState {
  pseudo: string | null;
  userId: number | null;
  loginError: string | null;
  isLoggingIn: boolean;
  isAuthenticated: boolean;
  login: (credentials: LoginCredentials) => Promise<void>;
  logout: () => void;
  clearLoginError: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  pseudo: getPseudo(),
  userId: getUserId(),
  loginError: null,
  isLoggingIn: false,
  isAuthenticated: isLoggedIn(),

  login: async (credentials) => {
    try {
      set({ isLoggingIn: true, loginError: null });
      const data = await apiLogin(credentials);
      set({
        isAuthenticated: true,
        pseudo: data.pseudo,
        userId: data.userId,
        isLoggingIn: false,
      });
    } catch (err) {
      set({
        loginError: err instanceof Error ? err.message : 'Connexion impossible.',
        isLoggingIn: false,
      });
    }
  },

  logout: () => {
    useConfirmStore.getState().reset();
    apiLogout();
    set({
      isAuthenticated: false,
      pseudo: null,
      userId: null,
    });
  },

  clearLoginError: () => set({ loginError: null }),
}));
