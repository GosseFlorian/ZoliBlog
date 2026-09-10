import { API_URL, getAuthHeaders } from './client.ts';

export interface Commentaire {
  id: number;
  contenu: string;
  userId: number;
  pseudo: string | null;
  date: string;
}

export async function fetchComments(articleId: number): Promise<Commentaire[]> {
  const response = await fetch(`${API_URL}/admin/articles/${articleId}/commentaires`, {
    headers: getAuthHeaders(),
  });

  if (response.status === 401) {
    throw new Error('Session expirée — reconnecte-toi.');
  }
  if (!response.ok) {
    throw new Error('Erreur lors du chargement des commentaires.');
  }
  return response.json();
}

export async function deleteComment(id: number): Promise<void> {
  const response = await fetch(`${API_URL}/admin/commentaires/${id}`, {
    method: 'DELETE',
    headers: getAuthHeaders(),
  });

  if (response.status === 401) {
    throw new Error('Session expirée — reconnecte-toi.');
  }
  if (!response.ok) {
    throw new Error(`Erreur HTTP ${response.status} lors de la suppression`);
  }
}
