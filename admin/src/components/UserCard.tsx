import type { User } from '../api/users.ts';

interface UserCardProps {
  user: User;
  onDelete: (id: number) => void;
}

function UserCard({ user, onDelete }: UserCardProps) {
  const { id, pseudo, mail } = user;

  return (
    <article className="user-card">
      <h3>{pseudo}</h3>
      <p>{mail}</p>
      <div className="article-actions">
        <button type="button" className="btn btn-danger btn-small" onClick={() => onDelete(id)}>
          Supprimer
        </button>
      </div>
    </article>
  );
}

export default UserCard;
