import { useState, useEffect } from 'react';
import AddToPlaylistModal from './AddToPlaylistModal';
import { favoriteService } from '../services/favoriteService';
import { useAuth } from '../context/AuthContext';
import './SongCard.css';

export default function SongCard({ song, onPlay, isPlaying, onUnlike }) {
  const [showPlaylistModal, setShowPlaylistModal] = useState(false);
  const [isLiked, setIsLiked] = useState(false);
  const { isPremium, isListener } = useAuth();

  useEffect(() => {
    checkIfLiked();
  }, [song.id]);

  const checkIfLiked = async () => {
    try {
      const liked = await favoriteService.checkIfLiked(song.id);
      setIsLiked(liked);
    } catch (err) {
      console.error('Failed to check like status:', err);
    }
  };

  const handleLike = async (e) => {
    e.stopPropagation();
    try {
      if (isLiked) {
        await favoriteService.unlikeSong(song.id);
        setIsLiked(false);
        if (onUnlike) onUnlike(song.id);
      } else {
        await favoriteService.likeSong(song.id);
        setIsLiked(true);
      }
    } catch (err) {
      console.error('Failed to toggle like:', err);
    }
  };

  const handleDownload = async (e) => {
    e.stopPropagation();
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`http://localhost:8080/api/songs/download/${song.id}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      
      if (!response.ok) throw new Error('Download failed');
      
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${song.title}.mp3`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (error) {
      console.error('Download failed:', error);
    }
  };

  const formatDuration = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
  };

  return (
    <>
      <div className="song-card">
        <div className="song-card-image-container">
          {song.coverImagePath ? (
            <img 
              src={`http://localhost:8080/uploads/pictures/${song.coverImagePath}`} 
              alt={song.title}
              className="song-card-image"
            />
          ) : (
            <div className="song-card-image"></div>
          )}
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
              <button className="song-card-action-btn" onClick={handleLike} title={isLiked ? 'Unlike' : 'Like'}>
                <i className={`bi bi-heart${isLiked ? '-fill' : ''}`} style={{ color: isLiked ? '#1db954' : 'inherit' }}></i>
              </button>
              <button className="song-card-action-btn" onClick={() => setShowPlaylistModal(true)}>
                <i className="bi bi-plus-circle"></i>
              </button>
              {isListener() && isPremium() && (
                <button className="song-card-action-btn" onClick={handleDownload} title="Download">
                  <i className="bi bi-download"></i>
                </button>
              )}
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
