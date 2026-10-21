import UserCard from './UserCard.tsx';
import type { User } from '../api/users.ts';

interface UserListProps {
  users: User[];
  onDelete: (id: number) => void;
}

function UserList({ users, onDelete }: UserListProps) {
  if (users.length === 0) {
    return <p className="empty-list">Aucun utilisateur à afficher.</p>;
  }

  return (
    <section className="article-list" aria-label="Liste des utilisateurs">
      {users.map((user) => (
        <UserCard key={user.id} user={user} onDelete={onDelete} />
      ))}
    </section>
  );
}

export default UserList;
