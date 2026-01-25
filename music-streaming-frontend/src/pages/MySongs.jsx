import { useState, useEffect } from 'react';
import { songService } from '../services/songService';

export default function MySongs() {
  const [songs, setSongs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadMySongs();
  }, []);

  const loadMySongs = async () => {
    try {
      const response = await songService.getMySongs();
      setSongs(response.content || response);
    } catch (err) {
      console.error('Failed to load songs:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (songId) => {
    if (window.confirm('Are you sure you want to delete this song?')) {
      try {
        await songService.deleteSong(songId);
        alert('Song deleted successfully');
        loadMySongs();
      } catch (err) {
        alert('Failed to delete song');
      }
    }
  };

  if (loading) return <div style={{ padding: '2rem' }}>Loading...</div>;

  return (
    <div style={{ padding: '2rem' }}>
      <h1 style={{ fontSize: '32px', fontWeight: '700', marginBottom: '1rem' }}>My Songs</h1>
      <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>Manage your uploaded songs</p>

      {songs.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <i className="bi bi-music-note" style={{ fontSize: '3rem', color: 'var(--text-secondary)' }}></i>
          <p style={{ marginTop: '1rem', color: 'var(--text-secondary)' }}>No songs uploaded yet</p>
        </div>
      ) : (
        <div className="card">
          <table className="table">
            <thead>
              <tr>
                <th>Title</th>
                <th>Album</th>
                <th>Genre</th>
                <th>Duration</th>
                <th>Plays</th>
                <th>Likes</th>
                <th>Uploaded</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {songs.map((song) => (
                <tr key={song.id}>
                  <td>{song.title}</td>
                  <td>{song.album || '-'}</td>
                  <td>{song.genre || '-'}</td>
                  <td>{song.formattedDuration}</td>
                  <td>{song.playCount}</td>
                  <td>{song.likeCount}</td>
                  <td>{new Date(song.createdAt).toLocaleDateString()}</td>
                  <td>
                    <button className="btn btn-secondary" onClick={() => handleDelete(song.id)}>
                      <i className="bi bi-trash"></i> Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
