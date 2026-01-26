import { useState, useEffect } from 'react';
import { userService } from '../services/userService';
import { toast } from 'react-toastify';
import '../styles/profile.css';

const Profile = () => {
  const [profile, setProfile] = useState(null);
  const [name, setName] = useState('');
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [changingPassword, setChangingPassword] = useState(false);
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const data = await userService.getProfile();
      setProfile(data);
      setName(data.name);
    } catch (error) {
      toast.error('Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const updated = await userService.updateProfile(name);
      setProfile(updated);
      setEditing(false);
      toast.success('Profile updated successfully!');
    } catch (error) {
      toast.error('Failed to update profile');
    }
  };

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    try {
      await userService.changePassword(currentPassword, newPassword);
      setChangingPassword(false);
      setCurrentPassword('');
      setNewPassword('');
      toast.success('Password changed successfully!');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to change password');
    }
  };

  if (loading) return <div className="loading">Loading...</div>;

  return (
    <div className="profile-container">
      <h1>My Profile</h1>
      
      <div className="profile-card">
        {editing ? (
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Name</label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </div>
            <div className="form-actions">
              <button type="submit" className="btn-save">Save</button>
              <button type="button" onClick={() => setEditing(false)} className="btn-cancel">Cancel</button>
            </div>
          </form>
        ) : (
          <>
            <div className="profile-info">
              <p><strong>Name:</strong> {profile.name}</p>
              <p><strong>Email:</strong> {profile.email}</p>
              <p><strong>Role:</strong> {profile.role?.replace('ROLE_', '')}</p>
              <p><strong>Premium:</strong> {profile.premium ? 'Yes' : 'No'}</p>
            </div>
            <button onClick={() => setEditing(true)} className="btn-edit">Edit Profile</button>
          </>
        )}
      </div>

      <div className="profile-card">
        <h2>Change Password</h2>
        {changingPassword ? (
          <form onSubmit={handlePasswordChange}>
            <div className="form-group">
              <label>Current Password</label>
              <input
                type="password"
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label>New Password</label>
              <input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                required
              />
            </div>
            <div className="form-actions">
              <button type="submit" className="btn-save">Change Password</button>
              <button type="button" onClick={() => setChangingPassword(false)} className="btn-cancel">Cancel</button>
            </div>
          </form>
        ) : (
          <button onClick={() => setChangingPassword(true)} className="btn-edit">Change Password</button>
        )}
      </div>
    </div>
  );
};

export default Profile;
