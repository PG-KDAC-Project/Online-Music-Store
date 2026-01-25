import { useEffect } from 'react';
import { usePlayer } from '../context/PlayerContext';
import './MusicPlayer.css';

export default function MusicPlayer() {
  const {
    currentSong,
    isPlaying,
    currentTime,
    duration,
    volume,
    audioRef,
    togglePlay,
    playNext,
    playPrevious,
    setCurrentTime,
    setDuration,
    setVolume,
    setIsPlaying
  } = usePlayer();

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
      const playPromise = audioRef.current.play();
      if (playPromise !== undefined) {
        playPromise.then(() => {
          setIsPlaying(true);
        }).catch(err => {
          console.error('Play error:', err);
          setIsPlaying(false);
        });
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
    playNext();
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
          <div className="player-song-image"></div>
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
          <button className="player-action-btn">
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
