import { describe, expect, it } from 'vitest';
import { excerpt, formatArticleDate, formatCategoryList } from '../../utils/formatUtils';

describe('formatUtils', () => {
  it('formatArticleDate formate en fr-FR', () => {
    const result = formatArticleDate('2024-01-15T10:30:00');
    expect(result).toMatch(/15/);
    expect(result).toMatch(/2024/);
  });

  it('excerpt retourne le texte complet si court', () => {
    expect(excerpt('hello')).toBe('hello');
  });

  it('excerpt tronque avec ellipsis', () => {
    const long = 'a'.repeat(200);
    expect(excerpt(long)).toBe(`${'a'.repeat(180)}…`);
  });

  it('formatCategoryList retourne null si vide', () => {
    expect(formatCategoryList([])).toBeNull();
    expect(formatCategoryList(undefined)).toBeNull();
  });

  it('formatCategoryList joint les noms', () => {
    expect(formatCategoryList([{ nom: 'Java' }, { nom: 'Spring' }])).toBe('Java, Spring');
  });
});
