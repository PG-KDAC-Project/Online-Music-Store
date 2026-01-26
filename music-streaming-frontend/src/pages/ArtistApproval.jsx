import { useState, useEffect } from 'react';
import { adminService } from '../services/adminService';
import '../styles/admin.css';

export default function ArtistApproval() {
  const [pendingArtists, setPendingArtists] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadPendingArtists();
  }, []);

  const loadPendingArtists = async () => {
    try {
      const response = await adminService.getPendingArtists();
      setPendingArtists(response.content || response);
    } catch (err) {
      console.error('Failed to load artists:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (artistId) => {
    try {
      await adminService.approveArtist(artistId);
      alert('Artist approved successfully!');
      loadPendingArtists();
    } catch (err) {
      alert('Failed to approve artist');
    }
  };

  const handleReject = async (artistId) => {
    if (window.confirm('Are you sure you want to reject this artist?')) {
      try {
        await adminService.rejectArtist(artistId);
        alert('Artist rejected');
        loadPendingArtists();
      } catch (err) {
        alert('Failed to reject artist');
      }
    }
  };

  if (loading) return <div style={{ padding: '2rem' }}>Loading...</div>;

  return (
    <div style={{ padding: '2rem' }}>
      <h1 style={{ fontSize: '32px', fontWeight: '700', marginBottom: '1rem' }}>Artist Approval</h1>
      <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>Review and approve artist registrations</p>

      {pendingArtists.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <i className="bi bi-check-circle" style={{ fontSize: '3rem', color: 'var(--text-secondary)' }}></i>
          <p style={{ marginTop: '1rem', color: 'var(--text-secondary)' }}>No pending artist approvals</p>
        </div>
      ) : (
        <div className="card">
          <table className="table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Joined Date</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {pendingArtists.map((artist) => (
                <tr key={artist.id}>
                  <td>{artist.name}</td>
                  <td>{artist.email}</td>
                  <td>{new Date(artist.createdAt).toLocaleDateString()}</td>
                  <td>
                    <button className="btn btn-primary" style={{ marginRight: '8px' }} onClick={() => handleApprove(artist.id)}>
                      <i className="bi bi-check-circle"></i> Approve
                    </button>
                    <button className="btn btn-secondary" onClick={() => handleReject(artist.id)}>
                      <i className="bi bi-x-circle"></i> Reject
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
