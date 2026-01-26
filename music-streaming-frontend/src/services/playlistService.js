import api from './api';

export const playlistService = {
  getMyPlaylists: async () => {
    const { data } = await api.get('/playlists/my/all');
    return data;
  },
  createPlaylist: async (name, description, isPublic) => {
    const { data } = await api.post('/playlists', { name, description, isPublic });
    return data;
  },
  addSongToPlaylist: async (playlistId, songId) => {
    const { data } = await api.post(`/playlists/${playlistId}/songs/${songId}`);
    return data;
  },
  removeSongFromPlaylist: async (playlistId, songId) => {
    const { data } = await api.delete(`/playlists/${playlistId}/songs/${songId}`);
    return data;
  },
  getPlaylistWithSongs: async (playlistId) => {
    const { data } = await api.get(`/playlists/${playlistId}/songs`);
    return data;
  },
  deletePlaylist: async (playlistId) => {
    await api.delete(`/playlists/${playlistId}`);
  }
};
