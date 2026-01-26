import { useState, useEffect } from 'react';
import { premiumService } from '../services/premiumService';
import { useAuth } from '../context/AuthContext';
import { toast } from 'react-toastify';
import '../styles/premium.css';

const Premium = () => {
  const [premiumPackage, setPremiumPackage] = useState(null);
  const [subscription, setSubscription] = useState(null);
  const [loading, setLoading] = useState(true);
  const { isPremium, updatePremium } = useAuth();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const pkg = await premiumService.getPremiumPackage();
      setPremiumPackage(pkg);
      
      try {
        const sub = await premiumService.getUserSubscription();
        setSubscription(sub);
      } catch (err) {
        // No active subscription
      }
    } catch (error) {
      toast.error('Failed to load premium package');
    } finally {
      setLoading(false);
    }
  };

  const handlePurchase = async () => {
    try {
      const sub = await premiumService.purchasePremium();
      setSubscription(sub);
      updatePremium(true);
      toast.success('Premium purchased successfully!');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to purchase premium');
    }
  };

  if (loading) return <div className="loading">Loading...</div>;

  return (
    <div className="premium-container">
      <h1>Premium Membership</h1>
      
      {subscription ? (
        <div className="subscription-card">
          <h2>Active Subscription</h2>
          <div className="subscription-details">
            <p><strong>Amount Paid:</strong> ${subscription.amountPaid}</p>
            <p><strong>Duration:</strong> {subscription.durationDays} days</p>
            <p><strong>Purchased:</strong> {new Date(subscription.purchasedAt).toLocaleDateString()}</p>
            <p><strong>Expires:</strong> {new Date(subscription.expiresAt).toLocaleDateString()}</p>
          </div>
        </div>
      ) : premiumPackage ? (
        <div className="package-card">
          <h2>{premiumPackage.name}</h2>
          <p className="description">{premiumPackage.description}</p>
          <div className="price">${premiumPackage.price}</div>
          <p className="duration">{premiumPackage.durationDays} days</p>
          <button onClick={handlePurchase} className="purchase-btn">
            Purchase Premium
          </button>
        </div>
      ) : (
        <p>No premium package available</p>
      )}
    </div>
  );
};

export default Premium;
