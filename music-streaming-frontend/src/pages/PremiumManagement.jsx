import { useState, useEffect } from 'react';
import { premiumService } from '../services/premiumService';
import { toast } from 'react-toastify';
import '../styles/premium.css';

const PremiumManagement = () => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    durationDays: ''
  });
  const [currentPackage, setCurrentPackage] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchCurrentPackage();
  }, []);

  const fetchCurrentPackage = async () => {
    try {
      const pkg = await premiumService.getAdminPremiumPackage();
      setCurrentPackage(pkg);
      setFormData({
        name: pkg.name,
        description: pkg.description || '',
        price: pkg.price,
        durationDays: pkg.durationDays
      });
    } catch (error) {
      // No package set yet
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const pkg = await premiumService.setPremiumPackage({
        ...formData,
        price: parseFloat(formData.price),
        durationDays: parseInt(formData.durationDays)
      });
      setCurrentPackage(pkg);
      toast.success('Premium package updated successfully!');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to update package');
    }
  };

  if (loading) return <div className="loading">Loading...</div>;

  return (
    <div className="premium-management">
      <h1>Premium Package Management</h1>
      
      {currentPackage && (
        <div className="current-package">
          <h3>Current Package</h3>
          <p><strong>Name:</strong> {currentPackage.name}</p>
          <p><strong>Price:</strong> ${currentPackage.price}</p>
          <p><strong>Duration:</strong> {currentPackage.durationDays} days</p>
        </div>
      )}

      <form onSubmit={handleSubmit} className="package-form">
        <div className="form-group">
          <label>Package Name</label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label>Description</label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            rows="3"
          />
        </div>

        <div className="form-group">
          <label>Price ($)</label>
          <input
            type="number"
            name="price"
            value={formData.price}
            onChange={handleChange}
            step="0.01"
            min="0.01"
            required
          />
        </div>

        <div className="form-group">
          <label>Duration (days)</label>
          <input
            type="number"
            name="durationDays"
            value={formData.durationDays}
            onChange={handleChange}
            min="1"
            required
          />
        </div>

        <button type="submit" className="submit-btn">
          {currentPackage ? 'Update Package' : 'Create Package'}
        </button>
      </form>
    </div>
  );
};

export default PremiumManagement;
