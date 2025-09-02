// src/lib/api.js

// -----------------------
// Base URL 정규화 함수들
// -----------------------
function normalizeBase(raw) {
  let s = (raw || "").trim();

  // 빈 값이면 기본값
  if (!s) s = "http://localhost:8081";

  // ":8081"처럼 포트만 온 경우 → localhost 붙이기
  if (s.startsWith(":")) s = "http://localhost" + s;

  // "localhost:8081" / "127.0.0.1:8081" 등 → 스킴 추가
  if (!/^https?:\/\//i.test(s)) s = "http://" + s;

  // URL 파싱 & 보정
  try {
    const u = new URL(s);
    if (!u.hostname) u.hostname = "localhost";
    s = u.toString();
  } catch {
    s = "http://localhost:8081";
  }

  // 끝 슬래시 제거
  return s.endsWith("/") ? s.slice(0, -1) : s;
}

function resolveBase() {
  // 반드시 VITE_API_BASE만 읽음 (Vite 규칙)
  const envVal = import.meta.env?.VITE_API_BASE;
  const base = normalizeBase(envVal);
  if (envVal !== undefined) {
    console.info("[api] VITE_API_BASE =", envVal, "→ normalized =", base);
  } else {
    console.info("[api] VITE_API_BASE not set → use default", base);
  }
  return base;
}

const API_BASE = resolveBase();

// -----------------------
// 토큰 관리
// -----------------------
const TOKEN_KEY = "token";
let _token = localStorage.getItem(TOKEN_KEY) || null;

export function setToken(t) {
  _token = t || null;
  if (_token) localStorage.setItem(TOKEN_KEY, _token);
  else localStorage.removeItem(TOKEN_KEY);
}
export function getToken() { return _token; }
export function clearToken() { setToken(null); }

// -----------------------
// URL 조립
// -----------------------
function joinUrl(base, path) {
  if (/^https?:\/\//i.test(path)) return path;    // 이미 풀 URL
  const left = base.endsWith("/") ? base.slice(0, -1) : base;
  const right = path.startsWith("/") ? path : `/${path}`;
  return left + right;
}

// -----------------------
// fetch 헬퍼
//  - timeout 지원 (기본 12초)
//  - FormData body면 Content-Type 자동 생략
//  - 에러 객체에 status / body 동봉
// -----------------------
export async function request(path, options = {}) {
  const {
    method = "GET",
    body,
    headers = {},
    timeout = 12000, // ms
    // 필요 시 credentials, mode 등 추가 가능
  } = options;

  const url = joinUrl(API_BASE, path);

  // body 타입에 따라 Content-Type 결정
  const isFormData =
    typeof FormData !== "undefined" && body instanceof FormData;
  const defaultHeaders = isFormData
    ? {} // 브라우저가 boundary 포함해서 자동 설정
    : { "Content-Type": "application/json" };

  const h = { ...defaultHeaders, ...headers };
  if (_token) h.Authorization = "Bearer " + _token;

  // body 직렬화 (FormData는 그대로)
  let payload = body;
  if (payload && !isFormData && typeof payload !== "string") {
    payload = JSON.stringify(payload);
  }

  // 타임아웃 컨트롤러
  const ac = new AbortController();
  const id = setTimeout(() => ac.abort(new DOMException("Timeout", "TimeoutError")), timeout);

  let res;
  try {
    res = await fetch(url, { method, headers: h, body: payload, signal: ac.signal });
  } catch (err) {
    clearTimeout(id);
    // 네트워크/타임아웃 에러 메시지 명확화
    const msg =
      err?.name === "AbortError" || err?.name === "TimeoutError"
        ? "요청이 시간 초과되었습니다."
        : `네트워크 오류: ${err?.message || err}`;
    const e = new Error(msg);
    e.cause = err;
    e.status = 0;
    throw e;
  } finally {
    clearTimeout(id);
  }

  // 본문 텍스트 확보 후 JSON 파싱 시도 (text/plain 대응)
  const status = res.status;
  const ok = res.ok;

  // 204 No Content 등 대비
  const raw = status === 204 ? "" : await res.text();
  let data = null;
  try {
    data = raw ? JSON.parse(raw) : null;
  } catch {
    data = raw || null;
  }

  if (!ok) {
    const message =
      (data && (data.message || data.error)) ||
      (typeof data === "string" && data) ||
      `HTTP ${status}`;

    const error = new Error(message);
    error.status = status;
    error.body = data;
    error.url = url;
    throw error;
  }

  return data;
}

// -----------------------
// 편의 메서드 (선택)
// -----------------------
export const api = {
  get: (p, o) => request(p, { ...o, method: "GET" }),
  post: (p, body, o) => request(p, { ...o, method: "POST", body }),
  patch: (p, body, o) => request(p, { ...o, method: "PATCH", body }),
  put: (p, body, o) => request(p, { ...o, method: "PUT", body }),
  del: (p, o) => request(p, { ...o, method: "DELETE" }),
};

export { API_BASE };
