import api from './api';

export const userService = {
  getProfile: async () => {
    const { data } = await api.get('/user/profile');
    return data;
  },
  updateProfile: async (name) => {
    const { data } = await api.put('/user/profile', { name });
    return data;
  },
  changePassword: async (currentPassword, newPassword) => {
    const { data } = await api.patch('/user/password', { currentPassword, newPassword });
    return data;
  }
};
