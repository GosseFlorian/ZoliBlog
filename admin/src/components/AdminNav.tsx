import { NavLink } from 'react-router-dom';

const links = [
  { to: '/articles', label: 'Articles' },
  { to: '/categories', label: 'Catégories' },
  { to: '/users', label: 'Utilisateurs' },
] as const;

function AdminNav() {
  return (
    <nav className="admin-nav" aria-label="Sections du back-office">
      {links.map(({ to, label }) => (
        <NavLink
          key={to}
          to={to}
          end={to === '/users'}
          className={({ isActive }) =>
            isActive ? 'admin-nav-link active' : 'admin-nav-link'
          }
        >
          {label}
        </NavLink>
      ))}
    </nav>
  );
}

export default AdminNav;
