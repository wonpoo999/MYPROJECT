// src/pages/Dashboard.jsx
export default function Dashboard(){
  return (
    <div className="dash">
      <h2 style={{margin:"0 0 8px 0"}}>대시보드</h2>
      <p className="caption">여기에 식단 요약, 오늘 섭취 칼로리, 캐릭터 스탯 등을 배치하세요.</p>
      <div style={{display:"grid", gridTemplateColumns:"1fr 1fr", gap:16, marginTop:16}}>
        <div className="tile">
          <div className="tile-title">오늘의 칼로리</div>
          <div className="tile-value">1,420 kcal</div>
        </div>
        <div className="tile">
          <div className="tile-title">단백질 / 탄수 / 지방</div>
          <div className="tile-value">102g / 180g / 42g</div>
        </div>
      </div>
    </div>
  );
}
