import CategorieList from '../components/CategorieList.tsx';
import CategorieForm from '../components/CategorieForm.tsx';
import LoadingMessage from '../components/LoadingMessage.tsx';
import { useAdminStore } from '../store/adminStore.ts';

function CategoriesPage() {
  const mode = useAdminStore((s) => s.mode);
  const categories = useAdminStore((s) => s.categories);
  const isLoading = useAdminStore((s) => s.isLoading);
  const error = useAdminStore((s) => s.error);
  const editingCategorie = useAdminStore((s) => s.editingCategorie);
  const showList = useAdminStore((s) => s.showList);
  const handleEditCategorie = useAdminStore((s) => s.handleEditCategorie);
  const handleDeleteCategorie = useAdminStore((s) => s.handleDeleteCategorie);
  const handleCreateCategorieSubmit = useAdminStore((s) => s.handleCreateCategorieSubmit);
  const handleEditCategorieSubmit = useAdminStore((s) => s.handleEditCategorieSubmit);

  return (
    <>
      {mode === 'create' && (
        <CategorieForm
          key="create-categorie"
          initialValues={null}
          submitLabel="Créer"
          onSubmit={handleCreateCategorieSubmit}
          onCancel={showList}
          showBackButton
        />
      )}

      {mode === 'edit' && editingCategorie && (
        <CategorieForm
          key={editingCategorie.id}
          initialValues={editingCategorie}
          submitLabel="Enregistrer"
          onSubmit={handleEditCategorieSubmit}
          onCancel={showList}
          showBackButton
        />
      )}

      {mode === 'list' && isLoading && <LoadingMessage />}

      {mode === 'list' && error && <p className="error-message">{error}</p>}

      {mode === 'list' && !isLoading && !error && (
        <CategorieList
          categories={categories}
          onEdit={handleEditCategorie}
          onDelete={handleDeleteCategorie}
        />
      )}
    </>
  );
}

export default CategoriesPage;
