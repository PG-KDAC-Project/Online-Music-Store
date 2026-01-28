import { useState, useEffect } from 'react';
import { favoriteService } from '../services/favoriteService';
import { usePlayer } from '../context/PlayerContext';
import SongCard from '../components/SongCard';

export default function Favorites() {
  const [songs, setSongs] = useState([]);
  const [loading, setLoading] = useState(true);
  const { playSong, currentSong, isPlaying, togglePlay } = usePlayer();

  useEffect(() => {
    loadFavorites();
  }, []);

  const loadFavorites = async () => {
    try {
      const data = await favoriteService.getFavoriteSongs();
      setSongs(data || []);
    } catch (error) {
      console.error('Failed to load favorites:', error);
    } finally {
      setLoading(false);
    }
  };

  const handlePlaySong = (song) => {
    if (currentSong?.id === song.id) {
      togglePlay();
    } else {
      playSong(song, songs);
    }
  };

  const handleUnlike = (songId) => {
    setSongs(songs.filter(song => song.id !== songId));
  };

  if (loading) return <div style={{ padding: '2rem' }}>Loading...</div>;

  return (
    <div style={{ padding: '2rem' }}>
      <h1 style={{ fontSize: '32px', fontWeight: '700', marginBottom: '1rem' }}>Liked Songs</h1>
      <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>{songs.length} songs</p>

      {songs.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <i className="bi bi-heart" style={{ fontSize: '3rem', color: 'var(--text-secondary)' }}></i>
          <p style={{ marginTop: '1rem', color: 'var(--text-secondary)' }}>No liked songs yet</p>
        </div>
      ) : (
        <div className="songs-grid">
          {songs.map((song) => (
            <SongCard
              key={song.id}
              song={song}
              onPlay={handlePlaySong}
              isPlaying={currentSong?.id === song.id && isPlaying}
              onUnlike={handleUnlike}
            />
          ))}
        </div>
      )}
    </div>
  );
}
