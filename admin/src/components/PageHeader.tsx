import { Link } from 'react-router-dom';

interface PageHeaderProps {
  pseudo?: string | null;
  onLogout?: () => void;
}

function PageHeader({ pseudo, onLogout }: PageHeaderProps) {
  return (
    <header className="admin-top-bar">
      <h1 className="admin-brand">
        <Link to="/" className="admin-brand-link">
          <span className="admin-brand-title">Back-office</span>
          <span className="admin-brand-subtitle">ZoliBlog</span>
        </Link>
      </h1>
      {pseudo != null && onLogout && (
        <p className="header-auth">
          <span>
            Connecté : <strong>{pseudo}</strong>
          </span>
          <button type="button" className="btn-logout" onClick={onLogout}>
            Déconnexion
          </button>
        </p>
      )}
    </header>
  );
}

export default PageHeader;
