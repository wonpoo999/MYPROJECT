// src/pages/Leaderboard.jsx
import { useEffect, useState } from "react";
import { request } from "../lib/api.js";
export default function Leaderboard(){
  const [b,setB]=useState(null);
  useEffect(()=>{ request("/api/leaderboard").then(setB); },[]);
  if(!b) return <div>불러오는 중…</div>;
  return (
    <div>
      <h2>주간 랭킹 <span className="caption">({b.week})</span></h2>
      <div className="tile">
        <ol style={{margin:0, paddingLeft:18}}>
          {b.top.map(e=>(
            <li key={e.userId} style={{margin:"4px 0"}}>
              {e.username} — <b>{e.score}</b>
            </li>
          ))}
        </ol>
      </div>
      {b.me && (
        <p className="caption" style={{marginTop:8}}>
          내 순위: <b>{b.me.rank}</b>위 / 점수 {b.me.score}
        </p>
      )}
    </div>
  );
}
