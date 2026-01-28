import api from './api';

export const favoriteService = {
  likeSong: async (songId) => {
    await api.post(`/favorites/songs/${songId}`);
  },
  unlikeSong: async (songId) => {
    await api.delete(`/favorites/songs/${songId}`);
  },
  getFavoriteSongs: async () => {
    const { data } = await api.get('/favorites/songs');
    return data;
  },
  checkIfLiked: async (songId) => {
    const { data } = await api.get(`/favorites/songs/${songId}/check`);
    return data;
  }
};
