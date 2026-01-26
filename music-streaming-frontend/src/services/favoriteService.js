import api from './api';

export const favoriteService = {
  likeSong: async (songId) => {
    await api.post(`/favorites/songs/${songId}`);
  },
  unlikeSong: async (songId) => {
    await api.delete(`/favorites/songs/${songId}`);
  },
  getFavoriteSongs: async (page = 0, size = 20) => {
    const { data } = await api.get(`/favorites/songs?page=${page}&size=${size}`);
    return data;
  },
  checkIfLiked: async (songId) => {
    const { data } = await api.get(`/favorites/songs/${songId}/check`);
    return data;
  }
};
