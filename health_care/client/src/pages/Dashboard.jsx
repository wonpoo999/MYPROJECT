import { useEffect, useMemo, useState } from "react";
import { request } from "../lib/api.js";
import { Link } from "react-router-dom";

/** 로컬 설정(활동계수) 읽기 */
function getActivity() {
  const v = parseFloat(localStorage.getItem("activityFactor") || "1.2");
  if (Number.isNaN(v) || v < 1.1 || v > 2.4) return 1.2;
  return v;
}

export default function Dashboard(){
  const [targets, setTargets] = useState(null);
  const [err, setErr] = useState("");

  async function load() {
    setErr("");
    try {
      const af = getActivity();
      const t = await request(`/api/nutrition/targets?activity=${af}`);
      setTargets(t);
    } catch (e) {
      setErr(e.message || "불러오기 실패");
      setTargets(null);
    }
  }
  useEffect(()=>{ load(); }, []);

  const macros = useMemo(()=>{
    if (!targets) return null;
    return {
      kcal: Math.round(targets.cutKcal),
      p: targets.proteinG,
      c: targets.carbG,
      f: targets.fatG
    };
  }, [targets]);

  return (
    <div className="dash">
      <h2 style={{margin:"0 0 8px 0"}}>대시보드</h2>
      <p className="caption">회원의 키/체중/나이/성별 기반으로 오늘 권장 섭취량을 자동 계산합니다.</p>

      {err && <div className="msg ng" aria-live="polite">{err}</div>}

      {targets && (
        <div style={{display:"grid", gridTemplateColumns:"1fr 1fr", gap:16, marginTop:16}}>
          <div className="tile">
            <div className="tile-title">오늘의 권장 칼로리</div>
            <div className="tile-value">{macros.kcal} kcal</div>
            <div className="caption">
              BMR {targets.bmr} · TDEE {Math.round(targets.tdee)}
              {"  "}· 활동계수 {targets.activityFactor}
            </div>
          </div>
          <div className="tile">
            <div className="tile-title">단백질 / 탄수 / 지방</div>
            <div className="tile-value">{macros.p}g / {macros.c}g / {macros.f}g</div>
            <div className="caption">감량 기준(−500kcal, 최소 1200kcal)</div>
          </div>
        </div>
      )}

      {!targets && !err && (
        <div className="tile" style={{marginTop:16}}>
          <div className="tile-title">로드 중...</div>
        </div>
      )}

      <div className="fab-row">
        <Link to="/settings" className="fab" aria-label="설정">⚙️</Link>
        <Link to="/profile" className="fab" aria-label="프로필">🧑</Link>
        <Link to="/upload" className="fab" aria-label="카메라">📷</Link>
      </div>
      <p className="caption" style={{marginTop:8}}>
        * 활동계수는 <Link to="/settings" style={{color:"var(--accent)"}}>설정</Link>에서 바꿀 수 있어요.
      </p>
    </div>
  );
}
