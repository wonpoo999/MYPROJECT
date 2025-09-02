import { useEffect, useMemo, useRef, useState } from "react";
import { request } from "../lib/api.js";
import Logo from "../components/Logo.jsx"

export default function Register() {
  const [form, setForm] = useState({
    username: "",
    password: "",
    passwordCheck: "",
    email: "",
    emailCheck: "",
    name: "",
    gender: "FEMALE",
    heightCm: 165,
    weightKg: 55.0,
    publicProfile: true,
    bornTown: "",
    livedTown: "",
    motherName: "",
    dogName: "",
    elementary: "",
  });

  const [exists, setExists] = useState(null);
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState("");
  const [checking, setChecking] = useState(false);

  function upd(k, v) { setForm((p) => ({ ...p, [k]: v })); }

  // 디바운스된 아이디 중복 확인
  const tRef = useRef(null);
  useEffect(() => {
    setExists(null);
    if (!form.username?.trim()) return;
    setChecking(true);
    clearTimeout(tRef.current);
    tRef.current = setTimeout(async () => {
      try {
        const res = await request(`/api/auth/exists?username=${encodeURIComponent(form.username.trim())}`);
        setExists(!!res?.exists);
      } catch {
        setExists(null);
      } finally {
        setChecking(false);
      }
    }, 450);
    return () => clearTimeout(tRef.current);
  }, [form.username]);

  const clientError = useMemo(() => {
    if (!form.username.trim()) return "아이디를 입력하세요.";
    if (!form.name.trim()) return "이름을 입력하세요.";
    if (!form.email.trim()) return "이메일을 입력하세요.";
    if (form.email !== form.emailCheck) return "이메일 확인이 일치하지 않습니다.";
    if (!form.password) return "비밀번호를 입력하세요.";
    if (form.password !== form.passwordCheck) return "비밀번호 확인이 일치하지 않습니다.";
    if (!["MALE", "FEMALE", "OTHER"].includes(String(form.gender).toUpperCase()))
      return "성별 값이 올바르지 않습니다.";
    if (Number.isNaN(Number(form.heightCm)) || Number(form.heightCm) <= 0)
      return "키(cm)는 양의 숫자여야 합니다.";
    if (Number.isNaN(Number(form.weightKg)) || Number(form.weightKg) <= 0)
      return "몸무게(kg)는 양의 숫자여야 합니다.";
    if (exists === true) return "이미 사용 중인 아이디입니다.";
    return "";
  }, [form, exists]);

  async function onSubmit(e) {
    e.preventDefault();
    setMsg("");
    if (clientError) { setMsg(clientError); return; }
    const payload = {
      username: form.username.trim(),
      name: form.name.trim(),
      email: form.email.trim(),
      password: form.password,
      gender: String(form.gender).toUpperCase(),
      heightCm: Number(form.heightCm),
      weightKg: Number(form.weightKg),
      publicProfile: !!form.publicProfile,
    };
    try {
      setLoading(true);
      await request("/api/auth/register", { method: "POST", body: payload });
      setMsg("회원가입 성공! 로그인하세요.");
      setForm((p) => ({ ...p, password: "", passwordCheck: "" }));
    } catch (err) {
      setMsg(`가입 실패: ${err.message}`);
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <div className="bg-orbs" />
      <div className="auth-wrap">
        <div className="auth-card">
          <header className="card-head">
            <Logo size={60}/>
            <div>
              <h1 className="card-title">DietCare 회원가입</h1>
              <p className="card-sub">식단 기록과 캐릭터 스탯을 연결하는 건강 어드벤처 🥗⚡</p>
            </div>
          </header>

          <div className="card-body">
            <div className="caption" style={{ marginBottom: 8 }}>
              <span className="kvp">API 기반: /api/auth/exists, /api/auth/register</span>
              <span className="kvp">JWT 로그인 연동</span>
            </div>

            <form onSubmit={onSubmit} className="form-grid">
              <div className="input-row">
                <label htmlFor="reg-username">아이디</label>
                <input
                  id="reg-username" name="username"
                  className="input" value={form.username}
                  onChange={(e) => upd("username", e.target.value)}
                  required placeholder="예) lcguava1" autoComplete="username"
                />
                <div className="caption" aria-live="polite">
                  {checking && <span className="badge loading">확인 중...</span>}
                  {exists === true && <span className="badge ng">이미 사용중</span>}
                  {exists === false && <span className="badge ok">사용 가능</span>}
                </div>
              </div>

              <div className="input-row">
                <label htmlFor="reg-name">이름</label>
                <input
                  id="reg-name" name="name" className="input"
                  value={form.name} onChange={(e) => upd("name", e.target.value)}
                  required placeholder="실명"
                />
              </div>

              <div className="form-grid cols-2">
                <div className="input-row">
                  <label htmlFor="reg-email">이메일</label>
                  <input
                    id="reg-email" name="email" type="email" className="input"
                    value={form.email} onChange={(e) => upd("email", e.target.value)}
                    required placeholder="you@example.com" autoComplete="email"
                  />
                </div>
                <div className="input-row">
                  <label htmlFor="reg-email2">이메일 확인</label>
                  <input
                    id="reg-email2" name="emailCheck" type="email" className="input"
                    value={form.emailCheck} onChange={(e) => upd("emailCheck", e.target.value)}
                    required placeholder="다시 입력" autoComplete="email"
                  />
                </div>
              </div>

              <div className="form-grid cols-2">
                <div className="input-row">
                  <label htmlFor="reg-pass">비밀번호</label>
                  <input
                    id="reg-pass" name="password" type="password" className="input"
                    value={form.password} onChange={(e) => upd("password", e.target.value)}
                    required placeholder="영문+숫자 조합 권장" autoComplete="new-password"
                  />
                </div>
                <div className="input-row">
                  <label htmlFor="reg-pass2">비번 확인</label>
                  <input
                    id="reg-pass2" name="passwordCheck" type="password" className="input"
                    value={form.passwordCheck} onChange={(e) => upd("passwordCheck", e.target.value)}
                    required placeholder="한 번 더" autoComplete="new-password"
                  />
                </div>
              </div>

              <div className="form-grid cols-3">
                <div className="input-row">
                  <label htmlFor="reg-gender">성별</label>
                  <select
                    id="reg-gender" name="gender" className="select"
                    value={form.gender} onChange={(e) => upd("gender", e.target.value)}
                  >
                    <option value="MALE">MALE</option>
                    <option value="FEMALE">FEMALE</option>
                    <option value="OTHER">OTHER</option>
                  </select>
                </div>
                <div className="input-row">
                  <label htmlFor="reg-height">키(cm)</label>
                  <input
                    id="reg-height" name="heightCm" type="number" inputMode="numeric" className="input"
                    value={form.heightCm}
                    onChange={(e) => upd("heightCm", e.target.value === "" ? "" : parseInt(e.target.value, 10))}
                  />
                </div>
                <div className="input-row">
                  <label htmlFor="reg-weight">몸무게(kg)</label>
                  <input
                    id="reg-weight" name="weightKg" type="number" step="0.1" inputMode="decimal" className="input"
                    value={form.weightKg}
                    onChange={(e) => upd("weightKg", e.target.value === "" ? "" : parseFloat(e.target.value))}
                  />
                </div>
              </div>

              <details>
                <summary>보안 질문 (선택)</summary>
                <div className="form-grid cols-2" style={{ marginTop: 8 }}>
                  <label className="sr-only" htmlFor="q-born">출생지</label>
                  <input id="q-born" name="bornTown" className="input" placeholder="출생지"
                         value={form.bornTown} onChange={(e) => upd("bornTown", e.target.value)} />
                  <label className="sr-only" htmlFor="q-lived">살았던 도시</label>
                  <input id="q-lived" name="livedTown" className="input" placeholder="살았던 도시"
                         value={form.livedTown} onChange={(e) => upd("livedTown", e.target.value)} />
                  <label className="sr-only" htmlFor="q-mother">어머니 성함</label>
                  <input id="q-mother" name="motherName" className="input" placeholder="어머니 성함"
                         value={form.motherName} onChange={(e) => upd("motherName", e.target.value)} />
                  <label className="sr-only" htmlFor="q-dog">반려견 이름</label>
                  <input id="q-dog" name="dogName" className="input" placeholder="반려견 이름"
                         value={form.dogName} onChange={(e) => upd("dogName", e.target.value)} />
                  <label className="sr-only" htmlFor="q-elem">초등학교</label>
                  <input id="q-elem" name="elementary" className="input" placeholder="초등학교"
                         value={form.elementary} onChange={(e) => upd("elementary", e.target.value)} />
                </div>
              </details>

              <div className="inline">
                <label className="inline" htmlFor="reg-public">
                  <input id="reg-public" name="publicProfile" type="checkbox"
                         checked={form.publicProfile}
                         onChange={(e) => upd("publicProfile", e.target.checked)} />
                  <span>프로필 공개</span>
                </label>
              </div>

              <div className="divider" />

              <button type="submit" className="btn" disabled={loading || checking}>
                {loading ? "가입 중..." : "가입하기"}
              </button>

              <div className={`msg ${msg.includes("성공") ? "ok" : msg ? "ng" : ""}`}>{msg}</div>
            </form>

            <p className="caption" style={{ marginTop: 8 }}>
              가입 후 설정에서 개인정보 질문들을 따로 저장할 수 있습니다. (예: <code>/api/users/privacy</code>)
            </p>
          </div>
        </div>
      </div>
    </>
  );
}
