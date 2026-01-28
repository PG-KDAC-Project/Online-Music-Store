import api from './api';

export const premiumService = {
  getPremiumPackage: async () => {
    const { data } = await api.get('/premium/package');
    return data;
  },
  createPaymentOrder: async (customerName, customerEmail, customerPhone) => {
    const { data } = await api.post('/payment/create-order', {
      customerName,
      customerEmail,
      customerPhone
    });
    return data;
  },
  verifyPayment: async (orderId) => {
    const { data } = await api.post('/payment/verify', { orderId });
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
