import React from "react";
import { createRoot } from "react-dom/client";
import App from "./App.jsx";
import './index.css';

const container = document.getElementById("root");
if (!container) throw new Error("#root not found");

// 중복 createRoot 방지(HMR 포함)
let root = container.__dietcare_root__;
if (!root) {
  root = createRoot(container);
  container.__dietcare_root__ = root;
}

root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);

if (import.meta.hot) {
  import.meta.hot.accept();
}
