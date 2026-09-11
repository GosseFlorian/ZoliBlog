import { beforeEach, describe, expect, it } from 'vitest';
import { clearUserId, getUserId, setUserId } from '../../api/userId';

describe('userId.ts', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('getUserId retourne null sans valeur', () => {
    expect(getUserId()).toBeNull();
  });

  it('setUserId puis getUserId retourne le nombre', () => {
    setUserId(42);
    expect(getUserId()).toBe(42);
  });

  it('getUserId retourne null si valeur invalide', () => {
    localStorage.setItem('java_blog_user_id', 'abc');
    expect(getUserId()).toBeNull();
  });

  it('clearUserId efface la valeur', () => {
    setUserId(1);
    clearUserId();
    expect(getUserId()).toBeNull();
  });
});
