// src/pages/MyLogs.jsx
import { useEffect, useState } from "react";
import { request } from "../lib/api.js";

export default function MyLogs(){
  const [items,setItems]=useState([]);
  useEffect(()=>{ request("/api/foodlogs/me").then(r=>setItems(r.items||[])); },[]);
  return (
    <div>
      <h2>내 식단 기록</h2>
      <div style={{display:"grid", gap:10}}>
        {items.map(it=>(
          <div key={it.id} className="tile">
            <div className="tile-title">{new Date(it.createdAt).toLocaleString()}</div>
            <div className="tile-value" style={{fontSize:18}}>
              {it.foodName} · {Math.round(it.cal)} kcal · P{Math.round(it.protein)} / C{Math.round(it.carb)} / F{Math.round(it.fat)}
            </div>
            <div className="caption">평가: {it.label??'-'} {typeof it.score==='number' ? `(${it.score})`:''}</div>
          </div>
        ))}
      </div>
    </div>
  );
}
