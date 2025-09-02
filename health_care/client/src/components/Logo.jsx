export default function Logo({ size = 56, rounded = 14 }) {
  // public/logo.png 를 사용 (Vite의 public 정적 경로)
  const src = "/logo.png";
  return (
    <img
      src={src}
      width={size}
      height={size}
      alt="DietCare"
      style={{
        display: "block",
        borderRadius: rounded,
        boxShadow:
          "0 0 0 2px rgba(255,255,255,.06), 0 8px 24px rgba(0,0,0,.35)",
      }}
    />
  );
}
