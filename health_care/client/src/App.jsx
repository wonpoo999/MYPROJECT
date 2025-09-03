import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Register from "./pages/Register.jsx";
import Login from "./pages/Login.jsx";
import Dashboard from "./pages/Dashboard.jsx";
import Shell from "./layout/Shell.jsx";
import { getToken } from "./lib/api.js";
import Profile from "./pages/Profile.jsx";
import Upload from "./pages/Upload.jsx";
import Settings from "./pages/Settings.jsx";

function Private({ children }) {
  return getToken() ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login/>} />
        <Route path="/register" element={<Register/>} />
        <Route element={<Shell/>}>
          <Route
            path="/"
            element={<Private><Dashboard/></Private>}
          />
          <Route
            path="/profile"
            element={<Private><Profile/></Private>}
          />
          <Route
            path="/upload"
            element={<Private><Upload/></Private>}
          />
          <Route
            path="/settings"
            element={<Private><Settings/></Private>}
          />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
