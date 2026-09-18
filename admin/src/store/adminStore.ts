import { create } from 'zustand';
import type { FeedbackType } from '../components/FeedbackMessage.tsx';
import {
  fetchAllArticles,
  createArticle,
  updateArticle,
  deleteArticle,
  enrichArticlesWithCategories,
  updateArticleCategories,
} from '../api/articles.ts';
import { fetchCategories } from '../api/categories.ts';
import type { CreateArticlePayload, UpdateArticlePayload } from '../api/articles.ts';
import { createCategory, updateCategory, deleteCategory } from '../api/categories.ts';
import type {
  Categorie,
  CreateCategoriePayload,
  UpdateCategoriePayload,
} from '../api/categories.ts';
import { fetchUsers, deleteUser } from '../api/users.ts';
import type { User } from '../api/users.ts';
import type { Article } from '../types/article.ts';
import { useAuthStore } from './authStore.ts';
import { requestConfirm } from './confirmStore.ts';

interface Feedback {
  type: FeedbackType;
  message: string;
}

interface AdminState {
  feedback: Feedback | null;
  articles: Article[];
  categories: Categorie[];
  users: User[];
  isLoading: boolean;
  error: string | null;

  loadArticles: () => Promise<void>;
  loadCategories: () => Promise<void>;
  loadUsers: () => Promise<void>;
  reset: () => void;
  clearFeedback: () => void;
  handleCreateArticleSubmit: (
    payload: CreateArticlePayload | (UpdateArticlePayload & { id: number }),
    categorieIds?: number[]
  ) => Promise<boolean>;
  handleEditArticleSubmit: (
    payload: CreateArticlePayload | (UpdateArticlePayload & { id: number }),
    categorieIds?: number[]
  ) => Promise<boolean>;
  handleDeleteArticle: (id: number) => Promise<void>;
  handleCreateCategorieSubmit: (
    payload: CreateCategoriePayload | (UpdateCategoriePayload & { id: number })
  ) => Promise<boolean>;
  handleEditCategorieSubmit: (
    payload: CreateCategoriePayload | (UpdateCategoriePayload & { id: number })
  ) => Promise<boolean>;
  handleDeleteCategorie: (id: number) => Promise<void>;
  handleDeleteUser: (id: number) => Promise<void>;
  handleSessionExpired: (message: string) => void;
}

const initialState = {
  feedback: null as Feedback | null,
  articles: [] as Article[],
  categories: [] as Categorie[],
  users: [] as User[],
  isLoading: false,
  error: null as string | null,
};

async function loadArticles(set: (partial: Partial<AdminState>) => void) {
  try {
    set({ isLoading: true, error: null });
    const data = await fetchAllArticles();
    const enriched = await enrichArticlesWithCategories(data);
    set({ articles: enriched, isLoading: false });
  } catch (err) {
    console.error(err);
    const message = err instanceof Error ? err.message : "Impossible de joindre l'API.";
    set({ error: message, isLoading: false });
  }
}

async function loadCategories(set: (partial: Partial<AdminState>) => void) {
  try {
    set({ isLoading: true, error: null });
    const data = await fetchCategories();
    set({ categories: data, isLoading: false });
  } catch (err) {
    console.error(err);
    const message = err instanceof Error ? err.message : "Impossible de joindre l'API.";
    set({ error: message, isLoading: false });
  }
}

async function loadUsers(set: (partial: Partial<AdminState>) => void) {
  try {
    set({ isLoading: true, error: null });
    const data = await fetchUsers();
    set({ users: data, isLoading: false });
  } catch (err) {
    console.error(err);
    const message = err instanceof Error ? err.message : "Impossible de joindre l'API.";
    set({ error: message, isLoading: false });
  }
}

export const useAdminStore = create<AdminState>((set, get) => ({
  ...initialState,

  loadArticles: async () => {
    await loadArticles(set);
    fetchCategories()
      .then((data) => set({ categories: data }))
      .catch((err) => console.error(err));
  },

  loadCategories: async () => {
    await loadCategories(set);
  },

  loadUsers: async () => {
    await loadUsers(set);
  },

  reset: () => set({ ...initialState }),

  clearFeedback: () => set({ feedback: null }),

  handleCreateArticleSubmit: async (payload, categorieIds) => {
    try {
      const created = await createArticle(payload as CreateArticlePayload);
      if (categorieIds != null && categorieIds.length > 0) {
        await updateArticleCategories(created.id, categorieIds);
      }
      await loadArticles(set);
      set({ feedback: { type: 'success', message: 'Article créé.' } });
      return true;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erreur à la création.';
      get().handleSessionExpired(message);
      set({ feedback: { type: 'error', message } });
      return false;
    }
  },

  handleEditArticleSubmit: async (payload, categorieIds) => {
    try {
      const editPayload = payload as UpdateArticlePayload & { id: number };
      await updateArticle(editPayload.id, {
        titre: editPayload.titre,
        contenu: editPayload.contenu,
        publie: editPayload.publie,
      });
      if (categorieIds != null) {
        await updateArticleCategories(editPayload.id, categorieIds);
      }
      await loadArticles(set);
      set({ feedback: { type: 'success', message: 'Article enregistré.' } });
      return true;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erreur à la modification.';
      get().handleSessionExpired(message);
      set({ feedback: { type: 'error', message } });
      return false;
    }
  },

  handleDeleteArticle: async (id) => {
    get().clearFeedback();
    const article = get().articles.find((a) => a.id === id);
    const titre = article?.titre ?? `#${id}`;

    if (!(await requestConfirm(`Supprimer l'article « ${titre} » ?\n\nCette action est définitive.`))) {
      return;
    }

    try {
      await deleteArticle(id);
      await loadArticles(set);
      set({ feedback: { type: 'success', message: 'Article supprimé.' } });
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erreur à la suppression.';
      get().handleSessionExpired(message);
      set({ feedback: { type: 'error', message } });
    }
  },

  handleCreateCategorieSubmit: async (payload) => {
    try {
      await createCategory(payload as CreateCategoriePayload);
      await loadCategories(set);
      set({ feedback: { type: 'success', message: 'Catégorie créée.' } });
      return true;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erreur à la création.';
      get().handleSessionExpired(message);
      set({ feedback: { type: 'error', message } });
      return false;
    }
  },

  handleEditCategorieSubmit: async (payload) => {
    try {
      const editPayload = payload as UpdateCategoriePayload & { id: number };
      await updateCategory(editPayload.id, {
        nom: editPayload.nom,
        description: editPayload.description,
      });
      await loadCategories(set);
      set({ feedback: { type: 'success', message: 'Catégorie enregistrée.' } });
      return true;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erreur à la modification.';
      get().handleSessionExpired(message);
      set({ feedback: { type: 'error', message } });
      return false;
    }
  },

  handleDeleteCategorie: async (id) => {
    get().clearFeedback();
    const categorie = get().categories.find((c) => c.id === id);
    const nom = categorie?.nom ?? `#${id}`;

    if (!(await requestConfirm(`Supprimer la catégorie « ${nom} » ?\n\nCette action est définitive.`))) {
      return;
    }

    try {
      await deleteCategory(id);
      await loadCategories(set);
      set({ feedback: { type: 'success', message: 'Catégorie supprimée.' } });
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erreur à la suppression.';
      get().handleSessionExpired(message);
      set({ feedback: { type: 'error', message } });
    }
  },

  handleDeleteUser: async (id) => {
    get().clearFeedback();
    const user = get().users.find((u) => u.id === id);
    const label = user?.pseudo ?? `#${id}`;

    if (!(await requestConfirm(`Supprimer l'utilisateur « ${label} » ?\n\nCette action est définitive.`))) {
      return;
    }

    try {
      await deleteUser(id);
      await loadUsers(set);
      set({ feedback: { type: 'success', message: 'Utilisateur supprimé.' } });
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Erreur à la suppression.';
      get().handleSessionExpired(message);
      set({ feedback: { type: 'error', message } });
    }
  },

  handleSessionExpired: (message) => {
    if (message.includes('Session expirée')) {
      useAuthStore.getState().logout();
      get().reset();
      useAuthStore.setState({
        loginError: 'Session expirée — reconnecte-toi.',
      });
    }
  },
}));
