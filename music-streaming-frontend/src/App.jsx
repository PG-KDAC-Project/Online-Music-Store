import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { PlayerProvider } from './context/PlayerContext';
import ProtectedRoute from './components/ProtectedRoute';
import MainLayout from './components/MainLayout';
import Login from './pages/Login';
import Register from './pages/Register';
import Home from './pages/Home';
import Browse from './pages/Browse';
import Playlists from './pages/Playlists';
import AdminDashboard from './pages/AdminDashboard';
import ArtistDashboard from './pages/ArtistDashboard';
import UploadSong from './pages/UploadSong';
import MySongs from './pages/MySongs';
import ArtistApproval from './pages/ArtistApproval';
import UserManagement from './pages/UserManagement';
import './styles/global.css';

function App() {
  return (
    <AuthProvider>
      <PlayerProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            
            <Route element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
              <Route path="/" element={<Home />} />
              <Route path="/browse" element={<Browse />} />
              <Route path="/playlists" element={<Playlists />} />
              
              <Route path="/admin" element={<ProtectedRoute requiredRole="ROLE_ADMIN"><AdminDashboard /></ProtectedRoute>} />
              <Route path="/admin/artists" element={<ProtectedRoute requiredRole="ROLE_ADMIN"><ArtistApproval /></ProtectedRoute>} />
              <Route path="/admin/users" element={<ProtectedRoute requiredRole="ROLE_ADMIN"><UserManagement /></ProtectedRoute>} />
              
              <Route path="/artist" element={<ProtectedRoute requiredRole="ROLE_ARTIST"><ArtistDashboard /></ProtectedRoute>} />
              <Route path="/artist/upload" element={<ProtectedRoute requiredRole="ROLE_ARTIST"><UploadSong /></ProtectedRoute>} />
              <Route path="/artist/songs" element={<ProtectedRoute requiredRole="ROLE_ARTIST"><MySongs /></ProtectedRoute>} />
            </Route>
            
            <Route path="*" element={<Navigate to="/" />} />
          </Routes>
        </BrowserRouter>
      </PlayerProvider>
    </AuthProvider>
  );
}

export default App;
