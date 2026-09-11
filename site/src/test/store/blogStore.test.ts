import { beforeEach, describe, expect, it, vi } from 'vitest';
import { useBlogStore } from '../../store/blogStore';
import * as articlesApi from '../../api/articles';
import * as commentairesApi from '../../api/commentaires';

vi.mock('../../api/articles');
vi.mock('../../api/commentaires');

describe('blogStore', () => {
  beforeEach(() => {
    useBlogStore.setState({
      articles: [],
      articlesLoading: false,
      articlesError: null,
      categories: [],
      categoriesLoading: false,
      categoriesError: null,
      selectedCategoryId: null,
      currentArticle: null,
      currentArticleLoading: false,
      currentArticleError: null,
      comments: [],
      commentsLoading: false,
      commentsError: null,
      commentSubmitting: false,
      commentSubmitError: null,
      commentUpdating: false,
      commentDeleting: false,
      commentActionError: null,
    });
    vi.clearAllMocks();
  });

  it('loadCategories charge les catégories', async () => {
    vi.mocked(articlesApi.fetchCategories).mockResolvedValue([
      { id: 1, nom: 'Java', description: 'D' },
    ]);

    await useBlogStore.getState().loadCategories();

    expect(useBlogStore.getState().categories).toHaveLength(1);
    expect(useBlogStore.getState().categoriesLoading).toBe(false);
  });

  it('loadArticles charge les articles récents par défaut', async () => {
    vi.mocked(articlesApi.fetchRecentArticles).mockResolvedValue([
      { id: 1, titre: 'A', contenu: 'C', publie: true, date: '2024-01-01' },
    ]);
    vi.mocked(articlesApi.enrichArticlesWithCategories).mockResolvedValue([
      { id: 1, titre: 'A', contenu: 'C', publie: true, date: '2024-01-01', categories: [] },
    ]);

    await useBlogStore.getState().loadArticles();

    expect(useBlogStore.getState().articles).toHaveLength(1);
  });

  it('setSelectedCategoryId filtre par catégorie', async () => {
    vi.mocked(articlesApi.fetchArticlesByCategory).mockResolvedValue([
      { id: 2, titre: 'B', contenu: 'C', publie: true, date: '2024-01-01' },
    ]);
    vi.mocked(articlesApi.enrichArticlesWithCategories).mockImplementation(async (a) => a);

    await useBlogStore.getState().setSelectedCategoryId(1);

    expect(articlesApi.fetchArticlesByCategory).toHaveBeenCalledWith(1);
    expect(useBlogStore.getState().selectedCategoryId).toBe(1);
  });

  it('loadArticle signale un article introuvable', async () => {
    vi.mocked(articlesApi.fetchArticle).mockResolvedValue(null);

    await useBlogStore.getState().loadArticle(999);

    expect(useBlogStore.getState().currentArticleError).toBe('Article introuvable.');
  });

  it('submitComment ajoute le commentaire créé', async () => {
    const created = { id: 5, contenu: 'Hi', userId: 1, pseudo: 'alice', date: '2024-01-01' };
    vi.mocked(commentairesApi.createComment).mockResolvedValue(created);

    const ok = await useBlogStore.getState().submitComment(1, { contenu: 'Hi', userId: 1 });

    expect(ok).toBe(true);
    expect(useBlogStore.getState().comments[0]).toEqual(created);
  });

  it('deleteComment retire le commentaire de la liste', async () => {
    useBlogStore.setState({
      comments: [{ id: 1, contenu: 'X', userId: 1, pseudo: 'a', date: '2024-01-01' }],
    });
    vi.mocked(commentairesApi.deleteComment).mockResolvedValue(undefined);

    const ok = await useBlogStore.getState().deleteComment(1);

    expect(ok).toBe(true);
    expect(useBlogStore.getState().comments).toHaveLength(0);
  });

  it('updateComment remplace le commentaire', async () => {
    useBlogStore.setState({
      comments: [{ id: 1, contenu: 'Ancien', userId: 1, pseudo: 'a', date: '2024-01-01' }],
    });
    vi.mocked(commentairesApi.updateComment).mockResolvedValue({
      id: 1,
      contenu: 'Nouveau',
      userId: 1,
      pseudo: 'a',
      date: '2024-01-01',
    });

    const ok = await useBlogStore.getState().updateComment(1, 'Nouveau');

    expect(ok).toBe(true);
    expect(useBlogStore.getState().comments[0].contenu).toBe('Nouveau');
  });

  it('loadArticle charge larticle avec ses catégories', async () => {
    vi.mocked(articlesApi.fetchArticle).mockResolvedValue({
      id: 1,
      titre: 'A',
      contenu: 'C',
      publie: true,
      date: '2024-01-01',
    });
    vi.mocked(articlesApi.fetchArticleCategories).mockResolvedValue([
      { id: 1, nom: 'Java', description: 'D' },
    ]);

    await useBlogStore.getState().loadArticle(1);

    expect(useBlogStore.getState().currentArticle?.categories).toHaveLength(1);
  });

  it('loadComments stocke une erreur si echec', async () => {
    vi.mocked(commentairesApi.fetchComments).mockRejectedValue(new Error('Erreur réseau'));

    await useBlogStore.getState().loadComments(1);

    expect(useBlogStore.getState().commentsError).toBe('Erreur réseau');
  });

  it('resetCurrentArticle remet à zéro', () => {
    useBlogStore.setState({
      currentArticle: { id: 1, titre: 'A', contenu: 'C', publie: true, date: '2024-01-01' },
      comments: [{ id: 1, contenu: 'X', userId: 1, pseudo: 'a', date: '2024-01-01' }],
    });

    useBlogStore.getState().resetCurrentArticle();

    expect(useBlogStore.getState().currentArticle).toBeNull();
    expect(useBlogStore.getState().comments).toHaveLength(0);
  });
});
