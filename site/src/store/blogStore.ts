import { create } from 'zustand';
import {
  fetchArticle,
  fetchRecentArticles,
  fetchCategories,
  fetchArticlesByCategory,
  enrichArticlesWithCategories,
  fetchArticleCategories,
} from '../api/articles';
import type { Article, Categorie } from '../api/articles';
import {
  createComment,
  fetchComments,
  updateComment as apiUpdateComment,
  deleteComment as apiDeleteComment,
} from '../api/commentaires';
import type { Commentaire, CommentairePayload } from '../api/commentaires';
import { requestConfirm } from './confirmStore';

interface BlogState {
  articles: Article[];
  articlesLoading: boolean;
  articlesError: string | null;
  categories: Categorie[];
  categoriesLoading: boolean;
  categoriesError: string | null;
  selectedCategoryId: number | null;
  loadCategories: () => Promise<void>;
  loadArticles: () => Promise<void>;
  setSelectedCategoryId: (id: number | null) => Promise<void>;

  currentArticle: Article | null;
  currentArticleLoading: boolean;
  currentArticleError: string | null;
  loadArticle: (id: number) => Promise<void>;
  resetCurrentArticle: () => void;

  comments: Commentaire[];
  commentsLoading: boolean;
  commentsError: string | null;
  loadComments: (articleId: number) => Promise<void>;

  commentSubmitting: boolean;
  commentSubmitError: string | null;
  submitComment: (articleId: number, payload: CommentairePayload) => Promise<boolean>;

  commentUpdating: boolean;
  commentDeleting: boolean;
  commentActionError: string | null;
  updateComment: (id: number, contenu: string) => Promise<boolean>;
  deleteComment: (id: number) => Promise<boolean>;
}

async function loadArticlesForFilter(selectedCategoryId: number | null): Promise<Article[]> {
  if (selectedCategoryId == null) {
    return fetchRecentArticles();
  }
  return fetchArticlesByCategory(selectedCategoryId);
}

export const useBlogStore = create<BlogState>((set, get) => ({
  articles: [],
  articlesLoading: false,
  articlesError: null,
  categories: [],
  categoriesLoading: false,
  categoriesError: null,
  selectedCategoryId: null,

  async loadCategories() {
    set({ categoriesLoading: true, categoriesError: null });
    try {
      const categories = await fetchCategories();
      set({ categories, categoriesLoading: false });
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Impossible de charger les catégories.';
      set({ categoriesError: message, categoriesLoading: false });
    }
  },

  async loadArticles() {
    const { selectedCategoryId } = get();
    set({ articlesLoading: true, articlesError: null });
    try {
      const articles = await loadArticlesForFilter(selectedCategoryId);
      const enriched = await enrichArticlesWithCategories(articles);
      set({ articles: enriched, articlesLoading: false });
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Impossible de charger les articles.';
      set({ articlesError: message, articlesLoading: false });
    }
  },

  async setSelectedCategoryId(id: number | null) {
    set({ selectedCategoryId: id, articlesLoading: true, articlesError: null });
    try {
      const articles = await loadArticlesForFilter(id);
      const enriched = await enrichArticlesWithCategories(articles);
      set({ articles: enriched, articlesLoading: false });
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Impossible de charger les articles.';
      set({ articlesError: message, articlesLoading: false });
    }
  },

  currentArticle: null,
  currentArticleLoading: false,
  currentArticleError: null,
  async loadArticle(id: number) {
    set({
      currentArticleLoading: true,
      currentArticleError: null,
      currentArticle: null,
    });
    try {
      const article = await fetchArticle(id);
      if (!article) {
        set({
          currentArticleError: 'Article introuvable.',
          currentArticleLoading: false,
        });
        return;
      }
      const categories = await fetchArticleCategories(id);
      set({
        currentArticle: { ...article, categories },
        currentArticleLoading: false,
      });
    } catch (err) {
      const message = err instanceof Error ? err.message : "Impossible de charger l'article.";
      set({ currentArticleError: message, currentArticleLoading: false });
    }
  },
  resetCurrentArticle() {
    set({
      currentArticle: null,
      currentArticleError: null,
      currentArticleLoading: false,
      comments: [],
      commentsError: null,
      commentSubmitError: null,
      commentActionError: null,
    });
  },

  comments: [],
  commentsLoading: false,
  commentsError: null,
  async loadComments(articleId: number) {
    set({ commentsLoading: true, commentsError: null });
    try {
      const comments = await fetchComments(articleId);
      set({ comments, commentsLoading: false });
    } catch (err) {
      const message =
        err instanceof Error ? err.message : 'Impossible de charger les commentaires.';
      set({ commentsError: message, commentsLoading: false });
    }
  },

  commentSubmitting: false,
  commentSubmitError: null,
  async submitComment(articleId: number, payload: CommentairePayload) {
    set({ commentSubmitting: true, commentSubmitError: null });
    try {
      const created = await createComment(articleId, payload);
      set({
        comments: [created, ...get().comments],
        commentSubmitting: false,
      });
      return true;
    } catch (err) {
      const message = err instanceof Error ? err.message : "Impossible d'envoyer le commentaire.";
      set({ commentSubmitError: message, commentSubmitting: false });
      return false;
    }
  },

  commentUpdating: false,
  commentDeleting: false,
  commentActionError: null,
  async updateComment(id: number, contenu: string) {
    set({ commentUpdating: true, commentActionError: null });
    try {
      const updated = await apiUpdateComment(id, contenu);
      set({
        comments: get().comments.map((c) => (c.id === id ? updated : c)),
        commentUpdating: false,
      });
      return true;
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Impossible de modifier le commentaire.';
      set({ commentActionError: message, commentUpdating: false });
      return false;
    }
  },
  async deleteComment(id: number) {
    if (!(await requestConfirm('Supprimer ce commentaire ?\n\nCette action est définitive.'))) {
      return false;
    }

    set({ commentDeleting: true, commentActionError: null });
    try {
      await apiDeleteComment(id);
      set({
        comments: get().comments.filter((c) => c.id !== id),
        commentDeleting: false,
      });
      return true;
    } catch (err) {
      const message =
        err instanceof Error ? err.message : 'Impossible de supprimer le commentaire.';
      set({ commentActionError: message, commentDeleting: false });
      return false;
    }
  },
}));
