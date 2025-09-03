import { useEffect, useState } from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { clearToken, getToken, request } from "../lib/api.js";

export default function Shell() {
  const [open, setOpen] = useState(true);
  const [me, setMe] = useState(null);
  const nav = useNavigate();

  // 토큰 변경 시 재조회: storage 이벤트로 반영(다른 탭 포함)
  useEffect(() => {
    const load = async () => {
      if (!getToken()) { setMe(null); return; }
      try {
        const data = await request("/api/me"); // ✅ 충돌 피한 단일 엔드포인트
        setMe(data?.authenticated ? data : null);
      } catch { setMe(null); }
    };
    load();

    const onStorage = (e) => { if (e.key === "token") load(); };
    window.addEventListener("storage", onStorage);
    return () => window.removeEventListener("storage", onStorage);
  }, []);

  function logout() {
    clearToken();
    setMe(null);
    nav("/login", { replace: true });
  }

  function greet() {
    if (!me) return null;
    const tag = me.role === "ADMIN" ? "관리자" : "회원";
    return `어서 오세요, ${tag}님 · @${me.username}${me.tier ? " · " + me.tier : ""}`;
  }

  return (
    <>
      <div className="bg-orbs" />
      <div className="app-shell">
        {/* Sidebar */}
        <aside className={`side ${open ? "open" : "close"}`}>
          <div className="side-head">
            {/* ✅ 아이콘 클릭 시 메인으로 (fit → 로고 이미지) */}
            <NavLink to="/" className="logo-pill" aria-label="메인으로">
              <img src="/logo.png" alt="DietCare" width="24" height="24" />
            </NavLink>
            {open && <div className="brand">DietCare</div>}
            <button className="side-toggle" onClick={() => setOpen(v => !v)} aria-label="toggle menu">
              ☰
            </button>
          </div>

          <nav className="side-nav">
            <NavLink to="/" end className="side-link"><span>대시보드</span></NavLink>
            <NavLink to="/register" className="side-link"><span>회원가입</span></NavLink>
            <NavLink to="/profile" className="side-link"><span>프로필</span></NavLink>
            <NavLink to="/upload" className="side-link"><span>카메라</span></NavLink>
            <NavLink to="/settings" className="side-link"><span>설정</span></NavLink>
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
            <div className="top-actions" style={{display:"flex", gap:12, alignItems:"center"}}>
              {me && <div className="badge" title="로그인 사용자">{greet()}</div>}
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
