import { describe, expect, it } from 'vitest';
import { excerpt, formatArticleDate, formatCategoryList } from '../../utils/formatUtils';

describe('excerpt', () => {
  it('returns full text when shorter than limit', () => {
    expect(excerpt('hello')).toBe('hello');
  });

  it('truncates long text with ellipsis', () => {
    const long = 'a'.repeat(200);
    expect(excerpt(long)).toBe(`${'a'.repeat(180)}…`);
  });
});

describe('formatArticleDate', () => {
  it('formats date in fr-FR locale', () => {
    const result = formatArticleDate('2024-01-15T10:30:00');
    expect(result).toMatch(/15/);
    expect(result).toMatch(/2024/);
  });
});

describe('formatCategoryList', () => {
  it('returns null for empty or undefined', () => {
    expect(formatCategoryList([])).toBeNull();
    expect(formatCategoryList(undefined)).toBeNull();
  });

  it('joins category names', () => {
    expect(formatCategoryList([{ nom: 'Java' }, { nom: 'Spring' }])).toBe('Java, Spring');
  });
});
