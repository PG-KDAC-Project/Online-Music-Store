import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Sidebar.css';

export default function Sidebar() {
  const { logout, user, isAdmin, isArtist } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getNavItems = () => {
    if (isAdmin()) {
      return [
        { path: '/admin', icon: 'speedometer2', label: 'Dashboard' },
        { path: '/admin/artists', icon: 'person-check', label: 'Artist Approval' },
        { path: '/admin/users', icon: 'people', label: 'User Management' },
      ];
    }
    if (isArtist()) {
      return [
        { path: '/artist', icon: 'speedometer2', label: 'Dashboard' },
        { path: '/artist/upload', icon: 'upload', label: 'Upload Song' },
        { path: '/artist/songs', icon: 'music-note-list', label: 'My Songs' },
      ];
    }
    return [
      { path: '/', icon: 'house', label: 'Home' },
      { path: '/browse', icon: 'search', label: 'Browse' },
      { path: '/playlists', icon: 'music-note-beamed', label: 'Playlists' },
    ];
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <h2>
          <span className="sidebar-logo-icon">S</span>
          SCOTIFY
        </h2>
      </div>

      <nav className="sidebar-nav">
        {getNavItems().map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) => `sidebar-nav-item ${isActive ? 'active' : ''}`}
          >
            <i className={`bi bi-${item.icon} sidebar-nav-icon`}></i>
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="sidebar-divider"></div>

      <div className="sidebar-footer">
        <div className="sidebar-user-info">
          <p className="sidebar-user-name">{user?.name || 'User'}</p>
          <p className="sidebar-user-role">{user?.role?.replace('ROLE_', '') || 'listener'}</p>
        </div>
        <button className="btn-logout" onClick={handleLogout}>
          <i className="bi bi-box-arrow-right"></i>
          <span>Logout</span>
        </button>
      </div>
    </aside>
  );
}
