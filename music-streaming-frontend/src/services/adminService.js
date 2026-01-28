import api from './api';

export const adminService = {
  getAllUsers: async () => {
    const { data } = await api.get('/admin/users');
    return data;
  },
  getUsersByRole: async (role) => {
    const { data } = await api.get(`/admin/users/role/${role}`);
    return data;
  },
  getPendingArtists: async () => {
    const { data } = await api.get('/admin/artists/pending');
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
