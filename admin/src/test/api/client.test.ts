import { describe, it, expect, beforeEach } from 'vitest';
import { adminJsonHeaders, getAuthHeaders } from '../../api/client';

describe('client.ts', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('getAuthHeaders retourne un objet vide sans token', () => {
    expect(getAuthHeaders()).toEqual({});
  });

  it('getAuthHeaders retourne Authorization avec token', () => {
    localStorage.setItem('java_blog_token', 'my-token');
    expect(getAuthHeaders()).toEqual({ Authorization: 'Bearer my-token' });
  });

  it('adminJsonHeaders inclut Content-Type et Authorization', () => {
    localStorage.setItem('java_blog_token', 'my-token');
    expect(adminJsonHeaders()).toEqual({
      'Content-Type': 'application/json',
      Authorization: 'Bearer my-token',
    });
  });
});
