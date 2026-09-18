import { create } from 'zustand';
import {
  clearAuth,
  getStoredAuth,
  login as apiLogin,
  persistAuth,
  register as apiRegister,
} from '../api/auth';
import type { LoginPayload, RegisterPayload } from '../api/auth';
import { useConfirmStore } from './confirmStore';

/**
 * Store zustand — session du visiteur (token JWT, userId, pseudo).
 *
 * Utilisé par le header (afficher pseudo / déconnexion), CommentForm
 * (rattacher le commentaire au bon userId sans le redemander), et les
 * pages Connexion / Créer un compte — sans props drilling.
 *
 * La session est persistée dans localStorage (voir api/auth.ts) pour
 * survivre à un rechargement de page, initialisée une fois au chargement
 * du module.
 */
interface AuthState {
  token: string | null;
  userId: number | null;
  pseudo: string | null;
  authSubmitting: boolean;
  authError: string | null;
  login: (payload: LoginPayload) => Promise<boolean>;
  register: (payload: RegisterPayload) => Promise<boolean>;
  logout: () => void;
}

const stored = getStoredAuth();

export const useAuthStore = create<AuthState>((set) => ({
  token: stored?.token ?? null,
  userId: stored?.userId ?? null,
  pseudo: stored?.pseudo ?? null,
  authSubmitting: false,
  authError: null,

  async login(payload) {
    set({ authSubmitting: true, authError: null });
    try {
      const auth = await apiLogin(payload);
      persistAuth(auth);
      set({
        token: auth.token,
        userId: auth.userId,
        pseudo: auth.pseudo,
        authSubmitting: false,
      });
      return true;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erreur de connexion.';
      set({ authError: message, authSubmitting: false });
      return false;
    }
  },

  async register(payload) {
    set({ authSubmitting: true, authError: null });
    try {
      const auth = await apiRegister(payload);
      persistAuth(auth);
      set({
        token: auth.token,
        userId: auth.userId,
        pseudo: auth.pseudo,
        authSubmitting: false,
      });
      return true;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erreur lors de la création du compte.';
      set({ authError: message, authSubmitting: false });
      return false;
    }
  },

  logout() {
    useConfirmStore.getState().reset();
    clearAuth();
    set({ token: null, userId: null, pseudo: null, authError: null });
  },
}));
