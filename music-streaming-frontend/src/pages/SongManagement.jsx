import { useState, useEffect } from 'react';
import { songService } from '../services/songService';
import { toast } from 'react-toastify';
import '../styles/admin.css';

export default function SongManagement() {
  const [songs, setSongs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadSongs();
  }, []);

  const loadSongs = async () => {
    try {
      const response = await songService.getAllSongs(0, 100);
      setSongs(response.content || response);
    } catch (err) {
      console.error('Failed to load songs:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (songId) => {
    try {
      await songService.deleteSong(songId);
      toast.success('Song deleted successfully');
      loadSongs();
    } catch (err) {
      toast.error('Failed to delete song');
    }
  };

  if (loading) return <div style={{ padding: '2rem' }}>Loading...</div>;

  return (
    <div style={{ padding: '2rem' }}>
      <h1 style={{ fontSize: '32px', fontWeight: '700', marginBottom: '1rem' }}>Song Management</h1>
      <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>Manage all songs on the platform</p>

      <div className="card" style={{ overflowX: 'auto' }}>
        <table className="table">
          <thead>
            <tr>
              <th>Title</th>
              <th>Artist</th>
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
                <td>{song.artistName}</td>
                <td>{song.album || '-'}</td>
                <td>{song.genre || '-'}</td>
                <td>{song.formattedDuration}</td>
                <td>{song.playCount}</td>
                <td>{song.likeCount}</td>
                <td>{new Date(song.createdAt).toLocaleDateString()}</td>
                <td>
                  <button className="btn btn-secondary" onClick={() => handleDelete(song.id)} style={{ padding: '6px 12px', fontSize: '13px' }}>
                    <i className="bi bi-trash"></i> Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
