import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { request, setToken } from "../lib/api.js";
import Logo from "../components/Logo.jsx"

export default function Login() {
  const [form, setForm] = useState({ username: "", password: "" });
  const [msg, setMsg] = useState("");
  const [loading, setLoading] = useState(false);
  const nav = useNavigate();

  async function onSubmit(e) {
    e.preventDefault();
    setMsg("");
    if (!form.username.trim() || !form.password) {
      setMsg("아이디/비밀번호를 입력하세요.");
      return;
    }
    try {
      setLoading(true);
      const res = await request("/api/auth/login", {
        method: "POST",
        body: { username: form.username.trim(), password: form.password },
      });
      if (!res?.token) throw new Error("토큰이 없습니다.");
      setToken(res.token);
      setMsg("로그인 성공!");
      nav("/", { replace: true });
    } catch (err) {
      setMsg(`로그인 실패: ${err.message}`);
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <div className="bg-orbs" />
      <div className="auth-wrap">
        <div className="auth-card" style={{ maxWidth: 560 }}>
          <header className="card-head">
            <Logo size={60} />
            <div>
              <h1 className="card-title">로그인</h1>
              <p className="card-sub">다이어트 여정을 계속 이어가요 🔒</p>
            </div>
          </header>

          <div className="card-body">
            <form onSubmit={onSubmit} className="form-grid">
              <div className="input-row">
                <label htmlFor="login-username">아이디</label>
                <input
                  id="login-username"
                  name="username"
                  className="input"
                  value={form.username}
                  onChange={(e) => setForm((p) => ({ ...p, username: e.target.value }))}
                  placeholder="아이디"
                  required
                  autoComplete="username"
                />
              </div>
              <div className="input-row">
                <label htmlFor="login-password">비밀번호</label>
                <input
                  id="login-password"
                  name="password"
                  type="password"
                  className="input"
                  value={form.password}
                  onChange={(e) => setForm((p) => ({ ...p, password: e.target.value }))}
                  placeholder="비밀번호"
                  required
                  autoComplete="current-password"
                />
              </div>

              <button type="submit" className="btn" disabled={loading}>
                {loading ? "로그인 중..." : "로그인"}
              </button>

              <div className={`msg ${msg.includes("성공") ? "ok" : msg ? "ng" : ""}`}>{msg}</div>

              <div className="caption">
                계정이 없나요?{" "}
                <Link to="/register" style={{ color: "var(--accent)" }}>
                  회원가입
                </Link>
              </div>
            </form>
          </div>
        </div>
      </div>
    </>
  );
}
