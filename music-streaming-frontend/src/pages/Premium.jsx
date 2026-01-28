import { useState, useEffect } from 'react';
import { premiumService } from '../services/premiumService';
import { useAuth } from '../context/AuthContext';
import { toast } from 'react-toastify';
import '../styles/premium.css';

const Premium = () => {
  const [premiumPackage, setPremiumPackage] = useState(null);
  const [subscription, setSubscription] = useState(null);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);
  const { user, isPremium, updatePremium } = useAuth();
  const [cashfree, setCashfree] = useState(null);

  useEffect(() => {
    fetchData();
    loadCashfreeSDK();
  }, []);

  const loadCashfreeSDK = () => {
    const script = document.createElement('script');
    script.src = 'https://sdk.cashfree.com/js/v3/cashfree.js';
    script.async = true;
    script.onload = () => {
      if (window.Cashfree) {
        const cf = window.Cashfree({ mode: 'sandbox' });
        setCashfree(cf);
      }
    };
    document.body.appendChild(script);
  };

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
    if (!cashfree) {
      toast.error('Payment system loading... Please wait and try again.');
      return;
    }

    setProcessing(true);
    try {
      // Create payment order
      const paymentData = await premiumService.createPaymentOrder(
        user.name,
        user.email,
        user.phone || '9999999999'
      );

      toast.info('Opening payment gateway...');

      // Cashfree checkout options
      const checkoutOptions = {
        paymentSessionId: paymentData.paymentSessionId,
        redirectTarget: '_modal'
      };

      // Open Cashfree checkout
      cashfree.checkout(checkoutOptions).then(async (result) => {
        if (result.error) {
          toast.error('Payment cancelled or failed');
          setProcessing(false);
          return;
        }

        if (result.paymentDetails || result.redirect) {
          // Payment completed, verify on backend
          toast.info('Verifying payment...');
          try {
            const sub = await premiumService.verifyPayment(paymentData.orderId);
            setSubscription(sub);
            updatePremium(true);
            toast.success('Premium activated successfully!');
          } catch (error) {
            toast.error('Payment verification failed. Contact support with Order ID: ' + paymentData.orderId);
          }
        }
        setProcessing(false);
      }).catch((error) => {
        console.error('Cashfree error:', error);
        toast.error('Payment gateway error. Please try again.');
        setProcessing(false);
      });
    } catch (error) {
      console.error('Payment error:', error);
      toast.error(error.response?.data?.message || 'Failed to initiate payment');
      setProcessing(false);
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
            <p><strong>Amount Paid:</strong> ₹{subscription.amountPaid}</p>
            <p><strong>Duration:</strong> {subscription.durationDays} days</p>
            <p><strong>Purchased:</strong> {new Date(subscription.purchasedAt).toLocaleDateString()}</p>
            <p><strong>Expires:</strong> {new Date(subscription.expiresAt).toLocaleDateString()}</p>
          </div>
        </div>
      ) : premiumPackage ? (
        <div className="package-card">
          <h2>{premiumPackage.name}</h2>
          <p className="description">{premiumPackage.description}</p>
          <div className="price">₹{premiumPackage.price}</div>
          <p className="duration">{premiumPackage.durationDays} days</p>
          <button 
            onClick={handlePurchase} 
            className="purchase-btn"
            disabled={processing}
          >
            {processing ? 'Processing...' : 'Purchase Premium'}
          </button>
        </div>
      ) : (
        <p>No premium package available</p>
      )}
    </div>
  );
};

export default Premium;
