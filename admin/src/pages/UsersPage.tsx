import { useEffect } from 'react';
import UserList from '../components/UserList.tsx';
import LoadingMessage from '../components/LoadingMessage.tsx';
import { useAdminStore } from '../store/adminStore.ts';

function UsersPage() {
  const users = useAdminStore((s) => s.users);
  const isLoading = useAdminStore((s) => s.isLoading);
  const error = useAdminStore((s) => s.error);
  const loadUsers = useAdminStore((s) => s.loadUsers);
  const handleDeleteUser = useAdminStore((s) => s.handleDeleteUser);

  useEffect(() => {
    loadUsers();
  }, [loadUsers]);

  return (
    <>
      <div className="page-header-row">
        <h1>Utilisateurs</h1>
      </div>

      {isLoading && <LoadingMessage />}
      {error && <p className="error-message">{error}</p>}
      {!isLoading && !error && <UserList users={users} onDelete={handleDeleteUser} />}
    </>
  );
}

export default UsersPage;
