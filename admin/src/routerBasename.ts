/** basename React Router aligné sur `base` Vite (prod Railway : /admin/). */
export function routerBasename(baseUrl: string = import.meta.env.BASE_URL): string | undefined {
  if (baseUrl === '/') {
    return undefined;
  }
  return baseUrl.replace(/\/$/, '');
}
