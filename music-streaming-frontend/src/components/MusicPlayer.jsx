import { useEffect, useState } from 'react';
import { usePlayer } from '../context/PlayerContext';
import { useAuth } from '../context/AuthContext';
import { favoriteService } from '../services/favoriteService';
import './MusicPlayer.css';

export default function MusicPlayer() {
  const {
    currentSong,
    isPlaying,
    currentTime,
    duration,
    volume,
    audioRef,
    shouldAutoPlay,
    togglePlay,
    playNext,
    playPrevious,
    setCurrentTime,
    setDuration,
    setVolume,
    setIsPlaying,
    setShouldAutoPlay
  } = usePlayer();

  const { isPremium, isListener } = useAuth();
  const [isLiked, setIsLiked] = useState(false);
  const [isLooping, setIsLooping] = useState(false);

  useEffect(() => {
    if (currentSong) {
      checkIfLiked();
    }
  }, [currentSong?.id]);

  const checkIfLiked = async () => {
    if (!currentSong) return;
    try {
      const liked = await favoriteService.checkIfLiked(currentSong.id);
      setIsLiked(liked);
    } catch (err) {
      console.error('Failed to check like status:', err);
    }
  };

  const handleLike = async () => {
    if (!currentSong) return;
    try {
      if (isLiked) {
        await favoriteService.unlikeSong(currentSong.id);
        setIsLiked(false);
      } else {
        await favoriteService.likeSong(currentSong.id);
        setIsLiked(true);
      }
    } catch (err) {
      console.error('Failed to toggle like:', err);
    }
  };

  const handleDownload = async () => {
    if (!currentSong) return;
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`http://localhost:8080/api/songs/download/${currentSong.id}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (!response.ok) throw new Error('Download failed');
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${currentSong.title}.mp3`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (error) {
      console.error('Download failed:', error);
    }
  };

  const toggleLoop = () => {
    setIsLooping(!isLooping);
    if (audioRef.current) {
      audioRef.current.loop = !isLooping;
    }
  };

  useEffect(() => {
    if (audioRef.current) {
      if (isPlaying) {
        audioRef.current.play().catch(err => {
          console.error('Play error:', err);
          setIsPlaying(false);
        });
      } else {
        audioRef.current.pause();
      }
    }
  }, [isPlaying]);

  useEffect(() => {
    if (currentSong && audioRef.current) {
      audioRef.current.src = `http://localhost:8080/api/songs/stream/${currentSong.id}`;
      audioRef.current.volume = volume;
      audioRef.current.load();
      setCurrentTime(0);
      if (shouldAutoPlay) {
        const playPromise = audioRef.current.play();
        if (playPromise !== undefined) {
          playPromise.then(() => {
            setIsPlaying(true);
          }).catch(err => {
            console.error('Play error:', err);
            setIsPlaying(false);
          });
        }
        setShouldAutoPlay(false);
      }
    }
  }, [currentSong?.id]);

  useEffect(() => {
    if (audioRef.current) {
      audioRef.current.volume = volume;
    }
  }, [volume]);

  const handleTimeUpdate = () => {
    if (audioRef.current) {
      setCurrentTime(audioRef.current.currentTime);
    }
  };

  const handleLoadedMetadata = () => {
    if (audioRef.current) {
      setDuration(audioRef.current.duration);
    }
  };

  const handleEnded = () => {
    if (!isLooping) {
      playNext();
    }
  };

  const handleProgressClick = (e) => {
    if (audioRef.current) {
      const rect = e.currentTarget.getBoundingClientRect();
      const percent = (e.clientX - rect.left) / rect.width;
      const newTime = percent * duration;
      audioRef.current.currentTime = newTime;
      setCurrentTime(newTime);
    }
  };

  const toggleMute = () => {
    if (volume > 0) {
      setVolume(0);
      if (audioRef.current) {
        audioRef.current.volume = 0;
      }
    } else {
      setVolume(0.5);
      if (audioRef.current) {
        audioRef.current.volume = 0.5;
      }
    }
  };

  const handleVolumeChange = (e) => {
    const rect = e.currentTarget.getBoundingClientRect();
    const percent = Math.max(0, Math.min(1, (e.clientX - rect.left) / rect.width));
    setVolume(percent);
    if (audioRef.current) {
      audioRef.current.volume = percent;
    }
  };

  const formatTime = (seconds) => {
    if (isNaN(seconds)) return '0:00';
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
  };

  if (!currentSong) return null;

  const progressPercent = duration ? (currentTime / duration) * 100 : 0;
  const volumePercent = volume * 100;

  return (
    <>
      <audio
        ref={audioRef}
        onTimeUpdate={handleTimeUpdate}
        onLoadedMetadata={handleLoadedMetadata}
        onEnded={handleEnded}
      />
      <div className="music-player">
        <div className="player-left">
          {currentSong.coverImagePath ? (
            <img 
              src={`http://localhost:8080/uploads/pictures/${currentSong.coverImagePath}`} 
              alt={currentSong.title}
              className="player-song-image"
            />
          ) : (
            <div className="player-song-image"></div>
          )}
          <div className="player-song-info">
            <p className="player-song-title">{currentSong.title}</p>
            <p className="player-song-artist">{currentSong.artistName}</p>
          </div>
        </div>

        <div className="player-center">
          <div className="player-controls">
            <button className="player-control-btn" onClick={playPrevious}>
              <i className="bi bi-skip-start-fill"></i>
            </button>
            <button className="player-control-btn player-play-btn" onClick={togglePlay}>
              <i className={`bi bi-${isPlaying ? 'pause' : 'play'}-fill`}></i>
            </button>
            <button className="player-control-btn" onClick={playNext}>
              <i className="bi bi-skip-end-fill"></i>
            </button>
            <button className="player-control-btn" onClick={toggleLoop} title="Loop">
              <i className={`bi bi-repeat ${isLooping ? 'active' : ''}`}></i>
            </button>
          </div>

          <div className="player-progress">
            <span className="player-time">{formatTime(currentTime)}</span>
            <div className="player-progress-bar" onClick={handleProgressClick}>
              <div className="player-progress-filled" style={{ width: `${progressPercent}%` }}>
                <div className="player-progress-handle"></div>
              </div>
            </div>
            <span className="player-time">{formatTime(duration)}</span>
          </div>
        </div>

        <div className="player-right">
          <button className="player-action-btn" onClick={handleLike} title={isLiked ? 'Unlike' : 'Like'}>
            <i className={`bi bi-heart${isLiked ? '-fill' : ''}`} style={{ color: isLiked ? '#1db954' : 'inherit' }}></i>
          </button>
          {isListener() && isPremium() && (
            <button className="player-action-btn" onClick={handleDownload} title="Download">
              <i className="bi bi-download"></i>
            </button>
          )}
          <button className="player-action-btn" onClick={toggleMute}>
            <i className={`bi bi-volume-${volume > 0.5 ? 'up' : volume > 0 ? 'down' : 'mute'}-fill player-volume-icon`}></i>
          </button>
          <div className="player-volume">
            <div className="player-volume-bar" onClick={handleVolumeChange}>
              <div className="player-volume-filled" style={{ width: `${volumePercent}%` }}></div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
