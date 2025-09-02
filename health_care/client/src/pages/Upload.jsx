// src/pages/Upload.jsx
import { useState } from "react";
import { request } from "../lib/api.js";

export default function Upload(){
  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState("");
  const [res, setRes] = useState(null);
  const [evalRes, setEvalRes] = useState(null);
  const [msg, setMsg] = useState("");

  function onPick(e){
    const f = e.target.files?.[0]; setFile(f||null);
    setPreview(f ? URL.createObjectURL(f) : "");
    setRes(null); setEvalRes(null); setMsg("");
  }

  async function onUpload(){
    if(!file) return;
    try{
      const fd = new FormData(); fd.append("file", file);
      const r = await request("/api/foodlogs", { method:"POST", body: fd });
      setRes(r);
      if(r.status === "ok"){
        // 바로 평가
        const er = await request("/api/foodlogs/evaluate", {
          method:"POST",
          body:{ foodName:r.foodName, cal:r.cal, protein:r.protein, carb:r.carb, fat:r.fat }
        });
        setEvalRes(er);
      }
    }catch(e){ setMsg(e.message); }
  }

  async function choose(name){
    try{
      const r = await request("/api/foodlogs/confirm", { method:"POST", body:{ foodName:name, imgUrl: preview }});
      setRes(r);
      const er = await request("/api/foodlogs/evaluate", {
        method:"POST",
        body:{ foodName:r.foodName, cal:r.cal, protein:r.protein, carb:r.carb, fat:r.fat }
      });
      setEvalRes(er);
    }catch(e){ setMsg(e.message); }
  }

  return (
    <div>
      <h2>사진 업로드</h2>
      <input type="file" accept="image/*" onChange={onPick}/>
      {preview && <div style={{marginTop:12}}><img src={preview} alt="" style={{maxWidth:320,borderRadius:8}}/></div>}
      <button className="btn" style={{marginTop:12}} onClick={onUpload} disabled={!file}>업로드</button>
      {msg && <div className="msg ng">{msg}</div>}

      {res && res.status==="ambiguous" && (
        <div className="tile" style={{marginTop:16}}>
          <div className="tile-title">후보가 모호합니다. 선택해주세요.</div>
          <div style={{display:"flex", gap:8, flexWrap:"wrap"}}>
            {res.candidates?.map(c=>(
              <button key={c.name} className="btn small" onClick={()=>choose(c.name)}>
                {c.name} ({Math.round(c.score*100)}%)
              </button>
            ))}
          </div>
        </div>
      )}

      {res && res.status==="ok" && (
        <div className="tile" style={{marginTop:16}}>
          <div className="tile-title">영양 요약</div>
          <div className="tile-value" style={{fontSize:18}}>
            {res.foodName} · {Math.round(res.cal)} kcal · P {Math.round(res.protein)}g / C {Math.round(res.carb)}g / F {Math.round(res.fat)}g
          </div>
        </div>
      )}

      {evalRes && (
        <div className="tile" style={{marginTop:12}}>
          <div className="tile-title">AI 캐릭터 평가</div>
          <div style={{fontSize:22, fontWeight:800}}>
            {evalRes.label} (점수 {evalRes.score})
          </div>
          <p className="caption" style={{marginTop:6}}>{evalRes.comment}</p>
          <div className="caption">스탯 변화 → HP {evalRes.delta.hp>=0?'+':''}{evalRes.delta.hp}, ATK +{evalRes.delta.atk}, DEF {evalRes.delta.def>=0?'+':''}{evalRes.delta.def}</div>
        </div>
      )}
    </div>
  );
}
