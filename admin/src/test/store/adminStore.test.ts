import { beforeEach, describe, expect, it, vi } from 'vitest';
import { useAdminStore } from '../../store/adminStore';
import * as articlesApi from '../../api/articles';
import * as categoriesApi from '../../api/categories';
import * as usersApi from '../../api/users';

vi.mock('../../api/articles.ts');
vi.mock('../../api/categories.ts');
vi.mock('../../api/users.ts');

describe('adminStore', () => {
  beforeEach(() => {
    useAdminStore.getState().reset();
    vi.clearAllMocks();
  });

  it('setSection change la section et remet le mode list', () => {
    useAdminStore.setState({ mode: 'edit', editingArticle: { id: 1 } as never });
    useAdminStore.getState().setSection('users');

    const state = useAdminStore.getState();
    expect(state.section).toBe('users');
    expect(state.mode).toBe('list');
    expect(state.editingArticle).toBeNull();
  });

  it('loadCurrentSection charge les articles', async () => {
    vi.mocked(articlesApi.fetchAllArticles).mockResolvedValue([
      { id: 1, titre: 'A', contenu: 'C', publie: true, date: '2024-01-01' },
    ]);
    vi.mocked(articlesApi.enrichArticlesWithCategories).mockResolvedValue([
      { id: 1, titre: 'A', contenu: 'C', publie: true, date: '2024-01-01', categories: [] },
    ]);
    vi.mocked(categoriesApi.fetchCategories).mockResolvedValue([]);

    await useAdminStore.getState().loadCurrentSection();

    expect(useAdminStore.getState().articles).toHaveLength(1);
    expect(useAdminStore.getState().isLoading).toBe(false);
  });

  it('handleViewArticle passe en mode view', () => {
    useAdminStore.getState().handleViewArticle(3);
    expect(useAdminStore.getState().viewingArticleId).toBe(3);
    expect(useAdminStore.getState().mode).toBe('view');
  });

  it('handleCreateArticleSubmit crée et affiche un feedback', async () => {
    vi.mocked(articlesApi.createArticle).mockResolvedValue({
      id: 10,
      titre: 'N',
      contenu: 'C',
      publie: false,
      date: '2024-01-01',
    });
    vi.mocked(articlesApi.fetchAllArticles).mockResolvedValue([]);
    vi.mocked(articlesApi.enrichArticlesWithCategories).mockResolvedValue([]);

    await useAdminStore.getState().handleCreateArticleSubmit({
      titre: 'N',
      contenu: 'C',
      userId: 1,
    });

    expect(useAdminStore.getState().feedback?.type).toBe('success');
    expect(useAdminStore.getState().mode).toBe('list');
  });

  it('handleDeleteUser ne fait rien si confirm annulé', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(false);
    useAdminStore.setState({
      section: 'users',
      users: [{ id: 2, pseudo: 'bob', mail: 'b@example.com' }],
    });

    await useAdminStore.getState().handleDeleteUser(2);

    expect(usersApi.deleteUser).not.toHaveBeenCalled();
  });

  it('clearFeedback efface le message', () => {
    useAdminStore.setState({ feedback: { type: 'success', message: 'OK' } });
    useAdminStore.getState().clearFeedback();
    expect(useAdminStore.getState().feedback).toBeNull();
  });

  it('loadCurrentSection charge les catégories', async () => {
    vi.mocked(categoriesApi.fetchCategories).mockResolvedValue([
      { id: 1, nom: 'Java', description: 'D' },
    ]);

    useAdminStore.getState().setSection('categories');
    await useAdminStore.getState().loadCurrentSection();

    expect(useAdminStore.getState().categories).toHaveLength(1);
  });

  it('loadCurrentSection charge les utilisateurs', async () => {
    vi.mocked(usersApi.fetchUsers).mockResolvedValue([
      { id: 1, pseudo: 'alice', mail: 'a@example.com' },
    ]);

    useAdminStore.getState().setSection('users');
    await useAdminStore.getState().loadCurrentSection();

    expect(useAdminStore.getState().users).toHaveLength(1);
  });

  it('handleEditArticle charge les catégories de larticle', async () => {
    useAdminStore.setState({
      articles: [{ id: 1, titre: 'A', contenu: 'C', publie: true, date: '2024-01-01' }],
    });
    vi.mocked(articlesApi.fetchArticleCategories).mockResolvedValue([
      { id: 1, nom: 'Java', description: 'D' },
    ]);

    await useAdminStore.getState().handleEditArticle(1);

    expect(useAdminStore.getState().mode).toBe('edit');
    expect(useAdminStore.getState().editingArticleCategoryIds).toEqual([1]);
  });

  it('handleEditCategorie passe en mode edit', () => {
    useAdminStore.setState({
      categories: [{ id: 2, nom: 'Spring', description: 'D' }],
    });

    useAdminStore.getState().handleEditCategorie(2);

    expect(useAdminStore.getState().editingCategorie?.nom).toBe('Spring');
    expect(useAdminStore.getState().mode).toBe('edit');
  });

  it('handleDeleteArticle supprime si confirmé', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    vi.mocked(articlesApi.deleteArticle).mockResolvedValue(undefined);
    vi.mocked(articlesApi.fetchAllArticles).mockResolvedValue([]);
    vi.mocked(articlesApi.enrichArticlesWithCategories).mockResolvedValue([]);
    useAdminStore.setState({
      articles: [{ id: 1, titre: 'A', contenu: 'C', publie: true, date: '2024-01-01' }],
    });

    await useAdminStore.getState().handleDeleteArticle(1);

    expect(articlesApi.deleteArticle).toHaveBeenCalledWith(1);
    expect(useAdminStore.getState().feedback?.type).toBe('success');
  });

  it('handleSessionExpired déconnecte si session expirée', () => {
    useAdminStore.getState().handleSessionExpired('Session expirée — reconnecte-toi.');
    expect(useAdminStore.getState().section).toBe('articles');
  });

  it('handleCreateCategorieSubmit crée une catégorie', async () => {
    vi.mocked(categoriesApi.createCategory).mockResolvedValue({
      id: 3,
      nom: 'New',
      description: 'D',
    });
    vi.mocked(categoriesApi.fetchCategories).mockResolvedValue([]);

    await useAdminStore.getState().handleCreateCategorieSubmit({ nom: 'New', description: 'D' });

    expect(useAdminStore.getState().feedback?.message).toBe('Catégorie créée.');
  });

  it('handleEditArticleSubmit modifie un article', async () => {
    vi.mocked(articlesApi.updateArticle).mockResolvedValue({
      id: 1,
      titre: 'Mod',
      contenu: 'C',
      publie: true,
      date: '2024-01-01',
    });
    vi.mocked(articlesApi.fetchAllArticles).mockResolvedValue([]);
    vi.mocked(articlesApi.enrichArticlesWithCategories).mockResolvedValue([]);

    await useAdminStore.getState().handleEditArticleSubmit({
      id: 1,
      titre: 'Mod',
      contenu: 'C',
      publie: true,
    });

    expect(useAdminStore.getState().feedback?.type).toBe('success');
  });

  it('showCreate passe en mode create', () => {
    useAdminStore.getState().showCreate();
    expect(useAdminStore.getState().mode).toBe('create');
  });

  it('handleDeleteUser supprime si confirmé', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    vi.mocked(usersApi.deleteUser).mockResolvedValue(undefined);
    vi.mocked(usersApi.fetchUsers).mockResolvedValue([]);
    useAdminStore.setState({
      section: 'users',
      users: [{ id: 2, pseudo: 'bob', mail: 'b@example.com' }],
    });

    await useAdminStore.getState().handleDeleteUser(2);

    expect(usersApi.deleteUser).toHaveBeenCalledWith(2);
  });

  it('loadCurrentSection stocke une erreur si echec', async () => {
    vi.mocked(articlesApi.fetchAllArticles).mockRejectedValue(new Error('API down'));

    await useAdminStore.getState().loadCurrentSection();

    expect(useAdminStore.getState().error).toBe('API down');
  });

  it('showList remet le mode list', () => {
    useAdminStore.setState({ mode: 'view', viewingArticleId: 1 });
    useAdminStore.getState().showList();
    expect(useAdminStore.getState().mode).toBe('list');
    expect(useAdminStore.getState().viewingArticleId).toBeNull();
  });
});
