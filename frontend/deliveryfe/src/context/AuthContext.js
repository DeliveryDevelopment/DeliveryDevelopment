import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import {
  setAuthTokenProvider,
  setUnauthorizedHandler,
} from "../api/client";

const TOKEN_STORAGE_KEY = "delivery.authToken";

const AuthContext = createContext({
  token: null,
  isAuthed: false,
  login: () => {},
  logout: () => {},
});

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(() =>
    localStorage.getItem(TOKEN_STORAGE_KEY)
  );

  const login = useCallback((newToken) => {
    localStorage.setItem(TOKEN_STORAGE_KEY, newToken);
    setToken(newToken);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    setToken(null);
  }, []);

  useEffect(() => {
    setAuthTokenProvider(() => token);
  }, [token]);

  useEffect(() => {
    setUnauthorizedHandler(() => {
      logout();
    });
  }, [logout]);

  const value = useMemo(
    () => ({
      token,
      isAuthed: Boolean(token),
      login,
      logout,
    }),
    [token, login, logout]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => useContext(AuthContext);

export const getStoredToken = () => localStorage.getItem(TOKEN_STORAGE_KEY);

export default AuthContext;
