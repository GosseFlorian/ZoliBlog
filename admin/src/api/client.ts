/**
 * client.ts — URL de base et en-têtes HTTP communs.
 */

const TOKEN_KEY = 'java_blog_token';

const DEFAULT_API_URL = 'http://localhost:8080';

/** URL de l’API (build prod : variable VITE_API_URL). */
export const API_URL = (import.meta.env.VITE_API_URL ?? DEFAULT_API_URL).replace(/\/$/, '');

export function getAuthHeaders(): Record<string, string> {
  const token = localStorage.getItem(TOKEN_KEY);
  if (!token) {
    return {};
  }
  return { Authorization: `Bearer ${token}` };
}

export function adminJsonHeaders(): Record<string, string> {
  return {
    'Content-Type': 'application/json',
    ...getAuthHeaders(),
  };
}
