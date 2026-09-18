import { beforeEach, describe, expect, it, vi } from 'vitest';
import { useAdminStore } from '../../store/adminStore';
import { useConfirmStore } from '../../store/confirmStore';
import * as articlesApi from '../../api/articles';
import * as categoriesApi from '../../api/categories';
import * as usersApi from '../../api/users';

vi.mock('../../api/articles.ts');
vi.mock('../../api/categories.ts');
vi.mock('../../api/users.ts');

describe('adminStore', () => {
  beforeEach(() => {
    useAdminStore.getState().reset();
    useConfirmStore.getState().reset();
    vi.clearAllMocks();
  });

  it('loadArticles charge les articles', async () => {
    vi.mocked(articlesApi.fetchAllArticles).mockResolvedValue([
      { id: 1, titre: 'A', contenu: 'C', publie: true, date: '2024-01-01' },
    ]);
    vi.mocked(articlesApi.enrichArticlesWithCategories).mockResolvedValue([
      { id: 1, titre: 'A', contenu: 'C', publie: true, date: '2024-01-01', categories: [] },
    ]);
    vi.mocked(categoriesApi.fetchCategories).mockResolvedValue([]);

    await useAdminStore.getState().loadArticles();

    expect(useAdminStore.getState().articles).toHaveLength(1);
    expect(useAdminStore.getState().isLoading).toBe(false);
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

    const success = await useAdminStore.getState().handleCreateArticleSubmit({
      titre: 'N',
      contenu: 'C',
      userId: 1,
    });

    expect(success).toBe(true);
    expect(useAdminStore.getState().feedback?.type).toBe('success');
  });

  it('handleDeleteUser ne fait rien si confirm annulé', async () => {
    useAdminStore.setState({
      users: [{ id: 2, pseudo: 'bob', mail: 'b@example.com' }],
    });

    const deletePromise = useAdminStore.getState().handleDeleteUser(2);
    await vi.waitFor(() => {
      expect(useConfirmStore.getState().confirmRequest).not.toBeNull();
    });
    useConfirmStore.getState().resolveConfirm(false);
    await deletePromise;

    expect(usersApi.deleteUser).not.toHaveBeenCalled();
  });

  it('clearFeedback efface le message', () => {
    useAdminStore.setState({ feedback: { type: 'success', message: 'OK' } });
    useAdminStore.getState().clearFeedback();
    expect(useAdminStore.getState().feedback).toBeNull();
  });

  it('loadCategories charge les catégories', async () => {
    vi.mocked(categoriesApi.fetchCategories).mockResolvedValue([
      { id: 1, nom: 'Java', description: 'D' },
    ]);

    await useAdminStore.getState().loadCategories();

    expect(useAdminStore.getState().categories).toHaveLength(1);
  });

  it('loadUsers charge les utilisateurs', async () => {
    vi.mocked(usersApi.fetchUsers).mockResolvedValue([
      { id: 1, pseudo: 'alice', mail: 'a@example.com' },
    ]);

    await useAdminStore.getState().loadUsers();

    expect(useAdminStore.getState().users).toHaveLength(1);
  });

  it('handleDeleteArticle supprime si confirmé', async () => {
    vi.mocked(articlesApi.deleteArticle).mockResolvedValue(undefined);
    vi.mocked(articlesApi.fetchAllArticles).mockResolvedValue([]);
    vi.mocked(articlesApi.enrichArticlesWithCategories).mockResolvedValue([]);
    useAdminStore.setState({
      articles: [{ id: 1, titre: 'A', contenu: 'C', publie: true, date: '2024-01-01' }],
    });

    const deletePromise = useAdminStore.getState().handleDeleteArticle(1);
    await vi.waitFor(() => {
      expect(useConfirmStore.getState().confirmRequest).not.toBeNull();
    });
    useConfirmStore.getState().resolveConfirm(true);
    await deletePromise;

    expect(articlesApi.deleteArticle).toHaveBeenCalledWith(1);
    expect(useAdminStore.getState().feedback?.type).toBe('success');
  });

  it('handleSessionExpired déconnecte si session expirée', () => {
    useAdminStore.getState().handleSessionExpired('Session expirée — reconnecte-toi.');
    expect(useAdminStore.getState().articles).toHaveLength(0);
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

    const success = await useAdminStore.getState().handleEditArticleSubmit({
      id: 1,
      titre: 'Mod',
      contenu: 'C',
      publie: true,
    });

    expect(success).toBe(true);
    expect(useAdminStore.getState().feedback?.type).toBe('success');
  });

  it('handleDeleteUser supprime si confirmé', async () => {
    vi.mocked(usersApi.deleteUser).mockResolvedValue(undefined);
    vi.mocked(usersApi.fetchUsers).mockResolvedValue([]);
    useAdminStore.setState({
      users: [{ id: 2, pseudo: 'bob', mail: 'b@example.com' }],
    });

    const deletePromise = useAdminStore.getState().handleDeleteUser(2);
    await vi.waitFor(() => {
      expect(useConfirmStore.getState().confirmRequest).not.toBeNull();
    });
    useConfirmStore.getState().resolveConfirm(true);
    await deletePromise;

    expect(usersApi.deleteUser).toHaveBeenCalledWith(2);
  });

  it('loadArticles stocke une erreur si echec', async () => {
    vi.mocked(articlesApi.fetchAllArticles).mockRejectedValue(new Error('API down'));

    await useAdminStore.getState().loadArticles();

    expect(useAdminStore.getState().error).toBe('API down');
  });
});
