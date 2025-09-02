// src/pages/Character.jsx
import { useEffect, useState } from "react";
import { request } from "../lib/api.js";
export default function Character(){
  const [c,setC]=useState(null);
  useEffect(()=>{ request("/api/character/me").then(setC).catch(()=>{}); },[]);
  if(!c) return <div>불러오는 중…</div>;
  return (
    <div>
      <h2>내 캐릭터</h2>
      <div className="tile">
        <div className="tile-title">{c.nickname}</div>
        <div className="tile-value">LV {c.level} · HP {c.hp} · ATK {c.atk} · DEF {c.def} · EXP {c.exp}/100</div>
      </div>
      <p className="caption" style={{marginTop:8}}>사진 평가가 반영될 때마다 스탯이 갱신돼요.</p>
    </div>
  );
}
