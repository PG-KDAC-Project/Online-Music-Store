import { useNavigate } from 'react-router-dom';

export default function Home() {
  const navigate = useNavigate();

  return (
    <div style={{ padding: '24px' }}>
      <h1 style={{ fontSize: '48px', fontWeight: '700', marginBottom: '16px' }}>Welcome to Scotify</h1>
      <p className="text-secondary" style={{ fontSize: '18px', marginBottom: '32px' }}>Your music, your way</p>
      
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px' }}>
        <div className="card" style={{ cursor: 'pointer', padding: '2rem' }} onClick={() => navigate('/browse')}>
          <i className="bi bi-search" style={{ fontSize: '48px', color: 'var(--accent-primary)', marginBottom: '16px' }}></i>
          <h3 style={{ marginBottom: '8px' }}>Browse Songs</h3>
          <p style={{ color: 'var(--text-secondary)' }}>Explore millions of tracks</p>
        </div>

        <div className="card" style={{ cursor: 'pointer', padding: '2rem' }} onClick={() => navigate('/playlists')}>
          <i className="bi bi-music-note-list" style={{ fontSize: '48px', color: 'var(--accent-primary)', marginBottom: '16px' }}></i>
          <h3 style={{ marginBottom: '8px' }}>My Playlists</h3>
          <p style={{ color: 'var(--text-secondary)' }}>Create and manage playlists</p>
        </div>
      </div>
    </div>
  );
}
