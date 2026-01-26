import { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedRole = localStorage.getItem('role');
    const storedPremium = localStorage.getItem('premium') === 'true';
    if (storedToken && storedRole) {
      setToken(storedToken);
      setUser({ role: storedRole, premium: storedPremium });
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    const response = await authService.login(email, password);
    localStorage.setItem('token', response.token);
    localStorage.setItem('role', response.role);
    localStorage.setItem('premium', response.premium || false);
    setToken(response.token);
    setUser({ role: response.role, premium: response.premium });
    return response;
  };

  const register = async (name, email, password, role) => {
    const response = await authService.register(name, email, password, role);
    localStorage.setItem('token', response.token);
    localStorage.setItem('role', response.role);
    localStorage.setItem('premium', response.premium || false);
    setToken(response.token);
    setUser({ role: response.role, premium: response.premium });
    return response;
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('premium');
    setToken(null);
    setUser(null);
  };

  const updatePremium = (premium) => {
    localStorage.setItem('premium', premium);
    setUser(prev => ({ ...prev, premium }));
  };

  const isAuthenticated = () => !!token;
  const isAdmin = () => user?.role === 'ROLE_ADMIN';
  const isArtist = () => user?.role === 'ROLE_ARTIST';
  const isListener = () => user?.role === 'ROLE_LISTENER';
  const isPremium = () => user?.premium === true;

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout, updatePremium, isAuthenticated, isAdmin, isArtist, isListener, isPremium, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
