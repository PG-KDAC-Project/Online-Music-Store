import api from './api';

export const adminService = {
  getAllUsers: async (page = 0, size = 20) => {
    const { data } = await api.get(`/admin/users?page=${page}&size=${size}`);
    return data;
  },
  getUsersByRole: async (role, page = 0, size = 20) => {
    const { data } = await api.get(`/admin/users/role/${role}?page=${page}&size=${size}`);
    return data;
  },
  getPendingArtists: async (page = 0, size = 20) => {
    const { data } = await api.get(`/admin/artists/pending?page=${page}&size=${size}`);
    return data;
  },
  approveArtist: async (artistId) => {
    const { data } = await api.post(`/admin/artists/${artistId}/approve`);
    return data;
  },
  rejectArtist: async (artistId) => {
    const { data } = await api.post(`/admin/artists/${artistId}/reject`);
    return data;
  },
  suspendUser: async (userId, reason) => {
    const { data } = await api.post(`/admin/users/${userId}/suspend`, reason);
    return data;
  },
  activateUser: async (userId) => {
    const { data } = await api.post(`/admin/users/${userId}/activate`);
    return data;
  },
  getUserStatistics: async () => {
    const { data } = await api.get('/admin/statistics/users');
    return data;
  }
};
