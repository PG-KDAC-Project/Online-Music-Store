import api from './api';

export const premiumService = {
  getPremiumPackage: async () => {
    const { data } = await api.get('/premium/package');
    return data;
  },
  purchasePremium: async () => {
    const { data } = await api.post('/premium/purchase');
    return data;
  },
  getUserSubscription: async () => {
    const { data } = await api.get('/premium/subscription');
    return data;
  },
  setPremiumPackage: async (packageData) => {
    const { data } = await api.post('/admin/premium/package', packageData);
    return data;
  },
  getAdminPremiumPackage: async () => {
    const { data } = await api.get('/admin/premium/package');
    return data;
  }
};
