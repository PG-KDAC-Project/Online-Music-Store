import { useState, useEffect } from 'react';
import { playlistService } from '../services/playlistService';
import { usePlayer } from '../context/PlayerContext';
import SongCard from '../components/SongCard';
import './Playlists.css';

export default function Playlists() {
  const [playlists, setPlaylists] = useState([]);
  const [selectedPlaylist, setSelectedPlaylist] = useState(null);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [newPlaylistName, setNewPlaylistName] = useState('');
  const { playSong, currentSong, isPlaying, togglePlay } = usePlayer();

  useEffect(() => {
    loadPlaylists();
  }, []);

  const loadPlaylists = async () => {
    try {
      const data = await playlistService.getMyPlaylists();
      setPlaylists(data || []);
    } catch (error) {
      console.error('Failed to load playlists:', error);
    }
  };

  const handleCreatePlaylist = async () => {
    if (!newPlaylistName.trim()) return;
    try {
      await playlistService.createPlaylist(newPlaylistName, '', false);
      setNewPlaylistName('');
      setShowCreateModal(false);
      loadPlaylists();
    } catch (error) {
      console.error('Failed to create playlist:', error);
    }
  };

  const handleDeletePlaylist = async (playlistId, e) => {
    e.stopPropagation();
    if (window.confirm('Are you sure you want to delete this playlist?')) {
      try {
        await playlistService.deletePlaylist(playlistId);
        loadPlaylists();
      } catch (error) {
        console.error('Failed to delete playlist:', error);
      }
    }
  };

  const viewPlaylist = async (playlist) => {
    try {
      const data = await playlistService.getPlaylistWithSongs(playlist.id);
      setSelectedPlaylist(data);
    } catch (error) {
      console.error('Failed to load playlist:', error);
    }
  };

  const handlePlaySong = (song) => {
    if (currentSong?.id === song.id) {
      togglePlay();
    } else {
      playSong(song, selectedPlaylist?.songs || []);
    }
  };

  if (selectedPlaylist) {
    return (
      <div className="playlists-page">
        <button className="btn btn-secondary" onClick={() => setSelectedPlaylist(null)}>
          <i className="bi bi-arrow-left"></i> Back to Playlists
        </button>
        <div className="playlist-header">
          <h1>{selectedPlaylist.name}</h1>
          <p className="text-secondary">{selectedPlaylist.songs?.length || 0} songs</p>
        </div>
        <div className="songs-grid">
          {selectedPlaylist.songs?.map((song) => (
            <SongCard
              key={song.id}
              song={song}
              onPlay={handlePlaySong}
              isPlaying={currentSong?.id === song.id && isPlaying}
            />
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="playlists-page">
      <div className="playlists-header">
        <h1>My Playlists</h1>
        <button className="btn btn-primary" onClick={() => setShowCreateModal(true)}>
          <i className="bi bi-plus-circle"></i> Create Playlist
        </button>
      </div>

      <div className="playlists-grid">
        {playlists.map((playlist) => (
          <div key={playlist.id} className="playlist-card" onClick={() => viewPlaylist(playlist)}>
            <div className="playlist-card-image"></div>
            <div className="playlist-card-body">
              <h6 className="playlist-card-title">{playlist.name}</h6>
              <p className="playlist-card-count">{playlist.songCount || 0} songs</p>
            </div>
            <button 
              className="playlist-delete-btn" 
              onClick={(e) => handleDeletePlaylist(playlist.id, e)}
              title="Delete playlist"
            >
              <i className="bi bi-trash"></i>
            </button>
          </div>
        ))}
      </div>

      {showCreateModal && (
        <div className="modal-overlay" onClick={() => setShowCreateModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h5>Create New Playlist</h5>
              <button className="modal-close-btn" onClick={() => setShowCreateModal(false)}>
                <i className="bi bi-x"></i>
              </button>
            </div>
            <div className="modal-body">
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
                <button className="btn btn-secondary" onClick={() => setShowCreateModal(false)}>
                  Cancel
                </button>
                <button className="btn btn-primary" onClick={handleCreatePlaylist} disabled={!newPlaylistName.trim()}>
                  Create
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
