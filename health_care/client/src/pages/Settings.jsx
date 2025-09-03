import { useEffect, useState } from "react";

const PRESETS = [
  { v: 1.2,   label: "거의 안 움직임 (사무/휴식)" },
  { v: 1.375, label: "가벼운 활동 (주 1~3일 운동)" },
  { v: 1.55,  label: "보통 활동 (주 3~5일 운동)" },
  { v: 1.725, label: "높은 활동 (매일 고강도)" },
  { v: 1.9,   label: "매우 높은 활동 (육체노동/선수)" },
];

export default function Settings(){
  const [af, setAf] = useState(1.2);

  useEffect(()=>{
    const v = parseFloat(localStorage.getItem("activityFactor") || "1.2");
    setAf(Number.isFinite(v) ? v : 1.2);
  }, []);

  function save(){
    localStorage.setItem("activityFactor", String(af));
    alert("활동계수가 저장되었습니다. 대시보드에서 재계산됩니다.");
  }

  return (
    <div style={{maxWidth:520}}>
      <h2>설정</h2>
      <div className="tile">
        <div className="tile-title">활동계수(TDEE)</div>
        <select className="select" value={af} onChange={e=>setAf(parseFloat(e.target.value))}>
          {PRESETS.map(p=>(
            <option key={p.v} value={p.v}>{p.v} · {p.label}</option>
          ))}
        </select>
        <p className="caption" style={{marginTop:8}}>
          Mifflin-St Jeor BMR × 활동계수 = TDEE.  
          대시보드의 권장 칼로리/영양소 계산에 적용됩니다.
        </p>
        <button className="btn" onClick={save} style={{marginTop:10}}>저장</button>
      </div>
    </div>
  );
}
