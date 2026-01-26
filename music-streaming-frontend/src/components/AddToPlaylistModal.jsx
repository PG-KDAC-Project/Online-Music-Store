import { useState, useEffect } from 'react';
import { playlistService } from '../services/playlistService';
import '../styles/modal.css';

export default function AddToPlaylistModal({ show, onClose, songId }) {
  const [playlists, setPlaylists] = useState([]);
  const [showCreateNew, setShowCreateNew] = useState(false);
  const [newPlaylistName, setNewPlaylistName] = useState('');
  const [notification, setNotification] = useState('');

  useEffect(() => {
    if (show) {
      loadPlaylists();
    }
  }, [show]);

  const loadPlaylists = async () => {
    try {
      const data = await playlistService.getMyPlaylists();
      setPlaylists(data || []);
    } catch (error) {
      console.error('Failed to load playlists:', error);
    }
  };

  const handleAddToPlaylist = async (playlistId) => {
    try {
      await playlistService.addSongToPlaylist(playlistId, songId);
      setNotification('Added to playlist!');
      setTimeout(() => {
        setNotification('');
        onClose();
      }, 1000);
    } catch (error) {
      setNotification('Failed to add song');
      setTimeout(() => setNotification(''), 2000);
    }
  };

  const handleCreatePlaylist = async () => {
    if (!newPlaylistName.trim()) return;
    try {
      await playlistService.createPlaylist(newPlaylistName, '', false);
      setNewPlaylistName('');
      setShowCreateNew(false);
      setNotification('Playlist created!');
      loadPlaylists();
      setTimeout(() => setNotification(''), 2000);
    } catch (error) {
      setNotification('Failed to create playlist');
      setTimeout(() => setNotification(''), 2000);
    }
  };

  if (!show) return null;

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h5>Add to Playlist</h5>
          <button className="modal-close-btn" onClick={onClose}>
            <i className="bi bi-x"></i>
          </button>
        </div>

        {notification && (
          <div className={`notification ${notification.includes('Failed') ? 'warning' : 'success'}`}>
            {notification}
          </div>
        )}

        <div className="modal-body">
          {!showCreateNew ? (
            <>
              <button
                className="create-playlist-btn"
                onClick={() => setShowCreateNew(true)}
              >
                <i className="bi bi-plus-circle"></i>
                <span>Create New Playlist</span>
              </button>

              <div className="playlists-list">
                {playlists && playlists.length > 0 ? (
                  playlists.map((playlist) => (
                    <div
                      key={playlist.id}
                      className="playlist-item"
                      onClick={() => handleAddToPlaylist(playlist.id)}
                    >
                      <div className="playlist-item-image"></div>
                      <div className="playlist-item-info">
                        <p className="playlist-item-name">{playlist.name}</p>
                        <p className="playlist-item-count">
                          {playlist.songCount || 0} songs
                        </p>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="no-playlists">
                    <i className="bi bi-music-note-list"></i>
                    <p>You don't have any playlists yet</p>
                  </div>
                )}
              </div>
            </>
          ) : (
            <div className="create-playlist-form">
              <div className="mb-3">
                <label className="form-label">Playlist Name</label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="My Awesome Playlist"
                  value={newPlaylistName}
                  onChange={(e) => setNewPlaylistName(e.target.value)}
                />
              </div>

              <div className="form-actions">
                <button
                  className="btn btn-secondary"
                  onClick={() => setShowCreateNew(false)}
                >
                  Cancel
                </button>
                <button
                  className="btn btn-primary"
                  onClick={handleCreatePlaylist}
                  disabled={!newPlaylistName.trim()}
                >
                  Create
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
