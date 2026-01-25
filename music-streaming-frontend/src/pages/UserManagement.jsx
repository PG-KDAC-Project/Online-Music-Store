import { useState, useEffect } from 'react';
import { adminService } from '../services/adminService';
import '../styles/admin.css';

export default function UserManagement() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('all');

  useEffect(() => {
    loadUsers();
  }, [filter]);

  const loadUsers = async () => {
    try {
      const response = filter === 'all' 
        ? await adminService.getAllUsers()
        : await adminService.getUsersByRole(filter);
      setUsers(response.content || response);
    } catch (err) {
      console.error('Failed to load users:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSuspend = async (userId) => {
    const reason = prompt('Enter suspension reason:');
    if (reason) {
      try {
        await adminService.suspendUser(userId, { reason });
        alert('User suspended');
        loadUsers();
      } catch (err) {
        alert('Failed to suspend user');
      }
    }
  };

  const handleActivate = async (userId) => {
    try {
      await adminService.activateUser(userId);
      alert('User activated');
      loadUsers();
    } catch (err) {
      alert('Failed to activate user');
    }
  };

  if (loading) return <div style={{ padding: '2rem' }}>Loading...</div>;

  return (
    <div style={{ padding: '2rem' }}>
      <h1 style={{ fontSize: '32px', fontWeight: '700', marginBottom: '1rem' }}>User Management</h1>
      <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>Manage platform users</p>

      <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem' }}>
        <button className={`btn ${filter === 'all' ? 'btn-primary' : 'btn-secondary'}`} onClick={() => setFilter('all')}>
          All Users
        </button>
        <button className={`btn ${filter === 'LISTENER' ? 'btn-primary' : 'btn-secondary'}`} onClick={() => setFilter('LISTENER')}>
          Listeners
        </button>
        <button className={`btn ${filter === 'ARTIST' ? 'btn-primary' : 'btn-secondary'}`} onClick={() => setFilter('ARTIST')}>
          Artists
        </button>
        <button className={`btn ${filter === 'ADMIN' ? 'btn-primary' : 'btn-secondary'}`} onClick={() => setFilter('ADMIN')}>
          Admins
        </button>
      </div>

      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Role</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((user) => (
              <tr key={user.id}>
                <td>{user.name}</td>
                <td>{user.email}</td>
                <td><span className="badge">{user.role?.replace('ROLE_', '')}</span></td>
                <td>
                  <span className={`badge ${user.enabled ? 'badge-success' : 'badge-danger'}`}>
                    {user.enabled ? 'Active' : 'Suspended'}
                  </span>
                </td>
                <td>
                  {user.enabled ? (
                    <button className="btn btn-secondary" onClick={() => handleSuspend(user.id)}>
                      Suspend
                    </button>
                  ) : (
                    <button className="btn btn-primary" onClick={() => handleActivate(user.id)}>
                      Activate
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
