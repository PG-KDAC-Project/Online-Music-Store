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
    if (storedToken && storedRole) {
      setToken(storedToken);
      setUser({ role: storedRole });
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    const response = await authService.login(email, password);
    localStorage.setItem('token', response.token);
    localStorage.setItem('role', response.role);
    setToken(response.token);
    setUser({ role: response.role });
    return response;
  };

  const register = async (name, email, password, role) => {
    const response = await authService.register(name, email, password, role);
    localStorage.setItem('token', response.token);
    localStorage.setItem('role', response.role);
    setToken(response.token);
    setUser({ role: response.role });
    return response;
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    setToken(null);
    setUser(null);
  };

  const isAuthenticated = () => !!token;
  const isAdmin = () => user?.role === 'ROLE_ADMIN';
  const isArtist = () => user?.role === 'ROLE_ARTIST';
  const isListener = () => user?.role === 'ROLE_LISTENER';

  return (
    <AuthContext.Provider value={{ user, token, login, register, logout, isAuthenticated, isAdmin, isArtist, isListener, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
