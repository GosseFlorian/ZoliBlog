import { useEffect, useState } from 'react';
import { Link, Navigate, Route, Routes, useNavigate, useParams } from 'react-router-dom';
import CategorieList from '../components/CategorieList.tsx';
import CategorieForm from '../components/CategorieForm.tsx';
import LoadingMessage from '../components/LoadingMessage.tsx';
import type { Categorie } from '../api/categories.ts';
import { useAdminStore } from '../store/adminStore.ts';

function CategoriesListView() {
  const navigate = useNavigate();
  const categories = useAdminStore((s) => s.categories);
  const isLoading = useAdminStore((s) => s.isLoading);
  const error = useAdminStore((s) => s.error);
  const loadCategories = useAdminStore((s) => s.loadCategories);
  const handleDeleteCategorie = useAdminStore((s) => s.handleDeleteCategorie);

  useEffect(() => {
    loadCategories();
  }, [loadCategories]);

  return (
    <>
      <div className="page-header-row">
        <h1>Catégories</h1>
        <Link to="/categories/new" className="btn btn-primary btn-small">
          + Nouvelle catégorie
        </Link>
      </div>

      {isLoading && <LoadingMessage />}
      {error && <p className="error-message">{error}</p>}
      {!isLoading && !error && (
        <CategorieList
          categories={categories}
          onEdit={(id) => navigate(`/categories/${id}/edit`)}
          onDelete={handleDeleteCategorie}
        />
      )}
    </>
  );
}

function CategorieCreateView() {
  const navigate = useNavigate();
  const handleCreateCategorieSubmit = useAdminStore((s) => s.handleCreateCategorieSubmit);

  return (
    <CategorieForm
      key="create-categorie"
      initialValues={null}
      submitLabel="Créer"
      onSubmit={async (payload) => {
        const success = await handleCreateCategorieSubmit(payload);
        if (success) {
          navigate('/categories');
        }
      }}
      onCancel={() => navigate('/categories')}
      showBackButton
    />
  );
}

function CategorieEditView() {
  const navigate = useNavigate();
  const { id } = useParams();
  const categorieId = Number(id);
  const loadCategories = useAdminStore((s) => s.loadCategories);
  const handleEditCategorieSubmit = useAdminStore((s) => s.handleEditCategorieSubmit);
  const [categorie, setCategorie] = useState<Categorie | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!Number.isFinite(categorieId)) {
      return;
    }

    let cancelled = false;

    async function loadEditData() {
      setLoading(true);
      await loadCategories();
      if (cancelled) {
        return;
      }
      const found = useAdminStore.getState().categories.find((c) => c.id === categorieId) ?? null;
      setCategorie(found);
      setLoading(false);
    }

    loadEditData();

    return () => {
      cancelled = true;
    };
  }, [categorieId, loadCategories]);

  if (!Number.isFinite(categorieId)) {
    return <Navigate to="/categories" replace />;
  }

  if (loading) {
    return <LoadingMessage />;
  }

  if (!categorie) {
    return <Navigate to="/categories" replace />;
  }

  return (
    <CategorieForm
      key={categorie.id}
      initialValues={categorie}
      submitLabel="Enregistrer"
      onSubmit={async (payload) => {
        const success = await handleEditCategorieSubmit(payload);
        if (success) {
          navigate('/categories');
        }
      }}
      onCancel={() => navigate('/categories')}
      showBackButton
    />
  );
}

function CategoriesPage() {
  return (
    <Routes>
      <Route index element={<CategoriesListView />} />
      <Route path="new" element={<CategorieCreateView />} />
      <Route path=":id/edit" element={<CategorieEditView />} />
    </Routes>
  );
}

export default CategoriesPage;
