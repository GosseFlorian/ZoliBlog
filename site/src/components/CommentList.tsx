import { useState } from 'react';
import type { Commentaire } from '../api/commentaires';
import CommentEditForm from './CommentEditForm';
import { formatArticleDate } from '../utils/formatUtils';

interface CommentListProps {
  comments: Commentaire[];
  currentUserId: number | null;
  onUpdate: (id: number, contenu: string) => Promise<boolean>;
  onDelete: (id: number) => Promise<boolean>;
  isUpdating: boolean;
  isDeleting: boolean;
  actionError: string | null;
}

function CommentList({
  comments,
  currentUserId,
  onUpdate,
  onDelete,
  isUpdating,
  isDeleting,
  actionError,
}: CommentListProps) {
  const [editingId, setEditingId] = useState<number | null>(null);

  if (comments.length === 0) {
    return (
      <p className="empty-list">Aucun commentaire pour l'instant. Soyez le premier à réagir !</p>
    );
  }

  return (
    <ul className="comment-list">
      {comments.map((comment) => {
        const isOwner = currentUserId != null && comment.userId === currentUserId;
        const isEditing = editingId === comment.id;

        return (
          <li key={comment.id} className="comment-item">
            <p className="comment-meta">
              <span className="comment-author">{comment.pseudo}</span>
              <span className="comment-date">{formatArticleDate(comment.date)}</span>
            </p>

            {isEditing ? (
              <CommentEditForm
                initialContenu={comment.contenu}
                onSubmit={async (contenu) => {
                  const ok = await onUpdate(comment.id, contenu);
                  if (ok) {
                    setEditingId(null);
                  }
                  return ok;
                }}
                onCancel={() => setEditingId(null)}
                isSubmitting={isUpdating}
                error={actionError}
              />
            ) : (
              <>
                <p className="comment-contenu">{comment.contenu}</p>
                {isOwner && (
                  <div className="comment-actions">
                    <button type="button" onClick={() => setEditingId(comment.id)}>
                      Modifier
                    </button>
                    <button
                      type="button"
                      onClick={async () => {
                        if (
                          !window.confirm(
                            'Supprimer ce commentaire ?\n\nCette action est définitive.'
                          )
                        ) {
                          return;
                        }
                        await onDelete(comment.id);
                      }}
                      disabled={isDeleting}
                    >
                      Supprimer
                    </button>
                  </div>
                )}
              </>
            )}
          </li>
        );
      })}
    </ul>
  );
}

export default CommentList;
