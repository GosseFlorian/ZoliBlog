import { API_URL } from './articles';

const TOKEN_KEY = 'site_token';
const PSEUDO_KEY = 'site_pseudo';
const USER_ID_KEY = 'site_user_id';

/**
 * Correspond au DTO LoginResponse renvoyé par /auth/login et /auth/register
 * (fr.ada.java_blog.dto.LoginResponse : token, pseudo, userId).
 */
export interface AuthResponse {
  token: string;
  pseudo: string;
  userId: number;
}

export interface LoginPayload {
  mail: string;
  mdp: string;
}

export interface RegisterPayload {
  pseudo: string;
  mail: string;
  mdp: string;
}

/** Relit une session déjà stockée (persistance après rechargement de page). */
export function getStoredAuth(): AuthResponse | null {
  const token = localStorage.getItem(TOKEN_KEY);
  const pseudo = localStorage.getItem(PSEUDO_KEY);
  const userId = localStorage.getItem(USER_ID_KEY);

  if (!token || !pseudo || !userId) return null;

  return { token, pseudo, userId: Number(userId) };
}

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function persistAuth(auth: AuthResponse): void {
  localStorage.setItem(TOKEN_KEY, auth.token);
  localStorage.setItem(PSEUDO_KEY, auth.pseudo);
  localStorage.setItem(USER_ID_KEY, String(auth.userId));
}

export function clearAuth(): void {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(PSEUDO_KEY);
  localStorage.removeItem(USER_ID_KEY);
}

/** POST /auth/login — public. */
export async function login(payload: LoginPayload): Promise<AuthResponse> {
  const res = await fetch(`${API_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });

  if (res.status === 401) {
    throw new Error('Email ou mot de passe incorrect.');
  }
  if (!res.ok) {
    throw new Error('Erreur lors de la connexion.');
  }

  return res.json();
}

async function readErrorMessage(res: Response, fallback: string): Promise<string> {
  try {
    const body = (await res.json()) as { message?: string };
    if (body.message?.trim()) {
      return body.message;
    }
  } catch {
    // corps absent ou non JSON
  }
  return fallback;
}

/** POST /auth/register — public, renvoie directement un token (connexion auto). */
export async function register(payload: RegisterPayload): Promise<AuthResponse> {
  const res = await fetch(`${API_URL}/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });

  if (res.status === 409) {
    throw new Error(await readErrorMessage(res, 'Conflit lors de la création du compte.'));
  }
  if (!res.ok) {
    throw new Error('Erreur lors de la création du compte.');
  }

  return res.json();
}
