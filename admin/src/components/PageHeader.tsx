interface PageHeaderProps {
  pseudo?: string | null;
  onLogout?: () => void;
}

function PageHeader({ pseudo, onLogout }: PageHeaderProps) {
  return (
    <header className="admin-top-bar">
      <div className="admin-brand">
        <h1 className="admin-brand-title">Back-office</h1>
        <span className="admin-brand-subtitle">ZoliBlog</span>
      </div>
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
