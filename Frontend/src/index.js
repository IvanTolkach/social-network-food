import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom"; // Оборачиваем всё приложение в BrowserRouter
import { I18nextProvider } from "react-i18next";
import App from "./App";
import reportWebVitals from "./reportWebVitals";
import i18n from "./i18n"; // импортируем i18n конфигурацию
const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <I18nextProvider i18n={i18n}>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </I18nextProvider>
);

reportWebVitals();
