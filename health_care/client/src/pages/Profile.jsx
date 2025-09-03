// src/pages/Profile.jsx
import {useEffect,useState} from "react";
import {clearToken, request} from "../lib/api.js";

export default function Profile(){
  const [me,setMe]=useState(null); const [msg,setMsg]=useState("");

  async function load(){ try{ setMe(await request("/api/users/me")); }catch(e){ setMsg("불러오기 실패: "+e.message); } }
  useEffect(()=>{ load(); },[]);

  async function togglePrivacy(){
    try{
      await request("/api/users/privacy",{method:"PATCH",body:{publicProfile:!me.publicProfile}});
      setMe({...me, publicProfile:!me.publicProfile});
    }catch(e){ alert("변경 실패: "+e.message); }
  }
  function logout(){ clearToken(); location.href="/login"; }

  if(!me) return <div>{msg||'로딩 중...'}</div>;

  return (
    <div style={{maxWidth:520}}>
      <h2>내 프로필</h2>
      <table><tbody>
        <tr><td>ID</td><td>{me.id}</td></tr>
        <tr><td>username</td><td>{me.username}</td></tr>
        <tr><td>email</td><td>{me.email}</td></tr>
        <tr><td>name</td><td>{me.name}</td></tr>
        <tr><td>gender</td><td>{me.gender}</td></tr>
        <tr><td>age</td><td>{me.age ?? '-'}</td></tr>
        <tr><td>height</td><td>{me.heightCm} cm</td></tr>
        <tr><td>weight</td><td>{me.weightKg} kg</td></tr>
        <tr><td>public</td><td>{String(me.publicProfile)}</td></tr>
        <tr><td>tier</td><td>{me.tier}</td></tr>
      </tbody></table>
      <button onClick={togglePrivacy} style={{marginTop:8}}>
        프로필 공개 {me.publicProfile?'끄기':'켜기'}
      </button>
      <button onClick={logout} style={{marginLeft:8}}>로그아웃</button>
    </div>
  );
}
