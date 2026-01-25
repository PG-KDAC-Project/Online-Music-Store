import { useState } from 'react';
import AddToPlaylistModal from './AddToPlaylistModal';
import './SongCard.css';

export default function SongCard({ song, onPlay, isPlaying }) {
  const [showPlaylistModal, setShowPlaylistModal] = useState(false);

  const formatDuration = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
  };

  return (
    <>
      <div className="song-card">
        <div className="song-card-image-container">
          <div className="song-card-image"></div>
          <div className="song-card-overlay">
            <button className="song-card-play-btn" onClick={() => onPlay(song)}>
              <i className={`bi bi-${isPlaying ? 'pause' : 'play'}-fill`}></i>
            </button>
          </div>
        </div>
        <div className="song-card-body">
          <h6 className="song-card-title" title={song.title}>{song.title}</h6>
          <p className="song-card-artist" title={song.artistName}>{song.artistName}</p>
          <div className="song-card-meta">
            <span className="song-card-duration">{formatDuration(song.duration || 180)}</span>
            <div className="song-card-actions">
              <button className="song-card-action-btn" onClick={() => setShowPlaylistModal(true)}>
                <i className="bi bi-plus-circle"></i>
              </button>
            </div>
          </div>
        </div>
      </div>

      <AddToPlaylistModal
        show={showPlaylistModal}
        onClose={() => setShowPlaylistModal(false)}
        songId={song.id}
      />
    </>
  );
}
