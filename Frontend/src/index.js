import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom"; // Оборачиваем всё приложение в BrowserRouter

import App from "./App";
import reportWebVitals from "./reportWebVitals";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <BrowserRouter>
      {" "}
      {/* Это единственный Router, который нам нужен */}
      <App />
    </BrowserRouter>
  </React.StrictMode>
);

reportWebVitals();
