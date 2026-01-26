import { createContext, useContext, useState, useRef, useEffect } from 'react';

const PlayerContext = createContext();

export const usePlayer = () => useContext(PlayerContext);

export const PlayerProvider = ({ children }) => {
  const [currentSong, setCurrentSong] = useState(null);
  const [queue, setQueue] = useState([]);
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [volume, setVolume] = useState(1);
  const [shouldAutoPlay, setShouldAutoPlay] = useState(false);
  const audioRef = useRef(null);

  const playSong = (song, songQueue = []) => {
    setCurrentSong(song);
    setQueue(songQueue);
    setIsPlaying(true);
    setShouldAutoPlay(true);
  };

  const togglePlay = () => {
    setIsPlaying(!isPlaying);
  };

  const playNext = () => {
    if (!currentSong || queue.length === 0) return;
    const currentIndex = queue.findIndex(s => s.id === currentSong.id);
    if (currentIndex >= 0 && currentIndex < queue.length - 1) {
      playSong(queue[currentIndex + 1], queue);
    }
  };

  const playPrevious = () => {
    if (!currentSong || queue.length === 0) return;
    const currentIndex = queue.findIndex(s => s.id === currentSong.id);
    if (currentIndex > 0) {
      playSong(queue[currentIndex - 1], queue);
    }
  };

  return (
    <PlayerContext.Provider value={{
      currentSong,
      queue,
      isPlaying,
      currentTime,
      duration,
      volume,
      audioRef,
      shouldAutoPlay,
      playSong,
      togglePlay,
      playNext,
      playPrevious,
      setCurrentTime,
      setDuration,
      setVolume,
      setIsPlaying,
      setShouldAutoPlay
    }}>
      {children}
    </PlayerContext.Provider>
  );
};
