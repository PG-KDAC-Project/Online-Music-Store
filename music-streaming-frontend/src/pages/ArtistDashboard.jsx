import { useNavigate } from 'react-router-dom';

export default function ArtistDashboard() {
  const navigate = useNavigate();

  return (
    <div style={{ padding: '2rem' }}>
      <h1 style={{ fontSize: '48px', fontWeight: '700', marginBottom: '32px' }}>
        Artist Dashboard
      </h1>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px' }}>
        <div className="card" style={{ cursor: 'pointer' }} onClick={() => navigate('/artist/upload')}>
          <i className="bi bi-upload" style={{ fontSize: '48px', color: 'var(--accent-primary)', marginBottom: '16px' }}></i>
          <h3 style={{ marginBottom: '8px' }}>Upload Song</h3>
          <p style={{ color: 'var(--text-secondary)' }}>Share your music with the world</p>
        </div>

        <div className="card" style={{ cursor: 'pointer' }} onClick={() => navigate('/artist/songs')}>
          <i className="bi bi-music-note-list" style={{ fontSize: '48px', color: 'var(--accent-primary)', marginBottom: '16px' }}></i>
          <h3 style={{ marginBottom: '8px' }}>My Songs</h3>
          <p style={{ color: 'var(--text-secondary)' }}>View and manage your uploads</p>
        </div>
      </div>
    </div>
  );
}
