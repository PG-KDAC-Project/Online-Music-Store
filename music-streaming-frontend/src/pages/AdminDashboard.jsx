import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminService } from '../services/adminService';

export default function AdminDashboard() {
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const data = await adminService.getUserStatistics();
      setStats(data);
    } catch (error) {
      console.error('Failed to load stats:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div style={{ padding: '2rem' }}>Loading...</div>;

  return (
    <div style={{ padding: '2rem' }}>
      <h1 style={{ fontSize: '48px', fontWeight: '700', marginBottom: '32px' }}>
        Admin Dashboard
      </h1>

      {stats && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '20px', marginBottom: '40px' }}>
          <div className="card">
            <h3 style={{ color: 'var(--text-secondary)', fontSize: '14px', marginBottom: '8px' }}>Total Users</h3>
            <p style={{ fontSize: '32px', fontWeight: '700' }}>{stats.totalUsers || 0}</p>
          </div>
          <div className="card">
            <h3 style={{ color: 'var(--text-secondary)', fontSize: '14px', marginBottom: '8px' }}>Pending Artists</h3>
            <p style={{ fontSize: '32px', fontWeight: '700', color: 'var(--warning)' }}>{stats.pendingArtists || 0}</p>
          </div>
          <div className="card">
            <h3 style={{ color: 'var(--text-secondary)', fontSize: '14px', marginBottom: '8px' }}>Total Artists</h3>
            <p style={{ fontSize: '32px', fontWeight: '700' }}>{stats.totalArtists || 0}</p>
          </div>
          <div className="card">
            <h3 style={{ color: 'var(--text-secondary)', fontSize: '14px', marginBottom: '8px' }}>Total Listeners</h3>
            <p style={{ fontSize: '32px', fontWeight: '700' }}>{stats.totalListeners || 0}</p>
          </div>
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px' }}>
        <div className="card" style={{ cursor: 'pointer' }} onClick={() => navigate('/admin/artists')}>
          <i className="bi bi-person-check" style={{ fontSize: '48px', color: 'var(--accent-primary)', marginBottom: '16px' }}></i>
          <h3 style={{ marginBottom: '8px' }}>Artist Approval</h3>
          <p style={{ color: 'var(--text-secondary)' }}>Review and approve artist registrations</p>
        </div>

        <div className="card" style={{ cursor: 'pointer' }} onClick={() => navigate('/admin/users')}>
          <i className="bi bi-people" style={{ fontSize: '48px', color: 'var(--accent-primary)', marginBottom: '16px' }}></i>
          <h3 style={{ marginBottom: '8px' }}>User Management</h3>
          <p style={{ color: 'var(--text-secondary)' }}>Manage platform users</p>
        </div>
      </div>
    </div>
  );
}
