import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { logout, isAdmin, isArtist } = useAuth();

  return (
    <nav className="navbar">
      <div className="navbar-content container">
        <Link to="/" className="navbar-brand">🎵 Music Stream</Link>
        <ul className="navbar-menu">
          {isAdmin() && <li><Link to="/admin" className="navbar-link">Dashboard</Link></li>}
          {isArtist() && <li><Link to="/artist" className="navbar-link">Upload</Link></li>}
          {!isAdmin() && !isArtist() && <li><Link to="/" className="navbar-link">Browse</Link></li>}
          <li><button onClick={logout} className="btn btn-secondary">Logout</button></li>
        </ul>
      </div>
    </nav>
  );
}
