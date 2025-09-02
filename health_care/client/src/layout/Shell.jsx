// src/layout/Shell.jsx
import { useState } from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { clearToken, getToken } from "../lib/api.js";

export default function Shell() {
  const [open, setOpen] = useState(true);
  const nav = useNavigate();

  function logout() {
    clearToken();
    nav("/login", { replace: true });
  }

  return (
    <>
      <div className="bg-orbs" />
      <div className="app-shell">
        {/* Sidebar */}
        <aside className={`side ${open ? "open" : "close"}`}>
          <div className="side-head">
            <div className="logo-pill">fit</div>
            {open && <div className="brand">DietCare</div>}
            <button className="side-toggle" onClick={() => setOpen(v => !v)} aria-label="toggle menu">
              ☰
            </button>
          </div>

          <nav className="side-nav">
            <NavLink to="/" end className="side-link">
              <span>대시보드</span>
            </NavLink>
            <NavLink to="/register" className="side-link">
              <span>회원가입</span>
            </NavLink>
          </nav>

          <div className="side-foot">
            {getToken() ? (
              <button className="btn small" onClick={logout}>로그아웃</button>
            ) : (
              <NavLink to="/login" className="btn small" style={{textDecoration:'none', display:'inline-block', textAlign:'center'}}>로그인</NavLink>
            )}
          </div>
        </aside>

        {/* Main */}
        <div className="main">
          <header className="topbar">
            <div className="top-title">Health · Diet Adventure</div>
            <div className="top-actions">
              <div className="badge" title="API Base">
                <span>API</span>
                <code style={{opacity:.85}}>{import.meta.env.VITE_API_BASE || "http://localhost:8081"}</code>
              </div>
            </div>
          </header>

          <main className="content">
            <Outlet />
          </main>
        </div>
      </div>
    </>
  );
}
