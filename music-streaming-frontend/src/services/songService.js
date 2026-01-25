import api from './api';

export const songService = {
  getAllSongs: async (page = 0, size = 20) => {
    const { data } = await api.get(`/songs?page=${page}&size=${size}`);
    return data;
  },
  getSongById: async (id) => {
    const { data } = await api.get(`/songs/${id}`);
    return data;
  },
  uploadSong: async (formData) => {
    const { data } = await api.post('/songs/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return data;
  },
  searchSongs: async (keyword, page = 0, size = 20) => {
    const { data } = await api.get(`/songs/search?keyword=${keyword}&page=${page}&size=${size}`);
    return data;
  },
  getMostPlayed: async (page = 0, size = 20) => {
    const { data } = await api.get(`/songs/most-played?page=${page}&size=${size}`);
    return data;
  },
  streamSong: (id) => {
    return `http://localhost:8080/api/songs/stream/${id}`;
  },
  likeSong: async (id) => {
    await api.post(`/songs/${id}/like`);
  },
  unlikeSong: async (id) => {
    await api.delete(`/songs/${id}/like`);
  },
  getMySongs: async (page = 0, size = 20) => {
    const { data } = await api.get(`/songs/my-songs?page=${page}&size=${size}`);
    return data;
  },
  deleteSong: async (id) => {
    await api.delete(`/songs/${id}`);
  }
};
