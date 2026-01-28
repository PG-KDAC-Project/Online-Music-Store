import { useState, useEffect } from 'react';
import { songService } from '../services/songService';
import { toast } from 'react-toastify';
import '../styles/modal.css';

export default function MySongs() {
  const [songs, setSongs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showEditModal, setShowEditModal] = useState(false);
  const [editingSong, setEditingSong] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    album: '',
    genre: '',
    language: '',
    duration: ''
  });
  const [audioFile, setAudioFile] = useState(null);
  const [coverImage, setCoverImage] = useState(null);

  useEffect(() => {
    loadMySongs();
  }, []);

  const loadMySongs = async () => {
    try {
      const response = await songService.getMySongs();
      setSongs(response || []);
    } catch (err) {
      console.error('Failed to load songs:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (song) => {
    setEditingSong(song);
    setFormData({
      title: song.title,
      album: song.album || '',
      genre: song.genre || '',
      language: song.language || '',
      duration: song.duration
    });
    setAudioFile(null);
    setCoverImage(null);
    setShowEditModal(true);
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    try {
      const data = new FormData();
      data.append('title', formData.title);
      data.append('album', formData.album);
      data.append('genre', formData.genre);
      data.append('language', formData.language);
      data.append('duration', formData.duration);
      if (audioFile) data.append('file', audioFile);
      if (coverImage) data.append('coverImage', coverImage);

      await songService.updateSong(editingSong.id, data);
      toast.success('Song updated successfully');
      setShowEditModal(false);
      loadMySongs();
    } catch (err) {
      toast.error('Failed to update song');
    }
  };

  const handleDelete = async (songId) => {
    try {
      await songService.deleteSong(songId);
      toast.success('Song deleted successfully');
      loadMySongs();
    } catch (err) {
      toast.error('Failed to delete song');
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
        <div className="card" style={{ overflowX: 'auto' }}>
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
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                      <button className="btn btn-primary" onClick={() => handleEdit(song)} style={{ padding: '6px 12px', fontSize: '13px', width: '80px' }}>
                        <i className="bi bi-pencil"></i> Edit
                      </button>
                      <button className="btn btn-secondary" onClick={() => handleDelete(song.id)} style={{ padding: '6px 12px', fontSize: '13px', width: '80px' }}>
                        <i className="bi bi-trash"></i> Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showEditModal && (
        <div className="modal-overlay" onClick={() => setShowEditModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h5>Edit Song</h5>
              <button className="modal-close-btn" onClick={() => setShowEditModal(false)}>
                <i className="bi bi-x"></i>
              </button>
            </div>
            <div className="modal-body">
              <form onSubmit={handleUpdate}>
                <div className="mb-3">
                  <label className="form-label">Title *</label>
                  <input type="text" className="form-control" value={formData.title} onChange={(e) => setFormData({...formData, title: e.target.value})} required />
                </div>
                <div className="mb-3">
                  <label className="form-label">Album</label>
                  <input type="text" className="form-control" value={formData.album} onChange={(e) => setFormData({...formData, album: e.target.value})} />
                </div>
                <div className="mb-3">
                  <label className="form-label">Genre</label>
                  <select className="form-control" value={formData.genre} onChange={(e) => setFormData({...formData, genre: e.target.value})}>
                    <option value="">Select Genre</option>
                    <option value="Pop">Pop</option>
                    <option value="Rock">Rock</option>
                    <option value="Hip-Hop">Hip-Hop</option>
                    <option value="Electronic">Electronic</option>
                    <option value="Jazz">Jazz</option>
                    <option value="Classical">Classical</option>
                  </select>
                </div>
                <div className="mb-3">
                  <label className="form-label">Language</label>
                  <input type="text" className="form-control" value={formData.language} onChange={(e) => setFormData({...formData, language: e.target.value})} />
                </div>
                <div className="mb-3">
                  <label className="form-label">Duration (seconds) *</label>
                  <input type="number" className="form-control" value={formData.duration} onChange={(e) => setFormData({...formData, duration: e.target.value})} required />
                </div>
                <div className="mb-3">
                  <label className="form-label">Replace Audio File (optional)</label>
                  <input type="file" className="form-control" accept=".mp3,audio/mpeg" onChange={(e) => setAudioFile(e.target.files[0])} />
                </div>
                <div className="mb-3">
                  <label className="form-label">Replace Cover Image (optional)</label>
                  <input type="file" className="form-control" accept="image/*" onChange={(e) => setCoverImage(e.target.files[0])} />
                </div>
                <div className="form-actions">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowEditModal(false)}>Cancel</button>
                  <button type="submit" className="btn btn-primary">Update Song</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
