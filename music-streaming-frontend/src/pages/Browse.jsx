import { useState, useEffect } from 'react';
import { songService } from '../services/songService';
import { usePlayer } from '../context/PlayerContext';
import SongCard from '../components/SongCard';
import './Browse.css';

export default function Browse() {
  const [songs, setSongs] = useState([]);
  const [filteredSongs, setFilteredSongs] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedGenre, setSelectedGenre] = useState('all');
  const [loading, setLoading] = useState(true);
  const { playSong, currentSong, isPlaying, togglePlay } = usePlayer();

  const genres = ['all', 'Pop', 'Rock', 'Jazz', 'Hip Hop', 'Electronic'];

  useEffect(() => {
    loadSongs();
  }, []);

  useEffect(() => {
    filterSongs();
  }, [searchQuery, selectedGenre, songs]);

  const loadSongs = async () => {
    try {
      const data = await songService.getAllSongs();
      setSongs(data || []);
    } catch (error) {
      console.error('Failed to load songs:', error);
    } finally {
      setLoading(false);
    }
  };

  const filterSongs = () => {
    let filtered = songs;

    if (searchQuery) {
      filtered = filtered.filter(song =>
        song.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        song.artistName.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    if (selectedGenre !== 'all') {
      filtered = filtered.filter(song => song.genre === selectedGenre);
    }

    setFilteredSongs(filtered);
  };

  const handlePlaySong = (song) => {
    if (currentSong?.id === song.id) {
      togglePlay();
    } else {
      playSong(song, filteredSongs);
    }
  };

  if (loading) return <div style={{ padding: '2rem' }}>Loading...</div>;

  return (
    <div className="browse-songs">
      <div className="browse-header">
        <h1>Browse Songs</h1>
        <p className="text-secondary">Explore millions of tracks</p>
      </div>

      <div className="search-section">
        <div className="search-input-container">
          <i className="bi bi-search search-icon"></i>
          <input
            type="text"
            className="search-input"
            placeholder="Search for songs, artists, albums..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          {searchQuery && (
            <button className="search-clear-btn" onClick={() => setSearchQuery('')}>
              <i className="bi bi-x"></i>
            </button>
          )}
        </div>
      </div>

      <div className="genre-filter">
        <h5>Genres</h5>
        <div className="genre-pills">
          {genres.map((genre) => (
            <button
              key={genre}
              className={`genre-pill ${selectedGenre === genre ? 'active' : ''}`}
              onClick={() => setSelectedGenre(genre)}
            >
              {genre === 'all' ? 'All' : genre}
            </button>
          ))}
        </div>
      </div>

      <div className="browse-results">
        <div className="results-header">
          <h5>
            {filteredSongs.length} {filteredSongs.length === 1 ? 'Song' : 'Songs'}
            {searchQuery && ` for "${searchQuery}"`}
          </h5>
        </div>

        {filteredSongs.length > 0 ? (
          <div className="songs-grid">
            {filteredSongs.map((song) => (
              <SongCard
                key={song.id}
                song={song}
                onPlay={handlePlaySong}
                isPlaying={currentSong?.id === song.id && isPlaying}
              />
            ))}
          </div>
        ) : (
          <div className="no-results">
            <i className="bi bi-music-note-list no-results-icon"></i>
            <p>No songs found</p>
            <p className="text-secondary">Try adjusting your search or filters</p>
          </div>
        )}
      </div>
    </div>
  );
}
